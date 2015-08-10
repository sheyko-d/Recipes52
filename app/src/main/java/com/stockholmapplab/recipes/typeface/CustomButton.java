package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * A Button subclass that allows you to specify a custom font in XML.
 */
public class CustomButton extends Button {
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomTextViewHelper.initialize(this, context, attrs);

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			setElevation(0);
			setStateListAnimator(null);
		}
	}
}