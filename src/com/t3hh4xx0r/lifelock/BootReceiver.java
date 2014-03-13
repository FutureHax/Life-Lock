package com.t3hh4xx0r.lifelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.t3hh4xx0r.lifelock.services.OnOffListenerService;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		OnOffListenerService.start(context);
	}
}