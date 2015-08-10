package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * MyTextView class used to rotate textview in a specified angle.
 * 
 */
public class MyTextView extends TextView {

	private int angle = 270;

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomTextViewHelper.initialize(this, context, attrs);
		this.setGravity(Gravity.CENTER);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
		canvas.rotate(angle, this.getWidth() / 2f, this.getHeight() / 2f);
		super.onDraw(canvas);
		canvas.restore();
	}

	public void setAngle(int textAngle) {
		angle = textAngle;
	}

}