package com.t3hh4xx0r.lifelock;

import java.util.ArrayList;
import java.util.Locale;

import android.R.raw;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.t3hh4xx0r.lifelock.objects.Peek;

public class UsageDetailsActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usage_details);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usage_details, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment;
			if (position == 0) {
				fragment = new AverageUnlockedTimeFragment();
			} else {
				fragment = new Fragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	public static class AverageUnlockedTimeFragment extends Fragment {
		ProgressDialog mProgressDialog;

		public class CalculateAverageTask extends AsyncTask<Void, Void, Void> {
			Context ctx;
			DBAdapter db;
			TextView rawView;
			TextView adjustedView;
			
			Long rawResult = 0L, adjustedResult = 0L;
			private CalculateAverageTask(Context ctx, TextView rawView, TextView adjustedView) {
				this.ctx = ctx;
				db = new DBAdapter(ctx);
				this.rawView = rawView;
				this.adjustedView = adjustedView;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				mProgressDialog.dismiss();
				rawView.setText(Long.toString(rawResult));
				adjustedView.setText(Long.toString(adjustedResult));
				ParseUser.getCurrentUser().put("rawAverage", rawResult);
				ParseUser.getCurrentUser().put("adjustedAverage", adjustedResult);
				ParseUser.getCurrentUser().saveInBackground();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mProgressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				ArrayList<Peek> peeks = db.getPeeks();
				long rawTotal = 0;
				long adjustedTotal = 0;
				int adjustedCount = 0;
				for (Peek p : peeks) {
					rawTotal = rawTotal + p.getSecondsSinceLastPeek();
					if (p.getSecondsSinceLastPeek() < 3600 * 3) {
						adjustedCount = adjustedCount + 1;
						adjustedTotal = adjustedTotal + p.getSecondsSinceLastPeek();
					}
				}
				
				rawResult = rawTotal / peeks.size();
				adjustedResult = adjustedTotal / adjustedCount;
				return null;
			}
		}

		public AverageUnlockedTimeFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(
					R.layout.fragment_user_averages, container, false);
			TextView rawAverageTextView = (TextView) rootView
					.findViewById(R.id.raw_average);
			
			TextView adjustedAverageTextView = (TextView) rootView
					.findViewById(R.id.adjusted_average);
			
			
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("A message");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);

			final CalculateAverageTask task = new CalculateAverageTask(
					getActivity(), rawAverageTextView, adjustedAverageTextView);
			task.execute();
			
			return rootView;
		}
	}

}
