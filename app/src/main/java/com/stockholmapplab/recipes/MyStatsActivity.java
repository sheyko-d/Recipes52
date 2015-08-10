package com.stockholmapplab.recipes;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Toast;

import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.ConnectionUtility;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;

import java.util.Calendar;

/**
 * MyStatsActivity class display information of diet cycle.
 */
public class MyStatsActivity extends ActionBarActivity {

	CustomTextView mTxtDurationInNumber, mTxtTimeLeftInNumber,
			mTxtIntensityInNumber, mTxtFastingDaysInNumber,
			mTxtFastingDaysMissedInNumber, mTxtPointsInNumber,
			mTxtAchievementsPointsInNumber, mTxtCurrentBodyStats,
			mTxtDietCycleHistory, mTxtAchievements, mTxtShare, mTxtBadge;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_my_stats);
		initActionBar();
		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_myStats));
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
		mTxtDurationInNumber = GenericView.findViewById(this,
				R.id.tv_txtDurationInNumber);
		mTxtTimeLeftInNumber = GenericView.findViewById(this,
				R.id.tv_txtTimeLeftInNumber);

		mTxtIntensityInNumber = GenericView.findViewById(this,
				R.id.tv_txtIntensityInNumber);

		mTxtFastingDaysInNumber = GenericView.findViewById(this,
				R.id.tv_txtFastingDaysInNumber);

		mTxtFastingDaysMissedInNumber = GenericView.findViewById(this,
				R.id.tv_txtFastingDaysMissedInNumber);

		mTxtPointsInNumber = GenericView.findViewById(this,
				R.id.tv_txtPointsInNumber);

		mTxtAchievementsPointsInNumber = GenericView.findViewById(this,
				R.id.tv_txtAchievementsPointsInNumber);
		mTxtCurrentBodyStats = GenericView.findViewById(this,
				R.id.tv_txtCurrent_Body_stats);
		mTxtDietCycleHistory = GenericView.findViewById(this,
				R.id.tv_txtDiet_Cycle_History);
		mTxtAchievements = GenericView.findViewById(this,
				R.id.tv_txtAchievements);
		mTxtShare = GenericView.findViewById(this, R.id.tv_txtShare);

		mTxtBadge = GenericView.findViewById(this, R.id.tv_txt_badge);
		mTxtDurationInNumber.setText(getDuration());

		mTxtTimeLeftInNumber.setText(getTimeLeft());

		mTxtIntensityInNumber.setText(getIntensity());

		mTxtFastingDaysInNumber.setText(""
				+ BaseConstant.mDietCycle.getFastingDays());

		mTxtFastingDaysMissedInNumber.setText(""
				+ BaseConstant.mDietCycle.getFastingDaysMissed());

		mTxtPointsInNumber.setText(""
				+ BaseConstant.mDietCycle.getmConsumeFoodPoints());

		mTxtAchievementsPointsInNumber.setText("" + getAchievementPoints());
		// show hint dialog after 500 milliseconds.
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showHintDialogs();
			}
		}, 500);
		if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			mTxtCurrentBodyStats.setEnabled(false);
			mTxtCurrentBodyStats.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_weight_graph_blur, 0, 0);
			mTxtCurrentBodyStats.setTextColor(ResourceUtil
					.getColor(R.color.blur_color));
		} else {
			mTxtCurrentBodyStats.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ic_weight_graph, 0, 0);
		}
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.tv_txtCurrent_Body_stats:

			if (mViews != null) {
				mViews.hide();
				mViews = null;
			}

			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				PrefHelper.setBoolean(
						getResources().getString(R.string.KEY_BADGE_SHOW_FLAG),
						false);
				Calendar endTimeCalendar = Calendar.getInstance();
				endTimeCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endTimeCalendar.set(Calendar.MINUTE, 59);
				endTimeCalendar.set(Calendar.SECOND, 59);
				long endTime = endTimeCalendar.getTimeInMillis();

				Intent intent = new Intent(MyStatsActivity.this,
						BodyStatsActivity.class);
				intent.putExtra(BodyStatsActivity.extraStartTime,
						BaseConstant.mDietCycle.getStartTime());
				intent.putExtra(BodyStatsActivity.extraEndTime, endTime);

				intent.putExtra(BodyStatsActivity.extraGraph,
						BaseConstant.mDietCycle.getmGraphs());
				intent.putExtra(BodyStatsActivity.extraUsedCal,
						BaseConstant.mDietCycle.getUsedCal().toString());
				intent.putExtra(BodyStatsActivity.extraFromHistory, false);
				startActivity(intent);
			}
			break;
		case R.id.tv_txtDiet_Cycle_History:
			if (mViews != null) {
				mViews.hide();
				mViews = null;
			}
			startActivity(new Intent(MyStatsActivity.this,
					DietCycleHistoryActivity.class));
			break;
		case R.id.tv_txtAchievements:
			startActivity(new Intent(MyStatsActivity.this,
					AchievementsActivity.class));
			break;
		case R.id.tv_txtShare:
			findViewById(R.id.tv_txtShare).setEnabled(false);
			if (ConnectionUtility.isNetAvailable()) {
				if (!isLoggedIn()) {
					connectToFacebook();
				} else {
					FacebookPost();
				}
			} else {
				findViewById(R.id.tv_txtShare).setEnabled(true);
				Toast.makeText(
						MyStatsActivity.this,
						ResourceUtil
								.getString(R.string.str_internet_connection),
						Toast.LENGTH_LONG).show();
			}
			break;
		default:
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
		Utility.showFastingAndNonFastingDays(MyStatsActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(MyStatsActivity.this);
		if (Utility.isDietCycleFinish()) {
			finish();
		} else {
		}
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()
				&& PrefHelper.getBoolean(
						getResources().getString(R.string.KEY_BADGE_SHOW_FLAG),
						true)) {
			mTxtBadge.setVisibility(View.VISIBLE);
		} else {
			mTxtBadge.setVisibility(View.INVISIBLE);
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
	 * getDuration() method return time in string format from diet cycle start
	 * time to current time.
	 * 
	 * @return String
	 */
	private String getDuration() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			Calendar currentDate = Calendar.getInstance();
			Calendar startDate = Calendar.getInstance();
			startDate.setTimeInMillis(BaseConstant.mDietCycle.getStartTime());
			long diffMilli = currentDate.getTimeInMillis()
					- startDate.getTimeInMillis();
			long copydiffMilli = diffMilli;
			diffMilli /= 1000;

			diffMilli /= 60;
			int minutes = (int) (diffMilli % 60);
			diffMilli /= 60;
			int hours = (int) (diffMilli % 24);
			diffMilli /= 24;
			int day = (int) (diffMilli);
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
			return "none";
		}
	}

	/**
	 * getTimeLeft() method return time difference in string format between
	 * current time to diet cycle finish time.
	 * 
	 * @return
	 */
	private String getTimeLeft() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {

			if (BaseConstant.mDietCycle.getDiet_duration() <= 5) {
				int totalDays = Utility.getWeek(BaseConstant.mDietCycle
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
				return "none";
			}
		} else {
			return "none";
		}
	}

	/**
	 * getIntensity() method return diet cycle intensity.
	 * 
	 * @return String
	 */
	private String getIntensity() {

		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			String str = "";
			switch (BaseConstant.mDietCycle.getFasting_intensity()) {
			case BaseConstant.FIVE_TWO_INTENSITY:
				str = "5:2";
				break;
			case BaseConstant.FOUR_THREE_INTENSITY:
				str = "4:3";
				break;
			case BaseConstant.SIX_ONE_INTENSITY:
				str = "6:1";
				break;
			}
			return str;
		} else {
			return "none";
		}
	}

	/**
	 * getAchievementPoints() method return total achievement points.
	 * 
	 * @return
	 */
	private int getAchievementPoints() {
		int points = 0;
		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_DIET_CYCLE_COMPLETED),
				false)) {
			points = points + 50;
		}
		if (BaseConstant.mDietCycle.getFastingDays() >= 1) {
			points = points + 20;
		}
		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_CHECK_ABOUT_US_OPTIONS),
				false)) {
			points = points + 30;
		}
		if (PrefHelper.getBoolean(
				ResourceUtil.getString(R.string.KEY_7_DAY_COMPLETED), false)) {
			points = points + 20;
		}
		return points;
	}

	/**
	 * Facebook Share Function to make login to facebook
	 */
	private void connectToFacebook() {
		Intent intent = new Intent(MyStatsActivity.this,
				FacebookLoginActivity.class);
		startActivityForResult(intent,
				FacebookLoginActivity.FACEBOOK_MY_STAT_REQUEST_CODE);
		overridePendingTransition(0, 0);
	}

	/**
	 * Function to check user is LoggedIn or not
	 * 
	 * @return boolean true/false
	 */
	public static boolean isLoggedIn() {
		/*Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			return true;
		} else {
			return false;
		}*/
		return true;//TODO:REMOVE
	}

	/**
	 * FaceBook Post Function
	 */

	public void FacebookPost() {

		/*String message = getString(R.string.str_facebook_share_message_1)
				+ "\n" + getString(R.string.str_myStats) + "\n"
				+ getString(R.string.str_duration) + ":"
				+ mTxtDurationInNumber.getText().toString() + "\n"
				+ getString(R.string.str_time_left) + ":"
				+ mTxtTimeLeftInNumber.getText().toString() + "\n"
				+ getString(R.string.str_intensity_1) + ":"
				+ mTxtIntensityInNumber.getText().toString() + "\n"
				+ getString(R.string.str_fasting_days_1) + ":"
				+ mTxtFastingDaysInNumber.getText().toString() + "\n"
				+ getString(R.string.str_fasting_days_missed) + ":"
				+ mTxtFastingDaysMissedInNumber.getText().toString() + "\n"
				+ getString(R.string.str_points) + ":"
				+ mTxtPointsInNumber.getText().toString() + "\n"
				+ getString(R.string.str_achievement_points) + ":"
				+ mTxtAchievementsPointsInNumber.getText().toString() + "\n\n"
				+ getString(R.string.str_facebook_share_message_4) + " "
				+ AppRater.getPlayStoreURL(MyStatsActivity.this);

		byte[] data = null;
		Bundle params = new Bundle();

		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_facebook_icon);

		if (bitmap != null) {
			progressDialog = new ProgressDialog(MyStatsActivity.this);
			progressDialog.setMessage(getString(R.string.str_wait));
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
					findViewById(R.id.tv_txtShare).setEnabled(true);
					if (response.toString().contains("200")) {
						Toast.makeText(MyStatsActivity.this,
								"Shared Successfully", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(MyStatsActivity.this, "Shared Fail",
								Toast.LENGTH_LONG).show();
					}
				}
			};
			Request request = new Request(Session.getActiveSession(),
					"me/photos", params, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		} else {
			findViewById(R.id.tv_txtShare).setEnabled(true);
		}*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case FacebookLoginActivity.FACEBOOK_MY_STAT_REQUEST_CODE:
			findViewById(R.id.tv_txtShare).setEnabled(true);
			if (requestCode == FacebookLoginActivity.FACEBOOK_MY_STAT_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					FacebookPost();
				}
			}

			break;
		default:
			break;
		}
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(this,
				BaseConstant.MYSTATS_ACTIVITY_ID);
		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(getApplicationContext().getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
			mOptions.block = false;
			mOptions.hideOnClickOutside = false;
			mViews = new ShowcaseViews(this, R.layout.showcase_view_template,
					new ShowcaseViews.OnShowcaseAcknowledged() {
						@Override
						public void onShowCaseAcknowledged(
								ShowcaseView showcaseView) {

						}
					});

			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_MY_STATS_CURRENT_BODY_STAT),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.tv_txtDiet_Cycle_History,
						R.string.str_start_message_11, 0,
						SHOWCASE_KITTEN_SCALE, BaseConstant.MYSTATS_ACTIVITY_ID));
			}
			if (PrefHelper.getBoolean(
					getString(R.string.KEY_IS_MY_STATS_DIET_CYCLE_HISTORY),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.tv_txtCurrent_Body_stats,
						R.string.str_start_message_10, 1,
						SHOWCASE_KITTEN_SCALE, BaseConstant.MYSTATS_ACTIVITY_ID));
			}
			mViews.show();
		}
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(MyStatsActivity.this);
		}
	};

	public void back(View v) {
		finish();
	}
}
