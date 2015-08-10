package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * An Edittext subclass that allows you to specify a custom font in XML.
 */
public class CustomEditText extends EditText {

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomTextViewHelper.initialize(this, context, attrs);
	}
}