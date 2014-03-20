/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t3hh4xx0r.lifelock.widgets;

import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.t3hh4xx0r.lifelock.R;
import com.t3hh4xx0r.lifelock.services.TimerDrawerService;
import com.t3hh4xx0r.lifelock.widgets.DragLayout.OnTimerDragDismissedListener;

/**
 * View used to draw a running timer.
 */
public class TimerView extends FrameLayout {
	TimerDrawerService.ServiceBinder drawerBinder;
	DragLayout dragLayout;

	/**
	 * Interface to listen for changes on the view layout.
	 */
	public interface ChangeListener {
		/** Notified of a change in the view. */
		public void onChange();
	}

	private static final long DELAY_MILLIS = 1000;

	private final TextView mMinutesView;
	private final TextView mSecondsView;

	private final int mWhiteColor;
	private final int mRedColor;

	private final Handler mHandler = new Handler();
	private final Runnable mUpdateTextRunnable = new Runnable() {
		@Override
		public void run() {
			if (mRunning) {
				mHandler.postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
				updateText();
			}
		}
	};

	private final Timer mTimer;
	private final Timer.TimerListener mTimerListener = new Timer.TimerListener() {
		@Override
		public void onStart() {
			mRunning = true;
			long delayMillis = Math.abs(mTimer.getRemainingTimeMillis())
					% DELAY_MILLIS;
			if (delayMillis == 0) {
				delayMillis = DELAY_MILLIS;
			}
			mHandler.postDelayed(mUpdateTextRunnable, delayMillis);
		}
	};

	private boolean mRunning;
	private boolean mRedText;

	private ChangeListener mChangeListener;

	public TimerView(Context context) {
		this(context, null, 0);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof TimerDrawerService.ServiceBinder) {
				drawerBinder = (com.t3hh4xx0r.lifelock.services.TimerDrawerService.ServiceBinder) service;
			}
			// No need to keep the service bound.
			getContext().unbindService(this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// Nothing to do here.
		}
	};

	public TimerView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		context.bindService(new Intent(context, TimerDrawerService.class),
				mConnection, 0);

		LayoutInflater.from(context).inflate(R.layout.timer, this);

		dragLayout = (DragLayout) findViewById(R.id.dragLayout);
		dragLayout.setDismissedListener(new OnTimerDragDismissedListener() {
			@Override
			public void onDismissed() {
				if (drawerBinder != null) {
					drawerBinder.remove();
				}
			}
		});
		mMinutesView = (TextView) findViewById(R.id.minutes);
		mSecondsView = (TextView) findViewById(R.id.seconds);

		mWhiteColor = context.getResources().getColor(android.R.color.white);
		mRedColor = Color.RED;

		mTimer = new Timer();
		mTimer.setListener(mTimerListener);
		mTimer.setDurationMillis(0);
	}

	public Timer getTimer() {
		return mTimer;
	}

	public DragLayout getDragLayout() {
		return dragLayout;
	}

	/**
	 * Set a {@link ChangeListener}.
	 */
	public void setListener(ChangeListener listener) {
		mChangeListener = listener;
	}

	/**
	 * Updates the text from the Timer's value.
	 */
	private void updateText() {
		long remainingTimeMillis = mTimer.getRemainingTimeMillis();

		if (remainingTimeMillis > 0) {
			mRedText = false;
			// Round up: x001 to (x + 1)000 milliseconds should resolve to x
			// seconds.
			remainingTimeMillis -= 1;
			remainingTimeMillis += TimeUnit.SECONDS.toMillis(1);
		} else {
			if (drawerBinder != null) {
				drawerBinder.remove();
			}
			return;
		}

		if (mRedText) {
			// Sync the sound with the red text.
		}

		updateText(remainingTimeMillis, mRedText ? mRedColor : mWhiteColor);
	}

	/**
	 * Updates the displayed text with the provided values.
	 */
	private void updateText(long timeMillis, int textColor) {
		timeMillis %= TimeUnit.HOURS.toMillis(1);
		mMinutesView.setText(String.format("%02d",
				TimeUnit.MILLISECONDS.toMinutes(timeMillis)));
		mMinutesView.setTextColor(textColor);
		timeMillis %= TimeUnit.MINUTES.toMillis(1);
		mSecondsView.setText(String.format("%02d",
				TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
		mSecondsView.setTextColor(textColor);
		if (mChangeListener != null) {
			mChangeListener.onChange();
		}

	}

	public void showMessage(boolean didGood) {
		// mTipView.setText((didGood ? "Good" : "Bad") + " job!");
	}

	public void setLocked(boolean b) {
		if (b) {
			((WindowManager.LayoutParams) getLayoutParams()).type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		} else {
			((WindowManager.LayoutParams) getLayoutParams()).type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		}
	}
}
