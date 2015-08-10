package com.stockholmapplab.recipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * About Activity is use for show information regarding to Health DIET. In this
 * class mainly two functionality 1) Visit US. In which redirect to
 * StockholmAppLab FaceBook page. 2) Contact US functionality.
 */
public class AboutActivity extends ActionBarActivity {

	private static final String visitUsURL = "http://www.facebook.com/StockholmAppLab";

	// Hint Dialog related params
	private boolean showDemo = true;
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_about);
		initActionBar();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_about));
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
	 * init() method is used for show hint dialog after 500 millisecond if
	 * require.
	 */
	public void init() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
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
							AboutActivity.this,
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
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		Utility.showFastingAndNonFastingDays(AboutActivity.this);
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
	protected void onStop() {

		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.ABOUT_ACTIVITY_ID);
		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(getApplicationContext().getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
			mOptions.block = false;
			mOptions.hideOnClickOutside = false;
		}
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(AboutActivity.this);
		}
	};
}
