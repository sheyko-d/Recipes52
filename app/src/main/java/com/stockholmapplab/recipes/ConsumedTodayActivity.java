package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.stockholmapplab.recipes.AppRater.AppRater;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.pojo.FoodItem;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomEditText;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.CalendarView;
import com.stockholmapplab.recipes.util.LayoutUtils;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * ConsumedTodayActivity class is use for maintain consume food items during
 * fasting and non fasting days.
 */
public class ConsumedTodayActivity extends ActionBarActivity {
	private int imageNumber = 1;
	private CustomTextView mTxtUntilFood, mTxtInfo, mTxtCalory, mTxtTap;
	private CustomButton mBtnDietTime, mBtnDietFoodTime, mBtnReminder, mBtnOk,
			mBtnCancel;
	public static final int REQUEST_CODE_CALORIEMENUACTIVITY = 3;
	private Dialog mCalendarDialog, mCalorieDialog, mValidationDialog,
			mRemoveFoodItemDialog, mNoLeftCaloriesDialog;
	private CustomTextView mValidationTextForValidationDialog,
			mRemoveFoodItemNameTextView;
	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	private CalendarView mCalendarView;
	private boolean showDemo = true, removeFlag = false;
	private CustomEditText mEdtTitle, mEdtSubTitle;
	private LinearLayout mConsumeFoodItemLinearLayout;
	private ArrayList<Long> mWeekFastingDates = new ArrayList<Long>();
	private Set<String> keySet;
	private String key;
	private int cal, counter = 0;
	private Handler mHandler = new Handler();

	public static boolean mActivityInBackgroundFlag = false;
	// mUpdateTimeTask is use for update time on screen
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			mBtnDietFoodTime.setText(getTimeForUntilFoodORuntilFasting());
			if (BaseConstant.mDietCycle.getOne_notification_time() >= 1
					&& mBtnReminder.getVisibility() == View.VISIBLE) {
				mBtnReminder.setText(oneHourNotificationTimeRemaining());
			}
			mHandler.postDelayed(mUpdateTimeTask, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_consumed_today);
		initActionBar();

		init();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, 1);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_consumed_today));
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

		mBtnDietTime = GenericView.findViewById(this, R.id.bt_btnDietTime);
		mConsumeFoodItemLinearLayout = GenericView.findViewById(this,
				R.id.lv_foodItemLinearLayout);
		mTxtCalory = GenericView.findViewById(this, R.id.tv_txt_cal);
		mBtnDietFoodTime = GenericView.findViewById(this,
				R.id.bt_btn_Diet_Food_Time);
		mTxtUntilFood = GenericView.findViewById(this, R.id.tv_txt_until_food);
		mBtnReminder = GenericView.findViewById(this, R.id.bt_btn_Reminder);
		mTxtInfo = GenericView.findViewById(this, R.id.tv_txt_Info);

		mBtnDietTime.setText(getTimeLeft());
		mBtnDietFoodTime.setText(getTimeForUntilFoodORuntilFasting());
		mTxtUntilFood.setText(setTodayFastingDayMessage());

		if (todayIsFastingDay()) {
			mBtnReminder.setVisibility(View.VISIBLE);
			String msg = getString(R.string.str_use)
					+ "<img src=\"ic_launcher.png\"/>"
					+ getString(R.string.str_add_item);
			mTxtInfo.setText(Html.fromHtml(msg, imgGetter, null));
			if (BaseConstant.mDietCycle.getOne_notification_time() == 0) {
				mBtnReminder.setText(ResourceUtil
						.getString(R.string.str_remind_1_hour));
			}
		} else {
			mBtnReminder.setVisibility(View.INVISIBLE);
			mTxtInfo.setText(R.string.str_info);
		}
		setConsumeFoodItemLayout();
		// show hint dialog after 500 milliseconds.
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showHintDialogs();
			}
		}, 500);
	}

	@Override
	protected void onResume() {
		mActivityInBackgroundFlag = true;
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(ConsumedTodayActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		mActivityInBackgroundFlag = false;
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.bt_btn_Reminder:
			if (BaseConstant.mDietCycle.getOne_notification_time() == 0) {
				Utility.set1HourAlarm(getApplicationContext());
				BaseConstant.mDietCycle.setOne_notification_time(Calendar
						.getInstance().getTimeInMillis());
				finish();
			}
			break;
		case R.id.iv_imgCalendar:

			showCalendarDialog();
			break;
		case R.id.rl_relativeConsumedToday_3:
			showAddCalorieDialog();
			break;
		case R.id.tv_txt_free_value:
			showAddCalorieDialog();
			break;
		case R.id.tv_txt_from_menu:
			overridePendingTransition(0, 0);
			startActivityForResult(new Intent(ConsumedTodayActivity.this,
					CalorieMenuActivity.class),
					REQUEST_CODE_CALORIEMENUACTIVITY);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CALORIEMENUACTIVITY
				&& resultCode == RESULT_OK) {
			setConsumeFoodItemLayout();
		} else if (requestCode == FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			Utility.FacebookPostOnAchievements(this, data.getExtras()
					.getString(FacebookLoginActivity.extraMessage), data
					.getExtras().getInt(FacebookLoginActivity.extraDrawableID));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * showCalendarDialog() method is use for show custom calendar dialog
	 */
	private void showCalendarDialog() {
		if (mCalendarDialog == null) {
			mCalendarDialog = new Dialog(ConsumedTodayActivity.this,
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
	 * showAddCalorieDialog() method is use for show add calories dialog.
	 */
	private void showAddCalorieDialog() {
		if (mCalorieDialog == null) {
			mCalorieDialog = new Dialog(ConsumedTodayActivity.this,
					R.style.DialogTheme);
			mCalorieDialog.setContentView(R.layout.raw_dialog4);

			mCalorieDialog.setCanceledOnTouchOutside(false);
			CustomTextView mTxtAddCalories = (CustomTextView) mCalorieDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtCaloryInfo = (CustomTextView) mCalorieDialog
					.findViewById(R.id.tv_txt_guide2);
			CustomButton mBtnOk = (CustomButton) mCalorieDialog
					.findViewById(R.id.bt_btn_ok);
			CustomButton mBtnCancel = (CustomButton) mCalorieDialog
					.findViewById(R.id.bt_btn_Cancel);
			mEdtTitle = (CustomEditText) mCalorieDialog
					.findViewById(R.id.et_edtTitle);
			mEdtSubTitle = (CustomEditText) mCalorieDialog
					.findViewById(R.id.et_edtSubTitle);

			mTxtAddCalories.setText(R.string.str_dialog_8);
			mTxtCaloryInfo.setText(R.string.str_dialog_9);
			mBtnOk.setText(R.string.str_dialog_12);
			mBtnCancel.setText(R.string.str_dialog_5);
			mBtnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mEdtTitle.getText().toString().equals("")) {
						showValidationDialog(true);
					} else if (mEdtSubTitle.getText().toString().equals("")) {
						showValidationDialog(false);
					} else {
						try {
							mCalorieDialog.dismiss();
							keySet = BaseConstant.mDietCycle.getUsedCal()
									.keySet();
							key = Utility.getTimeInddMMyy(Calendar
									.getInstance());
							if (keySet.contains(key)) {
								cal = BaseConstant.mDietCycle.getUsedCal().get(
										key);
								cal = cal
										+ Integer.parseInt(mEdtSubTitle
												.getText().toString());
								if (todayIsFastingDay()) {
									if (BaseConstant.mDietCycle
											.getCalories_consumption() < cal) {
										showNoLeftCaloriesDialog();
									} else {
										BaseConstant.mDietCycle
												.setConsume_food_item(new FoodItem(
														mEdtTitle.getText()
																.toString(),
														Integer.parseInt(mEdtSubTitle
																.getText()
																.toString()),
														Utility.getTimeInddMMyy(Calendar
																.getInstance())));
										BaseConstant.mDietCycle.getUsedCal()
												.put(key, cal);
										BaseConstant.mDietCycle
												.setmConsumeFoodPoints(BaseConstant.mDietCycle
														.getmConsumeFoodPoints() + 1);
										setConsumeFoodItemLayout();
									}
								} else {
									BaseConstant.mDietCycle
											.setConsume_food_item(new FoodItem(
													mEdtTitle.getText()
															.toString(),
													Integer.parseInt(mEdtSubTitle
															.getText()
															.toString()),
													Utility.getTimeInddMMyy(Calendar
															.getInstance())));
									BaseConstant.mDietCycle.getUsedCal().put(
											key, cal);
									setConsumeFoodItemLayout();
								}
							} else {
								cal = Integer.parseInt(mEdtSubTitle.getText()
										.toString());

								if (todayIsFastingDay()) {
									if (BaseConstant.mDietCycle
											.getCalories_consumption() < cal) {
										showNoLeftCaloriesDialog();
									} else {
										BaseConstant.mDietCycle
												.setConsume_food_item(new FoodItem(
														mEdtTitle.getText()
																.toString(),
														Integer.parseInt(mEdtSubTitle
																.getText()
																.toString()),
														Utility.getTimeInddMMyy(Calendar
																.getInstance())));
										BaseConstant.mDietCycle.getUsedCal()
												.put(key, cal);
										BaseConstant.mDietCycle
												.setmConsumeFoodPoints(BaseConstant.mDietCycle
														.getmConsumeFoodPoints() + 1);
										setConsumeFoodItemLayout();
									}
								} else {
									BaseConstant.mDietCycle
											.setConsume_food_item(new FoodItem(
													mEdtTitle.getText()
															.toString(),
													Integer.parseInt(mEdtSubTitle
															.getText()
															.toString()),
													Utility.getTimeInddMMyy(Calendar
															.getInstance())));
									BaseConstant.mDietCycle.getUsedCal().put(
											key, cal);
									setConsumeFoodItemLayout();
								}
							}
						} catch (NumberFormatException e) {
						}
					}
				}
			});
			mBtnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCalorieDialog.dismiss();

				}
			});
		}
		mEdtTitle.setText("");
		mEdtSubTitle.setText("");
		mCalorieDialog.show();
	}

	/**
	 * showNoLeftCaloriesDialog() method is use for show alert dialog for no
	 * more calories left.
	 */
	private void showNoLeftCaloriesDialog() {
		if (mNoLeftCaloriesDialog == null) {
			mNoLeftCaloriesDialog = new Dialog(ConsumedTodayActivity.this,
					R.style.DialogTheme);
			mNoLeftCaloriesDialog.setContentView(R.layout.raw_dialog3);
			mNoLeftCaloriesDialog.setCanceledOnTouchOutside(false);
			CustomTextView mTxtRemoveItem = (CustomTextView) mNoLeftCaloriesDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtRemove = (CustomTextView) mNoLeftCaloriesDialog
					.findViewById(R.id.tv_txt_guide2);
			mBtnOk = (CustomButton) mNoLeftCaloriesDialog
					.findViewById(R.id.bt_btn_ok);
			mBtnCancel = (CustomButton) mNoLeftCaloriesDialog
					.findViewById(R.id.bt_btn_Cancel);

			mTxtRemoveItem.setText(R.string.str_no_more_calories_left);
			mTxtRemove.setText(R.string.str_miss_goal);
			mBtnOk.setText(R.string.str_dialog_21);
			mBtnCancel.setText(R.string.str_no);

			mBtnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mNoLeftCaloriesDialog.dismiss();

				}
			});
			mBtnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BaseConstant.mDietCycle.setConsume_food_item(new FoodItem(
							mEdtTitle.getText().toString(),
							Integer.parseInt(mEdtSubTitle.getText().toString()),
							Utility.getTimeInddMMyy(Calendar.getInstance())));
					BaseConstant.mDietCycle.getUsedCal().put(key, cal);
					if (todayIsFastingDay()) {
						BaseConstant.mDietCycle
								.setmConsumeFoodPoints(BaseConstant.mDietCycle
										.getmConsumeFoodPoints() - 1);

					}
					setConsumeFoodItemLayout();
					mNoLeftCaloriesDialog.dismiss();
				}
			});
		}
		mNoLeftCaloriesDialog.show();
	}

	/**
	 * showValidationDialog method is use for show validation dialog.
	 * 
	 * @param flag
	 */
	private void showValidationDialog(boolean flag) {
		if (mValidationDialog == null) {
			mValidationDialog = new Dialog(ConsumedTodayActivity.this,
					R.style.DialogTheme);
			mValidationDialog.setContentView(R.layout.raw_dialog2);
			mValidationDialog.setCanceledOnTouchOutside(false);
			mValidationTextForValidationDialog = (CustomTextView) mValidationDialog
					.findViewById(R.id.tv_txtCalendarValidation);

			CustomButton mBtnOK = (CustomButton) mValidationDialog
					.findViewById(R.id.bt_btn_ok);

			mBtnOK.setText(R.string.str_dialog_2);

			mBtnOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mValidationDialog.dismiss();
				}
			});

		}
		if (flag) {
			mValidationTextForValidationDialog.setText(R.string.str_dialog_10);
		} else {
			mValidationTextForValidationDialog.setText(R.string.str_dialog_11);
		}
		mValidationDialog.show();
	}

	/**
	 * setConsumeFoodItemLayout() method is use for add or remove consume food
	 * item from today menu and display on screen.
	 */
	private void setConsumeFoodItemLayout() {
		mConsumeFoodItemLinearLayout.removeAllViews();
		String todayDate = Utility.getTimeInddMMyy(Calendar.getInstance());
		boolean flag = false;
		for (int i = 0; i < BaseConstant.mDietCycle.getConsume_food_item()
				.size(); i++) {
			if (BaseConstant.mDietCycle.getConsume_food_item().get(i).getDate()
					.equalsIgnoreCase(todayDate)) {
				addConsumeFoodItem(BaseConstant.mDietCycle
						.getConsume_food_item().get(i), i);
				flag = true;
			}
		}

		if (flag) {
			mTxtInfo.setVisibility(View.GONE);
			mConsumeFoodItemLinearLayout.setVisibility(View.VISIBLE);
			if (!removeFlag) {
				AppRater.incrementNumberOfConsumeFoodItems();
				counter++;

				Log.i("TAG", "COUNTER:" + counter);
				if (counter == 5) {
					counter = 0;
					AppRater.checkAppRateCondition(this);
				}
			}
		} else {
			mTxtInfo.setVisibility(View.VISIBLE);
			mConsumeFoodItemLinearLayout.setVisibility(View.GONE);
		}
		String key = Utility.getTimeInddMMyy(Calendar.getInstance());
		int cal = 0;
		if (BaseConstant.mDietCycle.getUsedCal().keySet().contains(key)) {
			cal = BaseConstant.mDietCycle.getUsedCal().get(key);
		}
		if (todayIsFastingDay()) {
			mTxtCalory
					.setText(getString(R.string.str_used_cal)
							+ " "
							+ cal
							+ "\t\t"
							+ getString(R.string.str_cal_left)
							+ " "
							+ (BaseConstant.mDietCycle
									.getCalories_consumption() - cal));
			if (cal > BaseConstant.mDietCycle.getCalories_consumption()) {
				mTxtCalory.setTextColor(Color.RED);
			} else {
				mTxtCalory.setTextColor(ResourceUtil
						.getColor(R.color.bold_grey_color));
			}
		} else {
			mTxtCalory.setText(getString(R.string.str_used_cal) + " " + cal);
		}
		removeFlag = false;
	}

	/**
	 * addConsumeFoodItem method is use for add consume food item and diaplay on
	 * screen.
	 * 
	 * @param foodItem
	 * @param itemCount
	 */
	private void addConsumeFoodItem(final FoodItem foodItem, int itemCount) {
		final View child = getLayoutInflater().inflate(
				R.layout.raw_calory_items, null);
		CustomTextView name = (CustomTextView) child
				.findViewById(R.id.tv_txtCaloryItem);
		CustomTextView cal = (CustomTextView) child
				.findViewById(R.id.tv_txtCalories);
		ImageView remove = (ImageView) child.findViewById(R.id.iv_imgCheck);
		RelativeLayout mMainRelative = (RelativeLayout) child
				.findViewById(R.id.rl_mainRelative);

		name.setText(foodItem.getName());
		cal.setText("" + foodItem.getCal() + " kcal");
		if ((itemCount % 2) != 0 && itemCount != 0) {
			mMainRelative
					.setBackgroundResource(R.drawable.ic_green_transparent_patch);
		}
		remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showRemoveDialog(foodItem);
			}
		});
		mConsumeFoodItemLinearLayout.addView(child);

	}

	/**
	 * showRemoveDialog method is use for show remove consume food item from
	 * menu.
	 * 
	 * @param item
	 */
	private void showRemoveDialog(final FoodItem item) {
		mRemoveFoodItemDialog = new Dialog(ConsumedTodayActivity.this,
				R.style.DialogTheme);
		mRemoveFoodItemDialog.setContentView(R.layout.raw_dialog3);
		mRemoveFoodItemDialog.setCanceledOnTouchOutside(false);
		CustomTextView mTxtRemove = (CustomTextView) mRemoveFoodItemDialog
				.findViewById(R.id.tv_txt_guide1);
		mRemoveFoodItemNameTextView = (CustomTextView) mRemoveFoodItemDialog
				.findViewById(R.id.tv_txt_guide2);

		CustomButton mBtnOK = (CustomButton) mRemoveFoodItemDialog
				.findViewById(R.id.bt_btn_ok);
		CustomButton mBtnCancel = (CustomButton) mRemoveFoodItemDialog
				.findViewById(R.id.bt_btn_Cancel);
		mTxtRemove.setText(R.string.str_dialog_17);
		mBtnOK.setText(R.string.str_dialog_2);
		mBtnCancel.setText(R.string.str_dialog_5);

		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Set<String> keySet = BaseConstant.mDietCycle.getUsedCal()
						.keySet();
				String key = Utility.getTimeInddMMyy(Calendar.getInstance());
				if (keySet.contains(key)) {
					int cal = BaseConstant.mDietCycle.getUsedCal().get(key);
					cal = cal - item.getCal();
					BaseConstant.mDietCycle.getUsedCal().put(key, cal);
				}
				removeFlag = BaseConstant.mDietCycle.getConsume_food_item()
						.remove(item);
				setConsumeFoodItemLayout();
				mRemoveFoodItemDialog.dismiss();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRemoveFoodItemDialog.dismiss();
			}
		});
		// }
		mRemoveFoodItemNameTextView.setText(getResources().getString(
				R.string.str_remove_1)
				+ " "
				+ item.getName()
				+ " "
				+ getResources().getString(R.string.str_remove_2));
		mRemoveFoodItemDialog.show();
	}

	/**
	 * getTimeLeft() method is use for calculate how much time left for finish
	 * diet cycle? and return in string format.
	 * 
	 * @return String
	 */
	private String getTimeLeft() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {

			if (BaseConstant.mDietCycle.getDiet_duration() <= 5) {
				int totalDays = getWeek(BaseConstant.mDietCycle
						.getDiet_duration()) * 7;
				Calendar currentDate = Calendar.getInstance();
				Calendar startDate = Calendar.getInstance();
				startDate.setTimeInMillis(BaseConstant.mDietCycle
						.getStartTime());
				long timeGone = currentDate.getTimeInMillis()
						- startDate.getTimeInMillis();

				Calendar lastDate = Calendar.getInstance();
				lastDate.set(Calendar.DATE, lastDate.get(Calendar.DATE)
						+ totalDays);

				lastDate.set(Calendar.HOUR_OF_DAY, 00);
				lastDate.set(Calendar.MINUTE, 00);
				lastDate.set(Calendar.SECOND, 01);

				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 00);
				c.set(Calendar.MINUTE, 00);
				c.set(Calendar.SECOND, 01);

				long totalTime = lastDate.getTimeInMillis()
						- c.getTimeInMillis() - timeGone;
				long copydiffMilli = totalTime;
				totalTime /= 1000;
				totalTime /= 60;
				int minutes = (int) (totalTime % 60);
				totalTime /= 60;
				int hours = (int) (totalTime % 24);
				totalTime /= 24;
				int day = (int) (totalTime);
				String str = "";
				if (copydiffMilli >= 86400000) {
					// day format
					str = day + "d, " + hours + "h, " + minutes + "m";
				} else if (copydiffMilli >= 3600000 && copydiffMilli < 86400000) {
					// hour format
					str = hours + "h, " + minutes + "m";
				} else if (copydiffMilli >= 60000 && copydiffMilli < 3600000) {
					// minute format
					str = minutes + "m";
				} else {
					// <1 min
					str = "< 1m";
				}
				return str;

			} else {
				return "forever";
			}
		} else {
			return "none";
		}
	}

	/**
	 * getWeek method return number of week for diet cycle duration.
	 * 
	 * @param number
	 * @return
	 */
	private int getWeek(int number) {
		int week = 1;
		switch (number) {
		case 0:
			week = 1;
			break;
		case 1:
			week = 2;
			break;
		case 2:
			week = 5;
			break;
		case 3:
			week = 8;
			break;
		case 4:
			week = 10;
			break;
		case 5:
			week = 20;
			break;
		}
		return week;
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
			mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
			Collections.sort(mWeekFastingDates);
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

		return str;

	}

	/**
	 * oneHourNotificationTimeRemaining() method return remaining time for 1
	 * hour remainder.
	 * 
	 * @return String
	 */
	private String oneHourNotificationTimeRemaining() {
		long diffMilli = (BaseConstant.mDietCycle.getOne_notification_time() + (1000 * 60 * 60))
				- Calendar.getInstance().getTimeInMillis();
		String str = "";
		if (diffMilli >= 0) {

			diffMilli /= 1000;
			int seconds = (int) (diffMilli % 60);
			diffMilli /= 60;
			int minutes = (int) (diffMilli % 60);
			diffMilli /= 60;
			str = minutes + "m, " + seconds + "s ";
		} else {
			str = ResourceUtil.getString(R.string.str_remind_1_hour);
			BaseConstant.mDietCycle.setOne_notification_time(0);
		}

		return str;
	}

	/**
	 * setTodayFastingDayMessage() method check today is today is fasting and
	 * return until food or until fasting message.
	 * 
	 * @return String
	 */
	private String setTodayFastingDayMessage() {
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		boolean flag = false;
		for (int i = 0; i < mWeekFastingDates.size(); i++) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(mWeekFastingDates.get(i));
			if (c.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
				flag = true;
				break;
			}
		}
		if (flag) {
			return ResourceUtil.getString(R.string.str_until_food);
		} else {
			return ResourceUtil.getString(R.string.str_until_fasting);
		}
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {

		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.CONSUMED_TODAY_ACTIVITY_ID);
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
						getString(R.string.KEY_IS_CONSUMED_TODAY_1), false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.rl_relative, R.string.str_start_message_14, 0,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.CONSUMED_TODAY_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_CONSUMED_TODAY_2), false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.rl_relativeConsumedToday_3,
							R.string.str_start_message_15, 1,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.CONSUMED_TODAY_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_CONSUMED_TODAY_3), false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.iv_imgCalendar, R.string.str_start_message_16,
							2, SHOWCASE_KITTEN_SCALE,
							BaseConstant.CONSUMED_TODAY_ACTIVITY_ID));
				}
				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_CONSUMED_TODAY_4), false)) {
					if (mBtnReminder.getVisibility() == View.VISIBLE) {
						mViews.addView(new ShowcaseViews.ItemViewProperties(
								R.id.ll_reminder,
								R.string.str_start_message_19, 3,
								SHOWCASE_KITTEN_SCALE,
								BaseConstant.CONSUMED_TODAY_ACTIVITY_ID));
					}
				}

				mViews.show();
			}
		}
		if (PrefHelper.getBoolean(getString(R.string.KEY_IS_CONSUMED_TODAY_5),
				true)) {
			showHelloDialog();
		}
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(ConsumedTodayActivity.this);
		}
	};

	private ImageGetter imgGetter = new ImageGetter() {
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			if (imageNumber == 1) {
				drawable = getResources().getDrawable(R.drawable.ic_pluse_btn);
				++imageNumber;
			} else
				drawable = getResources().getDrawable(R.drawable.ic_pluse_btn);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			return drawable;
		}
	};

	/**
	 * todayIsFastingDay() method is use for check today is fasting day or not
	 * 
	 * @return boolean
	 */
	private boolean todayIsFastingDay() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			if (mWeekFastingDates.size() == 0) {
				mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
				Collections.sort(mWeekFastingDates);
			}
			return Utility.todayIsFastingDay(mWeekFastingDates);
		}
		return false;

	}

	/**
	 * showHelloDialog() method is use for show hello dialog.
	 */
	private void showHelloDialog() {
		PrefHelper.setBoolean(
				getResources().getString(R.string.KEY_IS_CONSUMED_TODAY_5),
				false);
		final Dialog mDialog = new Dialog(ConsumedTodayActivity.this,
				R.style.DialogTheme);
		mDialog.setContentView(R.layout.raw_dialog3);

		mDialog.setCanceledOnTouchOutside(false);
		CustomTextView mTxtHello = (CustomTextView) mDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtDietInfo = (CustomTextView) mDialog
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnOk = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_ok);
		CustomButton mBtnCancel = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_Cancel);

		mTxtHello.setText(R.string.str_dialog_24);
		mTxtDietInfo.setText(R.string.str_dialog_25);
		mBtnOk.setText(R.string.str_setting);
		mBtnCancel.setText(R.string.str_dialog_5);

		mBtnCancel.setLayoutParams(LayoutUtils
				.getLayoutParamsWithoutMargin(0.75f));
		mBtnOk.setLayoutParams(LayoutUtils.getLayoutParamsWithMargin(1.25f));

		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
				Intent mIntent = new Intent(ConsumedTodayActivity.this,
						SettingsActivity.class);
				startActivity(mIntent);
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

}
