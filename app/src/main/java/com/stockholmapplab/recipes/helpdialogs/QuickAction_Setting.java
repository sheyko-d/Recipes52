package com.stockholmapplab.recipes.helpdialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.adapter.DietHistoryAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.PrefHelper;

/**
 * 
 * This class used to display QuickAction help dialogues.
 * 
 */
public class QuickAction_Setting extends PopupWindows {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;

	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private int mAnimStyle;
	private int mOrientation;
	private int mChildPos;
	private int height;
	private Activity mContext;
	private CustomTextView mTxthints, mTxtMessage;
	private int _activityID;
	private int _currentPosition;
	private boolean flag = true;
	private int pos;
	private int actionId;
	private int mInsertPos;
	private int mWhichClass = 0;
	private OnActionItemClickListener mItemClickListener;
	private List<ActionItem> actionItems = new ArrayList<ActionItem>();
	private int arrowYPosition = -1;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - context
	 * @param orientation
	 *            - Screen Orientation landscape or portrait
	 */
	public QuickAction_Setting(Activity context, int orientation) {
		super(context);
		this.mContext = context;
		mOrientation = orientation;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (mOrientation == HORIZONTAL) {
			setRootViewId(R.layout.popup_horizontal);
		} else {
			setRootViewId(R.layout.popup_vertical_quick_action_setting);
		}
		mAnimStyle = ANIM_AUTO;
		mChildPos = 0;
	}

	/**
	 * Get action item at an index
	 * 
	 * @param index
	 *            Index of item (position from callback)
	 * 
	 * @return Action Item at the position
	 */
	public ActionItem getActionItem(int index) {
		return actionItems.get(index);
	}

	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener
	 *            Listener
	 */
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Listener for item click
	 * 
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction_Setting source, int pos,
				int actionId, boolean requiredToStop);
	}

	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
		mTrack.setBackgroundColor(Color.TRANSPARENT);
		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}

	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	public void addActionItem(ActionItem action, int activityId, String msg,
			int currentPosition) {
		actionItems.add(action);
		mWindow.setOutsideTouchable(false);
		_activityID = activityId;
		_currentPosition = currentPosition;
		View container;
		container = mInflater.inflate(R.layout.quick_action_setting_dialog_1,
				null);
		container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mTxtMessage = (CustomTextView) container
				.findViewById(R.id.tv_txt_message);
		mTxthints = (CustomTextView) container.findViewById(R.id.tv_txt_hint);
		CustomButton mBtnOk = (CustomButton) container
				.findViewById(R.id.bt_btn_ok);
		mTxthints.setText(R.string.str_hide_hints);
		mTxtMessage.setText(msg);
		mTxthints.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_untik,
				0, 0, 0);
		mBtnOk.setOnClickListener(onClickListener);
		mTxthints.setOnClickListener(onClickListener);
		pos = mChildPos;
		actionId = action.getActionId();
		if (mOrientation == HORIZONTAL && mChildPos != 0) {
			View separator = mInflater.inflate(R.layout.horiz_separator, null);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			separator.setLayoutParams(params);
			separator.setPadding(5, 0, 5, 0);
			mTrack.addView(separator, mInsertPos);
			mInsertPos++;
		} else {
			if (mWhichClass == 1) {
				if (mInsertPos == 0) {
					View separator = mInflater.inflate(R.layout.section, null);
					separator.setPadding(0, 0, 0, 0);
					mTrack.addView(separator, mInsertPos);
					mInsertPos++;
				}
				if (mInsertPos != 0 && mInsertPos != 1) {
					View separator = mInflater.inflate(
							R.layout.verti_separator, null);
					separator.setPadding(0, 0, 0, 0);
					mTrack.addView(separator, mInsertPos);
					mInsertPos++;
				}
			} else if (mWhichClass == 2) {
				if (mInsertPos != 0) {
					View separator = mInflater.inflate(
							R.layout.verti_separator, null);
					separator.setPadding(0, 0, 0, 0);
					mTrack.addView(separator, mInsertPos);
					mInsertPos++;
				}
			}
		}
		container.setPadding(5, 10, 5, 10);
		mTrack.addView(container, mInsertPos);
		mChildPos++;
		mInsertPos++;
	}

	private View.OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_txt_hint:
				if (flag) {
					mTxthints.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.ic_tik, 0, 0, 0);
					PrefHelper.setBoolean(
							mContext.getString(R.string.KEY_IS_DIALOG_ACTIVE),
							false);
					flag = false;
				} else {
					mTxthints.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.ic_untik, 0, 0, 0);
					PrefHelper.setBoolean(
							mContext.getString(R.string.KEY_IS_DIALOG_ACTIVE),
							true);
					flag = true;
				}
				break;
			case R.id.bt_btn_ok:
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(QuickAction_Setting.this,
							pos, actionId, flag);
				}
				if (!getActionItem(pos).isSticky()) {
					dismiss();
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void show(View anchor) {
		preShow();
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootWidth = mRootView.getMeasuredWidth();
		int rootHeight = mRootView.getMeasuredHeight();
		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		int xPos = (screenWidth - rootWidth) / 2;
		int yPos;
		yPos = anchorRect.top - rootHeight;
		boolean popupOnTop = true;
		if (screenHeight - anchorRect.bottom > rootHeight) {
			yPos = anchorRect.bottom;
			popupOnTop = false;
		}
		int requestedX = anchorRect.centerX();
		switch (_activityID) {
		case BaseConstant.DIET_CYCLE_HISTORY_ACTIVITY_ID:
			yPos = anchorRect.top + DietHistoryAdapter.one_raw_height;
			popupOnTop = false;
			break;
		case BaseConstant.HOME_ACTIVITY_ID:
			if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
				if (_currentPosition == 0) {
					height = (int) BaseApplication.getAppContext()
							.getResources()
							.getDimension(R.dimen.diet_cycle_height);
					yPos = anchorRect.top - (rootHeight + height);

					if (arrowYPosition == -1) {
						arrowYPosition = (int) BaseApplication.getAppContext()
								.getResources()
								.getDimension(R.dimen.hint_arrow_position);
					}
					requestedX = anchorRect.centerX() - arrowYPosition;
					popupOnTop = true;
				}
			}
			break;
		case BaseConstant.CONSUMED_TODAY_ACTIVITY_ID:
			if (_currentPosition == 3) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.consumed_today_height);
				yPos = anchorRect.top - (rootHeight + height);
				popupOnTop = true;
			}
			break;
		case BaseConstant.ABOUT_ACTIVITY_ID:
			if (_currentPosition == 0) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.about_height);
				yPos = anchorRect.top - (rootHeight + height);
				popupOnTop = true;
			}
			break;
		case BaseConstant.MYSTATS_ACTIVITY_ID:
			if (_currentPosition == 0) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.my_stats_height_1);
				if (height != 0) {
					yPos = anchorRect.top - (rootHeight + height);
					popupOnTop = true;
				}
			}
			if (_currentPosition == 1) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.my_stats_height_2);
				if (height != 0) {
					yPos = anchorRect.top - (rootHeight + height);
					popupOnTop = true;
				}
			}
			break;
		case BaseConstant.BODY_STATS_ACTIVITY_ID:
			if (_currentPosition == 2) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.body_stats_height);
				yPos = anchorRect.top - (rootHeight + height);
				popupOnTop = true;
			}
			break;
		case BaseConstant.QUICK_START_ACTIVITY_ID:
			if (_currentPosition == 4) {
				height = (int) BaseApplication.getAppContext().getResources()
						.getDimension(R.dimen.quick_start_height);
				if (height != 0) {
					yPos = anchorRect.top - (rootHeight + height);
					popupOnTop = true;
				}
			}
			break;
		}
		showArrow(((popupOnTop) ? R.id.arrow_down : R.id.arrow_up), requestedX);
		setAnimationStyle(screenWidth, anchorRect.centerX(), popupOnTop);
		mWindow.setOutsideTouchable(false);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;
		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
					: R.style.Animations_PopDownMenu_Left);
			break;
		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			break;
		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect
					: R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp
				: mArrowDown;
		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown
				: mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();
		showArrow.setVisibility(View.VISIBLE);
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();
		param.leftMargin = requestedX - arrowWidth / 2;
		hideArrow.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onDismiss() {
		super.onDismiss();
	}
}