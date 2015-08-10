package com.stockholmapplab.recipes.util;

import android.widget.LinearLayout.LayoutParams;

/**
 * LayoutUtils class provide Layout related utility. 
 */
public class LayoutUtils {

	/**
	 * getLayoutParamsWithoutMargin method return LayoutParams without margin.
	 * @param weight
	 * @return
	 */
	public static LayoutParams getLayoutParamsWithoutMargin(float weight) {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, weight);
	}
	/**
	 * getLayoutParamsWithoutMargin method return LayoutParams with margin.
	 * @param weight
	 * @return
	 */
	public static LayoutParams getLayoutParamsWithMargin(float weight) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, weight);
		params.leftMargin = 10;
		return params;
	}
}
