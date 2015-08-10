package com.stockholmapplab.recipes.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.stockholmapplab.recipes.AchievementsActivity;
import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.ConsumedTodayActivity;
import com.stockholmapplab.recipes.DietCycleHistoryActivity;
import com.stockholmapplab.recipes.FacebookLoginActivity;
import com.stockholmapplab.recipes.NotificationReceiver;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.AppRater.AppRater;
import com.stockholmapplab.recipes.pojo.Days;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;

/**
 * Utility class provide general utility methods.
 */
public class Utility {

	private static final SimpleDateFormat format1 = new SimpleDateFormat(
			"dd.MM.yy", Locale.US);
	private static final SimpleDateFormat format2 = new SimpleDateFormat(
			"dd.MM", Locale.US);
	private static final int MORNING_ALARM_REQUEST_CODE = 111;
	private static final int EVENING_ALARM_REQUEST_CODE = 222;
	private static final int HOUR_NOTIFICATION_CODE = 333;
	private static ProgressDialog progressDialog;

	/**
	 * getFormatDate method return date in HH:mm format.
	 * 
	 * @param format
	 * @param cal
	 * @return String
	 */
	public static String getFormatDate(SimpleDateFormat format, Calendar cal) {
		format = new SimpleDateFormat("HH:mm", Locale.US);
		return format.format(cal.getTime());
	}

	/**
	 * set1HourAlarm function is use for set alarm for 1 hour
	 * 
	 * @param context
	 */
	public static void set1HourAlarm(Context context) {
		AlarmManager morningAlarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent morningIntent = new Intent(context, NotificationReceiver.class);
		morningIntent.putExtra(NotificationReceiver.extraAlarmCode,
				NotificationReceiver.HOUR_NOTIFICATION_CODE);
		PendingIntent morningPendingIntent = PendingIntent.getBroadcast(
				context, HOUR_NOTIFICATION_CODE, morningIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		morningAlarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance()
				.getTimeInMillis() + (1000 * 60 * 60), morningPendingIntent);

	}

	/**
	 * remove1HourAlarm method is use for remove 1 hour alarm.
	 * 
	 * @param context
	 */
	public static void remove1HourAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				HOUR_NOTIFICATION_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * setAlarm() function is use for set morning and evening remainder.
	 */
	public static void setAlarm() {

		if (BaseConstant.mDietCycle != null
				&& BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			if (PrefHelper.getBoolean(BaseApplication.getAppContext()
					.getString(R.string.KEY_IS_REMINDER_ON), true)) {
				String moringTime = PrefHelper.getString(
						BaseApplication.getAppContext().getString(
								R.string.KEY_MORNING_REMINDER), "08:00:0");
				String eveningTime = PrefHelper.getString(
						BaseApplication.getAppContext().getString(
								R.string.KEY_EVENING_REMINDER), "22:00:1");

				Calendar eveningTimeCalendar = null;
				Calendar morningTimeCalendar = null;
				if (!moringTime.equalsIgnoreCase("")
						&& !eveningTime.equalsIgnoreCase("")) {
					int moringHour = Integer.parseInt(moringTime.split(":")[0]);
					int moringMinute = Integer
							.parseInt(moringTime.split(":")[1]);
					int eveningHour = Integer
							.parseInt(eveningTime.split(":")[0]);
					int eveningMinute = Integer
							.parseInt(eveningTime.split(":")[1]);

					morningTimeCalendar = Calendar.getInstance();
					morningTimeCalendar.set(Calendar.HOUR_OF_DAY, moringHour);
					morningTimeCalendar.set(Calendar.MINUTE, moringMinute);
					eveningTimeCalendar = Calendar.getInstance();
					eveningTimeCalendar.set(Calendar.HOUR_OF_DAY, eveningHour);
					eveningTimeCalendar.set(Calendar.MINUTE, eveningMinute);

				}

				if (eveningTimeCalendar != null && morningTimeCalendar != null) {
					long moringTimeLong = 0;
					long eveningTimeLong = 0;
					if (morningTimeCalendar.after(Calendar.getInstance()
							.getTimeInMillis())) {
						moringTimeLong = morningTimeCalendar.getTimeInMillis();
					} else {
						moringTimeLong = morningTimeCalendar.getTimeInMillis() + 86400000L;
					}

					if (eveningTimeCalendar.after(Calendar.getInstance()
							.getTimeInMillis())) {
						eveningTimeLong = eveningTimeCalendar.getTimeInMillis();
					} else {
						eveningTimeLong = eveningTimeCalendar.getTimeInMillis() + 86400000L;
					}

					AlarmManager morningAlarmManager = (AlarmManager) BaseApplication
							.getAppContext().getSystemService(
									Context.ALARM_SERVICE);
					Intent morningIntent = new Intent(
							BaseApplication.getAppContext(),
							NotificationReceiver.class);
					morningIntent.putExtra(NotificationReceiver.extraAlarmCode,
							NotificationReceiver.MORNING_CODE);
					PendingIntent morningPendingIntent = PendingIntent
							.getBroadcast(BaseApplication.getAppContext(),
									MORNING_ALARM_REQUEST_CODE, morningIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					morningAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							moringTimeLong, 86400000, morningPendingIntent);

					AlarmManager eveningAlarmManager = (AlarmManager) BaseApplication
							.getAppContext().getSystemService(
									Context.ALARM_SERVICE);
					Intent eveningIntent = new Intent(
							BaseApplication.getAppContext(),
							NotificationReceiver.class);
					eveningIntent.putExtra(NotificationReceiver.extraAlarmCode,
							NotificationReceiver.EVENING_CODE);
					PendingIntent eveningPendingIntent = PendingIntent
							.getBroadcast(BaseApplication.getAppContext(),
									EVENING_ALARM_REQUEST_CODE, eveningIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					eveningAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							eveningTimeLong, 86400000, eveningPendingIntent);
				}
			}
		}
	}

	/**
	 * removeAlarm() function is use for remove morning and evening remainder.
	 */
	public static void removeAlarm() {
		AlarmManager alarmManager = (AlarmManager) BaseApplication
				.getAppContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(BaseApplication.getAppContext(),
				NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				BaseApplication.getAppContext(), MORNING_ALARM_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);

		AlarmManager alarmManager1 = (AlarmManager) BaseApplication
				.getAppContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent1 = new Intent(BaseApplication.getAppContext(),
				NotificationReceiver.class);
		PendingIntent pendingIntent1 = PendingIntent.getBroadcast(
				BaseApplication.getAppContext(), EVENING_ALARM_REQUEST_CODE,
				intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager1.cancel(pendingIntent1);
	}

	/**
	 * change method is use for convert 12 hour format date to 24 hour date
	 * format.
	 * 
	 * @param hour
	 * @param min
	 * @param ampm
	 * @return String
	 */
	public static String change(int hour, int min, int ampm) {
		if (ampm == 1) {
			if (hour < 12) {
				hour = hour + 12;
			}
		} else {
			if (hour == 12) {
				hour = 0;
			}
		}
		String str = "";
		if (hour < 10)
			str = str + "0" + hour + ":";
		else
			str = str + hour + ":";
		if (min < 10)
			str = str + "0" + min;
		else
			str = str + min;
		return str;
	}

	/**
	 * getTimeInddMMyy method is use for get date in ddMMyy format.
	 * 
	 * @param calendar
	 * @return String
	 */
	public static String getTimeInddMMyy(Calendar calendar) {
		return format1.format(calendar.getTime());
	}

	/**
	 * getTimeInddMM method is use for get date in ddMM format.
	 * 
	 * @param calendar
	 * @return String
	 */
	public static String getTimeInddMM(Calendar calendar) {
		return format2.format(calendar.getTime());
	}

	/**
	 * showFastingAndNonFastingDays method is show dialog for today is fasting
	 * day or non fasting day
	 * 
	 * @param activity
	 */
	public static void showFastingAndNonFastingDays(final Activity activity) {
		if (BaseConstant.mDietCycle.getLast_launch() >= 1) {

			Calendar currentDate = Calendar.getInstance();
			Calendar lastLaunchDate = Calendar.getInstance();
			lastLaunchDate.setTimeInMillis(BaseConstant.mDietCycle
					.getLast_launch());

			if (!getTimeInddMMyy(currentDate).equalsIgnoreCase(
					getTimeInddMMyy(lastLaunchDate))) {
				final Dialog mDialog2 = new Dialog(activity,
						R.style.DialogTheme);
				mDialog2.setContentView(R.layout.raw_dialog3);
				mDialog2.setCancelable(true);

				CustomTextView mTxtDialogTitle = (CustomTextView) mDialog2
						.findViewById(R.id.tv_txt_guide1);
				CustomTextView mtxtDialogSubTitle = (CustomTextView) mDialog2
						.findViewById(R.id.tv_txt_guide2);

				CustomButton mBtnDialogCancel = (CustomButton) mDialog2
						.findViewById(R.id.bt_btn_Cancel);
				CustomButton mBtnDialogOk = (CustomButton) mDialog2
						.findViewById(R.id.bt_btn_ok);

				switch (BaseConstant.mDietCycle.getFasting_intensity()) {
				case 1:
					mTxtDialogTitle.setText(ResourceUtil
							.getString(R.string.str_non_fasting_day_title_3));
					break;
				case 2:
					mTxtDialogTitle.setText(ResourceUtil
							.getString(R.string.str_non_fasting_day_title_1));
					break;
				case 3:
					mTxtDialogTitle.setText(ResourceUtil
							.getString(R.string.str_non_fasting_day_title_2));
					break;
				}
				int convertDay = Days.mapDay(currentDate
						.get(Calendar.DAY_OF_WEEK));
				if (BaseConstant.mDietCycle.getSelected_fasting_days()
						.contains(convertDay)) {
					// fasting day

					mtxtDialogSubTitle.setText(activity
							.getString(R.string.str_fasting_day_sub_title));
				} else {
					// non fasting day
					mtxtDialogSubTitle.setText(activity
							.getString(R.string.str_non_fasting_day_sub_title));
				}

				mBtnDialogCancel.setText(R.string.str_menu_dialog);
				mBtnDialogOk.setText(R.string.str_dialog_2);
				mBtnDialogOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog2.dismiss();

					}
				});
				mBtnDialogCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog2.dismiss();
						activity.startActivity(new Intent(activity,
								ConsumedTodayActivity.class));
					}
				});
				mDialog2.show();
			}
		}
	}

	/**
	 * getHowManyTimeAppContinouslyOpen method check how many time application
	 * open continuously for last seven days. and return result in percentage.
	 * 
	 * @return int
	 */
	public static int getHowManyTimeAppContinouslyOpen() {
		if (!PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_7_DAY_COMPLETED), false)) {
			int counter = 0;
			if (BaseConstant.mDietCycle.getLaunch_history().size() >= 7) {
				int startIndex = BaseConstant.mDietCycle.getLaunch_history()
						.size() - 7;
				counter = 7;
				for (int i = startIndex; i < (startIndex + 6); i++) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(BaseConstant.mDietCycle
							.getLaunch_history().get(i));

					Calendar cal1 = Calendar.getInstance();
					cal1.setTimeInMillis(BaseConstant.mDietCycle
							.getLaunch_history().get(i + 1));

					int dayDiff = cal1.get(Calendar.DAY_OF_YEAR)
							- cal.get(Calendar.DAY_OF_YEAR);
					if (!(dayDiff == -1 || dayDiff == 1 || dayDiff == -365
							|| dayDiff == 365 || dayDiff == -364 || dayDiff == 364)) {
						counter = counter - 1;
					}
				}
				if (counter == 7) {
					PrefHelper.setBoolean(ResourceUtil
							.getString(R.string.KEY_7_DAY_COMPLETED), true);
				}
			} else {
				if (BaseConstant.mDietCycle.getLaunch_history().size() >= 2) {
					counter = BaseConstant.mDietCycle.getLaunch_history()
							.size();
					for (int i = 0; i < BaseConstant.mDietCycle
							.getLaunch_history().size() - 1; i++) {
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(BaseConstant.mDietCycle
								.getLaunch_history().get(i));

						Calendar cal1 = Calendar.getInstance();
						cal1.setTimeInMillis(BaseConstant.mDietCycle
								.getLaunch_history().get(i + 1));

						int dayDiff = cal1.get(Calendar.DAY_OF_YEAR)
								- cal.get(Calendar.DAY_OF_YEAR);
						if (!(dayDiff == -1 || dayDiff == 1 || dayDiff == -365
								|| dayDiff == 365 || dayDiff == -364 || dayDiff == 364)) {
							counter = counter - 1;
						}
					}
				} else if (BaseConstant.mDietCycle.getLaunch_history().size() == 1) {
					counter = counter + 1;
				}
			}
			if (counter >= 1) {
				return (int) ((counter * 100) / 7);
			}
			return 0;
		} else {
			return 100;
		}
	}

	/**
	 * getLastDateWhenDietCycleFinish method calculate time from for last date
	 * of diet cycle. and return result in Calendar object.
	 * 
	 * @return Calendar
	 */
	private static Calendar getLastDateWhenDietCycleFinish() {
		Calendar dietCycleFinishTime = null;
		if (BaseConstant.mDietCycle.getDiet_duration() <= 5) {
			int totalDays = getWeek(BaseConstant.mDietCycle.getDiet_duration()) * 7;
			long dietCycleFinishLongValue = BaseConstant.mDietCycle
					.getStartTime() + (totalDays * 86400000L);
			dietCycleFinishTime = Calendar.getInstance();
			dietCycleFinishTime.setTimeInMillis(dietCycleFinishLongValue);
		}
		return dietCycleFinishTime;
	}

	/**
	 * isDietCycleFinish() method is use for check diet cycle finish condition.
	 * If current time is greater than diet cycle last date than this function
	 * stop diet cycle.
	 * 
	 * @return boolean
	 */
	public static boolean isDietCycleFinish() {

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
				if (totalTime <= 0) {

					checkLastLaunchApp();
					startDate.set(Calendar.DATE, startDate.get(Calendar.DATE)
							+ totalDays);
					BaseConstant.mDietCycle.setLast_launch(-1);
					BaseConstant.mDietCycle.setEndTime(startDate
							.getTimeInMillis());
					BaseConstant.mDietCycle.setOne_notification_time(0);
					BaseConstant.mDietCycle.setDiet_cycle_is_on(false);
					DietCycle.setDietCycleHistory();
					PrefHelper
							.setBoolean(
									ResourceUtil
											.getString(R.string.KEY_DIET_CYCLE_COMPLETED),
									true);
					removeAlarm();
					BaseConstant.mDietCycle.getUsedCal().clear();
					BaseConstant.mDietCycle.getConsume_food_item().clear();
					return true;
				} else
					return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * checkLastLaunchApp() function is use for maintain track of every day
	 * application launch or not.
	 */
	public static void checkLastLaunchApp() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			if (BaseConstant.mDietCycle.getLast_launch() >= 1) {
				Calendar lastLaunchDate = Calendar.getInstance();
				lastLaunchDate.setTimeInMillis(BaseConstant.mDietCycle
						.getLast_launch());
				Calendar currentDate = Calendar.getInstance();

				if (!getTimeInddMMyy(lastLaunchDate).equalsIgnoreCase(
						getTimeInddMMyy(currentDate))) {
					int convertDay = Days.mapDay(lastLaunchDate
							.get(Calendar.DAY_OF_WEEK));
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(convertDay)) {
						Calendar startTime = Calendar.getInstance();
						startTime.setTimeInMillis(BaseConstant.mDietCycle
								.getStartTime());
						if (getTimeInddMMyy(startTime).equalsIgnoreCase(
								getTimeInddMMyy(lastLaunchDate))) {
							if (BaseConstant.mDietCycle.isNow()) {
								BaseConstant.mDietCycle
										.setFastingDays(BaseConstant.mDietCycle
												.getFastingDays() + 1);
							}
						} else {
							BaseConstant.mDietCycle
									.setFastingDays(BaseConstant.mDietCycle
											.getFastingDays() + 1);
						}
					}
					int dayDiff = (int) Math.ceil(((currentDate
							.getTimeInMillis() - 86400000L) - (lastLaunchDate
							.getTimeInMillis() + 86400000L)) / 86400000f);
					if (dayDiff >= 1) {
						Calendar gapDay = Calendar.getInstance();
						gapDay.setTimeInMillis(lastLaunchDate.getTimeInMillis() + 86400000L);
						Calendar dietCycleFinishTime = getLastDateWhenDietCycleFinish();
						for (int i = 0; i < dayDiff; i++) {
							gapDay.set(Calendar.DATE, gapDay.get(Calendar.DATE)
									+ i);
							if (!getTimeInddMMyy(gapDay).equalsIgnoreCase(
									getTimeInddMMyy(currentDate))) {
								if (gapDay.before(currentDate)) {
									if (dietCycleFinishTime != null) {
										if (gapDay.before(dietCycleFinishTime)) {
											convertDay = Days.mapDay(gapDay
													.get(Calendar.DAY_OF_WEEK));
											if (BaseConstant.mDietCycle
													.getSelected_fasting_days()
													.contains(convertDay)) {
												BaseConstant.mDietCycle
														.setFastingDaysMissed(BaseConstant.mDietCycle
																.getFastingDaysMissed() + 1);
											}
										}
									} else {
										convertDay = Days.mapDay(gapDay
												.get(Calendar.DAY_OF_WEEK));
										if (BaseConstant.mDietCycle
												.getSelected_fasting_days()
												.contains(convertDay)) {
											BaseConstant.mDietCycle
													.setFastingDaysMissed(BaseConstant.mDietCycle
															.getFastingDaysMissed() + 1);
										}
									}
								}
							}
						}
					}
					PrefHelper.setBoolean(
							BaseApplication.getAppContext().getResources()
									.getString(R.string.KEY_BADGE_SHOW_FLAG),
							true);
					BaseConstant.mDietCycle.setLast_launch(currentDate
							.getTimeInMillis());
					BaseConstant.mDietCycle.setLaunch_history(currentDate
							.getTimeInMillis());
				}

			}
		}
	}

	public static boolean todayIsFastingDay(ArrayList<Long> mWeekFastingDates) {
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		boolean flag = false;
		for (int i = 0; i < mWeekFastingDates.size(); i++) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(mWeekFastingDates.get(i));
			if (c.get(Calendar.DAY_OF_YEAR) == dayOfWeek) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * getDateOfFastingDayInThisWeek() method returns fasting dates of current
	 * week.
	 * 
	 * @return ArrayList<Long>
	 */
	public static ArrayList<Long> getDateOfFastingDayInThisWeek() {
		boolean flag = BaseConstant.mDietCycle.isNow();
		ArrayList<Long> mWeekFastingDates = new ArrayList<Long>();
		Calendar currentDate = Calendar.getInstance();
		ArrayList<Integer> days = BaseConstant.mDietCycle
				.getSelected_fasting_days();
		int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
		for (int i = 0; i < days.size(); i++) {
			int convertCalendarDay = Days.reverseMapDay(days.get(i));
			if (convertCalendarDay < currentDayOfWeek) {
				Calendar fastingDate = Calendar.getInstance();
				fastingDate.set(Calendar.DATE, fastingDate.get(Calendar.DATE)
						+ (7 - (currentDayOfWeek - convertCalendarDay)));
				fastingDate.set(Calendar.HOUR, 00);
				fastingDate.set(Calendar.MINUTE, 00);
				fastingDate.set(Calendar.SECOND, 01);
				fastingDate.set(Calendar.AM_PM, Calendar.AM);
				mWeekFastingDates.add(fastingDate.getTimeInMillis());

			} else if (convertCalendarDay > currentDayOfWeek) {
				Calendar fastingDate = Calendar.getInstance();
				fastingDate.set(Calendar.DATE, fastingDate.get(Calendar.DATE)
						+ (convertCalendarDay - currentDayOfWeek));
				fastingDate.set(Calendar.HOUR, 00);
				fastingDate.set(Calendar.MINUTE, 00);
				fastingDate.set(Calendar.SECOND, 01);
				fastingDate.set(Calendar.AM_PM, Calendar.AM);
				mWeekFastingDates.add(fastingDate.getTimeInMillis());
			} else {
				Calendar startDate = Calendar.getInstance();
				startDate.setTimeInMillis(BaseConstant.mDietCycle
						.getStartTime());
				if (startDate.get(Calendar.DATE) == currentDate
						.get(Calendar.DATE)) {
					if (flag) {
						Calendar fastingDate = Calendar.getInstance();
						fastingDate.set(Calendar.HOUR, 00);
						fastingDate.set(Calendar.MINUTE, 00);
						fastingDate.set(Calendar.SECOND, 01);
						fastingDate.set(Calendar.AM_PM, Calendar.AM);
						mWeekFastingDates.add(fastingDate.getTimeInMillis());
					} else {
						Calendar fastingDate = Calendar.getInstance();
						fastingDate.set(Calendar.DATE,
								fastingDate.get(Calendar.DATE) + 7);
						fastingDate.set(Calendar.HOUR, 00);
						fastingDate.set(Calendar.MINUTE, 00);
						fastingDate.set(Calendar.SECOND, 01);
						fastingDate.set(Calendar.AM_PM, Calendar.AM);
						mWeekFastingDates.add(fastingDate.getTimeInMillis());
					}
				} else {
					Calendar fastingDate = Calendar.getInstance();
					fastingDate.set(Calendar.HOUR, 00);
					fastingDate.set(Calendar.MINUTE, 00);
					fastingDate.set(Calendar.SECOND, 01);
					fastingDate.set(Calendar.AM_PM, Calendar.AM);
					mWeekFastingDates.add(fastingDate.getTimeInMillis());
				}
			}
		}
		Collections.sort(mWeekFastingDates);
		return mWeekFastingDates;
	}

	public static int getWeek(int number) {
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
	 * showDietCycleOnDialog() method is use for showing diet cycle ON dialog.
	 * 
	 * @param context
	 */
	public static void showDietCycleOnDialog(final Activity context) {
		final Dialog mDialog2 = new Dialog(context, R.style.DialogTheme);
		mDialog2.setContentView(R.layout.raw_dialog3);
		mDialog2.setCancelable(true);

		CustomTextView mTxtDialogTitle = (CustomTextView) mDialog2
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mtxtDialogSubTitle = (CustomTextView) mDialog2
				.findViewById(R.id.tv_txt_guide2);

		CustomButton mBtnDialogCancel = (CustomButton) mDialog2
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnDialogOk = (CustomButton) mDialog2
				.findViewById(R.id.bt_btn_ok);

		mTxtDialogTitle.setText(R.string.str_diet_cycle_start_title);
		mtxtDialogSubTitle.setText(context
				.getString(R.string.str_diet_cycle_start_sub_title));

		mBtnDialogCancel.setText(R.string.str_no);
		mBtnDialogOk.setText(R.string.str_view);

		mBtnDialogOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog2.dismiss();
				PrefHelper.setBoolean(
						context.getString(R.string.KEY_IS_CONSUMED_TODAY_5),
						true);
				context.startActivity(new Intent(context,
						DietCycleHistoryActivity.class));
			}
		});
		mBtnDialogCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog2.dismiss();
				PrefHelper.setBoolean(
						context.getString(R.string.KEY_IS_CONSUMED_TODAY_5),
						true);
				context.startActivity(new Intent(context,
						ConsumedTodayActivity.class));
			}
		});

		// }
		mDialog2.show();
	}

	/**
	 * setALLPreferencesToTrue method set all preference true for hint dialog
	 * 
	 * @param context
	 */
	public static void setALLPreferencesToTrue(Context context) {

		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_DIALOG_ACTIVE),
				true);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_ABOUT_ACTIVE),
				true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_START), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_MYSTATS), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_DIET_CYCLE),
				true);

		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_PROGRESS),
				true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_RADIO),
				true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY),
				true);
		PrefHelper.setBoolean(context
				.getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY_MENU),
				true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_START),
				true);

		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_1),
				true);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_2),
				true);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_3),
				true);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_4),
				true);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_5),
				true);

		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_MY_STATS_CURRENT_BODY_STAT),
				true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_MY_STATS_DIET_CYCLE_HISTORY),
				true);

		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_1), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_2), true);

		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_1), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_2), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_3), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_4), true);

		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_STOP_DIET),
				true);

		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_GRAPH), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_STATS), true);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_SET_GOAL), true);
	}

	/**
	 * setALLPreferencesToTrue method set all preference false for hint dialog
	 * 
	 * @param context
	 */
	public static void setALLPreferencesToFalse(Context context) {
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_DIALOG_ACTIVE),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_ABOUT_ACTIVE),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_START), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_MYSTATS), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_START_ACTIVE_DIET_CYCLE),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_PROGRESS),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_RADIO),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY),
				false);
		PrefHelper.setBoolean(context
				.getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY_MENU),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_QUICK_START_ACTIVE_START),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_1),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_2),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_3),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_4),
				false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_FRAGMENT_5),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_MY_STATS_CURRENT_BODY_STAT),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_MY_STATS_DIET_CYCLE_HISTORY),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_1), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_DIET_CYCLE_HISTORY_2), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_1), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_2), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_3), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_CONSUMED_TODAY_4), false);
		PrefHelper.setBoolean(context.getString(R.string.KEY_IS_STOP_DIET),
				false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_GRAPH), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_STATS), false);
		PrefHelper.setBoolean(
				context.getString(R.string.KEY_IS_BODY_STAT_SET_GOAL), false);
	}

	/**
	 * checkAchievemtnDialog is use for showing achievement dialog if user get
	 * any achievement.
	 * 
	 * @param activity
	 */
	public static void checkAchievemtnDialog(Activity activity) {

		if (!PrefHelper.getBoolean(ResourceUtil
				.getString(R.string.KEY_SHOW_DIALOG_ABOUT_US_OPTIONS), false)) {
			if (PrefHelper
					.getBoolean(ResourceUtil
							.getString(R.string.KEY_CHECK_ABOUT_US_OPTIONS),
							false)) {
				PrefHelper.setBoolean(ResourceUtil
						.getString(R.string.KEY_SHOW_DIALOG_ABOUT_US_OPTIONS),
						true);
				setUpDialogForAchievements(activity, 0);
			}
		}

		if (!PrefHelper.getBoolean(ResourceUtil
				.getString(R.string.KEY_SHOW_DIALOG_DIET_CYCLE_COMPLETED),
				false)) {
			if (PrefHelper.getBoolean(
					ResourceUtil.getString(R.string.KEY_DIET_CYCLE_COMPLETED),
					false)) {
				PrefHelper
						.setBoolean(
								ResourceUtil
										.getString(R.string.KEY_SHOW_DIALOG_DIET_CYCLE_COMPLETED),
								true);
				setUpDialogForAchievements(activity, 2);
			}
		}

		if (!PrefHelper.getBoolean(ResourceUtil
				.getString(R.string.KEY_SHOW_DIALOG_FASTING_DAY_COMPLETED),
				false)) {
			if (BaseConstant.mDietCycle.getFastingDays() >= 1) {
				PrefHelper
						.setBoolean(
								ResourceUtil
										.getString(R.string.KEY_SHOW_DIALOG_FASTING_DAY_COMPLETED),
								true);
				setUpDialogForAchievements(activity, 1);
			}
		}

		if (!PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_SHOW_7_DAY_COMPLETED),
				false)) {
			if (PrefHelper
					.getBoolean(ResourceUtil
							.getString(R.string.KEY_7_DAY_COMPLETED), false)) {
				PrefHelper.setBoolean(ResourceUtil
						.getString(R.string.KEY_SHOW_7_DAY_COMPLETED), true);
				setUpDialogForAchievements(activity, 3);
			}
		}
	}

	/**
	 * setUpDialogForAchievements method is use for setup achievement dialog.
	 * 
	 * @param mActivity
	 * @param id
	 */
	public static void setUpDialogForAchievements(final Activity mActivity,
			final int id) {
		final Dialog mDialog1 = new Dialog(mActivity, R.style.DialogTheme);
		mDialog1.setContentView(R.layout.raw_dialog6);
		mDialog1.setCanceledOnTouchOutside(false);
		CustomTextView mTxtGuide_1 = (CustomTextView) mDialog1
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtGuide_2 = (CustomTextView) mDialog1
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnCancel = (CustomButton) mDialog1
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnMore = (CustomButton) mDialog1
				.findViewById(R.id.bt_btn_More);
		ImageView mImg_Image_Achievement = (ImageView) mDialog1
				.findViewById(R.id.iv_img_achievement);
		mTxtGuide_2.setText(R.string.str_share);
		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnMore.setText(R.string.str_achievement_5);

		if (id == 0) {
			mTxtGuide_1.setText(R.string.str_achievement_2);
			mImg_Image_Achievement.setBackgroundResource(R.drawable.ic_entered);
		} else if (id == 1) {
			mTxtGuide_1.setText(R.string.str_achievement_3);
			mImg_Image_Achievement.setBackgroundResource(R.drawable.ic_fasting);
		} else if (id == 2) {
			mTxtGuide_1.setText(R.string.str_achievement_4);
			mImg_Image_Achievement
					.setBackgroundResource(R.drawable.ic_diet_goal);
		} else if (id == 3) {
			mTxtGuide_1.setText(R.string.str_achievement_6);
			mImg_Image_Achievement.setBackgroundResource(R.drawable.ic_week);
		}
		mTxtGuide_2.setOnClickListener(new OnClickListener() {
			String message;
			int drawableID;

			@Override
			public void onClick(View v) {
				if (ConnectionUtility.isNetAvailable()) {
					switch (id) {
					case 0:
						message = mActivity
								.getString(R.string.str_facebook_share_message_5)
								+ "\n"
								+ mActivity.getString(R.string.str_join)
								+ ":"
								+ mActivity.getString(R.string.str_visited_1)
								+ "\n\n"
								+ mActivity
										.getString(R.string.str_facebook_share_message_4)
								+ " " + AppRater.getPlayStoreURL(mActivity);
						drawableID = R.drawable.ic_entered;
						if (!isLoggedIn()) {
							Intent intent = new Intent(mActivity,
									FacebookLoginActivity.class);
							intent.putExtra(FacebookLoginActivity.extraMessage,
									message);
							intent.putExtra(
									FacebookLoginActivity.extraDrawableID,
									drawableID);
							mActivity
									.startActivityForResult(
											intent,
											FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE);
							mActivity.overridePendingTransition(0, 0);
						} else {
							FacebookPostOnAchievements(mActivity, message,
									drawableID);
						}
						break;
					case 1:
						message = mActivity
								.getString(R.string.str_facebook_share_message_5)
								+ "\n"
								+ mActivity.getString(R.string.str_fasting_day)
								+ ":"
								+ mActivity
										.getString(R.string.str_you_have_done)
								+ "\n\n"
								+ mActivity
										.getString(R.string.str_facebook_share_message_4)
								+ " " + AppRater.getPlayStoreURL(mActivity);
						drawableID = R.drawable.ic_fasting;
						if (!isLoggedIn()) {
							Intent intent = new Intent(mActivity,
									FacebookLoginActivity.class);
							intent.putExtra(FacebookLoginActivity.extraMessage,
									message);
							intent.putExtra(
									FacebookLoginActivity.extraDrawableID,
									drawableID);
							mActivity
									.startActivityForResult(
											intent,
											FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE);
							mActivity.overridePendingTransition(0, 0);
						} else {
							FacebookPostOnAchievements(mActivity, message,
									drawableID);
						}
						break;
					case 2:
						message = mActivity
								.getString(R.string.str_facebook_share_message_5)
								+ "\n"
								+ mActivity.getString(R.string.str_become)
								+ ":"
								+ mActivity
										.getString(R.string.str_comoplete_diet_cycle)
								+ "\n\n"
								+ mActivity
										.getString(R.string.str_facebook_share_message_4)
								+ " " + AppRater.getPlayStoreURL(mActivity);
						drawableID = R.drawable.ic_diet_goal;
						if (!isLoggedIn()) {
							Intent intent = new Intent(mActivity,
									FacebookLoginActivity.class);
							intent.putExtra(FacebookLoginActivity.extraMessage,
									message);
							intent.putExtra(
									FacebookLoginActivity.extraDrawableID,
									drawableID);
							mActivity
									.startActivityForResult(
											intent,
											FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE);
							mActivity.overridePendingTransition(0, 0);
						} else {
							FacebookPostOnAchievements(mActivity, message,
									drawableID);
						}
						break;
					case 3:
						message = mActivity
								.getString(R.string.str_facebook_share_message_5)
								+ "\n"
								+ mActivity.getString(R.string.str_become)
								+ ":"
								+ mActivity
										.getString(R.string.str_comoplete_diet_cycle)
								+ "\n\n"
								+ mActivity
										.getString(R.string.str_facebook_share_message_6)
								+ " " + AppRater.getPlayStoreURL(mActivity);
						drawableID = R.drawable.ic_week;
						if (!isLoggedIn()) {
							Intent intent = new Intent(mActivity,
									FacebookLoginActivity.class);
							intent.putExtra(FacebookLoginActivity.extraMessage,
									message);
							intent.putExtra(
									FacebookLoginActivity.extraDrawableID,
									drawableID);
							mActivity
									.startActivityForResult(
											intent,
											FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE);
							mActivity.overridePendingTransition(0, 0);
						} else {
							FacebookPostOnAchievements(mActivity, message,
									drawableID);
						}
						break;
					}
				} else {
					Toast.makeText(mActivity, R.string.str_internet_connection,
							Toast.LENGTH_LONG).show();
				}
				mDialog1.dismiss();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog1.dismiss();
			}
		});
		mBtnMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog1.dismiss();
				mActivity.startActivity(new Intent(mActivity,
						AchievementsActivity.class));
			}
		});
		mDialog1.show();
	}

	/**
	 * Function to check user is LoggedIn or not
	 * 
	 * @return boolean true/false
	 */
	public static boolean isLoggedIn() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * FacebookPostOnAchievements method is use for achievement post on facebook
	 * with message and image.
	 * 
	 * @param mActivity
	 * @param message
	 * @param drawableID
	 */
	public static void FacebookPostOnAchievements(final Activity mActivity,
			String message, int drawableID) {
		byte[] data = null;
		Bundle params = new Bundle();
		Bitmap bitmap = null;

		bitmap = BitmapFactory.decodeResource(mActivity.getResources(),
				drawableID);
		if (bitmap != null) {
			progressDialog = new ProgressDialog(mActivity);
			progressDialog.setMessage(mActivity.getString(R.string.str_wait));
			progressDialog.setCancelable(false);
			progressDialog.show();
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			data = stream.toByteArray();
			params.putString("message", message);
			params.putByteArray("picture", data);
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					if (progressDialog != null && progressDialog.isShowing())
						progressDialog.dismiss();
					if (response.toString().contains("200")) {
						Toast.makeText(mActivity, "Shared Successfully",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mActivity, "Shared Fail",
								Toast.LENGTH_LONG).show();
					}
				}
			};
			Request request = new Request(Session.getActiveSession(),
					"me/photos", params, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	/**
	 * getDeviceUDID() method return device UDID
	 * 
	 * @return String
	 */
	public static String getDeviceUDID() {
		String android_id = Secure.getString(BaseApplication.getAppContext()
				.getContentResolver(), Secure.ANDROID_ID);
		return android_id;
	}

	/**
	 * leftCaloryDialog method is use for showing calories left dialog.
	 * 
	 * @param mActivity
	 */
	public static void leftCaloryDialog(final Activity mActivity) {
		String key = Utility.getTimeInddMMyy(Calendar.getInstance());
		int cal = 0;
		if (BaseConstant.mDietCycle.getUsedCal().containsKey(key)) {
			cal = BaseConstant.mDietCycle.getUsedCal().get(key);
		}

		final Dialog mDialog3 = new Dialog(mActivity, R.style.DialogTheme);
		mDialog3.setContentView(R.layout.raw_dialog3);
		mDialog3.setCanceledOnTouchOutside(false);
		CustomTextView mTxtRemove = (CustomTextView) mDialog3
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtRemoveMessage = (CustomTextView) mDialog3
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnOK = (CustomButton) mDialog3
				.findViewById(R.id.bt_btn_ok);
		CustomButton mBtnCancel = (CustomButton) mDialog3
				.findViewById(R.id.bt_btn_Cancel);

		mTxtRemove.setText(R.string.str_non_fasting_day_title_1);
		mTxtRemoveMessage.setText(ResourceUtil
				.getString(R.string.str_dialog_23)
				+ (BaseConstant.mDietCycle.getCalories_consumption() - cal)
				+ " " + ResourceUtil.getString(R.string.str_dialog_26));

		mBtnOK.setText(R.string.str_dialog_2);
		mBtnCancel.setText(R.string.str_menu_dialog);

		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog3.dismiss();
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.startActivity(new Intent(mActivity,
						ConsumedTodayActivity.class));
				mDialog3.dismiss();
			}
		});
		mDialog3.show();
	}

	/**
	 * hide method is use for hide hint dialog
	 * 
	 * @param mViews
	 */
	public static void hide(ShowcaseViews mViews) {
		if (mViews != null)
			mViews.hide();
	}

	/**
	 * hintDialogShow method is use for set hint dialog shown by user.
	 * 
	 * @param _activityID
	 * @param _currentPosition
	 */
	public static void hintDialogShow(int _activityID, int _currentPosition) {
		switch (_activityID) {
		case BaseConstant.HOME_ACTIVITY_ID:

			if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				if (_currentPosition == 0) {
					PrefHelper.setBoolean(BaseApplication.getAppContext()
							.getString(R.string.KEY_IS_START_ACTIVE_START),
							false);
				}
			} else {
				if (_currentPosition == 0) {
					PrefHelper.setBoolean(
							BaseApplication.getAppContext().getString(
									R.string.KEY_IS_START_ACTIVE_DIET_CYCLE),
							false);
				} else if (_currentPosition == 1) {
					PrefHelper.setBoolean(BaseApplication.getAppContext()
							.getString(R.string.KEY_IS_START_ACTIVE_MYSTATS),
							false);
				}
			}
			break;
		case BaseConstant.ABOUT_ACTIVITY_ID:
			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_ABOUT_ACTIVE), false);
			break;
		case BaseConstant.QUICK_START_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper.setBoolean(
						BaseApplication.getAppContext().getString(
								R.string.KEY_IS_QUICK_START_ACTIVE_PROGRESS),
						false);
			} else if (_currentPosition == 1) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_QUICK_START_ACTIVE_RADIO),
						false);
			} else if (_currentPosition == 2) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_QUICK_START_ACTIVE_CALORY),
						false);
			} else if (_currentPosition == 3) {
				PrefHelper
						.setBoolean(
								BaseApplication
										.getAppContext()
										.getString(
												R.string.KEY_IS_QUICK_START_ACTIVE_CALORY_MENU),
								false);
			} else if (_currentPosition == 4) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_QUICK_START_ACTIVE_START),
						false);
			}
			break;
		case BaseConstant.STEP_1_FRAGMENT_ID:

			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_FRAGMENT_1), false);
			break;
		case BaseConstant.STEP_2_FRAGMENT_ID:

			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_FRAGMENT_2), false);
			break;
		case BaseConstant.STEP_3_FRAGMENT_ID:

			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_FRAGMENT_3), false);
			break;
		case BaseConstant.STEP_4_FRAGMENT_ID:

			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_FRAGMENT_4), false);
			break;
		case BaseConstant.STEP_5_FRAGMENT_ID:

			PrefHelper.setBoolean(
					BaseApplication.getAppContext().getString(
							R.string.KEY_IS_FRAGMENT_5), false);
			break;
		case BaseConstant.MYSTATS_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_MY_STATS_CURRENT_BODY_STAT),
						false);
			} else if (_currentPosition == 1) {
				PrefHelper.setBoolean(
						BaseApplication.getAppContext().getString(
								R.string.KEY_IS_MY_STATS_DIET_CYCLE_HISTORY),
						false);
			}
			break;
		case BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper
						.setBoolean(
								BaseApplication.getAppContext().getString(
										R.string.KEY_IS_DIET_CYCLE_HISTORY_1),
								false);
			} else if (_currentPosition == 1) {
				PrefHelper
						.setBoolean(
								BaseApplication.getAppContext().getString(
										R.string.KEY_IS_DIET_CYCLE_HISTORY_2),
								false);
			}
			break;
		case BaseConstant.CONSUMED_TODAY_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_CONSUMED_TODAY_1), false);
			} else if (_currentPosition == 1) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_CONSUMED_TODAY_2), false);
			} else if (_currentPosition == 2) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_CONSUMED_TODAY_3), false);
			} else if (_currentPosition == 3) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_CONSUMED_TODAY_4), false);
			}
			break;
		case BaseConstant.STOP_DIET_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_STOP_DIET), false);
			}
			break;
		case BaseConstant.BODY_STATS_ACTIVITY_ID:
			if (_currentPosition == 0) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_BODY_STAT_GRAPH), false);
			} else if (_currentPosition == 1) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_BODY_STAT_STATS), false);
			} else if (_currentPosition == 2) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getString(R.string.KEY_IS_BODY_STAT_SET_GOAL), false);
			}
			break;
		}
	}

	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	/**
	 * getDeviceName method is use for getting device name
	 * 
	 * @return String
	 */
	private static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	/**
	 * getUserInfo() method is use for generate json object of device related
	 * information. and provide result in string of json object.
	 * 
	 * @return String
	 */
	public static String getUserInfo() {
		String deviceModel = getDeviceName();
		String androidVersion = "Android " + Build.VERSION.RELEASE + " OS";
		String osVersion = Build.VERSION.RELEASE;
		String udid = getDeviceUDID();

		JSONObject userInfoJson = new JSONObject();

		try {
			PackageInfo pInfo = BaseApplication.getAppContext().getPackageManager().getPackageInfo(
					BaseApplication.getAppContext().getPackageName(), 0);
			userInfoJson.put("v",
					pInfo.versionName);
			userInfoJson.put("b",
					pInfo.versionCode);
			userInfoJson.put("m", deviceModel);
			userInfoJson.put("sn", androidVersion);
			userInfoJson.put("sv", osVersion);
			userInfoJson.put("i", udid);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return userInfoJson.toString();
	}

	/**
	 * get process entity
	 * 
	 * @param entity
	 * @return String
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String processEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				entity.getContent()));
		String line, result = "";
		while ((line = br.readLine()) != null)
			result += line;
		return result;
	}

	/**
	 * checkResponse method is use for check response of any web api calling
	 * result. and return true if api call successfully call otherwise return
	 * false.
	 * 
	 * @param json
	 * @return boolean
	 */
	public static boolean checkResponse(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.getString("status").equalsIgnoreCase("ok")) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int convertDpToPixel(float dp) {
		Resources resources = BaseApplication.getAppContext().getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}
}