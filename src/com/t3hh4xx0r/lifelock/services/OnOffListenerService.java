package com.t3hh4xx0r.lifelock.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.t3hh4xx0r.lifelock.DBAdapter;
import com.t3hh4xx0r.lifelock.MainActivity;
import com.t3hh4xx0r.lifelock.R;
import com.t3hh4xx0r.lifelock.objects.Peek;

public class OnOffListenerService extends Service {
	protected static final int AVERAGE = 2;
	Peek currentInstance;
	DBAdapter db;
	private ServiceBinder mBinder = new ServiceBinder();
	TimerDrawerService.ServiceBinder drawerBinder;

	public class ServiceBinder extends Binder {
	
	}

	static public void start(Context c) {
		c.startService(new Intent(c, OnOffListenerService.class));
	}

	static public void stop(Context c) {
		c.stopService(new Intent(c, OnOffListenerService.class));
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			if (i.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (currentInstance == null) {
					currentInstance = new Peek(System.currentTimeMillis());
				} else {
					// Well thats weird;
				}
				if (drawerBinder == null || drawerBinder.getRoot() == null) {
					TimerDrawerService.start(c, currentInstance);
				} else {
					drawerBinder.add();
				}
			} else if (i.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				if (currentInstance != null) {
					currentInstance.setUnlockTime(System.currentTimeMillis());
					db.addPeek(currentInstance);
					boolean didGood = (currentInstance
							.getSecondsSinceLastPeek() > AVERAGE);
					if (drawerBinder != null) {
						if (didGood) {							
							drawerBinder.remove();
						} else {
							drawerBinder.getRoot().setLocked(true);
						}
					}
					Toast.makeText(c, (didGood ? "Good" : "Bad") + " job!",
							Toast.LENGTH_LONG).show();
					currentInstance = null;
				} else {
					// Well thats weird;
				}
			}
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof TimerDrawerService.ServiceBinder) {
				drawerBinder = (com.t3hh4xx0r.lifelock.services.TimerDrawerService.ServiceBinder) service;
			}
			// No need to keep the service bound.
			unbindService(this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// Nothing to do here.
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startupService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
		db.close();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startupService();
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private void startupService() {
		startForeground(1337, getNotification());
		bindService(new Intent(this, TimerDrawerService.class), mConnection, 0);

		final IntentFilter iF = new IntentFilter();
		iF.addAction(Intent.ACTION_SCREEN_OFF);
		iF.addAction(Intent.ACTION_SCREEN_ON);
		this.registerReceiver(this.receiver, iF);
		db = new DBAdapter(this);
		db.open();
	}

	public Notification getNotification() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return getNotificationWithNewApis();
		} else {
			return getNotificationWithoutNewApis();
		}
	}

	@SuppressWarnings("deprecation")
	private Notification getNotificationWithoutNewApis() {

		Intent i = new Intent(this, MainActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
		Builder mBuilder = new Notification.Builder(this).setOngoing(true)
				.setOnlyAlertOnce(true).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText("Monitoring").setContentIntent(pIntent)
				.setAutoCancel(true);
		Notification note = mBuilder.getNotification();
		note.flags |= Notification.FLAG_NO_CLEAR;
		return note;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private Notification getNotificationWithNewApis() {

		Intent i = new Intent(this, MainActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
		Builder mBuilder = new Notification.Builder(this).setOngoing(true)
				.setOnlyAlertOnce(true).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText("Monitoring").setContentIntent(pIntent)
				.setAutoCancel(true).setPriority(Notification.PRIORITY_MIN);
		Notification note = mBuilder.build();
		note.flags |= Notification.FLAG_NO_CLEAR;
		return note;
	}
}