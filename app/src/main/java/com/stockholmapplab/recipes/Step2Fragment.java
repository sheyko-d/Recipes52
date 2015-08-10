package com.stockholmapplab.recipes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.CalendarView;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

/**
 * This fragment used to set fasting intensity. Intensity will be any of 5:2 (5-
 * Non Fasting days,2- Fasting days), 4:3 (4- Non Fasting days,3- Fasting days),
 * 6:1 (6- Non Fasting days,1- Fasting day)
 * 
 * According to above fasting intensity set fasting days. During selection of
 * fasting days make sure you cannot select fasting in a sequence manner.
 * 
 */

public class Step2Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	private View mView;
	private ImageView mImg52, mImg43, mImg61;
	private LinearLayout mLinear52, mLinear43, mLinear61;
	private CalendarView mCalendarView;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		mView = inflater.inflate(R.layout.raw_help_guide_2, container, false);
		mCalendarView = GenericView.findViewById(mView, R.id.calendarView);

		mLinear52 = GenericView.findViewById(mView, R.id.ll_linear11);
		mLinear43 = GenericView.findViewById(mView, R.id.ll_linear12);
		mLinear61 = GenericView.findViewById(mView, R.id.ll_linear13);

		mImg52 = GenericView.findViewById(mView, R.id.iv_img52);
		mImg43 = GenericView.findViewById(mView, R.id.iv_img43);
		mImg61 = GenericView.findViewById(mView, R.id.iv_img61);

		switch (BaseConstant.mDietCycle.getFasting_intensity()) {
		case BaseConstant.FIVE_TWO_INTENSITY:
			mImg52.setImageResource(R.drawable.ic_selected_tik);
			break;

		case BaseConstant.FOUR_THREE_INTENSITY:
			mImg43.setImageResource(R.drawable.ic_selected_tik);
			break;
		case BaseConstant.SIX_ONE_INTENSITY:
			mImg61.setImageResource(R.drawable.ic_selected_tik);
			break;
		}

		mLinear52.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mImg52.setImageResource(R.drawable.ic_selected_tik);
				mImg43.setImageResource(R.drawable.ic_unselected_tik);
				mImg61.setImageResource(R.drawable.ic_unselected_tik);

				BaseConstant.mDietCycle
						.setFasting_intensity(BaseConstant.FIVE_TWO_INTENSITY);
				BaseConstant.mDietCycle.getSelected_fasting_days().clear();
				mCalendarView.invalidate();

			}
		});
		mLinear43.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mImg52.setImageResource(R.drawable.ic_unselected_tik);
				mImg43.setImageResource(R.drawable.ic_selected_tik);
				mImg61.setImageResource(R.drawable.ic_unselected_tik);

				BaseConstant.mDietCycle
						.setFasting_intensity(BaseConstant.FOUR_THREE_INTENSITY);
				BaseConstant.mDietCycle.getSelected_fasting_days().clear();
				mCalendarView.invalidate();

			}
		});
		mLinear61.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mImg52.setImageResource(R.drawable.ic_unselected_tik);
				mImg43.setImageResource(R.drawable.ic_unselected_tik);
				mImg61.setImageResource(R.drawable.ic_selected_tik);

				BaseConstant.mDietCycle
						.setFasting_intensity(BaseConstant.SIX_ONE_INTENSITY);
				BaseConstant.mDietCycle.getSelected_fasting_days().clear();
				mCalendarView.invalidate();
			}
		});
		return mView;
	}

	/**
	 * show hint dialog after 500 millisecond if require.
	 */
	public void callHelpDialog() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Add where you want to open the hint
				showHintDialogs();
			}
		}, 500);
	}

	@Override
	public void onResume() {
		super.onResume();
		mCalendarView = GenericView.findViewById(mView, R.id.calendarView);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(
				StepByStepHelpGuideActivity.activity,
				BaseConstant.STEP_2_FRAGMENT_ID);
		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(StepByStepHelpGuideActivity.activity
						.getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
			mOptions.block = false;
			mOptions.hideOnClickOutside = false;
			mViews = new ShowcaseViews(StepByStepHelpGuideActivity.activity,
					R.layout.showcase_view_template,
					new ShowcaseViews.OnShowcaseAcknowledged() {
						@Override
						public void onShowCaseAcknowledged(
								ShowcaseView showcaseView) {
						}
					});
			if (PrefHelper.getBoolean(getString(R.string.KEY_IS_FRAGMENT_2),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_linear1, R.string.str_start_message_9,
						R.string.showcase_image_message, SHOWCASE_KITTEN_SCALE,
						BaseConstant.STEP_2_FRAGMENT_ID));
			}
			mViews.show();
		}
	}
}