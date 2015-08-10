package com.stockholmapplab.recipes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.wheel.widget.adapters.ArrayWheelAdapter;
import com.stockholmapplab.recipes.widget.OnWheelClickedListener;
import com.stockholmapplab.recipes.widget.OnWheelScrollListener;
import com.stockholmapplab.recipes.widget.WheelView;
import com.stockholmapplab.recipes.R;


/**
 * This fragment used to select calory consumption obn fasting days.
 * 
 * You can select calory between O to 2500.
 *
 */
public class Step3Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	private View mView;
	private CustomButton mBtnCalorie;
	private static Dialog mDialog;
	private WheelView number;
	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;
	private final String calories[] = { "0", "100", "200", "300", "400", "500",
			"600", "700", "800", "900", "1000", "1100", "1200", "1300", "1400",
			"1500", "1600", "1700", "1800", "1900", "2000", "2100", "2200",
			"2300", "2400", "2500" };

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.d
			return null;
		}
		mView = inflater.inflate(R.layout.raw_help_guide_3, container, false);

		mBtnCalorie = GenericView.findViewById(mView, R.id.bt_btnCalorie);
		mBtnCalorie.setText(""
				+ BaseConstant.mDietCycle.getCalories_consumption());

		mBtnCalorie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNumberPicker();
			}
		});
		// Dialog
		mDialog = new Dialog(StepByStepHelpGuideActivity.activity, R.style.CustomDialogTheme);
		mDialog.setContentView(R.layout.raw_number_picker);
		mDialog.setCanceledOnTouchOutside(true);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		number = (WheelView) mDialog.findViewById(R.id.number);

		ArrayWheelAdapter<String> caloryAdapter = new ArrayWheelAdapter<String>(
				StepByStepHelpGuideActivity.activity, calories);
		caloryAdapter.setItemResource(R.layout.wheel_text_item);
		caloryAdapter.setItemTextResource(R.id.text);
		number.setViewAdapter(caloryAdapter);
		number.setCurrentItem(getIndex(mBtnCalorie.getText().toString()));
		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		number.addClickingListener(click);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				mBtnCalorie.setText(calories[number.getCurrentItem()]);
			}

			public void onScrollingFinished(WheelView wheel) {
				mBtnCalorie.setText(calories[number.getCurrentItem()]);
				BaseConstant.mDietCycle.setCalories_consumption(Integer
						.parseInt(mBtnCalorie.getText().toString()));
			}
		};
		number.addScrollingListener(scrollListener);
		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
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

	public void showNumberPicker() {
		mDialog.show();
	}

	/**
	 * get index of calory value.
	 * 
	 * @param value
	 * @return - index
	 */
	private int getIndex(String value) {
		int index = -1;
		for (int i = 0; i < calories.length; i++) {
			if (calories[i].equalsIgnoreCase(value)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(StepByStepHelpGuideActivity.activity,
				BaseConstant.STEP_3_FRAGMENT_ID);
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

			if (PrefHelper.getBoolean(getString(R.string.KEY_IS_FRAGMENT_3),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_help_guide_3, R.string.str_start_message_6,
						R.string.showcase_image_message, SHOWCASE_KITTEN_SCALE,
						BaseConstant.STEP_3_FRAGMENT_ID));
			}
			mViews.show();
		}
	}
}