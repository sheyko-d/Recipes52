package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.pojo.Graph;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.wheel.widget.adapters.ArrayWheelAdapter;
import com.stockholmapplab.recipes.widget.OnWheelClickedListener;
import com.stockholmapplab.recipes.widget.OnWheelScrollListener;
import com.stockholmapplab.recipes.widget.WheelView;
import com.stockholmapplab.recipes.R;

/**
 * BodyStatsActivity is use for show body statistics on graph. This class show
 * different types of graph.
 */
public class BodyStatsActivity extends ActionBarActivity {

	private static final int KCAL = 0;
	private static final int WEIGHT = 1;
	private static final int WEIGHT2 = 7;
	private static final int WEIGHT3 = 8;
	private static final int WEIGHT4 = 9;
	private static final int WEIST = 2;
	private static final int CHOLESTEROL = 3;
	private static final int TRIGLYCERIDS = 4;
	private static final int GLUCOSE = 5;
	private static final int IGF = 6;

	private static final String[] y_axis_KCAL = { "0", "500", "1000", "1500",
			"2000", "2500", "3000" };
	private static final String[] y_axis_WEIGHT = { "30", "50", "70", "90",
			"110", "130", "150", "170" };
	private static final String[] y_axis_WAIST = { "35", "50", "65", "80",
			"95", "110", "125", "140" };
	private static final String[] y_axis_CHOLESTEROL = { "50", "85", "120",
			"155", "190", "225", "260", "295" };
	private static final String[] y_axis_TRIGLYCERIDS = { "75", "150", "225",
			"300", "375", "450", "525", "600" };
	private static final String[] y_axis_GLUCOSE = { "3.3", "4.0", "4.7",
			"5.4", "6.1", "6.8", "7.5", "8.2", "8.9" };
	private static final String[] y_axis_IGF = { "50", "100", "150", "200",
			"250", "300", "350", "400" };

	// COLOR CODES
	private static final String KCAL_COLOR = "#659a59";
	private static final String WEI_COLOR = "#ff0000";
	private static final String WAI_COLOR = "#1962a2";
	private static final String CHO_COLOR = "#996633";
	private static final String TRI_COLOR = "#ff8000";
	private static final String GLU_COLOR = "#ff00ff";
	private static final String IGF_COLOR = "#800080";

	private static final float[] defaultValue = { 0, 60, 72, 180, 150, 4.5f,
			180 };

	public static final String extraGraph = "extraGraph";
	public static final String extraStartTime = "extraStartTime";
	public static final String extraEndTime = "extraEndTime";
	public static final String extraFromHistory = "extraFromHistory";
	public static final String extraUsedCal = "extraUsedCal";

	private String[] x_axis;
	private String[] x_axis_original;
	private int mapIndex = 1;

	private CustomTextView mTxtKCAL, mTxtWEI, mTxtWAI, mTxtCHO, mTxtTRI,
			mTxtGLU, mTxtIGF, mTxtTap;
	private RelativeLayout mRelative;
	private ImageView mImgKCAL, mImgWEI, mImgWAI, mImgCHO, mImgTRI, mImgGLU,
			mImgIGF;

	private Dialog mGoalPickerDialog = null, mValuePickerDialog = null;
	private LinearLayout mLinear1;
	private WheelView number, number2, number3, number4, date;

	private ArrayList<String> mWeight = new ArrayList<String>();
	private ArrayList<String> mWeight2 = new ArrayList<String>();
	private ArrayList<String> mWeight3 = new ArrayList<String>();
	private ArrayList<String> mWeight4 = new ArrayList<String>();
	private ArrayList<Graph> mGraph = new ArrayList<Graph>();
	private boolean mFromHistory = false;
	private Map<String, Integer> mUsedCal = new HashMap<String, Integer>();

	private boolean showDemo = true;

	// ACHART ENGING LIBARAY VIEWS AND SETS
	private GraphicalView mChartView;
	/** The main dataset that includes all the series that go into a chart. */
	private static XYMultipleSeriesDataset mDataset;// = new
													// XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	private static XYMultipleSeriesRenderer mRenderer;// = new
														// XYMultipleSeriesRenderer();
	protected boolean isPanEnables;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	private double mHoundreds = 0;
	private double mDozens = 0;
	private double mUnits = 0;
	private double mDecimals = 0;

	private double mHoundredsGoal = 0;
	private double mDozensGoal = 0;
	private double mUnitsGoal = 0;
	private double mDecimalsGoal = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_body_stats);

		initActionBar();

		mGraph = (ArrayList<Graph>) getIntent()
				.getSerializableExtra(extraGraph);
		mFromHistory = getIntent().getExtras().getBoolean(extraFromHistory);
		mUsedCal = getMap(getIntent().getExtras().getString(extraUsedCal));
		mDataset = new XYMultipleSeriesDataset();
		/**
		 * The main renderer that includes all the renderers customizing a
		 * chart.
		 */
		mRenderer = new XYMultipleSeriesRenderer();
		init();
		initializeSeries();
		setGraph(WEIGHT);
		addGraph();

	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
	}

	private void setTitle(String title) {
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		default:
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {
		getX_axis();
		mLinear1 = GenericView.findViewById(this, R.id.chart);
		mImgKCAL = GenericView.findViewById(this, R.id.iv_imgKCAL);
		mImgWEI = GenericView.findViewById(this, R.id.iv_imgWEI);
		mImgWAI = GenericView.findViewById(this, R.id.iv_imgWAI);
		mImgCHO = GenericView.findViewById(this, R.id.iv_imgCHO);
		mImgTRI = GenericView.findViewById(this, R.id.iv_imgTRI);
		mImgGLU = GenericView.findViewById(this, R.id.iv_imgGLU);
		mImgIGF = GenericView.findViewById(this, R.id.iv_imgIGF);

		mTxtKCAL = GenericView.findViewById(this, R.id.tv_txt_KCAL);
		mTxtWEI = GenericView.findViewById(this, R.id.tv_txt_WEI);
		mTxtWAI = GenericView.findViewById(this, R.id.tv_txt_WAI);
		mTxtCHO = GenericView.findViewById(this, R.id.tv_txt_CHO);
		mTxtTRI = GenericView.findViewById(this, R.id.tv_txt_TRI);
		mTxtGLU = GenericView.findViewById(this, R.id.tv_txt_GLU);
		mTxtIGF = GenericView.findViewById(this, R.id.tv_txt_IGF);

		mRelative = GenericView.findViewById(this, R.id.rl_relativeBodyStats2);

		setTitle(getString(R.string.str_graph) + " "
				+ getString(R.string.str_weight));
		mRelative.setVisibility(View.VISIBLE);

		mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
		mImgWEI.setBackgroundResource(R.drawable.ic_weight_pressed);
		mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
		mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
		mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
		mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
		mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

		mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
		mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
		// show hint dialog after 500 milliseconds
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Add the views and text
				showHintDialogs();
			}
		}, 500);
	}

	/**
	 * initialize x_axis, y_axis, annotation and other properties of graph
	 */
	private void initializeSeries() {
		// enable the chart click events
		mRenderer.setClickEnabled(true);
		mRenderer.setSelectableBuffer(10);
		mRenderer.setExternalZoomEnabled(false);
		mRenderer.setZoomButtonsVisible(false);
		if (x_axis.length > 4) {
			mRenderer.setZoomEnabled(true, false);
			mRenderer.setZoomRate(2);
			mRenderer.setPanEnabled(true, false);
			mRenderer
					.setPanLimits(new double[] { -0.1, x_axis.length + 1, 0, 0 });
		} else {
			mRenderer.setZoomRate(1);
			mRenderer.setPanEnabled(false);
			mRenderer.setZoomEnabled(false);
		}
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.TRANSPARENT);
		mRenderer.setShowLegend(false);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);
		mRenderer.setAxisTitleTextSize(12);
		mRenderer.setLabelsTextSize(getResources().getDimension(
				R.dimen.label_text_size));
		mRenderer.setChartTitleTextSize(getResources().getDimension(
				R.dimen.label_text_size));
		mRenderer.setLegendTextSize(getResources().getDimension(
				R.dimen.label_text_size));
		mRenderer.setYLabelsPadding(15);
		mRenderer.setXLabelsPadding(15);
		mRenderer.setYLabelsVerticalPadding(-10);

		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setMargins(new int[] { 30, 60, 0, 20 });
		mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		mRenderer.setXLabels(0);
		mRenderer.setYLabels(0);
		mRenderer.setGridColor(Color.WHITE);
		mRenderer.setShowGrid(true);
		mRenderer.setShowGridY(true);
		mRenderer.setInScroll(true);
		mRenderer.setShowCustomTextGridX(true);

		try {
			XYSeries a = mDataset.getSeriesAt(0);
			if (a != null) {
				mDataset.removeSeries(a);
				SimpleSeriesRenderer xxx = mRenderer.getSeriesRendererAt(0);
				mRenderer.removeSeriesRenderer(xxx);
			}
			XYSeries b = mDataset.getSeriesAt(1);
			if (b != null) {
				SimpleSeriesRenderer xxx = mRenderer.getSeriesRendererAt(1);
				mRenderer.removeSeriesRenderer(xxx);
				mDataset.removeSeries(b);
			}
		} catch (Exception e) {
		}

		XYSeries xySeries = new XYSeries("");
		Set<String> keySet = mGraph.get(mapIndex).getValues().keySet();
		for (int i = 0; i < x_axis.length; i++) {

			if (mapIndex == 0) {
				Set<String> keySet1 = mUsedCal.keySet();
				if (keySet1.contains(x_axis_original[i])) {

					xySeries.addAnnotation(
							String.valueOf(mUsedCal.get(x_axis_original[i])),
							i + 1, mUsedCal.get(x_axis_original[i]));
					xySeries.add(i + 1, mUsedCal.get(x_axis_original[i]));

				} else {
					xySeries.addAnnotation(
							String.valueOf(defaultValue[mapIndex]), i + 1,
							defaultValue[mapIndex]);
					xySeries.add(i + 1, defaultValue[mapIndex]);
					mGraph.get(mapIndex).setValues(x_axis_original[i],
							defaultValue[mapIndex]);
				}
			} else {
				if (keySet.contains(x_axis_original[i])) {
					xySeries.addAnnotation(
							String.valueOf(mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i])),
							i + 1,
							mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i]));
					xySeries.add(
							i + 1,
							mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i]));
				} else {

					xySeries.addAnnotation(
							String.valueOf(defaultValue[mapIndex]), i + 1,
							defaultValue[mapIndex]);
					xySeries.add(i + 1, defaultValue[mapIndex]);
					mGraph.get(mapIndex).setValues(x_axis_original[i],
							defaultValue[mapIndex]);
				}
			}
		}

		mDataset.addSeries(0, xySeries);
		XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(0, xyRenderer);

		// SET PROPERTY OF XY RENDERER
		// xyRenderer.setColor(Color.RED);
		xyRenderer.setFillPoints(true);
		xyRenderer.setLineWidth(6);
		xyRenderer.setDisplayChartValues(false);
		xyRenderer.setDisplayChartString(false);
		xyRenderer.setDisplayChartValuesDistance(10);
		xyRenderer.setAnnotationsColor(Color.BLACK);
		xyRenderer.setAnnotationsTextSize(getResources().getDimension(
				R.dimen.annotation_text_size));

		XYSeries goalSeries = new XYSeries("");

		if (mGraph.get(mapIndex).getGoal() != 0) {
			for (int i = 0; i < mGraph.get(mapIndex).getValues().size() + 1; i++) {
				goalSeries.add(i, mGraph.get(mapIndex).getGoal());
			}

			goalSeries.addAnnotation("Goal", 0.3, mGraph.get(mapIndex)
					.getGoal());
		}

		mDataset.addSeries(1, goalSeries);

		XYSeriesRenderer goalRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(1, goalRenderer);

		// SET PROPERTY OF Goal RENDERER
		goalRenderer.setColor(Color.GRAY);
		goalRenderer.setFillPoints(true);
		goalRenderer.setLineWidth(1);
		goalRenderer.setDisplayChartValues(false);
		goalRenderer.setDisplayChartString(false);
		goalRenderer.setDisplayChartValuesDistance(10);
		goalRenderer.setAnnotationsColor(Color.GRAY);
		goalRenderer.setAnnotationsTextSize(30);
	}

	// SET THE DATA OF GRAPH HERE
	private void setGraph(int id) {
		mRenderer.clearXTextLabels();
		mRenderer.clearYTextLabels();
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(x_axis.length + 1);

		for (int i = 0; i < x_axis.length; i++) {
			mRenderer.addXTextLabel(i + 1, x_axis[i]);
		}

		if (id == KCAL) {
			mRenderer.setYAxisMin(0);
			mRenderer.setYAxisMax(3000);

			for (int i = 0; i < y_axis_KCAL.length; i++) {
				mRenderer.addYTextLabel(Double.parseDouble(y_axis_KCAL[i]),
						y_axis_KCAL[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(KCAL_COLOR));

		} else if (id == WEIGHT) {
			mRenderer.setYAxisMin(20);
			mRenderer.setYAxisMax(170);

			for (int i = 0; i < y_axis_WEIGHT.length; i++) {
				mRenderer.addYTextLabel(Double.parseDouble(y_axis_WEIGHT[i]),
						y_axis_WEIGHT[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(WEI_COLOR));
		} else if (id == WEIST) {

			mRenderer.setYAxisMin(40);
			mRenderer.setYAxisMax(140);

			for (int i = 0; i < y_axis_WAIST.length; i++) {
				mRenderer.addYTextLabel(Double.parseDouble(y_axis_WAIST[i]),
						y_axis_WAIST[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(WAI_COLOR));
		} else if (id == CHOLESTEROL) {
			mRenderer.setYAxisMin(20);
			mRenderer.setYAxisMax(300);

			for (int i = 0; i < y_axis_CHOLESTEROL.length; i++) {
				mRenderer.addYTextLabel(
						Double.parseDouble(y_axis_CHOLESTEROL[i]),
						y_axis_CHOLESTEROL[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(CHO_COLOR));
		} else if (id == TRIGLYCERIDS) {
			mRenderer.setYAxisMin(30);
			mRenderer.setYAxisMax(600);

			for (int i = 0; i < y_axis_TRIGLYCERIDS.length; i++) {
				mRenderer.addYTextLabel(
						Double.parseDouble(y_axis_TRIGLYCERIDS[i]),
						y_axis_TRIGLYCERIDS[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(TRI_COLOR));
		} else if (id == GLUCOSE) {
			mRenderer.setYAxisMin(3.0);
			mRenderer.setYAxisMax(8.9);

			for (int i = 0; i < y_axis_GLUCOSE.length; i++) {
				mRenderer.addYTextLabel(Double.parseDouble(y_axis_GLUCOSE[i]),
						y_axis_GLUCOSE[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(GLU_COLOR));
		} else if (id == IGF) {
			mRenderer.setYAxisMin(50);
			mRenderer.setYAxisMax(400);

			for (int i = 0; i < y_axis_IGF.length; i++) {
				mRenderer.addYTextLabel(Double.parseDouble(y_axis_IGF[i]),
						y_axis_IGF[i]);
			}
			mRenderer.getSeriesRendererAt(0).setColor(
					Color.parseColor(IGF_COLOR));
		}

		if (mChartView != null) {
			mChartView.invalidate();
		}
	}

	// ADD THE GRAPH IN LINEAR LAYOUT.
	@SuppressLint("NewApi")
	private void addGraph() {
		if (mChartView == null) {
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			mChartView.setClickable(true);
			mChartView.addPanListener(new PanListener() {
				@Override
				public void panApplied() {
					isPanEnables = true;
				}
			});
			mChartView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// handle the click event on the chart
					if (!mFromHistory && !isPanEnables) {
						if (mapIndex >= 1)
							setCurrentValuePickerDialog();

					} else {
						isPanEnables = false;
					}
				}
			});
			mLinear1.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	/**
	 * change or update annotation on graph and repaint This annotation is use
	 * to show set values of body statistics on graph
	 */
	private void setCurrentBodyStatisticsAnnotation() {

		mDataset.removeSeries(0);
		XYSeries xySeries = new XYSeries("");

		Set<String> keySet = mGraph.get(mapIndex).getValues().keySet();

		for (int i = 0; i < x_axis.length; i++) {

			if (mapIndex == 0) {
				Set<String> keySet1 = mUsedCal.keySet();
				if (keySet1.contains(x_axis_original[i])) {

					xySeries.addAnnotation(
							String.valueOf(mUsedCal.get(x_axis_original[i])),
							i + 1, mUsedCal.get(x_axis_original[i]));
					xySeries.add(i + 1, mUsedCal.get(x_axis_original[i]));

				} else {
					xySeries.addAnnotation(
							String.valueOf(defaultValue[mapIndex]), i + 1,
							defaultValue[mapIndex]);
					xySeries.add(i + 1, defaultValue[mapIndex]);
					mGraph.get(mapIndex).setValues(x_axis_original[i],
							defaultValue[mapIndex]);
				}
			} else {
				if (keySet.contains(x_axis_original[i])) {
					xySeries.addAnnotation(
							String.valueOf(mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i])),
							i + 1,
							mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i]));
					xySeries.add(
							i + 1,
							mGraph.get(mapIndex).getValues()
									.get(x_axis_original[i]));
				} else {

					xySeries.addAnnotation(
							String.valueOf(defaultValue[mapIndex]), i + 1,
							defaultValue[mapIndex]);
					xySeries.add(i + 1, defaultValue[mapIndex]);
					mGraph.get(mapIndex).setValues(x_axis_original[i],
							defaultValue[mapIndex]);
				}
			}
		}
		mDataset.addSeries(0, xySeries);
		mChartView.repaint();
	}

	/**
	 * change or update goal on graph and repaint
	 */
	private void setGoalAnnotation() {
		mDataset.removeSeries(1);
		XYSeries xySeries = new XYSeries("");

		if (mGraph.get(mapIndex).getGoal() != 0) {
			for (int i = 0; i < mGraph.get(mapIndex).getValues().size() + 1; i++) {
				xySeries.add(i, mGraph.get(mapIndex).getGoal());
			}
			xySeries.addAnnotation("Goal", 0.3, mGraph.get(mapIndex).getGoal());
		}
		mDataset.addSeries(1, xySeries);
		mChartView.repaint();
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.ll_linear_BodyStats1:
			if (mapIndex != KCAL) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = KCAL;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(KCAL);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_pressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_kcal_2));
			mRelative.setVisibility(View.INVISIBLE);

			break;
		case R.id.ll_linear_BodyStats2:
			if (mapIndex != WEIGHT) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = WEIGHT;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(WEIGHT);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_pressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_weight));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.ll_linear_BodyStats3:
			if (mapIndex != WEIST) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = WEIST;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(WEIST);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_pressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_waist));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.ll_linear_BodyStats4:
			if (mapIndex != CHOLESTEROL) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = CHOLESTEROL;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(CHOLESTEROL);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_pressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_cholesterol));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.ll_linear_BodyStats5:
			if (mapIndex != TRIGLYCERIDS) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = TRIGLYCERIDS;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(TRIGLYCERIDS);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_pressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_triglycerids));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.ll_linear_BodyStats6:
			if (mapIndex != GLUCOSE) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = GLUCOSE;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(GLUCOSE);
			setAfterClick();
			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_pressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_unpressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_glucose));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.ll_linear_BodyStats7:
			if (mapIndex != IGF) {
				mDozens = 0;
				mHoundreds = 0;
				mUnits = 0;
				mDecimals = 0;
				mapIndex = IGF;
			}
			setCurrentBodyStatisticsAnnotation();
			setGoalAnnotation();
			setGraph(IGF);
			setAfterClick();

			mImgKCAL.setBackgroundResource(R.drawable.ic_kcal_unpressed);
			mImgWEI.setBackgroundResource(R.drawable.ic_weight_unpressed);
			mImgWAI.setBackgroundResource(R.drawable.ic_waist_unpressed);
			mImgCHO.setBackgroundResource(R.drawable.ic_cholesterol_unpressed);
			mImgTRI.setBackgroundResource(R.drawable.ic_triglycerids_unpressed);
			mImgGLU.setBackgroundResource(R.drawable.ic_glucose_unpressed);
			mImgIGF.setBackgroundResource(R.drawable.ic_igf_pressed);

			mTxtKCAL.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWEI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtWAI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtCHO.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtTRI.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtGLU.setTextColor(ResourceUtil.getColor(R.color.grey_unpressed));
			mTxtIGF.setTextColor(ResourceUtil.getColor(R.color.grey_pressed));

			setTitle(getString(R.string.str_graph) + " "
					+ getString(R.string.str_igf_1));
			mRelative.setVisibility(View.VISIBLE);

			break;
		case R.id.bt_btn_set_goal:
			if (!mFromHistory) {
				GoalPickerDialog();
			}
			break;
		default:
			break;
		}
	}

	private void setAfterClick() {
		mLinear1.removeAllViews();
		mChartView = null;
		addGraph();
	}

	@Override
	protected void onStop() {

		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		if (!mFromHistory) {
			BaseConstant.mDietCycle.setmGraphs(mGraph);
			DietCycle.setDietCycle();
		}
		super.onStop();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE:
			if (requestCode == FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					Utility.FacebookPostOnAchievements(
							this,
							data.getExtras().getString(
									FacebookLoginActivity.extraMessage),
							data.getExtras().getInt(
									FacebookLoginActivity.extraDrawableID));
				}
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		}
		super.onResume();
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(BodyStatsActivity.this);
		}
	};

	@Override
	protected void onDestroy() {
		mLinear1.removeAllViews();
		mRenderer = null;
		mDataset = null;
		mChartView = null;
		super.onDestroy();
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.BODY_STATS_ACTIVITY_ID);
		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(getApplicationContext().getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
			mOptions.block = false;
			mOptions.hideOnClickOutside = false;
			mViews = new ShowcaseViews(this, R.layout.showcase_view_template,
					new ShowcaseViews.OnShowcaseAcknowledged() {
						@Override
						public void onShowCaseAcknowledged(
								ShowcaseView showcaseView) {
						}
					});

			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_BODY_STAT_GRAPH), false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(R.id.chart,
						R.string.str_start_message_20, 0,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.BODY_STATS_ACTIVITY_ID));
			}
			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_BODY_STAT_STATS), false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_MainLinearBodyStats2,
						R.string.str_start_message_21, 1,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.BODY_STATS_ACTIVITY_ID));
			}
			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_BODY_STAT_SET_GOAL), false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_body_stats_1, R.string.str_start_message_22, 2,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.BODY_STATS_ACTIVITY_ID));
			}
			mViews.show();
		}
	}

	/**
	 * GoalPickerDialog() method is use show dialog for set goal value.
	 */
	public void GoalPickerDialog() {
		mWeight.clear();
		mWeight2.clear();
		mWeight3.clear();
		mWeight4.clear();
		for (int i = 0; i <= 1; i++) {
			mWeight.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight2.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight3.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight4.add(String.valueOf(i));
		}

		mGoalPickerDialog = new Dialog(this, R.style.CustomDialogTheme);
		mGoalPickerDialog.setContentView(R.layout.raw_goal_picker);
		mGoalPickerDialog.setCanceledOnTouchOutside(true);
		Window window = mGoalPickerDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		number = (WheelView) mGoalPickerDialog.findViewById(R.id.number);
		number2 = (WheelView) mGoalPickerDialog.findViewById(R.id.number2);
		number3 = (WheelView) mGoalPickerDialog.findViewById(R.id.number3);
		number4 = (WheelView) mGoalPickerDialog.findViewById(R.id.number4);
		mTxtTap = (CustomTextView) mGoalPickerDialog
				.findViewById(R.id.tv_txtTap);
		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		number.addClickingListener(click);
		number2.addClickingListener(click);
		number3.addClickingListener(click);
		number4.addClickingListener(click);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {
				switch (wheel.getId()) {
				case R.id.number:
					mHoundredsGoal = Double.parseDouble(mWeight.get(number
							.getCurrentItem())) * 100;
					mGraph.get(mapIndex).setGoal(
							mHoundredsGoal + mDozensGoal + mUnitsGoal
									+ mDecimalsGoal);
					setGoalAnnotation();
					break;
				case R.id.number2:
					mDozensGoal = Double.parseDouble(mWeight2.get(number2
							.getCurrentItem())) * 10;
					mGraph.get(mapIndex).setGoal(
							mHoundredsGoal + mDozensGoal + mUnitsGoal
									+ mDecimalsGoal);
					setGoalAnnotation();
					break;
				case R.id.number3:
					mUnitsGoal = Double.parseDouble(mWeight3.get(number3
							.getCurrentItem()));
					mGraph.get(mapIndex).setGoal(
							mHoundredsGoal + mDozensGoal + mUnitsGoal
									+ mDecimalsGoal);
					setGoalAnnotation();
					break;
				case R.id.number4:
					mDecimalsGoal = Double.parseDouble(mWeight4.get(number4
							.getCurrentItem())) / 10;
					mGraph.get(mapIndex).setGoal(
							mHoundredsGoal + mDozensGoal + mUnitsGoal
									+ mDecimalsGoal);
					setGoalAnnotation();
					break;
				}
			}
		};
		number.addScrollingListener(scrollListener);
		number2.addScrollingListener(scrollListener);
		number3.addScrollingListener(scrollListener);
		number4.addScrollingListener(scrollListener);

		mGoalPickerDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mGoalPickerDialog.dismiss();
			}
		});
		mTxtTap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGoalPickerDialog.dismiss();
			}
		});
		ArrayWheelAdapter<String> caloryAdapter = new ArrayWheelAdapter<String>(
				this, mWeight.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter2 = new ArrayWheelAdapter<String>(
				this, mWeight2.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter3 = new ArrayWheelAdapter<String>(
				this, mWeight3.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter4 = new ArrayWheelAdapter<String>(
				this, mWeight4.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		caloryAdapter.setItemResource(R.layout.wheel_text_item);
		caloryAdapter.setItemTextResource(R.id.text);
		caloryAdapter2.setItemResource(R.layout.wheel_text_item);
		caloryAdapter2.setItemTextResource(R.id.text);
		caloryAdapter3.setItemResource(R.layout.wheel_text_item);
		caloryAdapter3.setItemTextResource(R.id.text);
		caloryAdapter4.setItemResource(R.layout.wheel_text_item);
		caloryAdapter4.setItemTextResource(R.id.text);
		number.setViewAdapter(caloryAdapter);
		number2.setViewAdapter(caloryAdapter2);
		number3.setViewAdapter(caloryAdapter3);
		number4.setViewAdapter(caloryAdapter4);
		number.setCurrentItem((int) (mHoundredsGoal / 100));
		number2.setCurrentItem((int) (mDozensGoal / 10));
		number3.setCurrentItem((int) mUnitsGoal);
		number4.setCurrentItem((int) (mDecimalsGoal * 10));
		mGoalPickerDialog.show();
	}

	public void setCurrentValuePickerDialog() {
		mWeight.clear();
		mWeight2.clear();
		mWeight3.clear();
		mWeight4.clear();

		for (int i = 0; i <= 1; i++) {
			mWeight.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight2.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight3.add(String.valueOf(i));
		}
		for (int i = 0; i <= 9; i++) {
			mWeight4.add(String.valueOf(i));
		}

		mValuePickerDialog = new Dialog(this, R.style.CustomDialogTheme);
		mValuePickerDialog.setContentView(R.layout.raw_date_picker);
		mValuePickerDialog.setCanceledOnTouchOutside(true);
		Window window = mValuePickerDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		date = (WheelView) mValuePickerDialog.findViewById(R.id.date);
		number = (WheelView) mValuePickerDialog.findViewById(R.id.number);
		number2 = (WheelView) mValuePickerDialog.findViewById(R.id.number2);
		number3 = (WheelView) mValuePickerDialog.findViewById(R.id.number3);
		number4 = (WheelView) mValuePickerDialog.findViewById(R.id.number4);
		mTxtTap = (CustomTextView) mValuePickerDialog
				.findViewById(R.id.tv_txtTap);

		ArrayWheelAdapter<String> dateAdapter = new ArrayWheelAdapter<String>(
				this, x_axis);
		dateAdapter.setItemResource(R.layout.wheel_text_item);
		dateAdapter.setItemTextResource(R.id.text);
		date.setViewAdapter(dateAdapter);

		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		number.addClickingListener(click);
		number2.addClickingListener(click);
		number3.addClickingListener(click);
		number4.addClickingListener(click);
		date.addClickingListener(click);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {

				switch (wheel.getId()) {
				case R.id.date:
					number.setCurrentItem(getIndexOfYValue(mGraph.get(mapIndex)
							.getValues()
							.get(x_axis_original[date.getCurrentItem()])));
					break;
				case R.id.number:
					mHoundreds = Double.parseDouble(mWeight.get(number
							.getCurrentItem())) * 100;
					mGraph.get(mapIndex).setValues(
							x_axis_original[date.getCurrentItem()],
							mHoundreds + mDozens + mUnits + mDecimals);
					setCurrentBodyStatisticsAnnotation();
					break;
				case R.id.number2:
					mDozens = Double.parseDouble(mWeight2.get(number2
							.getCurrentItem())) * 10;
					mGraph.get(mapIndex).setValues(
							x_axis_original[date.getCurrentItem()],
							mHoundreds + mDozens + mUnits + mDecimals);
					setCurrentBodyStatisticsAnnotation();
					break;
				case R.id.number3:
					mUnits = Double.parseDouble(mWeight3.get(number3
							.getCurrentItem()));
					mGraph.get(mapIndex).setValues(
							x_axis_original[date.getCurrentItem()],
							mHoundreds + mDozens + mUnits + mDecimals);
					setCurrentBodyStatisticsAnnotation();

					break;
				case R.id.number4:
					mDecimals = Double.parseDouble(mWeight4.get(number4
							.getCurrentItem())) / 10;
					mGraph.get(mapIndex).setValues(
							x_axis_original[date.getCurrentItem()],
							mHoundreds + mDozens + mUnits + mDecimals);
					setCurrentBodyStatisticsAnnotation();
					break;
				}

			}
		};
		number.addScrollingListener(scrollListener);
		number2.addScrollingListener(scrollListener);
		number3.addScrollingListener(scrollListener);
		number4.addScrollingListener(scrollListener);
		date.addScrollingListener(scrollListener);
		mValuePickerDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mValuePickerDialog.dismiss();
			}
		});
		ArrayWheelAdapter<String> caloryAdapter = new ArrayWheelAdapter<String>(
				this, mWeight.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter2 = new ArrayWheelAdapter<String>(
				this, mWeight2.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter3 = new ArrayWheelAdapter<String>(
				this, mWeight3.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		ArrayWheelAdapter<String> caloryAdapter4 = new ArrayWheelAdapter<String>(
				this, mWeight4.toString().replace("[", "").replace("]", "")
						.replace(" ", "").trim().split(","));
		caloryAdapter.setItemResource(R.layout.wheel_text_item);
		caloryAdapter.setItemTextResource(R.id.text);
		caloryAdapter2.setItemResource(R.layout.wheel_text_item);
		caloryAdapter2.setItemTextResource(R.id.text);
		caloryAdapter3.setItemResource(R.layout.wheel_text_item);
		caloryAdapter3.setItemTextResource(R.id.text);
		caloryAdapter4.setItemResource(R.layout.wheel_text_item);
		caloryAdapter4.setItemTextResource(R.id.text);
		number.setViewAdapter(caloryAdapter);
		number2.setViewAdapter(caloryAdapter2);
		number3.setViewAdapter(caloryAdapter3);
		number4.setViewAdapter(caloryAdapter4);

		date.setCurrentItem(x_axis.length - 1);
		number.setCurrentItem((int) (mHoundreds / 100));
		number2.setCurrentItem((int) (mDozens / 10));
		number3.setCurrentItem((int) mUnits);
		number4.setCurrentItem((int) (mDecimals * 10));

		mTxtTap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mValuePickerDialog.dismiss();
			}
		});
		mValuePickerDialog.show();
	}

	/**
	 * getNumberOfDays method return number of days from starting date of diet
	 * cycle to today
	 * 
	 * @param timeDiff
	 * @return
	 */
	private static int getNumberOfDays(long timeDiff) {
		float days = timeDiff / 86400000f;
		return (int) Math.ceil(days);
	}

	/**
	 * get X AXIS value for graph. getX_axis() method ise also use for find
	 * number of days from start diet cycle to current date. and show on graph.
	 */
	private void getX_axis() {

		long startTime = getIntent().getExtras().getLong(extraStartTime);
		long endTime = getIntent().getExtras().getLong(extraEndTime);
		int numberOfDay = getNumberOfDays(endTime - startTime);

		ArrayList<Long> days = new ArrayList<Long>();
		for (int i = 0; i < numberOfDay; i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(startTime);
			cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
			days.add(cal.getTimeInMillis());
		}
		x_axis = new String[days.size()];
		x_axis_original = new String[days.size()];
		for (int i = 0; i < days.size(); i++) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(days.get(i));
			x_axis_original[i] = Utility.getTimeInddMMyy(c);
			if (Calendar.getInstance().get(Calendar.DATE) == c
					.get(Calendar.DATE)) {
				x_axis[i] = "today";
			} else {
				x_axis[i] = Utility.getTimeInddMM(c);
			}

		}
	}

	/**
	 * getIndexOfYValue method is used for get index of number picker for set
	 * current item in number picker
	 * 
	 * @param yValue
	 * @return
	 */
	private int getIndexOfYValue(double yValue) {

		int index = 0;
		for (int i = 0; i < mWeight.size(); i++) {
			if (!mWeight.get(i).equalsIgnoreCase("none")
					&& Double.parseDouble(mWeight.get(i)) == yValue) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * getMap method is use for convert Map object from string
	 * 
	 * @param x
	 * @return
	 */
	private Map<String, Integer> getMap(String x) {

		Map<String, Integer> xxx = new HashMap<String, Integer>();
		String a[] = x.replace("{", "").replace("}", "").replace(" ", "")
				.split(",");
		for (int i = 0; i < a.length; i++) {
			String b[] = a[i].split("=");
			if (b.length >= 2) {
				xxx.put(b[0], Integer.parseInt(b[1]));
			}
		}
		return xxx;
	}
}
