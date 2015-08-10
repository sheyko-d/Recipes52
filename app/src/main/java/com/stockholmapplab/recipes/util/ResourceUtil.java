package com.stockholmapplab.recipes.util;

import android.content.res.TypedArray;

import com.stockholmapplab.recipes.BaseApplication;
/**
 * ResourceUtil class is use for getting different type of resources. 
 */
public class ResourceUtil {

	/**
	 * getString method is use for get String resource
	 * @param resourceID
	 * @return String
	 */
	public static String getString(int resourceID) {
		return BaseApplication.getAppContext().getResources()
				.getString(resourceID);
	}
	/**
	 * getString method is use for get Integer resource
	 * @param resourceID
	 * @return Integer
	 */
	public static int getInt(int resourceID) {
		return BaseApplication.getAppContext().getResources()
				.getInteger(resourceID);
	}
	
	/**
	 * getString method is use for get StringArray resource
	 * @param resourceID
	 * @return String[]
	 */
	public static String[] getStringArray(int resourceID) {
		return BaseApplication.getAppContext().getResources()
				.getStringArray(resourceID);
	}
	/**
	 * getString method is use for get Color resource
	 * @param resourceID
	 * @return Integer
	 */
	public static int getColor(int colorID) {
		return BaseApplication.getAppContext().getResources().getColor(colorID);
	}

	/**
	 * getString method is use for get TypedArray resource
	 * @param resourceID
	 * @return TypedArray
	 */
	public static TypedArray getTypedArray(int resourceID) {
		return BaseApplication.getAppContext().getResources()
				.obtainTypedArray(resourceID);
	}

}
