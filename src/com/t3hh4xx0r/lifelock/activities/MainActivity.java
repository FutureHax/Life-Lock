package com.t3hh4xx0r.lifelock.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.t3hh4xx0r.lifelock.R;
import com.t3hh4xx0r.lifelock.SettingsProvider;
import com.t3hh4xx0r.lifelock.services.OnOffListenerService;

public class MainActivity extends Activity {
	SettingsProvider settings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = new SettingsProvider(this);
        OnOffListenerService.start(this);
        
        findViewById(R.id.vew_graph).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), UsageDetailsActivity.class);
				startActivity(i);
			}
		});
        
        if (settings.isFirstLaunchEver()) {
        	Intent i = new Intent(this, AboutActivity.class);
    		startActivity(i);
        } else {
        	if (settings.shouldSaveUserStats() && !settings.didSaveUserStats()) {
        		Intent i = new Intent(this, UserStatsActivity.class);
        		startActivity(i);
        	}
        }
		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
