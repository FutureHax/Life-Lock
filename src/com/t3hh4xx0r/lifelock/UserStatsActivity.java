package com.t3hh4xx0r.lifelock;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserStatsActivity extends Activity {
	NumberPicker agePicker;
	EditText countryInput;
	private RadioGroup radioSexGroup;
	View save, skip;
	SettingsProvider settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_stats);
		settings = new SettingsProvider(this);

		agePicker = (NumberPicker) findViewById(R.id.age_picker);
		agePicker.setMaxValue(99);
		agePicker.setMinValue(13);

		countryInput = (EditText) findViewById(R.id.country_input);
		save = findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseUser user = ParseUser.getCurrentUser();
				user.put("gender", radioSexGroup
						.getCheckedRadioButtonId() == R.id.male ? "Male"
						: "Female");
				user.put("country", countryInput.getText().toString());
				user.put("age", agePicker.getValue());
				user.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						finish();						
					}
				});
			}
		});
		skip = findViewById(R.id.skip);
		skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				settings.setShouldSaveUserStats(false);
				finish();
			}
		});
		radioSexGroup = (RadioGroup) findViewById(R.id.radioGroup1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_stats, menu);
		return true;
	}

}
