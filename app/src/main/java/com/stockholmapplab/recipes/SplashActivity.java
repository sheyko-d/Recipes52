package com.stockholmapplab.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import com.facebook.Session;
import com.stockholmapplab.recipes.AppRater.AppRater;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

/**
 * 
 * Splash screen will appear when app launch.It remains for 1 second and move to
 * the Home Acivity.
 * 
 */
public class SplashActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);

		setContentView(R.layout.ac_splash);
		BaseConstant.mDietCycle = DietCycle.getDietCycle();
		AppRater.incrementNumberOfTimeAppLaunch();

		if (!PrefHelper.getBoolean(
				getString(R.string.KEY_IS_APP_LAUNCH_FIRST_TIME), true)) {
			AppRater.incrementNumberOfTimeDialogLaunch();
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// Check if user is already authorized
				if (PrefHelper.getInt("UserId", -1) == -1) {
					// User isn't authorized yet, launch login screen
					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
				} else {
					// User is already authorized, launch home screen
					startActivity(new Intent(SplashActivity.this,
							HomeActivity.class));
				}
				finish();
			}
		}, 1000);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

		if (Session.getActiveSession().isOpened()) {
			startActivity(new Intent(SplashActivity.this, HomeActivity.class));
			finish();
		}
	}
}