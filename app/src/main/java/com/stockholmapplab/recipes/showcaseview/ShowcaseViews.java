package com.stockholmapplab.recipes.showcaseview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.DietCycleHistoryActivity;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.helpdialogs.QuickAction_Setting;
import com.stockholmapplab.recipes.helpdialogs.QuickAction_Setting.OnActionItemClickListener;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;

/**
 * 
 * This class used to display hint dialogues.
 * 
 */
public class ShowcaseViews {
	private static final int ID_TEST = 50;
	private final List<ShowcaseView> views = new ArrayList<ShowcaseView>();
	private final Activity activity;
	@SuppressWarnings("unused")
	private final int showcaseTemplateId;
	private ShowcaseView view = null;
	private OnShowcaseAcknowledged showcaseAcknowledgedListener = new OnShowcaseAcknowledged() {
		@Override
		public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
			// DEFAULT LISTENER - DOESN'T DO ANYTHING!
		}
	};

	public interface OnShowcaseAcknowledged {
		void onShowCaseAcknowledged(ShowcaseView showcaseView);
	}

	/**
	 * Constructor
	 * 
	 * @param activity
	 *            - current activity
	 * @param showcaseTemplateLayout
	 *            - int value
	 */
	public ShowcaseViews(Activity activity, int showcaseTemplateLayout) {
		this.activity = activity;
		this.showcaseTemplateId = showcaseTemplateLayout;
	}

	/**
	 * Constructor
	 * 
	 * @param activity
	 *            - current activity
	 * @param showcaseTemplateLayout
	 *            - int value
	 * @param acknowledgedListener
	 *            - interface listener
	 */
	public ShowcaseViews(Activity activity, int showcaseTemplateLayout,
			OnShowcaseAcknowledged acknowledgedListener) {
		this(activity, showcaseTemplateLayout);
		this.showcaseAcknowledgedListener = acknowledgedListener;
	}

	public void addView(ItemViewProperties properties) {
		ShowcaseViewBuilder builder = new ShowcaseViewBuilder(activity)
				.setText(properties.titleResId, properties.messageResId)
				.setShowcaseIndicatorScale(properties.scale)
				.setConfigOptions(properties.configOptions)
				.setActivityId(properties.activityId)
				.setCurrentPosition(properties.messageResId)
				.setShowcaseView(activity.findViewById(properties.id));

		ShowcaseView showcaseView = builder.build();
		showcaseView
				.overrideButtonClick(createShowcaseViewDismissListener(showcaseView));
		showcaseView
				.overrideActionItemClick(createActionItemListener(showcaseView));
		views.add(showcaseView);
	}

	/**
	 * Used to call next dialogue in the same screen if available.
	 * 
	 * @param showcaseView
	 *            - object of ShowcaseView class
	 */
	private void callNext(final ShowcaseView showcaseView) {
		showcaseView.onClick(showcaseView);
		int fadeOutTime = showcaseView.getConfigOptions().fadeOutDuration;
		if (fadeOutTime > 0) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					showNextView(showcaseView);
				}
			}, fadeOutTime);
		} else {
			showNextView(showcaseView);
		}
	}

	/**
	 * On Item Click Listener
	 * 
	 * @param showcaseView
	 *            - object of ShowcaseView class
	 * @return
	 */
	private OnActionItemClickListener createActionItemListener(
			final ShowcaseView showcaseView) {
		return new QuickAction_Setting.OnActionItemClickListener() {
			public void onItemClick(QuickAction_Setting source, int pos,
					int actionId, boolean requiredToHide) {

				Utility.hintDialogShow(showcaseView.getActivityId(),
						showcaseView.mDetailText);
				if (PrefHelper.getBoolean(BaseApplication.getAppContext()
						.getResources()
						.getString(R.string.KEY_IS_DIALOG_ACTIVE), false)) {
					if (actionId == ID_TEST) {
						source.dismiss();
						switch (showcaseView.getActivityId()) {
						case BaseConstant.HOME_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.ABOUT_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.QUICK_START_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STEP_1_FRAGMENT_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STEP_2_FRAGMENT_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STEP_3_FRAGMENT_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STEP_4_FRAGMENT_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STEP_5_FRAGMENT_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.MYSTATS_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.CONSUMED_TODAY_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.STOP_DIET_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						case BaseConstant.BODY_STATS_ACTIVITY_ID:
							callNext(showcaseView);
							break;
						}
					}
				} else {
					if (showcaseView.getActivityId() == BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID)
						DietCycleHistoryActivity.mTouchFlag = false;
					hide();
				}
			}
		};
	}

	/**
	 * On Click Listener
	 * 
	 * @param showcaseView
	 *            - object of ShowcaseView class
	 * @return
	 */
	private View.OnClickListener createShowcaseViewDismissListener(
			final ShowcaseView showcaseView) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showcaseView.onClick(showcaseView);
				int fadeOutTime = showcaseView.getConfigOptions().fadeOutDuration;
				if (fadeOutTime > 0) {
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							showNextView(showcaseView);
						}
					}, fadeOutTime);
				} else {
					showNextView(showcaseView);
				}
			}
		};
	}

	/**
	 * Show Next view on same screen if availables
	 * 
	 * @param showcaseView
	 *            - object of ShowcaseView class
	 */
	public void showNextView(ShowcaseView showcaseView) {
		if (views.isEmpty()) {
			showcaseAcknowledgedListener.onShowCaseAcknowledged(showcaseView);
		} else {
			show();
		}
	}

	public void show() {
		if (views.isEmpty()) {
			return;
		} else {
		}

		view = views.get(0);
		Log.d("Log", view+"");
		Log.d("Log", view.getShowcaseView()+"");
		view.setpopupshare();
		view.showShareAction(view.getShowcaseView());

		boolean hasShot = activity.getSharedPreferences(
				ShowcaseView.PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
				.getBoolean("hasShot" + view.getConfigOptions().showcaseId,
						false);
		if (hasShot
				&& view.getConfigOptions().shotType == ShowcaseView.TYPE_ONE_SHOT) {
			// The showcase has already been shot once, so we don't need to do
			// show it again.
			view.setVisibility(View.GONE);
			views.remove(0);
			view.getConfigOptions().fadeOutDuration = 0;
			view.performButtonClick();
			return;
		}

		view.setVisibility(View.INVISIBLE);
		((ViewGroup) activity.getWindow().getDecorView()).addView(view);
		view.show();
		views.remove(0);

	}

	public boolean hasViews() {
		return !views.isEmpty();
	}

	public void hide() {
		if (view != null) {
			view.hide();
		}
	}

	public static class ItemViewProperties {

		public static final int ID_NO_SHOWCASE = -2202;
		public static final int ID_NOT_IN_ACTIONBAR = -1;
		public static final int ID_SPINNER = 0;
		public static final int ID_TITLE = 1;
		public static final int ID_OVERFLOW = 2;

		protected final int titleResId;
		protected final int messageResId;
		protected final int id;
		protected final int itemType;
		protected final float scale;
		protected final int activityId;
		protected final ShowcaseView.ConfigOptions configOptions;

		public ItemViewProperties(int id, int titleResId, int messageResId,
				float scale, int activityId) {
			this(id, titleResId, messageResId, scale, null, activityId);
		}

		public ItemViewProperties(int id, int titleResId, int messageResId,
				float scale, ShowcaseView.ConfigOptions configOptions,
				int activityId) {
			this.id = id;
			this.titleResId = titleResId;
			this.messageResId = messageResId;
			this.scale = scale;
			this.itemType = 0;
			this.configOptions = configOptions;
			this.activityId = activityId;
		}
	}
}
