package com.t3hh4xx0r.lifelock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.t3hh4xx0r.lifelock.objects.Peek;

public class GraphedDetailsActivity extends Activity implements OnTouchListener {
	ArrayList<Integer> plotPoints;
	XYPlot plot;
	XYSeries series1;
	private PointF minXY;
	private PointF maxXY;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	String tag;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.graph);
		findViewById(R.id.text_holder).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setAnimation(AnimationUtils.loadAnimation(v.getContext(),
						android.R.anim.fade_out));
				v.setVisibility(View.GONE);
			}
		});
		plot = (XYPlot) findViewById(R.id.plot);
		plot.setOnTouchListener(this);

		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
		plot.setPlotMargins(40, 40, 40, 40);
		plot.setPlotPadding(40, 40, 40, 40);
		plot.setGridPadding(40, 40, 40, 40);

		plot.setBackgroundColor(Color.WHITE);

		plot.getGraphWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT, 0,
				YLayoutStyle.RELATIVE_TO_CENTER, AnchorPosition.LEFT_MIDDLE);

		plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
		plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

		plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
		plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

		plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
		plot.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);

		// Domain
		plot.getGraphWidget().getDomainGridLinePaint()
				.setColor(Color.TRANSPARENT);
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		// Range
		plot.getGraphWidget().getRangeLabelPaint().setTextSize(24);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 5);
		plot.setRangeValueFormat(new DecimalFormat("0"));
		plot.getGraphWidget().getRangeGridLinePaint()
				.setColor(Color.TRANSPARENT);

		// Remove legend
		plot.getLayoutManager().remove(plot.getLegendWidget());
		plot.getLayoutManager().remove(plot.getDomainLabelWidget());
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

		plot.calculateMinMaxVals();
		minXY = new PointF(plotPoints.size() - 5, 0);
		maxXY = new PointF(plotPoints.size(), 120);

		// go to the end.
		plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
		plot.setRangeBoundaries(minXY.y, maxXY.y, BoundaryMode.FIXED);
		plot.redraw();

	}

	protected ArrayList<Integer> plotPointsFromList(ArrayList<Peek> list) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (Peek unlock : list) {
			result.add(unlock.getSecondsSinceLastPeek());
		}
		return result;
	}

	// Definition of the touch states
	static final int NONE = 0;
	static final int ONE_FINGER_DRAG = 1;
	static final int TWO_FINGERS_DRAG = 2;
	int mode = NONE;

	PointF firstFinger;
	float lastScrollingX;
	float lastScrollingY;

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			firstFinger = new PointF(event.getX(), event.getY());
			mode = ONE_FINGER_DRAG;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					while (Math.abs(lastScrollingX) > 1f
							|| Math.abs(lastScrollingY) > 1f) {
						lastScrollingX *= .8;
						lastScrollingY *= .8;
						scrollX(lastScrollingX);
						scrollY(lastScrollingY);
					}
				}
			}, 0);

		case MotionEvent.ACTION_MOVE:
			if (mode == ONE_FINGER_DRAG) {
				PointF oldFirstFinger = firstFinger;
				firstFinger = new PointF(event.getX(), event.getY());
				lastScrollingX = oldFirstFinger.x - firstFinger.x;
				lastScrollingY = oldFirstFinger.y - firstFinger.y;
				if (Math.abs(lastScrollingX) > Math.abs(lastScrollingY)) {
					scrollX(lastScrollingX);
				} else {
					scrollY(lastScrollingY);
				}
			}
			break;
		}
		return true;
	}

	private void scrollX(float pan) {
		float domainSpan = maxXY.x - minXY.x;
		float step = domainSpan / plot.getWidth();
		float offset = pan * step;
		if (maxXY.x + offset <= plotPoints.size() && minXY.x + offset > 0) {
			minXY.x += offset;
			maxXY.x += offset;

			plot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
			plot.redraw();
		}
	}

	private void scrollY(float pan) {
		float rangeSpan = maxXY.y - minXY.y;
		float step = rangeSpan / plot.getHeight();
		float offset = pan * step;
		if (minXY.y - offset > 0) {
			minXY.y -= offset;
			maxXY.y -= offset;

			plot.setRangeBoundaries(minXY.y, maxXY.y, BoundaryMode.FIXED);
			plot.redraw();
		}

	}
}
