package com.stockholmapplab.recipes.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;

/**
 * CalendarView class provide custom calendar functionality.
 */
public class CalendarView extends View {

	private int width = -1, height = -1;

	private int rectangleWidth = -1, rectangleHeight = -1;
	private Bitmap rectangle;

	// 1 for 6:1
	// 2 for 5:2
	// 3 for 4:3

	private Dialog mDialog;
	private List<ArrayList<android.graphics.Point>> points = new ArrayList<ArrayList<android.graphics.Point>>();
	private CustomTextView mTxtfastingDays;
	private boolean changeFlag = false;

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onDraw(final Canvas canvas) {

		if (width != -1) {

			canvas.drawBitmap(decodeBitmap(), 0, 0, null);

			for (int i = 0; i < BaseConstant.mDietCycle
					.getSelected_fasting_days().size(); i++) {
				int x = calculateX(BaseConstant.rectangleX[BaseConstant.mDietCycle
						.getSelected_fasting_days().get(i)]);
				int y = calculateY(BaseConstant.rectangleY[BaseConstant.mDietCycle
						.getSelected_fasting_days().get(i)]);
				canvas.drawBitmap(rectangle, x, y, null);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {

		super.onWindowFocusChanged(hasWindowFocus);

		if (width == -1 && height == -1) {
			setUpDialog();

			width = getWidth();
			height = getHeight();
			setPoint();
			calculateRectangleWidthHeight();
			invalidate();
		}
	}

	/**
	 * calculateRectangleWidthHeight() calculate green rectangle width and
	 * height according to screen and calendar background image size.
	 */
	private void calculateRectangleWidthHeight() {
		rectangleWidth = (119 * width) / 449;
		rectangleHeight = (113 * height) / 423;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			checkPoint((int) event.getX(), (int) event.getY());
		}
		return true;
	}

	/**
	 * decodeBitmap() method is use for convert drawable image to bitmap image
	 * according to screen size.
	 * 
	 * @return Bitmap
	 */
	private Bitmap decodeBitmap() {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// Calculate inSampleSize
		// options.inPreferredConfig = Config.RGB_565;
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.calendar, options);

		rectangle = BitmapFactory.decodeResource(getResources(),
				R.drawable.rectangle, options);
		Log.d("Log", "rectangle = "+rectangle);
		Log.d("Log", "rectangleWidth = "+rectangleWidth);
		Log.d("Log", "rectangleHeight = "+rectangleHeight);
		
		rectangle = Bitmap.createScaledBitmap(rectangle, rectangleWidth,
				rectangleHeight, false);
		return Bitmap.createScaledBitmap(b, width, height, false);
	}

	/**
	 * setPoint() method is use for set points of week day. means sunday have
	 * particular area in calendar background image.
	 */
	private void setPoint() {
		points.clear();
		for (int i = 0; i < (BaseConstant.calendarXPointOriginal.length / 5); i++) {
			ArrayList<android.graphics.Point> point = new ArrayList<android.graphics.Point>();
			for (int j = (5 * i); j < 5 * (i + 1); j++) {
				int x = calculateX(BaseConstant.calendarXPointOriginal[j]);
				int y = calculateY(BaseConstant.calendarYPointOriginal[j]);
				point.add(new android.graphics.Point(x, y));
			}
			points.add(point);
		}
	}

	public void update() {
		changeFlag = false;
		invalidate();
	}

	/**
	 * checkPoint method check screen x, y point with calendar background image.
	 * 
	 * @param x
	 * @param y
	 */
	private void checkPoint(int x, int y) {
		int j = 4;
		boolean oddNodes = false;
		int index = -1;
		for (int p = 0; p < points.size(); p++) {
			for (int i = 0; i < 5; i++) {

				if (points.get(p).get(i).y < y && points.get(p).get(j).y >= y
						|| points.get(p).get(j).y < y
						&& points.get(p).get(i).y >= y) {
					if (points.get(p).get(i).x + (y - points.get(p).get(i).y)
							/ (points.get(p).get(j).y - points.get(p).get(i).y)
							* (points.get(p).get(j).x - points.get(p).get(i).x) < x) {
						oddNodes = !oddNodes;
						index = p;
					}
				}
				j = i;
			}
		}
		if (oddNodes) {
			checkFastingDay(index);
		}
	}

	/**
	 * checkFastingDay method is use for check validation for select
	 * Continuously two day and more than fasting intensity.
	 * 
	 * @param selected_day
	 */
	private void checkFastingDay(int selected_day) {
		int index = -1;
		for (int i = 0; i < BaseConstant.mDietCycle.getSelected_fasting_days()
				.size(); i++) {
			if (BaseConstant.mDietCycle.getSelected_fasting_days().get(i) == selected_day) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			BaseConstant.mDietCycle.removeSelected_fasting_days(index);
			changeFlag = true;
			invalidate();
		} else {

			if (BaseConstant.mDietCycle.getSelected_fasting_days().size() < BaseConstant.mDietCycle
					.getFasting_intensity()) {
				boolean flag = false;
				switch (selected_day) {
				case 0:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(6)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(1))
						flag = true;
					break;
				case 1:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(0)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(2))
						flag = true;
					break;
				case 2:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(1)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(3))
						flag = true;
					break;
				case 3:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(2)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(4))
						flag = true;
					break;
				case 4:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(3)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(5))
						flag = true;
					break;
				case 5:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(4)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(6))
						flag = true;
					break;
				case 6:
					if (BaseConstant.mDietCycle.getSelected_fasting_days()
							.contains(5)
							|| BaseConstant.mDietCycle
									.getSelected_fasting_days().contains(0))
						flag = true;
					break;
				}

				if (flag) {
					showDialog(ResourceUtil.getString(R.string.str_dialog_1));
				} else {
					changeFlag = true;
					BaseConstant.mDietCycle.getSelected_fasting_days().add(
							selected_day);
					invalidate();
				}
			} else {
				showDialog(ResourceUtil.getString(R.string.str_dialog_3));
			}
		}
	}

	private int calculateX(int x) {
		return (width * x) / 449;
	}

	private int calculateY(int y) {
		return (height * y) / 423;
	}

	public boolean isAnyChange() {
		return changeFlag;
	}
	
	/**
	 * setUpDialog() method is use for setUp validation dialog.
	 */
	private void setUpDialog() {
		mDialog = new Dialog(getContext(), R.style.DialogTheme);
		mDialog.setContentView(R.layout.raw_dialog2);
		mDialog.setCanceledOnTouchOutside(false);
		mTxtfastingDays = (CustomTextView) mDialog
				.findViewById(R.id.tv_txtCalendarValidation);
		CustomButton mBtnOk = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_ok);

		mBtnOk.setText(R.string.str_dialog_2);
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

	}
	/**
	 * showDialog is use for show validation dialog.
	 * @param msg
	 */
	private void showDialog(String msg) {
		mTxtfastingDays.setText(msg);
		mDialog.show();
	}

	public void checkCalendarValidation(Dialog dialog) {

		if (BaseConstant.mDietCycle.getFasting_intensity() == BaseConstant.mDietCycle
				.getSelected_fasting_days().size()) {
			dialog.dismiss();
		} else {
			showDialog(ResourceUtil.getString(R.string.str_dialog_7));
		}
	}
}