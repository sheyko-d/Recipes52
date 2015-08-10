package com.stockholmapplab.recipes.helpdialogs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This view will draw all {@link LabeledPoint} on its surface. It uses a
 * {@link DrawViewAdapter } to get the content to draw.
 */
public class DrawView extends View {
	private DrawViewAdapter drawViewAdapter;
	public int currentPointPositionToDisplay = 0;
	private boolean isDrawingOnePointAtATime = true;
	private boolean isClearPorterDuffXfermodeEnabled = true;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - context
	 * @param attrs
	 *            - Object of AttributeSet Interface
	 * @param defStyle
	 *            - integer value
	 * 
	 */
	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - context
	 * @param attrs
	 *            - Object of AttributeSet Interface
	 */
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - context
	 */
	public DrawView(Context context) {
		super(context);

	}

	/**
	 * onDraw() method
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (currentPointPositionToDisplay >= 0) {
			if (isDrawingOnePointAtATime) {
				drawPoint(currentPointPositionToDisplay, canvas);
			}
		}
	}

	public void setIsClearPorterDuffXfermodeEnabled(
			boolean isClearPorterDuffXfermodeEnabled) {
		this.isClearPorterDuffXfermodeEnabled = isClearPorterDuffXfermodeEnabled;
	}

	public void setDrawViewAdapter(DrawViewAdapter drawViewAdapter) {
		this.drawViewAdapter = drawViewAdapter;
	}

	public DrawViewAdapter getDrawViewAdapter() {
		return drawViewAdapter;
	}

	/**
	 * Whether to show one point at a time or a point and all previous points.
	 * 
	 * @param isDrawingOnePointAtATime
	 *            if true, only one point will be displayed at a time. If false,
	 *            a point and all its predecessors will be displayed
	 *            simultaneously.
	 */
	public void setDrawingOnePointAtATime(boolean isDrawingOnePointAtATime) {
		this.isDrawingOnePointAtATime = isDrawingOnePointAtATime;
	}

	public boolean isDrawingOnePointAtATime() {
		return isDrawingOnePointAtATime;
	}

	public void showNextPoint() {
		if (currentPointPositionToDisplay < getDrawViewAdapter()
				.getPointsCount() - 1) {
			currentPointPositionToDisplay++;
			refreshDrawableState();
			invalidate();
		}
	}

	/**
	 * Draw the point at the position specified by
	 * {@link DrawViewAdapter#getTextPoint(int)}
	 * 
	 * @param position
	 *            the index of the point to draw.
	 * @param canvas
	 *            the canvas on which to draw the point at position.
	 */
	protected void drawPoint(int position, Canvas canvas) {
		drawDrawable(position, canvas);
	}

	/**
	 * Draw the drawable of the point at a given position specified by
	 * {@link DrawViewAdapter#getDrawablePoint(int)}
	 * 
	 * @param position
	 *            the index of the point to draw.
	 * @param canvas
	 *            the canvas on which to draw the point at position.
	 */
	protected void drawDrawable(int position, Canvas canvas) {
		Drawable drawable = drawViewAdapter.getDrawableAt(position);
		if (drawable == null) {
			return;
		}
		doUseClearPorterDuffXfermode(canvas, drawable);
		drawable.draw(canvas);
	}

	/**
	 * if PorterDuff xfermode is active, this method can be used to remove the
	 * background inside the {@link Drawable} associated to a
	 * {@link LabeledPoint}. This method is called just before drawing each
	 * {@link LabeledPoint}'s drawable.
	 * 
	 * @param canvas
	 *            the canvas on which to draw the point at position.
	 * @param drawable
	 *            the {@link Drawable} that is going to be drawn.
	 * @see #isClearPorterDuffXfermodeEnabled
	 * @see #setIsClearPorterDuffXfermodeEnabled(boolean)
	 */

	protected void doUseClearPorterDuffXfermode(Canvas canvas, Drawable drawable) {
		if (isClearPorterDuffXfermodeEnabled) {
			Paint p = new Paint();
			p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawRect(drawable.getBounds().left,
					drawable.getBounds().top, drawable.getBounds().right,
					drawable.getBounds().bottom, p);
		}
	}

}