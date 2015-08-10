package com.stockholmapplab.recipes;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.R;

/**
 * This fragment used to set duration of health diet. duration will be any of 1
 * week, 2 weeks, 5 weeks, 8 weeks, 10 weeks, 20 weeks, forever
 * 
 */
public class Step1Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	private View mView;
	private CustomTextView mTxtWeeks;
	private SeekBar mSeekbar;
	private String[] diet_duration;

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
		diet_duration = ResourceUtil.getStringArray(R.array.diet_duration);
		mView = inflater.inflate(R.layout.raw_help_guide_1, container, false);

		mTxtWeeks = GenericView.findViewById(mView, R.id.tv_txtWeeks);
		mSeekbar = GenericView.findViewById(mView, R.id.sk_seekBar);
		mTxtWeeks.setText(diet_duration[BaseConstant.mDietCycle
				.getDiet_duration()]);
		mSeekbar.setProgress(BaseConstant.mDietCycle.getDiet_duration() * 15);
		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				BaseConstant.mDietCycle.setDiet_duration((int) (mSeekbar
						.getProgress() / 15));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int index = (int) (progress / 15);
				mTxtWeeks.setText(diet_duration[index]);
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
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		try {
			boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(
					StepByStepHelpGuideActivity.activity, BaseConstant.STEP_1_FRAGMENT_ID);
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

				if (PrefHelper.getBoolean(
						getString(R.string.KEY_IS_FRAGMENT_1), false)) {
					mViews.addView(new ShowcaseViews.ItemViewProperties(
							R.id.sk_seekBar, R.string.str_start_message_4,
							R.string.showcase_image_message,
							SHOWCASE_KITTEN_SCALE,
							BaseConstant.STEP_1_FRAGMENT_ID));
				}
				mViews.show();

			}
		} catch (Exception e) {
		}
	}
}