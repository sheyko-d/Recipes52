package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A TextView subclass that allows you to specify a custom font in XML.
 */
public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomTextViewHelper.initialize(this, context, attrs);
	}
}