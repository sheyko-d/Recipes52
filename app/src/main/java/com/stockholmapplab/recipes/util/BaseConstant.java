package com.stockholmapplab.recipes.util;

import com.stockholmapplab.recipes.pojo.DietCycle;

public class BaseConstant {

	// Notification Broadcast Action
	public static final String DISPLAY_1_HOUR_NOTIFICATION_ACTION = "com.stockholmapplab.recipes.DISPLAY_1_HOUR_NOTIFICATION_ACTION";
	// GRAPH DETAILS

	public static final int[] calendarXPointOriginal = { 35, 151, 165, 40, 35,
			151, 262, 274, 165, 151, 262, 391, 404, 274, 262, 40, 165, 177, 49,
			40, 165, 275, 287, 177, 165, 275, 403, 414, 287, 275, 49, 178, 190,
			48, 49 };
	public static final int[] calendarYPointOriginal = { 51, 43, 160, 171, 51,
			43, 34, 146, 160, 43, 34, 23, 131, 146, 34, 171, 160, 265, 279,
			171, 160, 146, 252, 265, 160, 146, 131, 237, 252, 146, 279, 265,
			378, 379, 279 };

	public static final int[] rectangleX = { 43, 158, 268, 56, 164, 280, 71 };
	public static final int[] rectangleY = { 56, 43, 29, 165, 150, 135, 273 };

	public static final float[] scaleXPointInPercentage = { 15.45372866f,
			28.21203953f, 41.15004492f, 53.9083558f, 66.75651393f,
			79.60467206f, 92.45283019f };

	public static final float scaleYPointInPercentage = 65.15789474f;

	public static final int FIVE_TWO_INTENSITY = 2;
	public static final int FOUR_THREE_INTENSITY = 3;
	public static final int SIX_ONE_INTENSITY = 1;

	public static DietCycle mDietCycle = null;

	public final static int HOME_ACTIVITY_ID = 1000;
	public final static int QUICK_START_ACTIVITY_ID = 1001;
	public final static int ABOUT_ACTIVITY_ID = 1002;
	public final static int MYSTATS_ACTIVITY_ID = 1003;
	public final static int DIET_CYCLE_HISTORY_ACTIVITY_ID = 1004;
	public final static int CONSUMED_TODAY_ACTIVITY_ID = 1005;

	public final static int STEP_1_FRAGMENT_ID = 1006;
	public final static int STEP_2_FRAGMENT_ID = 1007;
	public final static int STEP_3_FRAGMENT_ID = 1008;
	public final static int STEP_4_FRAGMENT_ID = 1009;
	public final static int STEP_5_FRAGMENT_ID = 1010;

	public final static int STOP_DIET_ACTIVITY_ID = 1011;
	public final static int BODY_STATS_ACTIVITY_ID = 1012;

	// Feedback and join URL
	public static final String FEEDBACK_URL = "http://dev.core.stockholmapplab.com/Webservice/Message/Submit";

}
