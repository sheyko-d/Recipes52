package com.stockholmapplab.recipes;

import java.util.Calendar;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.Days;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.showcaseview.ShowcaseView;
import com.stockholmapplab.recipes.showcaseview.ShowcaseViews;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.LayoutUtils;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * This fragment used to start diet cycle based on selected fasting days.
 * 
 */
public class Step5Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	private View mView;
	private Button mBtnStart;
	private Dialog mNextNowDayDialog, mValidationDialog;
	private OnChangePagerListener mOnChangePagerListener;
	private int index = -1;

	// Hint Dialog related params
	private static final float SHOWCASE_KITTEN_SCALE = 2f;
	private boolean showDemo = true;
	private ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
	private ShowcaseViews mViews;

	/**
	 * Represents a listener that will be use for validation
	 */
	public interface OnChangePagerListener {
		/**
		 * Called when some condition not fulfill
		 * 
		 * @param index
		 * 
		 */
		public void onChangePager(int index);
	}

	public void setOnChangePagerListener(
			OnChangePagerListener onChangePagerListener) {
		mOnChangePagerListener = onChangePagerListener;
	}

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

		mView = inflater.inflate(R.layout.raw_help_guide_5, container, false);
		mBtnStart = GenericView.findViewById(mView, R.id.bt_btnStart);

		/**
		 * chooseDietStartDay() : Diet Starting Day Selection Dialog
		 * validationDialogs(true) : If true : Calory Validations dialog If
		 * False: Fasting Intensity Selection Dialog
		 */
		mBtnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkValidation();
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

	@SuppressWarnings("static-access")
	private void checkValidation() {
		if (checkCalendarValidation()) {
			index = 1;
			showValidationDialog(false);
		} else {
			if (checkCaloriesValidation()) {
				index = 2;
				showValidationDialog(true);
			} else {
				int currentDayOfWeek = Calendar.getInstance().get(
						Calendar.DAY_OF_WEEK);
				int mFastingDay = -1;
				for (int i = 0; i < BaseConstant.mDietCycle
						.getSelected_fasting_days().size(); i++) {
					int fastingDay = BaseConstant.mDietCycle
							.getSelected_fasting_days().get(i);
					int convertCalendarDay = Days.reverseMapDay(fastingDay);
					if (currentDayOfWeek == convertCalendarDay) {
						mFastingDay = fastingDay;
						break;
					}
				}
				//
				if (mFastingDay != -1) {
					showNextNowDayDialog();
				} else {
					// save and return to previous activity
					BaseConstant.mDietCycle.setLast_launch(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setLaunch_history(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
					BaseConstant.mDietCycle.setNow(false);
					StepByStepHelpGuideActivity.activity.setResult(StepByStepHelpGuideActivity.activity.RESULT_OK);
					BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
							.getTimeInMillis());
					Utility.setAlarm();
					DietCycle.setDietCycle();
					StepByStepHelpGuideActivity.activity.finish();

				}
			}
		}
	}

	/**
	 * Will check calendar validation
	 * 
	 * @return - true if fasting days are selected - false if not selected
	 */
	private boolean checkCalendarValidation() {

		if (BaseConstant.mDietCycle.getSelected_fasting_days().size() == BaseConstant.mDietCycle
				.getFasting_intensity())
			return false;
		else
			return true;
	}

	/**
	 * will check calory validation
	 * 
	 * @return
	 */
	private boolean checkCaloriesValidation() {
		if (BaseConstant.mDietCycle.getCalories_consumption() == 0)
			return true;
		return false;
	}

	/**
	 * If any field left blank from all the fragment this dialog will notify.
	 * 
	 * @param flag
	 */
	private void showValidationDialog(boolean flag) {
		if (mValidationDialog == null) {
			mValidationDialog = new Dialog(StepByStepHelpGuideActivity.activity, R.style.DialogTheme);
			mValidationDialog.setContentView(R.layout.raw_help_dialog);
			mValidationDialog.setCanceledOnTouchOutside(false);
			CustomButton mBtnNo = (CustomButton) mValidationDialog
					.findViewById(R.id.bt_btn_help_Guide);
			CustomButton mBtnView = (CustomButton) mValidationDialog
					.findViewById(R.id.bt_btn_Skip);
			mBtnNo.setText(R.string.str_no);
			mBtnView.setText(R.string.str_view);
			mBtnNo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mValidationDialog.dismiss();
				}
			});
			mBtnView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mValidationDialog.dismiss();
					mOnChangePagerListener.onChangePager(index);
				}
			});
		}
		CustomTextView mTxtTitle_2 = (CustomTextView) mValidationDialog
				.findViewById(R.id.tv_txt_help_guide);
		if (flag) {
			mTxtTitle_2.setText(getString(R.string.str_calories_validation));
		} else {
			mTxtTitle_2.setText(getString(R.string.str_fasting_intensity));
		}
		mValidationDialog.show();

	}

	/**
	 * show dialog for start fasting from now or from next day.
	 */
	private void showNextNowDayDialog() {
		if (mNextNowDayDialog == null) {
			mNextNowDayDialog = new Dialog(StepByStepHelpGuideActivity.activity, R.style.DialogTheme);
			mNextNowDayDialog.setContentView(R.layout.raw_dialog3);
			mNextNowDayDialog.setCanceledOnTouchOutside(false);

			CustomTextView mTxtTitle_1 = (CustomTextView) mNextNowDayDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtSubTitle_1 = (CustomTextView) mNextNowDayDialog
					.findViewById(R.id.tv_txt_guide2);

			CustomButton mBtnNextDay = (CustomButton) mNextNowDayDialog
					.findViewById(R.id.bt_btn_Cancel);
			CustomButton mBtnNow = (CustomButton) mNextNowDayDialog
					.findViewById(R.id.bt_btn_ok);

			mTxtTitle_1.setText(R.string.str_start_fasting_now);
			mTxtSubTitle_1.setText(getString(R.string.str_fasting_message));

			mBtnNextDay.setText(R.string.str_next_day);
			mBtnNextDay.setLayoutParams(LayoutUtils
					.getLayoutParamsWithoutMargin(1.25f));

			mBtnNow.setText(R.string.str_now);
			mBtnNow.setLayoutParams(LayoutUtils
					.getLayoutParamsWithMargin(0.75f));

			mBtnNextDay.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void onClick(View v) {
					mNextNowDayDialog.dismiss();
					BaseConstant.mDietCycle.setLast_launch(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setLaunch_history(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setNow(false);
					BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
					StepByStepHelpGuideActivity.activity.setResult(StepByStepHelpGuideActivity.activity.RESULT_OK);
					BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
							.getTimeInMillis());
					Utility.setAlarm();
					DietCycle.setDietCycle();
					StepByStepHelpGuideActivity.activity.finish();
				}
			});

			mBtnNow.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void onClick(View v) {
					mNextNowDayDialog.dismiss();
					BaseConstant.mDietCycle.setLast_launch(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setLaunch_history(Calendar
							.getInstance().getTimeInMillis());
					BaseConstant.mDietCycle.setNow(true);
					BaseConstant.mDietCycle.setDiet_cycle_is_on(true);
					StepByStepHelpGuideActivity.activity.setResult(StepByStepHelpGuideActivity.activity.RESULT_OK);
					BaseConstant.mDietCycle.setStartTime(Calendar.getInstance()
							.getTimeInMillis());
					Utility.setAlarm();
					DietCycle.setDietCycle();
					StepByStepHelpGuideActivity.activity.finish();
				}
			});
		}

		mNextNowDayDialog.show();
	}

	/**
	 * Show Hint Dialogs if require.
	 */
	private void showHintDialogs() {
		boolean neverShowDemoAgain = PrefHelper.isNeverShowAgain(StepByStepHelpGuideActivity.activity,
				BaseConstant.STEP_5_FRAGMENT_ID);
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

			if (PrefHelper.getBoolean(getString(R.string.KEY_IS_FRAGMENT_5),
					false)) {
				mViews.addView(new ShowcaseViews.ItemViewProperties(
						R.id.ll_help_guide_5, R.string.str_start_message_8,
						R.string.showcase_image_message, SHOWCASE_KITTEN_SCALE,
						BaseConstant.STEP_5_FRAGMENT_ID));
			}
			mViews.show();
		}
	}
}