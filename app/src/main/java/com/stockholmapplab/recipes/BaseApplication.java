package com.stockholmapplab.recipes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.stockholmapplab.recipes.util.PrefHelper;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Arrays;
import java.util.Locale;

@ReportsCrashes(formKey = "", mailTo = "sheyko.dmitriy@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash)
public class BaseApplication extends Application {

	private static Context mContext;
	public static Session session = null;

	public static Context getAppContext() {
		return mContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//ACRA.init(this);
		mContext = getApplicationContext();

		session = Session.getActiveSession();
	}

	public static Session openActiveSession(Activity activity,
			boolean allowLoginUI, StatusCallback callback) {
		OpenRequest openRequest = new OpenRequest(activity)
				.setCallback(callback);
		session = new Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())
				|| allowLoginUI) {
			Session.setActiveSession(session);
			openRequest.setPermissions(Arrays.asList("email"));
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	// Check if current user is administrator
	public static boolean isAdmin() {
		if (PrefHelper.getInt("UserId", -1) == 1519
				|| PrefHelper.getInt("UserId", -1) == 1520) {
			return true;
		} else {
			return false;
		}
	}

	// Change alpha of a view from 30% to 100%
	public static void fadeIn(View v) {
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1f);
		aa.setDuration(300);
		aa.setFillAfter(true);
		v.startAnimation(aa);
		v.setEnabled(true);
	}

	// Change alpha of a view from 100% to 30%
	public static void fadeOut(View v) {
		AlphaAnimation aa = new AlphaAnimation(1f, 0.3f);
		aa.setDuration(300);
		aa.setFillAfter(true);
		v.startAnimation(aa);
		v.setEnabled(false);
	}

	// Check if default device language is swedish
	public static boolean isSwedish() {
		if (Locale.getDefault().getISO3Language().equals("swe")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void lockOrientation(Activity activity){
		DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		float scaleFactor = metrics.density;
		float widthDp = widthPixels / scaleFactor;
		float heightDp = heightPixels / scaleFactor;
		float smallestWidth = Math.min(widthDp, heightDp);

		if (smallestWidth < 720) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

}
