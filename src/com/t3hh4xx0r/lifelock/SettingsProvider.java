package com.t3hh4xx0r.lifelock;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class SettingsProvider {

	Context ctx;

	public SettingsProvider(Context ctx) {
		this.ctx = ctx;
	}

	public static int getAppVersionCode(Context c) {
		try {
			PackageInfo packageInfo = c.getPackageManager().getPackageInfo(
					c.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public void setUserStats(UserStats stats) {
		PreferenceManager.getDefaultSharedPreferences(ctx).edit()
				.putInt("sex", stats.getSex()).putInt("age", stats.getAge())
				.putBoolean("didSave", true)
				.putString("country", stats.getCountry()).apply();
		stats.saveInBackground();
	}

	public boolean isFirstLaunchForVersion() {
		boolean isInPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
				.getBoolean("seen_" + getAppVersionCode(ctx), false);
		if (!isInPrefs) {
			PreferenceManager.getDefaultSharedPreferences(ctx).edit()
					.putBoolean("seen_" + getAppVersionCode(ctx), true).apply();
		}
		return !isInPrefs;
	}

	public boolean isFirstLaunchEver() {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getAll()
				.isEmpty();
	}

	public boolean didSaveUserStats() {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
				"didSave", false);
	}

	public boolean shouldSaveUserStats() {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
				"shouldSave", true);
	}

	public void setShouldSaveUserStats(boolean shouldSave) {
		PreferenceManager.getDefaultSharedPreferences(ctx).edit()
				.putBoolean("shouldSave", false).apply();
	}

	public UserStats getStats() {
		if (!didSaveUserStats()) {
			return null;
		}
		int age = PreferenceManager.getDefaultSharedPreferences(ctx).getInt(
				"age", -1);
		int sex = PreferenceManager.getDefaultSharedPreferences(ctx).getInt(
				"sex", -1);
		String country = PreferenceManager.getDefaultSharedPreferences(ctx)
				.getString("country", "UNKNOWN");

		UserStats stats = new UserStats();
		stats.setAge(age);
		stats.setCountry(country);
		stats.setSex(sex);
		return stats;
	}
}
