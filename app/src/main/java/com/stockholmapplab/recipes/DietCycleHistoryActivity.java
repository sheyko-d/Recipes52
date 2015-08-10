package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.DietHistoryAdapter;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * DietCycleHistoryActivity class is use for show diet cycle history.
 */
public class DietCycleHistoryActivity extends ActionBarActivity {

	private ExpandableListView mLstDietHistory;
	private DietHistoryAdapter mDietHistoryAdapter;
	private ArrayList<Object> mUsedCalHistory = new ArrayList<Object>();
	private ArrayList<DietCycle> mDietCycleHistory = null;

	// Hint Dialog related params
	private boolean showDemo = true;
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	public static boolean mTouchFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_diet_cycle_history);
		initActionBar();

		mTouchFlag = false;
		// get diet cycle history
		mDietCycleHistory = DietCycle.getDietCycleHistory();
		// if diet cycle on than also current diet cycle add in history
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			mDietCycleHistory.add(BaseConstant.mDietCycle);
		}
		getUsedCal();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_diet_cycle_history_2));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
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
	 * getUsedCal() method is use for get use calories during diet cycle from
	 * history for display purpose.
	 */
	private void getUsedCal() {
		for (int i = 0; i < mDietCycleHistory.size(); i++) {
			long startTime = mDietCycleHistory.get(i).getStartTime();
			long endTime;
			if (mDietCycleHistory.get(i).isDiet_cycle_is_on()) {
				Calendar endTimeCalendar = Calendar.getInstance();
				endTimeCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endTimeCalendar.set(Calendar.MINUTE, 59);
				endTimeCalendar.set(Calendar.SECOND, 59);
				endTime = endTimeCalendar.getTimeInMillis();
			}

			else
				endTime = mDietCycleHistory.get(i).getEndTime();
			int days = getNumberOfDays(endTime - startTime);
			Set<String> keySet = mDietCycleHistory.get(i).getUsedCal().keySet();

			ArrayList<String> mUsedCal = new ArrayList<String>();
			for (int j = 0; j < days; j++) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(startTime);
				cal.set(Calendar.DATE, cal.get(Calendar.DATE) + j);
				String key = Utility.getTimeInddMMyy(cal);
				if (keySet.contains(key)) {
					mUsedCal.add(key + "@"
							+ mDietCycleHistory.get(i).getUsedCal().get(key));
				} else {
					mUsedCal.add(key + "@0");
				}
			}
			mUsedCalHistory.add(mUsedCal);
		}
	}

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {
		mLstDietHistory = GenericView.findViewById(this, R.id.el_dietHistory);

		if (mDietCycleHistory.size() >= 1) {
			mDietHistoryAdapter = new DietHistoryAdapter(mDietCycleHistory,
					mUsedCalHistory);
			mDietHistoryAdapter
					.setInflater(
							(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),
							this);
			mLstDietHistory.setAdapter(mDietHistoryAdapter);
			mLstDietHistory.setScrollContainer(false);
			mLstDietHistory.setOnTouchListener(touchListener);
		} else {
			CustomTextView noHistoryFoundTextView = GenericView.findViewById(
					this, R.id.tv_no_history_found);
			noHistoryFoundTextView.setVisibility(View.VISIBLE);
			mLstDietHistory.setVisibility(View.GONE);
		}
		// show hint dialog after 500 milliseconds.
		if (mDietCycleHistory.size() >= 1) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					showHintDialogs();
				}
			}, 500);
		}
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	private View.OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mTouchFlag;
		}
	};

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(DietCycleHistoryActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		} else {
			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		}

		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onStop() {
		DietHistoryAdapter.one_raw_height = 0;
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	/**
	 * getNumberOfDays is use for find number of days base on time in
	 * milliseconds.
	 * 
	 * @param timeDiff
	 * @return
	 */
	private int getNumberOfDays(long timeDiff) {
		float days = timeDiff / 86400000f;
		return (int) Math.ceil(days);
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

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID);
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
							mTouchFlag = false;
						}
					});

			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_1), false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.el_dietHistory, R.string.str_start_message_12, 0,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID));
			}
			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_2), false)) {
				mTouchFlag = true;
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.el_dietHistory, R.string.str_start_message_13, 1,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID));
			}
			mViews.show();
		}
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(DietCycleHistoryActivity.this);
		}
	};

	public void back(View v) {
		finish();
	}
}
