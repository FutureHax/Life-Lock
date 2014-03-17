package com.t3hh4xx0r.lifelock.services;

/*
 Copyright 2011 jawsware international

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.t3hh4xx0r.lifelock.objects.Peek;
import com.t3hh4xx0r.lifelock.widgets.TimerView;

public class TimerDrawerService extends Service {
	TimerView root;
	Peek currentInstance;

	private ServiceBinder mBinder = new ServiceBinder();

	public class ServiceBinder extends Binder {
		public TimerView getRoot() {
			return root;
		}

		public void remove() {
			removeViews();
		}

		public void add() {
			addViews();
		}

	}

	static public void start(Context c, Peek currentInstance) {
		Intent i = new Intent(c, TimerDrawerService.class);
		i.putExtra("peek", currentInstance);
		c.startService(i);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		root = new TimerView(this);
		root.getTimer().setDurationMillis(90 * 1000);
		root.getTimer().start();
		addViews();
	}

	public void addViews() {
		try {
			((WindowManager) getSystemService(Context.WINDOW_SERVICE)).addView(
					root, getLayoutParams());
		} catch (Exception e) {

		}
		root.getDragLayout().maximize();
	}

	private WindowManager.LayoutParams getLayoutParams() {
		LayoutParams layoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, 0,
				PixelFormat.TRANSLUCENT);
		layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentInstance = (Peek) intent.getSerializableExtra("peek");
		return START_STICKY;
	}

	public void removeViews() {
		try {
			((WindowManager) getSystemService(Context.WINDOW_SERVICE))
					.removeView(root);
		} catch (Exception e) {
		}
	}
}
