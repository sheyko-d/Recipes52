package com.stockholmapplab.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

/**
 * This fragment used to get preffered item from calory menu during the fasting
 * days.
 * 
 */
public class Step4Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	private View mView;
	private CustomButton mBtnCalorieMenu;

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
		mView = inflater.inflate(R.layout.raw_help_guide_4, container, false);
		mBtnCalorieMenu = GenericView.findViewById(mView,
				R.id.bt_btnCalorieMenu);
		mBtnCalorieMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(StepByStepHelpGuideActivity.activity,
						CalorieMenuActivity.class);
				startActivity(mIntent);
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
				showHintDialogs();
			}
		}, 500);
	}

	/**
	 * Displays demo if never show again has never been checked by the user.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(StepByStepHelpGuideActivity.activity,
				BaseConstant.STEP_4_FRAGMENT_ID);
		if (!neverShowDemoAgain
				&& showDemo
				&& PrefHelper.getBoolean(StepByStepHelpGuideActivity.activity.getResources()
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
			if (PrefHelper.getBoolean(getString(R.string.KEY_IS_FRAGMENT_4),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_help_guide_4, R.string.str_start_message_7,
						R.string.showcase_image_message, SHOWCASE_KITTEN_SCALE,
						BaseConstant.STEP_4_FRAGMENT_ID));
			}

			mViews.show();
		}
	}
}