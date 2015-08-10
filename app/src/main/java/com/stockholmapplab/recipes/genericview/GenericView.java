package com.stockholmapplab.recipes.genericview;

import android.app.Activity;
import android.view.View;

/**
 * 
 * This class used to describe convenient methid of findViewById for activity as
 * well as for View.
 * 
 */
public class GenericView {

	/**
	 * Convenience method of findViewById
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(View parent, int id) {
		return (T) parent.findViewById(id);
	}

	/**
	 * Convenience method of findViewById
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}

}
