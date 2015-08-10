package com.stockholmapplab.recipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.verticalprogressbar.VerticalProgressBar;
import com.stockholmapplab.recipes.R;

public class AchievementsActivity extends ActionBarActivity {

	private CustomTextView mTxtpoints1, mTxtpoints2, mTxtpoints3, mTxtpoints4;
	private VerticalProgressBar mVerticleProgressBar1, mVerticleProgressBar2,
			mVerticleProgressBar3, mVerticleProgressBar4;
	public static final int VISITED_DIALOG = 1, COMPLETE_DIET_CYCLE_DIALOG = 2,
			FASTING_DAY_DIALOG = 3;
	private RelativeLayout mRelative1, mRelative2, mRelative3, mRelative4;
	private AlphaAnimation alphaAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_achievements);

		BaseApplication.lockOrientation(this);

		// alphaAnimation is use for show 50% down opacity of view
		alphaAnimation = new AlphaAnimation(0.5f, 0.5f);
		alphaAnimation.setStartTime(0);
		alphaAnimation.setDuration(0);
		alphaAnimation.setFillAfter(true);

		initActionBar();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_achievements_title));
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

		mTxtpoints1 = GenericView.findViewById(this, R.id.tv_txt_points1);
		mTxtpoints2 = GenericView.findViewById(this, R.id.tv_txt_point2);
		mTxtpoints3 = GenericView.findViewById(this, R.id.tv_txt_point3);
		mTxtpoints4 = GenericView.findViewById(this, R.id.tv_txt_point4);

		mVerticleProgressBar1 = GenericView.findViewById(this,
				R.id.pb_verticalProgressBar1);
		mVerticleProgressBar2 = GenericView.findViewById(this,
				R.id.pb_verticalProgressBar2);
		mVerticleProgressBar3 = GenericView.findViewById(this,
				R.id.pb_verticalProgressBar3);
		mVerticleProgressBar4 = GenericView.findViewById(this,
				R.id.pb_verticalProgressBar4);

		mRelative1 = GenericView.findViewById(this,
				R.id.rl_relativeAchievements2);
		mRelative2 = GenericView.findViewById(this,
				R.id.rl_relativeAchievements4);
		mRelative3 = GenericView.findViewById(this,
				R.id.rl_relativeAchievements6);
		mRelative4 = GenericView.findViewById(this,
				R.id.rl_relativeAchievements8);

		mVerticleProgressBar1.setOnTouchListener(mTouch);
		mVerticleProgressBar2.setOnTouchListener(mTouch);
		mVerticleProgressBar3.setOnTouchListener(mTouch);
		mVerticleProgressBar4.setOnTouchListener(mTouch);

		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_CHECK_ABOUT_US_OPTIONS),
				false)) {
			mTxtpoints1.setText(getString(R.string.str_points) + "30" + "\t"
					+ getString(R.string.str_complete) + "100%");
			mVerticleProgressBar1.setProgress(100);

		} else {
			mTxtpoints1.setText(getString(R.string.str_points) + "30" + "\t"
					+ getString(R.string.str_complete) + "0%");
			mRelative1.setAnimation(alphaAnimation);
			mRelative1.postInvalidate();
		}
		if (BaseConstant.mDietCycle.getFastingDays() >= 1) {
			mTxtpoints2.setText(getString(R.string.str_points) + "20" + "\t"
					+ getString(R.string.str_complete) + "100%");
			mVerticleProgressBar2.setProgress(100);
		} else {
			mTxtpoints2.setText(getString(R.string.str_points) + "20" + "\t"
					+ getString(R.string.str_complete) + "0%");
			mRelative2.setAnimation(alphaAnimation);
			mRelative2.postInvalidate();
		}

		int numberOfTimeAppOpen = Utility.getHowManyTimeAppContinouslyOpen();
		mVerticleProgressBar3.setProgress(numberOfTimeAppOpen);
		mTxtpoints3.setText(getString(R.string.str_points) + "20" + "\t"
				+ getString(R.string.str_complete) + numberOfTimeAppOpen + "%");

		if (numberOfTimeAppOpen != 100) {
			mRelative3.setAnimation(alphaAnimation);
			mRelative3.postInvalidate();
		}
		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_DIET_CYCLE_COMPLETED),
				false)) {

			mTxtpoints4.setText(getString(R.string.str_points) + "50" + "\t"
					+ getString(R.string.str_complete) + "100%");
			mVerticleProgressBar4.setProgress(100);

		} else {
			mTxtpoints4.setText(getString(R.string.str_points) + "50" + "\t"
					+ getString(R.string.str_complete) + "0%");
			mRelative4.setAnimation(alphaAnimation);
			mRelative4.postInvalidate();
		}
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
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(AchievementsActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
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

	private View.OnTouchListener mTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return true;
		}
	};
	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(AchievementsActivity.this);
		}
	};

}
