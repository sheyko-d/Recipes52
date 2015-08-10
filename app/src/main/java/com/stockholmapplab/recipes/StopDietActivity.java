package com.stockholmapplab.recipes;

import java.util.Calendar;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.Days;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.CalendarView;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * StopDietActivity used to stop current diet cycle. In this class mainly three
 * functionality: 1) Stop diet cycle. 2) select calory item from calory menu. 3)
 * change fasting day.
 */
public class StopDietActivity extends ActionBarActivity {
	private CustomTextView mTxtFasting, mTxtTap, mTxtDialogTitle,
			mtxtDialogSubTitle;
	private CustomButton mBtnStopDiet, mBtnCalorieMenu, mBtnDialogCancel,
			mBtnDialogOk;
	private final int REQUEST_CODE_START_DIET_CYCLE = 2;
	private Dialog mCalendarDialog, mStopDietCycleDialog;
	private CalendarView mCalendarView;
	private boolean changeFlag = false;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_stop_diet);
		initActionBar();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_help_guide));
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

	public void back(View v) {
		if (changeFlag) {
			setResult(RESULT_OK);
			finish();
		} else {
			finish();
		}
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

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {

		mBtnStopDiet = GenericView.findViewById(this, R.id.bt_btn_stop_diet);
		mBtnCalorieMenu = GenericView.findViewById(this,
				R.id.bt_btn_calorie_menu);
		mTxtFasting = GenericView.findViewById(this, R.id.tv_txt_fasting);
		mBtnStopDiet.setText(R.string.str_stop_diet);
		mBtnCalorieMenu.setText(R.string.str_calorie_menu);

		mTxtFasting.setText(Days.getStringFormatDays());

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showHintDialogs();
			}
		}, 500);
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.bt_btn_select_calendar:
			showCalendarDialg();
			break;
		case R.id.bt_btn_stop_diet:
			showStopDietCycleDialog();
			break;
		case R.id.bt_btn_calorie_menu:
			Intent mIntent = new Intent(StopDietActivity.this,
					CalorieMenuActivity.class);
			startActivity(mIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * show calendar dialog.
	 */
	private void showCalendarDialg() {
		if (mCalendarDialog == null) {
			mCalendarDialog = new Dialog(StopDietActivity.this,
					R.style.CustomDialogTheme);
			mCalendarDialog.setContentView(R.layout.raw_calendar_pick);
			mCalendarDialog.setCanceledOnTouchOutside(true);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = mCalendarDialog.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			lp.copyFrom(window.getAttributes());
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
					changeFlag = true;
					mTxtFasting.setText(Days.getStringFormatDays());
				}
			});
			mainRelativeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCalendarView.checkCalendarValidation(mCalendarDialog);
				}
			});
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
	 * show stop diet cycle dialog
	 */
	private void showStopDietCycleDialog() {
		if (mStopDietCycleDialog == null) {
			mStopDietCycleDialog = new Dialog(StopDietActivity.this,
					R.style.DialogTheme);
			mStopDietCycleDialog.setContentView(R.layout.raw_dialog3);
			mStopDietCycleDialog.setCancelable(true);

			mTxtDialogTitle = (CustomTextView) mStopDietCycleDialog
					.findViewById(R.id.tv_txt_guide1);
			mtxtDialogSubTitle = (CustomTextView) mStopDietCycleDialog
					.findViewById(R.id.tv_txt_guide2);
			mBtnDialogCancel = (CustomButton) mStopDietCycleDialog
					.findViewById(R.id.bt_btn_Cancel);
			mBtnDialogOk = (CustomButton) mStopDietCycleDialog
					.findViewById(R.id.bt_btn_ok);
			mTxtDialogTitle.setText(R.string.str_stop_diet_cycle);
			mtxtDialogSubTitle
					.setText(getString(R.string.str_stop_diet_cycle_message));
			mBtnDialogCancel.setText(R.string.str_dialog_5);
			mBtnDialogOk.setText(R.string.str_dialog_2);
			mBtnDialogOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BaseConstant.mDietCycle.setLast_launch(-1);
					BaseConstant.mDietCycle.setEndTime(Calendar.getInstance()
							.getTimeInMillis());
					BaseConstant.mDietCycle.setOne_notification_time(0);
					BaseConstant.mDietCycle.setDiet_cycle_is_on(false);
					setResult(RESULT_OK);
					DietCycle.setDietCycleHistory();
					Utility.removeAlarm();
					BaseConstant.mDietCycle.getUsedCal().clear();
					BaseConstant.mDietCycle.getConsume_food_item().clear();
					finish();
					Intent mIntent = new Intent(StopDietActivity.this,
							StepByStepHelpGuideActivity.class);
					startActivityForResult(mIntent,
							REQUEST_CODE_START_DIET_CYCLE);
				}
			});
			mBtnDialogCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mStopDietCycleDialog.dismiss();
				}
			});
		}
		mStopDietCycleDialog.show();
	}

	@Override
	public void onBackPressed() {
		if (changeFlag) {
			setResult(RESULT_OK);
			finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(StopDietActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE:
			if (requestCode == FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					Utility.FacebookPostOnAchievements(
							StopDietActivity.this,
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
				BaseConstant.STOP_DIET_ACTIVITY_ID);
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
			if (PrefHelper.getBoolean(getString(R.string.KEY_IS_STOP_DIET),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_stop_diet, R.string.str_start_message_18, 0,
						SHOWCASE_KITTEN_SCALE,
						BaseConstant.STOP_DIET_ACTIVITY_ID));
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
			Utility.leftCaloryDialog(StopDietActivity.this);
		}
	};
}