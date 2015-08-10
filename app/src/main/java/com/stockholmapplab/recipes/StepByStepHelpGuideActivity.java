package com.stockholmapplab.recipes;

import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.stockholmapplab.recipes.Step5Fragment.OnChangePagerListener;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * 
 * This is the fragment activity which manages multiple diet cycle using step by
 * step fragments.
 * 
 */
public class StepByStepHelpGuideActivity extends ActionBarActivity implements
		OnChangePagerListener {
	private android.support.v4.view.PagerAdapter mPagerAdapter;
	private ViewPager pager;
	private final int MIN_DISTANCE = 50;
	private float downX, upX;
	private ImageView mIvScale1;
	private Step1Fragment step1Fragment;
	private Step2Fragment step2Fragment;
	private Step3Fragment step3Fragment;
	private Step4Fragment step4Fragment;
	private Step5Fragment step5Fragment;
	private View mBackButton, mNextButton;
	private int mWidth;
	private View mScaleView;
	private float mMarginLeft;
	public static Activity activity;
	private final static float CM_PERCENT = 12f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_step_by_step_guide);
		activity = this;
		initActionBar();
		initialisePaging();

		Display display = getWindowManager().getDefaultDisplay();
		mWidth = display.getWidth();

		mScaleView = findViewById(R.id.iv_scale_1);

		mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT;
		updateRulerMargin();
	}

	private void updateRulerMargin() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mScaleView
				.getLayoutParams();
		layoutParams.leftMargin = (int) mMarginLeft;
		mScaleView.setLayoutParams(layoutParams);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_help_guide));
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

	@Override
	protected void onStop() {
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	public void back(View v) {
		finish();
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

	/**
	 * Initialise the fragments to be paged
	 */
	private void initialisePaging() {

		mIvScale1 = GenericView.findViewById(this, R.id.iv_scale_1);
		mIvScale1.setImageResource(R.drawable.one_number);
		mBackButton = GenericView.findViewById(this, R.id.bt_btnLeftArrow);
		mNextButton = GenericView.findViewById(this, R.id.bt_btnRightArrow);

		List<android.support.v4.app.Fragment> fragments = new Vector<android.support.v4.app.Fragment>();
		step1Fragment = new Step1Fragment();
		step2Fragment = new Step2Fragment();
		step3Fragment = new Step3Fragment();
		step4Fragment = new Step4Fragment();
		step5Fragment = new Step5Fragment();
		((Step5Fragment) step5Fragment).setOnChangePagerListener(this);

		fragments.add(step1Fragment);
		fragments.add(step2Fragment);
		fragments.add(step3Fragment);
		fragments.add(step4Fragment);
		fragments.add(step5Fragment);

		this.mPagerAdapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(this.mPagerAdapter);
		pager.setOnPageChangeListener(PageChangeListener);
		pager.setOnTouchListener(mTouchListener);

		step1Fragment.callHelpDialog();
	}

	private View.OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				downX = event.getX();
				return true;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				upX = event.getX();

				float deltaX = downX - upX;
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
					if (deltaX < 0) {

						int index = pager.getCurrentItem();
						if (index >= 1) {
							pager.setCurrentItem(--index);
						}
					}
					if (deltaX > 0) {
						int index = pager.getCurrentItem();
						if (index < 4) {
							pager.setCurrentItem(++index);
						}
					}
				}
			}
			return true;
		}
	};
	private ViewPager.OnPageChangeListener PageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {

			switch (arg0) {
			case 0:
				mIvScale1.setImageResource(R.drawable.one_number);
				mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT;
				updateRulerMargin();
				step1Fragment.callHelpDialog();
				break;
			case 1:
				mIvScale1.setImageResource(R.drawable.two_number);
				mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT * 2;
				updateRulerMargin();
				step2Fragment.callHelpDialog();
				break;
			case 2:
				mIvScale1.setImageResource(R.drawable.three_number);
				mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT * 3;
				updateRulerMargin();
				step3Fragment.callHelpDialog();
				break;
			case 3:
				mIvScale1.setImageResource(R.drawable.four_number);
				mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT * 4;
				updateRulerMargin();
				step4Fragment.callHelpDialog();
				break;
			case 4:
				mIvScale1.setImageResource(R.drawable.five_number);
				mMarginLeft = mWidth / 2 - mWidth / CM_PERCENT * 5;
				updateRulerMargin();
				step5Fragment.callHelpDialog();
				break;
			}

			if (arg0 == 0) {
				mBackButton.setVisibility(View.GONE);
				mNextButton.setVisibility(View.VISIBLE);
			} else if (arg0 == 4) {
				mBackButton.setVisibility(View.VISIBLE);
				mNextButton.setVisibility(View.GONE);
			} else if (arg0 >= 1 && arg0 <= 3) {
				mBackButton.setVisibility(View.VISIBLE);
				mNextButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	public class PagerAdapter extends FragmentStatePagerAdapter {
		private List<android.support.v4.app.Fragment> fragments;
		private android.support.v4.app.FragmentManager fragmentManager = null;

		/**
		 * @param fragmentManager2
		 * @param fragments
		 */
		public PagerAdapter(
				android.support.v4.app.FragmentManager fragmentManager2,
				List<android.support.v4.app.Fragment> fragments) {
			super(fragmentManager2);
			this.fragmentManager = fragmentManager2;
			this.fragments = fragments;

		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.fragments.size();
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, 0, object);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			fragmentManager.executePendingTransactions();
			try {
				fragmentManager.saveFragmentInstanceState(fragments
						.get(position));
			} catch (Exception e) {
			}
		}

		public void replaceItem(int position,
				android.support.v4.app.Fragment fragment) {
			fragments.set(position, fragment);
			this.notifyDataSetChanged();
		}
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.bt_btnLeftArrow:
			if (pager.getCurrentItem() >= 1) {
				pager.setCurrentItem(pager.getCurrentItem() - 1);
			}
			break;
		case R.id.bt_btnRightArrow:

			if (pager.getCurrentItem() <= 3) {
				pager.setCurrentItem(pager.getCurrentItem() + 1);
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onChangePager(int index) {

		pager.setCurrentItem(index);
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(StepByStepHelpGuideActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(StepByStepHelpGuideActivity.this);
		}
	};
}
