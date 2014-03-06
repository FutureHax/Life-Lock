package com.t3hh4xx0r.lifelock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.t3hh4xx0r.lifelock.objects.Peek;
import com.t3hh4xx0r.lifelock.services.OnOffListenerService;
import com.t3hh4xx0r.lifelock.services.OnOffListenerService.ServiceBinder;
import com.t3hh4xx0r.lifelock.widgets.MultitouchPlot;

public class GraphedDetailsActivity extends Activity {
	ArrayList<Integer> plotPoints;
	MultitouchPlot plot;
	Handler handy = new Handler();
	XYSeries series1;
	Random rndm = new Random();
	String tag;
	ServiceBinder mBinder;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		bindService(new Intent(this, OnOffListenerService.class), mConnection,
				0);
		setContentView(R.layout.graph);

		plot = (MultitouchPlot) findViewById(R.id.plot);
		tag = "Graph Title";

		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
		plot.setPlotMargins(10, 10, 10, 10);
		plot.setPlotPadding(10, 10, 10, 10);
		plot.setGridPadding(10, 10, 10, 10);

		plot.setBackgroundColor(Color.WHITE);

		plot.getGraphWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0,
				YLayoutStyle.RELATIVE_TO_CENTER, AnchorPosition.LEFT_MIDDLE);

		plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
		plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

		plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
		plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

		plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
		plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

		// Domain
		plot.getGraphWidget().getDomainGridLinePaint()
				.setColor(Color.TRANSPARENT);

		// Range
		plot.getGraphWidget().getRangeLabelPaint().setTextSize(24);
		plot.setRangeBoundaries(0, 120, BoundaryMode.FIXED);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 5);
		plot.setRangeValueFormat(new DecimalFormat("0"));
		plot.getGraphWidget().getRangeGridLinePaint()
				.setColor(Color.TRANSPARENT);

		// Remove legend
		plot.getLayoutManager().remove(plot.getLegendWidget());
		plot.getLayoutManager().remove(plot.getDomainLabelWidget());
		plot.getLayoutManager().remove(plot.getRangeLabelWidget());
		plot.getLayoutManager().remove(plot.getTitleWidget());

		// TESTING STUFF
		plot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
		plotPoints = plotPointsFromList(new DBAdapter(this).getPeeks());
		XYSeries series1 = new SimpleXYSeries(plotPoints,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
		LineAndPointFormatter series1Format = new LineAndPointFormatter();
		series1Format.setPointLabelFormatter(new PointLabelFormatter());
		series1Format.configure(GraphedDetailsActivity.this,
				R.xml.line_point_formatter_with_plf1);
		plot.addSeries(series1, series1Format);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof ServiceBinder) {
				mBinder = (ServiceBinder) service;
			}
			// No need to keep the service bound.
			unbindService(this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// Nothing to do here.
		}
	};

	protected ArrayList<Integer> plotPointsFromList(ArrayList<Peek> list) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (Peek unlock : list) {
			result.add(unlock.getSecondsSinceLastPeek());
		}
		return result;
	}

}
