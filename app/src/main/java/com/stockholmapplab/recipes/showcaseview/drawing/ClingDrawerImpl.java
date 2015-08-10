package com.stockholmapplab.recipes.showcaseview.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.adapter.DietHistoryAdapter;

public class ClingDrawerImpl implements ClingDrawer {

	private Paint mEraser;
	private Drawable mShowcaseDrawable;
	private Rect mShowcaseRect;

	public ClingDrawerImpl(Resources resources, int showcaseColor) {
		mEraser = new Paint();
		mEraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		mShowcaseDrawable = resources
				.getDrawable(R.drawable.showcase_transparent);
		mShowcaseDrawable.setColorFilter(showcaseColor,
				PorterDuff.Mode.MULTIPLY);
	}

	@Override
	public void eraseCircle(Canvas canvas, float x, float y, float radius) {

		if (DietHistoryAdapter.one_raw_height >= 1) {
			mShowcaseRect.bottom = mShowcaseRect.top
					+ DietHistoryAdapter.one_raw_height;
			canvas.drawRect(mShowcaseRect.left, mShowcaseRect.top,
					mShowcaseRect.right, mShowcaseRect.bottom, mEraser);

		} else {
			canvas.drawRect(mShowcaseRect.left, mShowcaseRect.top,
					mShowcaseRect.right, mShowcaseRect.bottom, mEraser);
		}

	}

	@Override
	public void scale(Canvas canvas, float x, float y, float scaleMultiplier) {
		Matrix mm = new Matrix();
		mm.postScale(scaleMultiplier, scaleMultiplier, x, y);
		canvas.setMatrix(mm);
	}

	@Override
	public void revertScale(Canvas canvas) {
		canvas.setMatrix(new Matrix());
	}

	@Override
	public void drawCling(Canvas canvas) {
		mShowcaseDrawable.setBounds(mShowcaseRect);
		mShowcaseDrawable.draw(canvas);
	}

	@Override
	public int getShowcaseWidth() {
		return mShowcaseDrawable.getIntrinsicWidth();
	}

	@Override
	public int getShowcaseHeight() {
		return mShowcaseDrawable.getIntrinsicHeight();
	}

	/**
	 * Creates a {@link android.graphics.Rect} which represents the area the
	 * showcase covers. Used to calculate where best to place the text
	 * 
	 * @return true if voidedArea has changed, false otherwise.
	 */
	public boolean calculateShowcaseRect(float x, float y, float width,
			float height) {

		if (mShowcaseRect == null) {
			mShowcaseRect = new Rect();
		}

		int cx = (int) x, cy = (int) y;
		int dw = (int) width;
		int dh = (int) height;
		mShowcaseRect.left = cx - dw / 2;
		mShowcaseRect.top = cy - dh / 2;
		mShowcaseRect.right = cx + dw / 2;
		mShowcaseRect.bottom = cy + dh / 2;
		return true;
	}

	@Override
	public Rect getShowcaseRect() {
		return mShowcaseRect;
	}
}
