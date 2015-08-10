package com.stockholmapplab.recipes.pojo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.Utility;

/**
 * Days class use for 7 days of week.
 */
public class Days {

	private int day;
	private boolean isFastDay;
	private boolean isToday;

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isFastDay() {
		return isFastDay;
	}

	public void setFastDay(boolean isFastDay) {
		this.isFastDay = isFastDay;
	}

	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public static ArrayList<Days> getDays() {
		ArrayList<Days> days = new ArrayList<Days>();
		ArrayList<Integer> fasting_days = null;
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on())
			fasting_days = BaseConstant.mDietCycle.getSelected_fasting_days();
		for (int i = 0; i < 7; i++) {
			Days day = new Days();
			day.setDay(i);

			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				if (fasting_days.contains(i))
					day.setFastDay(true);
			} else {
				day.setFastDay(false);
			}

			day.setToday(false);
			days.add(day);
		}

		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			if (fasting_days.contains(mapDay(Calendar.getInstance().get(
					Calendar.DAY_OF_WEEK)))) {
				if (Utility.todayIsFastingDay(Utility.getDateOfFastingDayInThisWeek())) {
					days.get(
							mapDay(Calendar.getInstance().get(
									Calendar.DAY_OF_WEEK))).setFastDay(true);
				} else {
					days.get(
							mapDay(Calendar.getInstance().get(
									Calendar.DAY_OF_WEEK))).setFastDay(false);
				}
			}
		}

		days.get(mapDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))
				.setToday(true);
		return days;
	}

	/**
	 * This method is use for map calendar day to display day in our app
	 * 
	 * @param calendarDay
	 * @return
	 */
	public static int mapDay(int calendarDay) {
		int day = -1;
		switch (calendarDay) {
		case 1:
			day = 6;
			break;

		case 2:
			day = 0;
			break;
		case 3:
			day = 1;
			break;
		case 4:
			day = 2;
			break;
		case 5:
			day = 3;
			break;
		case 6:
			day = 4;
			break;
		case 7:
			day = 5;
			break;
		}

		return day;
	}

	/**
	 * This method is use for map our day to calendar day
	 * 
	 * @param mDay
	 * @return
	 */
	public static int reverseMapDay(int mDay) {
		int day = -1;
		switch (mDay) {

		case 0:
			day = 2;
			break;
		case 1:
			day = 3;
			break;
		case 2:
			day = 4;
			break;
		case 3:
			day = 5;
			break;
		case 4:
			day = 6;
			break;
		case 5:
			day = 7;
			break;
		case 6:
			day = 1;
			break;

		}

		return day;
	}

	public static String getStringFormatDays() {
		ArrayList<Integer> days = BaseConstant.mDietCycle
				.getSelected_fasting_days();
		Collections.sort(days);
		String str = "";
		for (int i = 0; i < days.size(); i++) {
			switch (days.get(i)) {
			case 0:
				str = str + "mon" + " ";
				break;
			case 1:
				str = str + "tue" + " ";
				break;
			case 2:
				str = str + "wed" + " ";
				break;
			case 3:
				str = str + "thu" + " ";
				break;
			case 4:
				str = str + "fri" + " ";
				break;
			case 5:
				str = str + "sat" + " ";
				break;
			case 6:
				str = str + "sun" + " ";
				break;

			}
		}
		return str;
	}
}
