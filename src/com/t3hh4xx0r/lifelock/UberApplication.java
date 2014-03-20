/**
 * 
 */
package com.t3hh4xx0r.lifelock;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class UberApplication extends Application {
	public static final int AVERAGE = 5;
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "MNwjBOngL7g9a5Er7XXew0rPmvpKUUXSk0zx1D1d",
				"Iv2fuUirS7VhAI4PZF1g2cQ3f7XSsBnK3OTtdIB9");
	}
}
