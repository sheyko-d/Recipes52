package com.stockholmapplab.recipes.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.pojo.Days;

/**
 * ScaleView class is use for draw week days on custom scale. Custom scale is
 * use in home screen.
 */
public class ScaleView extends View {

	private int width = -1, height = -1;
	private Bitmap mScaleBitmap = null;
	private Bitmap mNumberBitmap = null;
	private TypedArray bigGreenDayNumberImageID = null,
			bigBlackDayNumberImageID = null, smallGreenDayNumberImageID = null,
			smallBlackDayNumberImageID = null;

	public ScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (width != -1) {
			if (mScaleBitmap == null) {
				decodeBitmap();
			}
			if (mScaleBitmap != null) {
				canvas.drawBitmap(mScaleBitmap, 0, 20, null);
				getXPosition(canvas);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);

		if (bigGreenDayNumberImageID == null) {
			bigGreenDayNumberImageID = ResourceUtil
					.getTypedArray(R.array.bigGreenDayNumberImageID);
			bigBlackDayNumberImageID = ResourceUtil
					.getTypedArray(R.array.bigBlackDayNumberImageID);
			smallGreenDayNumberImageID = ResourceUtil
					.getTypedArray(R.array.smallGreenDayNumberImageID);
			smallBlackDayNumberImageID = ResourceUtil
					.getTypedArray(R.array.smallBlackDayNumberImageID);

		}

		width = getWidth();
		height = getHeight();
		invalidate();
	}

	/**
	 * decodeBitmap() method is use for decode bitmap
	 */
	private void decodeBitmap() {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			// Calculate inSampleSize
			// options.inPreferredConfig = Config.RGB_565;
			Bitmap b = BitmapFactory.decodeResource(getResources(),
					R.drawable.scale, options);
			mScaleBitmap = Bitmap.createScaledBitmap(b, width, height - 20,
					false);
			b.recycle();
		} catch (Exception e) {
			mScaleBitmap = null;
		}
	}

	/**
	 * getXPosition method is use for draw number character at particular
	 * position in scale view
	 * 
	 * @param canvas
	 */
	private void getXPosition(Canvas canvas) {
		float y = (float) ((height * BaseConstant.scaleYPointInPercentage) / 100);
		ArrayList<Days> days = new ArrayList<Days>();
		days = Days.getDays();
		for (int i = 0; i < days.size(); i++) {
			if (days.get(i).isToday()) {
				if (days.get(i).isFastDay()) {
					// big green
					getNumberBitmap(bigGreenDayNumberImageID.getResourceId(i,
							-1));
					canvas.drawBitmap(
							mNumberBitmap,
							(float) ((width * BaseConstant.scaleXPointInPercentage[i]) / 100)
									- (mNumberBitmap.getWidth() / 2), y
									- mNumberBitmap.getHeight(), null);
				} else {
					// big black
					getNumberBitmap(bigBlackDayNumberImageID.getResourceId(i,
							-1));
					canvas.drawBitmap(
							mNumberBitmap,
							(float) ((width * BaseConstant.scaleXPointInPercentage[i]) / 100)
									- (mNumberBitmap.getWidth() / 2), y
									- mNumberBitmap.getHeight(), null);
				}
			} else {
				// not today... check is fast day
				if (days.get(i).isFastDay()) {
					// small green
					getNumberBitmap(smallGreenDayNumberImageID.getResourceId(i,
							-1));
					canvas.drawBitmap(
							mNumberBitmap,
							(float) ((width * BaseConstant.scaleXPointInPercentage[i]) / 100)
									- (mNumberBitmap.getWidth() / 2), y
									- mNumberBitmap.getHeight(), null);
				} else {
					// small black
					getNumberBitmap(smallBlackDayNumberImageID.getResourceId(i,
							-1));
					canvas.drawBitmap(
							mNumberBitmap,
							(float) ((width * BaseConstant.scaleXPointInPercentage[i]) / 100)
									- (mNumberBitmap.getWidth() / 2), y
									- mNumberBitmap.getHeight(), null);
				}
			}
		}
	}

	public void updateView() {
		invalidate();
	}

	/**
	 * getNumberBitmap method is use for getting number images in Bitmap
	 * 
	 * @param imageId
	 */
	private void getNumberBitmap(int imageId) {
		if (mNumberBitmap != null) {
			mNumberBitmap.recycle();
			mNumberBitmap = null;
		}
		mNumberBitmap = BitmapFactory.decodeResource(getResources(), imageId);
	}
}