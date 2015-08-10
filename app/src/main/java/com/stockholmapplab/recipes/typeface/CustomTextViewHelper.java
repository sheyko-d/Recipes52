package com.stockholmapplab.recipes.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.stockholmapplab.recipes.R;

/**
 * Share functionality across multiple view subclasses using composition.
 */
public class CustomTextViewHelper {
	private static final String NS_SLK = "http://schemas.slktechlabs.com/slk";

	/** Corresponds to the constructor of TextView and children */
	public static void initialize(TextView view, Context context,
			AttributeSet attributeSet) {
		if (!view.isInEditMode()) {
			TypedArray attributes = context.obtainStyledAttributes(
					attributeSet, R.styleable.Slk, R.attr.typeface, 0);
			String typefaceDesc = attributes
					.getString(R.styleable.Slk_typeface);

			// If not set in the style, attempt to pull from the messageTypeface
			if (typefaceDesc == null) {
				typefaceDesc = attributes
						.getString(R.styleable.Slk_messageTypeface);
			}

			// If not set in the messageTypeface, attempt to pull from
			// "slk:typeface"
			if (typefaceDesc == null) {
				typefaceDesc = attributeSet.getAttributeValue(NS_SLK,
						"typeface");
			}

			if (typefaceDesc != null) {
				Typeface typeface = CustomTypefaceHelper.getTypeface(context,
						typefaceDesc);

				view.setTypeface(typeface);

			} else {
			}
			attributes.recycle();
		}
	}
}