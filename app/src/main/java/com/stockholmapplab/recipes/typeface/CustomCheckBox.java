package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * A Checkbox subclass that allows you to specify a custom font in XML.
 */
public class CustomCheckBox extends CheckBox {
	public CustomCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomTextViewHelper.initialize(this, context, attrs);
	}
}