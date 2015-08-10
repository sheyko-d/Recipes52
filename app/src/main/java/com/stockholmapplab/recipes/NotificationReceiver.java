package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stockholmapplab.recipes.pojo.Days;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * NotificationReceiver class use for notification. MORNING_CODE morning alarm
 * EVENING_CODE evening alarm HOUR_NOTIFICATION_CODE alarm is use in Consume
 * Today activity. HOUR_NOTIFICATION_CODE notification time fire than generate
 * broadcast in all activity
 */
public class NotificationReceiver extends BroadcastReceiver {

	public static final String extraAlarmCode = "extra_alarm_code";
	public static final int MORNING_CODE = 1;
	public static final int EVENING_CODE = 2;
	public static final int HOUR_NOTIFICATION_CODE = 3;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}

		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			int code = arg1.getExtras().getInt(extraAlarmCode);
			String msg = "";
			switch (code) {
			case 1:
				msg = getMsg(arg0, code);
				if (!msg.equalsIgnoreCase("")) {
					setNotification(arg0, msg);
				}
				break;
			case 2:
				msg = getMsg(arg0, code);
				if (!msg.equalsIgnoreCase("")) {
					setNotification(arg0, msg);
				}
				break;
			case 3:
				Intent intent = new Intent(
						BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION);
				arg0.sendBroadcast(intent);
				break;

			}

		}

	}

	@SuppressWarnings("deprecation")
	private void setNotification(Context context, String msg) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, msg, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent;
		int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

		notificationIntent = new Intent(context, SplashActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent intent = PendingIntent.getActivity(context, iUniqueId,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, msg, intent);
		notification.flags = Notification.FLAG_SHOW_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE;
		notificationManager.notify(iUniqueId, notification);
	}

	private String getMsg(Context context, int code) {
		String msg = "";
		if (code == MORNING_CODE) {
			ArrayList<Long> mWeekFastingDates = Utility
					.getDateOfFastingDayInThisWeek();
			if (Utility.todayIsFastingDay(mWeekFastingDates)) {
				msg = ResourceUtil
						.getString(R.string.str_fasting_day_sub_title);
			} else {
				msg = ResourceUtil
						.getString(R.string.str_non_fasting_day_sub_title);
			}
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK) + 1);

			int convertDay = Days.mapDay(cal.get(Calendar.DAY_OF_WEEK));
			if (BaseConstant.mDietCycle.getSelected_fasting_days().contains(
					convertDay)) {
				msg = context.getString(R.string.str_dont_forget);
			}
		}
		return msg;
	}

}
