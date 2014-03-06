package com.t3hh4xx0r.lifelock.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.t3hh4xx0r.lifelock.DBAdapter;
import com.t3hh4xx0r.lifelock.objects.Peek;

public class OnOffListenerService extends Service {
	Peek currentInstance;
	DBAdapter db;
	private ServiceBinder mBinder = new ServiceBinder();

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
					currentInstance = new Peek(
							System.currentTimeMillis());
				} else {
					// Well thats weird;
				}
			} else if (i.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				if (currentInstance != null) {
					currentInstance.setUnlockTime(System.currentTimeMillis());
					db.addPeek(currentInstance);
					currentInstance = null;
				} else {
					// Well thats weird;
				}
			}
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
		final IntentFilter iF = new IntentFilter();
		iF.addAction(Intent.ACTION_SCREEN_OFF);
		iF.addAction(Intent.ACTION_SCREEN_ON);
		this.registerReceiver(this.receiver, iF);
		db = new DBAdapter(this);
		db.open();
	}
}