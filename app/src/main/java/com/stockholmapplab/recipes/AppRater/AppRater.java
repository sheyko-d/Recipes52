package com.stockholmapplab.recipes.AppRater;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.util.PrefHelper;
/**
 * AppRater class is use for showing app rater dialog and calculate how many times application launch. 
 */
public class AppRater {

	private static final int LIMIT_CONSUME_FOOD_ITEM = 5;
	private static Dialog mAppRateDialog;

	public static String getPlayStoreURL(Context mContext) {
		return "http://play.google.com/store/apps/details?id="
				+ mContext.getPackageName();
	}

	public static String IPhoneURL = "http://bit.ly/1499E3p";

	public static String getShortURL() {
		return "http://bit.ly/11BWBZM";

	}
	
	/**
	 * incrementNumberOfTimeAppLaunch() method calculate and increment how many time application launch.
	 */
	public static void incrementNumberOfTimeAppLaunch() {
		int numberOfTimeAppLaunch = PrefHelper.getInt(
				BaseApplication.getAppContext().getResources()
						.getString(R.string.KEY_NUMBER_OF_TIME_APP_LAUNCH), 0);
		numberOfTimeAppLaunch = numberOfTimeAppLaunch + 1;
		PrefHelper.setInt(BaseApplication.getAppContext().getResources()
				.getString(R.string.KEY_NUMBER_OF_TIME_APP_LAUNCH),
				numberOfTimeAppLaunch);
	}

	public static void incrementNumberOfTimeDialogLaunch() {
		int numberOfTimeDialogLaunch = PrefHelper.getInt(
				BaseApplication.getAppContext().getResources()
						.getString(R.string.KEY_NUMBER_OF_TIME_DIALOG_LAUNCH),
				0);
		numberOfTimeDialogLaunch = numberOfTimeDialogLaunch + 1;
		PrefHelper.setInt(BaseApplication.getAppContext().getResources()
				.getString(R.string.KEY_NUMBER_OF_TIME_DIALOG_LAUNCH),
				numberOfTimeDialogLaunch);
	}
	
	/**
	 * checkAppRateCondition method is use for check conditions for open app rater dialog. 
	 * base on condition this method open app rater dialog.
	 * @param activity
	 */
	public static void checkAppRateCondition(Activity activity) {
		if (PrefHelper.getBoolean(BaseApplication.getAppContext()
				.getResources().getString(R.string.KEY_NO_THANKS_FLAG), true)) {
			int numberOfTimeAppLaunch = PrefHelper.getInt(
					BaseApplication.getAppContext().getResources()
							.getString(R.string.KEY_NUMBER_OF_TIME_APP_LAUNCH),
					0);
			int numberOfFoodItemEnter = PrefHelper.getInt(
					BaseApplication.getAppContext().getResources()
							.getString(R.string.KEY_ENTER_CONSUME_ITEM_COUNT),
					0);
			boolean openFlag = false;

			if (numberOfTimeAppLaunch >= PrefHelper.getInt(
					BaseApplication.getAppContext().getResources()
							.getString(R.string.KEY_APP_LAUNCH_COUNT), 4)) {
				// open dialog for rating
				PrefHelper
						.setInt(BaseApplication
								.getAppContext()
								.getResources()
								.getString(
										R.string.KEY_NUMBER_OF_TIME_APP_LAUNCH),
								0);
				PrefHelper.setInt(BaseApplication.getAppContext()
						.getResources()
						.getString(R.string.KEY_APP_LAUNCH_COUNT), 5);
				openFlag = true;
			}
			if (numberOfFoodItemEnter >= LIMIT_CONSUME_FOOD_ITEM) {
				PrefHelper.setInt(
						BaseApplication
								.getAppContext()
								.getResources()
								.getString(
										R.string.KEY_ENTER_CONSUME_ITEM_COUNT),
						0);
				openFlag = true;
			}

			if (openFlag) {
				showRateDialog(activity);
			}
		}
	}
	
	/**
	 * incrementNumberOfConsumeFoodItems() method calculate and increment how many food items consumed.
	 */
	public static void incrementNumberOfConsumeFoodItems() {
		int numberOfFoodItemEnter = PrefHelper.getInt(
				BaseApplication.getAppContext().getResources()
						.getString(R.string.KEY_ENTER_CONSUME_ITEM_COUNT), 0);
		numberOfFoodItemEnter = numberOfFoodItemEnter + 1;

		PrefHelper.setInt(BaseApplication.getAppContext().getResources()
				.getString(R.string.KEY_ENTER_CONSUME_ITEM_COUNT),
				numberOfFoodItemEnter);

	}
	/**
	 * showRateDialog method is use for showing app rater dialog.
	 * @param activity
	 */
	public static void showRateDialog(final Activity activity) {
		if (mAppRateDialog != null && mAppRateDialog.isShowing())
			mAppRateDialog.dismiss();

		mAppRateDialog = new Dialog(activity, R.style.DialogTheme);
		mAppRateDialog.setContentView(R.layout.raw_rate_dialog);
		mAppRateDialog.setCanceledOnTouchOutside(false);

		RelativeLayout firstOption = (RelativeLayout) mAppRateDialog
				.findViewById(R.id.ll_rate);
		RelativeLayout secondOption = (RelativeLayout) mAppRateDialog
				.findViewById(R.id.ll_remaind_me);
		RelativeLayout thirdOption = (RelativeLayout) mAppRateDialog
				.findViewById(R.id.ll_no_thanks);

		firstOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchGooglePlay(activity);
				mAppRateDialog.dismiss();

			}
		});
		secondOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAppRateDialog.dismiss();

			}
		});
		thirdOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PrefHelper.setBoolean(BaseApplication.getAppContext()
						.getResources().getString(R.string.KEY_NO_THANKS_FLAG),
						false);
				mAppRateDialog.dismiss();

			}
		});
		mAppRateDialog.show();
	}

	/**
	 * Launch google play application for rating
	 * 
	 * @param activity
	 */
	public static void launchGooglePlay(Activity activity) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="
					+ activity.getApplicationContext().getPackageName()));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException e) {

		}

	}
}
