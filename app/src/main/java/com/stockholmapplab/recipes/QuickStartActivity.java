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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.Days;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.MyTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.CalendarView;
import com.stockholmapplab.recipes.util.LayoutUtils;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.wheel.widget.adapters.ArrayWheelAdapter;
import com.stockholmapplab.recipes.widget.OnWheelClickedListener;
import com.stockholmapplab.recipes.widget.OnWheelScrollListener;
import com.stockholmapplab.recipes.widget.WheelView;
import com.stockholmapplab.recipes.R;

/**
 * QuickStartActivity is use to start Diet Session Quickly. In this class mainly
 * five functionality: 1) Select Duration 2) Select Fasting Intensity 3) Select
 * Calories/day. 4) Select Menu. 5) Select fasting Days.
 */
public class QuickStartActivity extends ActionBarActivity {
	private CustomTextView mTxtWeeks, mTxtTap;
	private MyTextView mWeekDays;
	private ImageView mImg52, mImg43, mImg61;
	private CustomButton mBtnCalory;
	private Dialog mNumberPickerDialog, mCalendarDialog, mValidationDialog;//
	private WheelView number;
	private CalendarView mCalendarView;
	private SeekBar mSeekbar;
	private final String calories[] = { "0", "100", "200", "300", "400", "500",
			"600", "700", "800", "900", "1000", "1100", "1200", "1300", "1400",
			"1500", "1600", "1700", "1800", "1900", "2000", "2100", "2200",
			"2300", "2400", "2500" };
	private String[] diet_duration;
	private boolean mDialog3Flag;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_quick_start);
		initActionBar();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_quick_start));
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
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {
		diet_duration = ResourceUtil.getStringArray(R.array.diet_duration);
		mTxtWeeks = GenericView.findViewById(this, R.id.tv_txtWeeks);
		mWeekDays = GenericView.findViewById(this, R.id.tv_txtweekday);
		mWeekDays.setText(Days.getStringFormatDays());
		mImg52 = GenericView.findViewById(this, R.id.iv_img52);
		mImg43 = GenericView.findViewById(this, R.id.iv_img43);
		mImg61 = GenericView.findViewById(this, R.id.iv_img61);

		switch (BaseConstant.mDietCycle.getFasting_intensity()) {
		case BaseConstant.FIVE_TWO_INTENSITY:
			mImg52.setImageResource(R.drawable.ic_selected_tik);
			break;
		case BaseConstant.FOUR_THREE_INTENSITY:
			mImg43.setImageResource(R.drawable.ic_selected_tik);
			break;
		case BaseConstant.SIX_ONE_INTENSITY:
			mImg61.setImageResource(R.drawable.ic_selected_tik);
			break;
		}

		mBtnCalory = GenericView.findViewById(this, R.id.bt_btnCalorie);
		mBtnCalory.setText(""
				+ BaseConstant.mDietCycle.getCalories_consumption());
		mSeekbar = GenericView.findViewById(this, R.id.sk_seekBar);
		mTxtWeeks.setText(diet_duration[BaseConstant.mDietCycle
				.getDiet_duration()]);
		mSeekbar.setProgress(BaseConstant.mDietCycle.getDiet_duration() * 15);
		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				BaseConstant.mDietCycle.setDiet_duration((int) (mSeekbar
						.getProgress() / 15));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int index = (int) (progress / 15);
				mTxtWeeks.setText(diet_duration[index]);
			}
		});
		// show hint dialog after 500 milliseconds
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Add the views and text
				showHintDialogs();
			}
		}, 500);
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
	protected void onStop() {
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.bt_btnStart:
			checkValidation();
			break;
		case R.id.bt_btnCalorieMenu:
			Intent mIntent = new Intent(QuickStartActivity.this,
					CalorieMenuActivity.class);
			startActivity(mIntent);
			break;
		case R.id.bt_btnCalorie:
			showNumberPickerDialog();
			break;
		case R.id.bt_btn_select_calendar:
			showCalendarDialog();
			break;
		case R.id.ll_linear11:
			mImg52.setImageResource(R.drawable.ic_selected_tik);
			mImg43.setImageResource(R.drawable.ic_unselected_tik);
			mImg61.setImageResource(R.drawable.ic_unselected_tik);
			BaseConstant.mDietCycle
					.setFasting_intensity(BaseConstant.FIVE_TWO_INTENSITY);
			BaseConstant.mDietCycle.getSelected_fasting_days().clear();
			if (mCalendarView != null)
				mCalendarView.invalidate();
			mWeekDays.setText("");
			break;
		case R.id.ll_linear12:
			mImg52.setImageResource(R.drawable.ic_unselected_tik);
			mImg43.setImageResource(R.drawable.ic_selected_tik);
			mImg61.setImageResource(R.drawable.ic_unselected_tik);
			BaseConstant.mDietCycle
					.setFasting_intensity(BaseConstant.FOUR_THREE_INTENSITY);
			BaseConstant.mDietCycle.getSelected_fasting_days().clear();
			if (mCalendarView != null)
				mCalendarView.invalidate();
			mWeekDays.setText("");
			break;
		case R.id.ll_linear13:
			mImg52.setImageResource(R.drawable.ic_unselected_tik);
			mImg43.setImageResource(R.drawable.ic_unselected_tik);
			mImg61.setImageResource(R.drawable.ic_selected_tik);
			BaseConstant.mDietCycle
					.setFasting_intensity(BaseConstant.SIX_ONE_INTENSITY);
			BaseConstant.mDietCycle.getSelected_fasting_days().clear();
			if (mCalendarView != null)
				mCalendarView.invalidate();
			mWeekDays.setText("");
			break;
		default:
			break;
		}
	}

	/**
	 * Show NumberPicker Dialog to select number from 0 to 2500.
	 */
	private void showNumberPickerDialog() {
		if (mNumberPickerDialog == null) {
			mNumberPickerDialog = new Dialog(QuickStartActivity.this,
					R.style.CustomDialogTheme);
			mNumberPickerDialog.setContentView(R.layout.raw_number_picker);
			mNumberPickerDialog.setCanceledOnTouchOutside(true);
			Window window = mNumberPickerDialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();
			wlp.gravity = Gravity.BOTTOM;
			wlp.width = LayoutParams.MATCH_PARENT;
			window.setAttributes(wlp);
			number = (WheelView) mNumberPickerDialog.findViewById(R.id.number);
			ArrayWheelAdapter<String> caloryAdapter = new ArrayWheelAdapter<String>(
					QuickStartActivity.this, calories);
			caloryAdapter.setItemResource(R.layout.wheel_text_item);
			caloryAdapter.setItemTextResource(R.id.text);
			number.setViewAdapter(caloryAdapter);
			OnWheelClickedListener click = new OnWheelClickedListener() {
				public void onItemClicked(WheelView wheel, int itemIndex) {
					wheel.setCurrentItem(itemIndex, true);
				}
			};
			number.addClickingListener(click);
			OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
				public void onScrollingStarted(WheelView wheel) {
					mBtnCalory.setText(calories[number.getCurrentItem()]);
				}

				public void onScrollingFinished(WheelView wheel) {
					mBtnCalory.setText(calories[number.getCurrentItem()]);
					BaseConstant.mDietCycle.setCalories_consumption(Integer
							.parseInt(mBtnCalory.getText().toString()));
				}
			};
			number.addScrollingListener(scrollListener);
			mNumberPickerDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
				}
			});
		}
		number.setCurrentItem(getIndex(mBtnCalory.getText().toString()));
		mNumberPickerDialog.show();
	}

	/**
	 * Show Calendar Dialog to select fasting days based on Fasting Intensity.
	 */
	private void showCalendarDialog() {
		if (mCalendarDialog == null) {
			mCalendarDialog = new Dialog(QuickStartActivity.this,
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
					mWeekDays.setText(Days.getStringFormatDays());
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
	 * Show Validation Dialog If any of the field left Blank.
	 * 
	 * @param msg
	 *            message will change runtime based on the blank field.
	 */
	private void showValidationDialog(String msg) {
		if (mValidationDialog == null) {
			mValidationDialog = new Dialog(QuickStartActivity.this,
					R.style.DialogTheme);
			mValidationDialog.setContentView(R.layout.raw_help_dialog);
			mValidationDialog.setCanceledOnTouchOutside(false);
			CustomButton mBtnNo = (CustomButton) mValidationDialog
					.findViewById(R.id.bt_btn_help_Guide);
			CustomButton mBtnView = (CustomButton) mValidationDialog
					.findViewById(R.id.bt_btn_Skip);
			mBtnNo.setText(R.string.str_no);
			mBtnView.setText(R.string.str_view);
			mBtnNo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mValidationDialog.dismiss();
				}
			});
			mBtnView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mValidationDialog.dismiss();
					if (mDialog3Flag) {
						showNumberPickerDialog();
					} else {
						showCalendarDialog();
					}
				}
			});
		}
		CustomTextView mTxtTitle_2 = (CustomTextView) mValidationDialog
				.findViewById(R.id.tv_txt_help_guide);
		mTxtTitle_2.setText(msg);
		mValidationDialog.show();
	}

	/**
	 * get index of calory value.
	 * 
	 * @param value
	 * @return - index
	 */
	private int getIndex(String value) {
		int index = -1;
		for (int i = 0; i < calories.length; i++) {
			if (calories[i].equalsIgnoreCase(value)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private void checkValidation() {
		if (checkCalendarValidation()) {
			mDialog3Flag = false;
			showValidationDialog(getString(R.string.str_fasting_intensity));
		} else {
			if (mBtnCalory.getText().toString().equalsIgnoreCase("0")
					|| mBtnCalory.getText().toString().equalsIgnoreCase("")) {
				mDialog3Flag = true;
				showValidationDialog(getString(R.string.str_calories_validation));
			} else {
				int currentDayOfWeek = Calendar.getInstance().get(
						Calendar.DAY_OF_WEEK);
				int mFastingDay = -1;
				for (int i = 0; i < BaseConstant.mDietCycle
						.getSelected_fasting_days().size(); i++) {
					int fastingDay = BaseConstant.mDietCycle
							.getSelected_fasting_days().get(i);
					int convertCalendarDay = Days.reverseMapDay(fastingDay);
					if (currentDayOfWeek == convertCalendarDay) {
						mFastingDay = fastingDay;
						break;
					}
				}
				if (mFastingDay != -1) {
					showCurrentDayValidation();
				} else {
					// save and return to previous activity
					BaseConstant.mDietCycle.setLast_launch(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setLaunch_history(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
					BaseConstant.mDietCycle.setNow(false);
					setResult(RESULT_OK);
					BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
							.getTimeInMillis());
					Utility.setAlarm();
					DietCycle.setDietCycle();
					finish();
				}
			}
		}
	}

	/**
	 * Check fasting days matches selected fasting Intensity.
	 * 
	 * @return - true - match - false - did not match.
	 */
	private boolean checkCalendarValidation() {
		if (BaseConstant.mDietCycle.getSelected_fasting_days().size() == BaseConstant.mDietCycle
				.getFasting_intensity())
			return false;
		else
			return true;
	}

	/**
	 * If checked current day as a fasting day. Dialog will arise to tell user
	 * If he/she wants to start Fasting from current day.
	 */
	private void showCurrentDayValidation() {
		final Dialog mDialog = new Dialog(QuickStartActivity.this,
				R.style.DialogTheme);
		mDialog.setContentView(R.layout.raw_dialog3);
		mDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtTitle_1 = (CustomTextView) mDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtSubTitle_1 = (CustomTextView) mDialog
				.findViewById(R.id.tv_txt_guide2);

		CustomButton mBtnNextDay = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_Cancel);
		mBtnNextDay.setLayoutParams(LayoutUtils
				.getLayoutParamsWithoutMargin(1.25f));
		CustomButton mBtnNow = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_ok);
		mBtnNow.setLayoutParams(LayoutUtils.getLayoutParamsWithMargin(0.75f));

		mTxtTitle_1.setText(R.string.str_start_fasting_now);
		mTxtSubTitle_1.setText(getString(R.string.str_fasting_message));

		mBtnNextDay.setText(R.string.str_next_day);
		mBtnNow.setText(R.string.str_now);
		mBtnNextDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				BaseConstant.mDietCycle.setLast_launch(Calendar.getInstance()
						.getTimeInMillis());
				BaseConstant.mDietCycle.setLaunch_history(Calendar
						.getInstance().getTimeInMillis());
				BaseConstant.mDietCycle.setNow(false);
				BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
				setResult(RESULT_OK);
				BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
						.getTimeInMillis());
				Utility.setAlarm();
				DietCycle.setDietCycle();
				finish();
			}
		});
		mBtnNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				BaseConstant.mDietCycle.setLast_launch(Calendar.getInstance()
						.getTimeInMillis());
				BaseConstant.mDietCycle.setLaunch_history(Calendar
						.getInstance().getTimeInMillis());
				BaseConstant.mDietCycle.setNow(true);
				BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
				setResult(RESULT_OK);
				BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
						.getTimeInMillis());
				Utility.setAlarm();
				DietCycle.setDietCycle();
				finish();
			}
		});
		mDialog.show();
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(QuickStartActivity.this);
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
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_QUICK_START_ACTIVE_PROGRESS),
						false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.sk_seekBar, R.string.str_start_message_4, 0,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.QUICK_START_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_QUICK_START_ACTIVE_RADIO),
						false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.ll_linear1, R.string.str_start_message_5, 1,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.QUICK_START_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY),
						false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.ll_quick_start_1,
							R.string.str_start_message_6, 2,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.QUICK_START_ACTIVITY_ID));
				}
				if (PrefHelper
						.getBoolean(
								getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY_MENU),
								false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.ll_quick_start_2,
							R.string.str_start_message_7, 3,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.QUICK_START_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_QUICK_START_ACTIVE_START),
						false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.ll_quick_start_3,
							R.string.str_start_message_8, 4,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.QUICK_START_ACTIVITY_ID));
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
			Utility.leftCaloryDialog(QuickStartActivity.this);
		}
	};
}