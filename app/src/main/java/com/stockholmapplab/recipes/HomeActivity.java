package com.stockholmapplab.recipes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.stockholmapplab.recipes.AppRater.AppRater;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.CalendarView;
import com.stockholmapplab.recipes.util.ConnectionUtility;
import com.stockholmapplab.recipes.util.LayoutUtils;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.ScaleView;
import com.stockholmapplab.recipes.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * HomeActivity class is main screen. This class providing following
 * functionality 1) Start/Stop diet cycle 2) My Stats screen 3) About Screen 4)
 * Calorie Menu 5) Setting screen 6) Facebook sharing 7) Consume Today screen
 */

public class HomeActivity extends ActionBarActivity {
	public static Activity activity;
	private CustomTextView mTxtUntilFood, mTxtUsedCal, mTxtTap, mTxtKcalLeft,
			mTxtBadge;
	Button mBtnStart;
	private EditText mEdtTimer;
	static final private int START_DIALOG = 1, APP_LAUNCH_DIALOG = 2,
			JOIN_DIALOG = 3;
	private Dialog mCalendarDialog = null, mDialog1, mDialog2,
			mRemoveDialog = null, mJoinDialog;
	private ScaleView mScaleView;
	private final int REQUEST_CODE_START_DIET_CYCLE = 2;
	private final int REQUEST_CODE_STOP_DIET_CYCLE = 3;
	private CalendarView mCalendarView;
	ProgressDialog progressDialog;
	private Intent mIntent;
	private boolean showDemo = true;
	private ArrayList<Long> mWeekFastingDates = new ArrayList<Long>();

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews = null;

	private List<NameValuePair> mNameValuePairs;
	private ProgressDialog mProgressDialog;
	// mHandler is use for showing progress of join functionality.
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mProgressDialog = new ProgressDialog(HomeActivity.this);
				mProgressDialog.setMessage(getString(R.string.str_wait));
				mProgressDialog.show();
				break;
			case 2:
				mProgressDialog.dismiss();
				mJoinDialog.dismiss();
				Toast.makeText(getApplicationContext(),
						getString(R.string.str_sent_request_successful),
						Toast.LENGTH_LONG).show();
				PrefHelper.setBoolean(
						ResourceUtil.getString(R.string.KEY_JOIN_SUCCESSFULLY),
						false);
				break;
			case -1:
				mProgressDialog.dismiss();
				mJoinDialog.dismiss();
				Toast.makeText(getApplicationContext(),
						getString(R.string.str_sent_request_fail),
						Toast.LENGTH_LONG).show();
				break;
			}
		};
	};
	// mUpdateTimeTask is use for update time on screen
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				mEdtTimer.setText(getTimeForUntilFoodORuntilFasting());
				mHandler.postDelayed(mUpdateTimeTask, 1000);
			} else {
				mTxtUsedCal.setVisibility(View.INVISIBLE);
				mTxtKcalLeft.setVisibility(View.GONE);
				mTxtUntilFood.setVisibility(View.INVISIBLE);
				mEdtTimer.setVisibility(View.INVISIBLE);
				mScaleView.updateView();
				mHandler.removeCallbacks(mUpdateTimeTask);
				mBtnStart.setText(R.string.str_start);
			}
		}
	};
	// mJoinRunnable is use for register user email for join us.
	private Runnable mJoinRunnable = new Runnable() {
		public void run() {
			mHandler.sendEmptyMessage(1);
			synchronized (this) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(BaseConstant.FEEDBACK_URL);
				try {
					// Add your data
					httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					if (Utility.checkResponse(Utility.processEntity(response
							.getEntity()))) {
						mHandler.sendEmptyMessage(2);
					} else {
						mHandler.sendEmptyMessage(-1);
					}
				} catch (ClientProtocolException e) {
					mHandler.sendEmptyMessage(-1);
				} catch (IOException e) {
					mHandler.sendEmptyMessage(-1);
				}
			}

		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.parseColor("#cecba4"));
		}

		BaseApplication.lockOrientation(this);

		try {

			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);

			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("Log",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}

		} catch (NameNotFoundException e) {
			Log.e("name not found", e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		}

		activity = this;
		init();
	}

	@Override
	protected void onStop() {
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	public boolean isTabletLandscape10() {
		return (BaseApplication.getAppContext().getResources()
				.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE & (BaseApplication
				.getAppContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	@SuppressWarnings("deprecation")
	public void init() {
		if (!isTabletLandscape10()) {
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();

			findViewById(R.id.bt_btnRecipesLayout).setPadding(
					width / 2 - Utility.convertDpToPixel(20), 0, 0, 0);
			findViewById(R.id.bt_btnMediaLayout).setPadding(
					width - width / 3 - Utility.convertDpToPixel(20), 0, 0, 0);
			findViewById(R.id.bt_btnMyStartMainLayout).setPadding(
					width / 2 - Utility.convertDpToPixel(20), 0, 0, 0);
			findViewById(R.id.bt_btnCalorieMenuLayout).setPadding(
					width / 3 - Utility.convertDpToPixel(20), 0, 0, 0);
			findViewById(R.id.bt_btnMyStatsMainLayout).setPadding(
					width / 4 - Utility.convertDpToPixel(40), 0, 0, 0);
		}

		CustomTextView tv_build = GenericView.findViewById(this,
				R.id.tv_txtBuild);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			tv_build.setText("v" + pInfo.versionName + " (" + pInfo.versionCode
					+ ")");
		} catch (NameNotFoundException e) {
		}
		mBtnStart = GenericView.findViewById(this, R.id.bt_btnStart);

		mScaleView = GenericView.findViewById(this, R.id.scaleView);

		// Diet Settings.....
		mTxtUsedCal = GenericView.findViewById(this, R.id.tv_txtused_cal);
		mTxtUntilFood = GenericView.findViewById(this, R.id.tv_txtuntil_food);
		mTxtKcalLeft = GenericView.findViewById(this, R.id.tv_txtkcal_left);
		mEdtTimer = GenericView.findViewById(this, R.id.et_edtTimer);

		mTxtBadge = GenericView.findViewById(this, R.id.tv_txt_badge);

		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			mBtnStart.setText(R.string.str_diet_setting);
			mTxtUsedCal.setVisibility(View.VISIBLE);
			mTxtKcalLeft.setVisibility(View.VISIBLE);
			mTxtUntilFood.setVisibility(View.VISIBLE);
			mEdtTimer.setVisibility(View.VISIBLE);
			setTodayFastingDayMessage();
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, 1);
		} else {
			mBtnStart.setText(R.string.str_start);
		}

		mEdtTimer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mViews != null) {
					mViews.hide();
					mViews = null;
				}
				startActivity(new Intent(HomeActivity.this,
						ConsumedTodayActivity.class));
			}
		});

		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_IS_APP_LAUNCH_FIRST_TIME),
				true)) {
			showDialog(JOIN_DIALOG);
		}
		int numberOfTimeDialogLaunch = PrefHelper.getInt(ResourceUtil
				.getString(R.string.KEY_NUMBER_OF_TIME_DIALOG_LAUNCH), 0);
		if (numberOfTimeDialogLaunch >= PrefHelper.getInt(
				ResourceUtil.getString(R.string.KEY_DIALOG_LAUNCH_COUNT), 3)) {
			PrefHelper.setInt(ResourceUtil
					.getString(R.string.KEY_NUMBER_OF_TIME_DIALOG_LAUNCH), 0);
			PrefHelper
					.setInt(ResourceUtil
							.getString(R.string.KEY_DIALOG_LAUNCH_COUNT), 3);

			if (PrefHelper.getBoolean(
					ResourceUtil.getString(R.string.KEY_JOIN_SUCCESSFULLY),
					false)) {
				showDialog(JOIN_DIALOG);
			}
		}

		if (PrefHelper.getBoolean(
				getString(R.string.KEY_IS_APP_LAUNCH_FIRST_TIME), true)) {
			PrefHelper.setBoolean(
					ResourceUtil.getString(R.string.KEY_JOIN_SUCCESSFULLY),
					true);
			showDialog(APP_LAUNCH_DIALOG);
		}
	}

	@SuppressWarnings("deprecation")
	public void onViewClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.bt_btnFacebook:
			findViewById(R.id.bt_btnFacebook).setEnabled(false);
			if (ConnectionUtility.isNetAvailable()) {
				if (!isLoggedIn()) {
					connectToFacebook();
				} else {
					FacebookPost();
				}
			} else {
				findViewById(R.id.bt_btnFacebook).setEnabled(true);
				Toast.makeText(
						HomeActivity.this,
						ResourceUtil
								.getString(R.string.str_internet_connection),
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.bt_btnSetting:
			if (mViews != null) {
				mViews.hide();
				mViews = null;
			}
			intent = new Intent(HomeActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_btnRecipes:
			startActivity(new Intent(HomeActivity.this, RecipesActivity.class));
			break;
		case R.id.bt_btnStart:

			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				intent = new Intent(HomeActivity.this, StopDietActivity.class);
				startActivityForResult(intent, REQUEST_CODE_STOP_DIET_CYCLE);
			} else {
				showDialog(START_DIALOG);
			}
			break;
		case R.id.bt_btnMedia:
			intent = new Intent(HomeActivity.this, MediaActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_btnMyStats:
			if (mViews != null) {
				mViews.hide();
				mViews = null;
			}
			intent = new Intent(HomeActivity.this, MyStatsActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_btnCalorieMenu:
			intent = new Intent(HomeActivity.this, CalorieMenuActivity.class);
			startActivity(intent);
			break;
		case R.id.scaleView:
			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				setAndShowUpCalendarDialog();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * onCreateDialog method is use for showing different dialog.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DIALOG:
			mDialog1 = new Dialog(HomeActivity.this, R.style.DialogTheme);
			mDialog1.setContentView(R.layout.raw_help_dialog);
			mDialog1.setCanceledOnTouchOutside(false);

			CustomTextView mTxtHelpMessage = (CustomTextView) mDialog1
					.findViewById(R.id.tv_txt_help_guide);
			CustomButton mBtnHelpGuide = (CustomButton) mDialog1
					.findViewById(R.id.bt_btn_help_Guide);
			mBtnHelpGuide.setLayoutParams(LayoutUtils
					.getLayoutParamsWithoutMargin(1.25f));
			CustomButton mBtnSkip = (CustomButton) mDialog1
					.findViewById(R.id.bt_btn_Skip);
			mBtnSkip.setLayoutParams(LayoutUtils
					.getLayoutParamsWithMargin(0.75f));

			mTxtHelpMessage.setText(R.string.str_help);
			mBtnHelpGuide.setText(R.string.str_help_guide);
			mBtnSkip.setText(R.string.str_skip);

			mBtnHelpGuide.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog1.dismiss();
					// Utility.hide(mViews);
					if (mViews != null) {
						mViews.hide();
						mViews = null;
					}

					Intent mIntent = new Intent(HomeActivity.this,
							StepByStepHelpGuideActivity.class);
					startActivityForResult(mIntent,
							REQUEST_CODE_START_DIET_CYCLE);

				}
			});
			mBtnSkip.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Utility.hide(mViews);
					if (mViews != null) {
						mViews.hide();
						mViews = null;
					}
					mIntent = new Intent(HomeActivity.this,
							QuickStartActivity.class);
					startActivityForResult(mIntent,
							REQUEST_CODE_START_DIET_CYCLE);
					mDialog1.dismiss();
				}
			});
			return mDialog1;

		case APP_LAUNCH_DIALOG:

			mDialog2 = new Dialog(HomeActivity.this, R.style.DialogTheme);
			mDialog2.setContentView(R.layout.raw_dialog3);
			mDialog2.setCanceledOnTouchOutside(false);
			mDialog2.setCancelable(false);
			CustomTextView mTxtGuide_1 = (CustomTextView) mDialog2
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtGuide_2 = (CustomTextView) mDialog2
					.findViewById(R.id.tv_txt_guide2);

			CustomButton mBtnNo = (CustomButton) mDialog2
					.findViewById(R.id.bt_btn_Cancel);
			CustomButton mBtnYes = (CustomButton) mDialog2
					.findViewById(R.id.bt_btn_ok);

			mTxtGuide_1.setText(R.string.str_dialog_19);
			mTxtGuide_2.setText(R.string.str_dialog_20);
			mBtnNo.setText(R.string.str_no);
			mBtnYes.setText(R.string.str_dialog_21);
			PrefHelper.setBoolean(
					getString(R.string.KEY_IS_APP_LAUNCH_FIRST_TIME), false);
			mBtnNo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog2.dismiss();
				}
			});
			mBtnYes.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog2.dismiss();
					Utility.setALLPreferencesToTrue(HomeActivity.this);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							// Add the views and text
							if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
								showHintDialogs();
							}
						}
					}, 500);
				}
			});

			return mDialog2;

		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_START_DIET_CYCLE:
			if (resultCode == RESULT_OK) {
				if (requestCode == REQUEST_CODE_START_DIET_CYCLE
						&& resultCode == RESULT_OK) {
					mTxtUsedCal.setVisibility(View.VISIBLE);
					mTxtKcalLeft.setVisibility(View.VISIBLE);
					mTxtUntilFood.setVisibility(View.VISIBLE);
					mEdtTimer.setVisibility(View.VISIBLE);
					mScaleView.updateView();
					setTodayFastingDayMessage();
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1);
					mBtnStart.setText(R.string.str_diet_setting);
					Utility.showDietCycleOnDialog(this);
				}
			}
			break;
		case REQUEST_CODE_STOP_DIET_CYCLE:
			if (resultCode == RESULT_OK) {
				if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
					mTxtUsedCal.setVisibility(View.VISIBLE);
					mTxtKcalLeft.setVisibility(View.VISIBLE);
					mTxtUntilFood.setVisibility(View.VISIBLE);
					mEdtTimer.setVisibility(View.VISIBLE);
					mScaleView.updateView();
					setTodayFastingDayMessage();
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1);
					mBtnStart.setText(R.string.str_diet_setting);
					if (mRemoveDialog != null && mRemoveDialog.isShowing()) {
						mRemoveDialog.dismiss();
						Utility.showDietCycleOnDialog(this);
					}

				} else {
					mTxtUsedCal.setVisibility(View.INVISIBLE);
					mTxtKcalLeft.setVisibility(View.GONE);
					mTxtUntilFood.setVisibility(View.INVISIBLE);
					mEdtTimer.setVisibility(View.INVISIBLE);
					mScaleView.updateView();
					mHandler.removeCallbacks(mUpdateTimeTask);
					mBtnStart.setText(R.string.str_start);
				}
			}
			break;
		case FacebookLoginActivity.FACEBOOK_HOME_REQUEST_CODE:
			findViewById(R.id.bt_btnFacebook).setEnabled(true);
			if (requestCode == FacebookLoginActivity.FACEBOOK_HOME_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					FacebookPost();
				}
			}
			break;
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
	 * getFormatDate() return until food or until fasting time from current time
	 * 
	 * @return String
	 */
	private String getTimeForUntilFoodORuntilFasting() {
		String str = "";
		Calendar currentDay = Calendar.getInstance();
		long diffMilli = -1;
		if (mWeekFastingDates.size() == 0) {
			return str;
		}

		for (int i = 0; i < mWeekFastingDates.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(mWeekFastingDates.get(i));
			if (cal.get(Calendar.DATE) == currentDay.get(Calendar.DATE)) {
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				diffMilli = cal.getTimeInMillis()
						- currentDay.getTimeInMillis();
				break;
			}
		}

		if (diffMilli == -1) {
			diffMilli = mWeekFastingDates.get(0) - currentDay.getTimeInMillis();

			if (diffMilli < 0) {
				mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
				Collections.sort(mWeekFastingDates);
				diffMilli = mWeekFastingDates.get(0)
						- currentDay.getTimeInMillis();
			}
		}

		diffMilli /= 1000;
		int seconds = (int) (diffMilli % 60);
		diffMilli /= 60;
		int minutes = (int) (diffMilli % 60);
		diffMilli /= 60;
		int hours = (int) (diffMilli % 24);
		diffMilli /= 24;
		int day = (int) (diffMilli);

		if (day >= 1) {
			str = str + day + "d, ";
		}

		str = str + hours + "h, " + minutes + "m, " + seconds + "s ";

		String key = Utility.getTimeInddMMyy(Calendar.getInstance());
		int cal = 0;
		if (BaseConstant.mDietCycle.getUsedCal().containsKey(key)) {
			cal = BaseConstant.mDietCycle.getUsedCal().get(key);
		}
		if (mTxtUntilFood
				.getText()
				.toString()
				.equalsIgnoreCase(
						getResources().getString(R.string.str_until_food))) {
			mTxtUsedCal.setText(getResources().getString(R.string.use_cal)
					+ " " + cal);
			mTxtKcalLeft
					.setText(getResources().getString(R.string.cal_left)
							+ " "
							+ (BaseConstant.mDietCycle
									.getCalories_consumption() - cal));

			if (cal > BaseConstant.mDietCycle.getCalories_consumption()) {
				mTxtUsedCal.setTextColor(Color.RED);
				mTxtKcalLeft.setTextColor(Color.RED);
			} else {
				mTxtUsedCal.setTextColor(ResourceUtil
						.getColor(R.color.bold_grey_color));
				mTxtKcalLeft.setTextColor(ResourceUtil
						.getColor(R.color.bold_grey_color));
			}
		} else {
			mTxtUsedCal.setText(getResources().getString(R.string.use_cal)
					+ " " + cal);
		}
		return str;

	}

	/**
	 * setTodayFastingDayMessage() method check today is today is fasting and
	 * set until food or until fasting message.
	 * 
	 * @return String
	 */
	private void setTodayFastingDayMessage() {

		mWeekFastingDates.clear();
		mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
		Collections.sort(mWeekFastingDates);
		if (Utility.todayIsFastingDay(mWeekFastingDates)) {
			mTxtUsedCal.setText(getResources().getString(R.string.use_cal)
					+ " " + "0");
			mTxtKcalLeft.setText(getResources().getString(R.string.cal_left)
					+ " " + BaseConstant.mDietCycle.getCalories_consumption());
			mTxtUntilFood.setText(getResources().getString(
					R.string.str_until_food));
		} else {
			mTxtUsedCal.setText(getResources().getString(R.string.use_cal)
					+ " " + "0");
			mTxtUntilFood.setText(getResources().getString(
					R.string.str_until_fasting));
			mTxtKcalLeft.setVisibility(View.GONE);
		}
	}

	/**
	 * Show calendar show and user can set fasting days from custom calendar.
	 */
	private void setAndShowUpCalendarDialog() {

		if (mCalendarDialog == null) {
			mCalendarDialog = new Dialog(HomeActivity.this,
					R.style.CustomDialogTheme);

			mCalendarDialog.setContentView(R.layout.raw_calendar_pick);
			mCalendarDialog.setCanceledOnTouchOutside(true);

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = mCalendarDialog.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			lp.copyFrom(window.getAttributes());
			// This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			window.setAttributes(lp);

			mCalendarView = (CalendarView) mCalendarDialog
					.findViewById(R.id.calendarView);
			mTxtTap = (CustomTextView) mCalendarDialog
					.findViewById(R.id.tv_txtTap);
			RelativeLayout mainRelativeLayout = (RelativeLayout) mCalendarDialog
					.findViewById(R.id.rl_main);
			mCalendarDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

					if (mCalendarView.isAnyChange()) {
						mTxtUsedCal.setVisibility(View.VISIBLE);
						mTxtKcalLeft.setVisibility(View.VISIBLE);
						mTxtUntilFood.setVisibility(View.VISIBLE);
						mEdtTimer.setVisibility(View.VISIBLE);
						mScaleView.updateView();
						setTodayFastingDayMessage();
						mHandler.removeCallbacks(mUpdateTimeTask);
						mHandler.postDelayed(mUpdateTimeTask, 1);
						mBtnStart.setText(R.string.str_diet_setting);
					}

				}
			});
			mainRelativeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCalendarView.checkCalendarValidation(mCalendarDialog);
				}
			});
		} else {
			mCalendarView.update();
		}

		switch (BaseConstant.mDietCycle.getFasting_intensity()) {
		case 2:
			mTxtTap.setText(ResourceUtil.getString(R.string.str_5_2));
			break;
		case 3:
			mTxtTap.setText(ResourceUtil.getString(R.string.str_4_3));
			break;
		case 1:
			mTxtTap.setText(ResourceUtil.getString(R.string.str_6_1));
			break;
		}

		mCalendarDialog.show();
	}

	/**
	 * Function to make login to facebook
	 */
	private void connectToFacebook() {
		Intent intent = new Intent(HomeActivity.this,
				FacebookLoginActivity.class);
		startActivityForResult(intent,
				FacebookLoginActivity.FACEBOOK_HOME_REQUEST_CODE);
		overridePendingTransition(0, 0);
	}

	/**
	 * Function to check user is LoggedIn or not
	 * 
	 * @return boolean true/false
	 */
	public static boolean isLoggedIn() {
			return true;
	}

	/**
	 * FaceBook Post Function
	 */

	public void FacebookPost() {

		/*byte[] data = null;
		Bundle params = new Bundle();

		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_facebook_icon);

		if (bitmap != null) {
			progressDialog = new ProgressDialog(HomeActivity.this);
			progressDialog.setMessage(getString(R.string.str_wait));
			progressDialog.setCancelable(false);
			progressDialog.show();
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			data = stream.toByteArray();

			params.putString(
					"message",
					getString(R.string.str_facebook_share_message_1) + "\n"
							+ getString(R.string.str_facebook_share_message_2)
							+ "\n\n\n"
							+ getString(R.string.str_facebook_share_message_3)
							+ "\t"
							+ AppRater.getPlayStoreURL(HomeActivity.this));
			params.putByteArray("picture", data);
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {

					if (progressDialog != null && progressDialog.isShowing())
						progressDialog.dismiss();
					findViewById(R.id.bt_btnFacebook).setEnabled(true);
					if (response.toString().contains("200")) {

						Toast.makeText(HomeActivity.this,
								"Shared Successfully", Toast.LENGTH_LONG)
								.show();
					} else {

						Toast.makeText(HomeActivity.this, "Shared Fail",
								Toast.LENGTH_LONG).show();
					}
				}
			};
			Request request = new Request(Session.getActiveSession(),
					"me/photos", params, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		} else {
			findViewById(R.id.bt_btnFacebook).setEnabled(true);
		}*/
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		if (Utility.isDietCycleFinish()) {
			stopDietCycle();
		} else {
			Utility.showFastingAndNonFastingDays(HomeActivity.this);
			Utility.checkLastLaunchApp();
			AppRater.checkAppRateCondition(this);
		}

		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()
				&& PrefHelper.getBoolean(
						getResources().getString(R.string.KEY_BADGE_SHOW_FLAG),
						true)) {
			// mTxtBadge.setVisibility(View.VISIBLE);
		} else {
			// mTxtBadge.setVisibility(View.INVISIBLE);
		}
		Utility.checkAchievemtnDialog(this);
		// show hint dialog after 500 milliseconds.
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showHintDialogs();
			}
		}, 500);

		RecipesFragment.searchText = "";
		RecipesFragment.isFavorite = false;
		RecipesActivity.mSubcategory = -1;
		RecipesActivity.mCategory = -1;
		RecipesActivity.mCalorie = null;
		RecipesActivity.filter = "";
		PrefHelper.setInt("CategoryPos", 0);
		PrefHelper.setInt("SubCategoryPos", 0);
		PrefHelper.setInt("CaloriePos", 0);
		super.onResume();
	}

	/**
	 * stopDietCycle() metohd is use for stop diet cycle base on condition.
	 */
	private void stopDietCycle() {

		mTxtUsedCal.setVisibility(View.INVISIBLE);
		mTxtKcalLeft.setVisibility(View.INVISIBLE);
		mTxtUntilFood.setVisibility(View.INVISIBLE);
		mEdtTimer.setVisibility(View.INVISIBLE);
		mScaleView.updateView();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mBtnStart.setText(R.string.str_start);
		dietCycleStopDialog();
	}

	/**
	 * dietCycleStopDialog() show stop diet cycle dialog.
	 */
	private void dietCycleStopDialog() {
		if (mRemoveDialog == null) {
			mRemoveDialog = new Dialog(HomeActivity.this, R.style.DialogTheme);
			mRemoveDialog.setContentView(R.layout.raw_dialog5);
			mRemoveDialog.setCancelable(true);

			CustomTextView mTxtDialogTitle = (CustomTextView) mRemoveDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mtxtDialogSubTitle = (CustomTextView) mRemoveDialog
					.findViewById(R.id.tv_txt_guide2);

			CustomButton mBtnDialogOk = (CustomButton) mRemoveDialog
					.findViewById(R.id.bt_btn_ok);

			mTxtDialogTitle.setText(R.string.str_diet_cycle_finish_title);
			mtxtDialogSubTitle
					.setText(getString(R.string.str_diet_cycle_finish_sub_title));

			mBtnDialogOk.setText(R.string.str_dialog_2);
			mBtnDialogOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mRemoveDialog.dismiss();

				}
			});
		}
		mRemoveDialog.show();
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {

		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.HOME_ACTIVITY_ID);

		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(getApplicationContext().getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
			if (mViews == null) {
				mOptions.block = false;
				mOptions.hideOnClickOutside = false;
				mViews = new ShowcaseViews(this,
						R.layout.showcase_view_template,
						new ShowcaseViews.OnShowcaseAcknowledged() {
							@Override
							public void onShowCaseAcknowledged(
									ShowcaseView showcaseView) {
								mViews = null;
							}
						});
				if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
					if (PrefHelper.getBoolean(
							getString(R.string.KEY_IS_START_ACTIVE_START),
							false)) {
						mViews.addView(new ShowcaseViews.ItemViewProperties(
								R.id.bt_btnStartLayout,
								R.string.str_start_message_1, 0,
								SHOWCASE_KITTEN_SCALE,
								BaseConstant.HOME_ACTIVITY_ID));
					}
				} else {
					if (PrefHelper.getBoolean(
							getString(R.string.KEY_IS_START_ACTIVE_DIET_CYCLE),
							false)) {
						mViews.addView(new ShowcaseViews.ItemViewProperties(
								R.id.et_edtTimer,
								R.string.str_start_message_17, 0,
								SHOWCASE_KITTEN_SCALE,
								BaseConstant.HOME_ACTIVITY_ID));
					}
					if (PrefHelper.getBoolean(
							getString(R.string.KEY_IS_START_ACTIVE_MYSTATS),
							false)) {
						mViews.addView(new ShowcaseViews.ItemViewProperties(
								R.id.bt_btnMyStatsLayout,
								R.string.str_start_message_3, 1,
								SHOWCASE_KITTEN_SCALE,
								BaseConstant.HOME_ACTIVITY_ID));
					}
				}
				mViews.show();
			}
		}
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(HomeActivity.this);
		}
	};

	/*
	 * Join request send
	 */

	private void joinRequest(String emailID) {
		mNameValuePairs = new ArrayList<NameValuePair>(2);
		mNameValuePairs.add(new BasicNameValuePair("Title",
				"Registration request"));
		mNameValuePairs.add(new BasicNameValuePair("Content",
				"Registration request"));
		mNameValuePairs.add(new BasicNameValuePair("Email", emailID));
		mNameValuePairs.add(new BasicNameValuePair("AppId",
				"com.sweekend.HealthDiet"));
		mNameValuePairs.add(new BasicNameValuePair("TypeId", "6"));
		mNameValuePairs.add(new BasicNameValuePair("UDID", Utility
				.getDeviceUDID()));
		mNameValuePairs.add(new BasicNameValuePair("UserInfo", Utility
				.getUserInfo()));

		Thread mythread = new Thread(mJoinRunnable);
		mythread.start();
	}

	/**
	 * showValidationDialog() method show validation dialog
	 */
	private void showValidationDialog() {
		final Dialog mDialog = new Dialog(HomeActivity.this,
				R.style.DialogTheme);
		mDialog.setContentView(R.layout.raw_dialog2);
		mDialog.setCanceledOnTouchOutside(false);
		CustomTextView mTxtValidate = (CustomTextView) mDialog
				.findViewById(R.id.tv_txtCalendarValidation);

		CustomButton mBtnOK = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_ok);
		mBtnOK.setText(R.string.str_dialog_2);
		mTxtValidate.setText(R.string.str_dialog_14);
		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
}
