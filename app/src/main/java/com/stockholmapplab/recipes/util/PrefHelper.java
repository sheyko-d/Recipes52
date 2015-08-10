package com.stockholmapplab.recipes.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.helpdialogs.LabeledPoint;
/**
 * PrefHelper class is use for managing Preference. 
 */
public class PrefHelper {

	public static final String SHARED_PREFERENCE_NAME = "default";
	public static final String BUNDLE_KEY_DEMO_ACTIVITY_ARRAY_LIST_POINTS = "BUNDLE_KEY_DEMO_ARRAY_LIST_POINTS";
	public static final String BUNDLE_KEY_DEMO_ACTIVITY_ID = "BUNDLE_KEY_DEMO_ACTIVITY_ID";
	
	/**
	 * set String preference
	 * @param key
	 * @param value
	 */
	public static void setString(final String key, final String value) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.putString(key, value);
		apply(editor);
	}
	
	/**
	 * getString method is use for getting String preference.
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public static String getString(final String key, final String defaultValue) {
		final SharedPreferences _preference = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext());
		return _preference.getString(key, defaultValue);
	}
	
	public static void setBoolean(final String key, final boolean value) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.putBoolean(key, value);
		apply(editor);
	}
	/**
	 * setBoolean method is use for getting Boolean preference.
	 * @param key
	 * @param defaultValue
	 * @return boolean
	 */
	public static boolean getBoolean(final String key,
			final boolean defaultValue) {
		final SharedPreferences _preference = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext());
		return _preference.getBoolean(key, defaultValue);
	}
	
	/**
	 * setLong method is use for set Long preference
	 * @param key
	 * @param value
	 */
	public static void setLong(final String key, final long value) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.putLong(key, value);
		apply(editor);
	}

	/**
	 * setBoolean method is use for getting Long preference.
	 * @param key
	 * @param defaultValue
	 * @return Long
	 */
	public static long getLong(final String key, final long defaultValue) {
		final SharedPreferences _preference = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext());
		return _preference.getLong(key, defaultValue);
	}
	/**
	 * setBoolean method is use for set Integer preference.
	 * @param key
	 * @param value
	 */
	public static void setInt(final String key, final int value) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.putInt(key, value);
		apply(editor);
	}
	/**
	 * getInt method is use for get integer preference.
	 * @param key
	 * @param defaultValue
	 * @return Integer
	 */
	public static int getInt(final String key, final int defaultValue) {
		final SharedPreferences _preference = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext());
		return _preference.getInt(key, defaultValue);
	}
	/**
	 * setFloat method is use for set Float preference.
	 * @param key
	 * @param value
	 */
	public static void setFloat(final String key, final float value) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.putFloat(key, value);
		apply(editor);
	}
	/**
	 * getFloat method is use for get Float preference.
	 * @param key
	 * @param defaultValue
	 * @return Float
	 */
	public static float getFloat(final String key, final float defaultValue) {
		final SharedPreferences _preference = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext());
		return _preference.getFloat(key, defaultValue);
	}
	
	/**
	 * deletePreference method is use for delete preference. 
	 * @param key
	 */
	public static void deletePreference(final String key) {
		final SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit();
		editor.remove(key);
		apply(editor);
	}

	// Faster pref saving for high performance
	private static final Method sApplyMethod = findApplyMethod();

	private static Method findApplyMethod() {
		try {
			final Class<Editor> cls = SharedPreferences.Editor.class;
			return cls.getMethod("apply");
		} catch (final NoSuchMethodException unused) {
			// fall through
		}
		return null;
	}

	public static void apply(final SharedPreferences.Editor editor) {
		if (sApplyMethod != null)
			try {
				sApplyMethod.invoke(editor);
				return;
			} catch (final InvocationTargetException unused) {
				// fall through
			} catch (final IllegalAccessException unused) {
				// fall through
			}
		editor.commit();
	}

	/**
	 * Prepares an intent for a DemoActivity.
	 * 
	 * @param intent
	 *            the intent to be used to launch the sublcass of
	 *            {@link DemoActivity}.
	 * @param demoActivityId
	 *            the id that will be used to store the information about the
	 *            'never show again' checkbox.
	 * @param listPoints
	 *            an {@link ArrayList} of {@link LabeledPoint} to Display.
	 */
	public static void prepareDemoActivityIntent(Intent intent,
			String demoActivityId, ArrayList<LabeledPoint> listPoints) {
		intent.putExtra(BUNDLE_KEY_DEMO_ACTIVITY_ID, demoActivityId);
		intent.putParcelableArrayListExtra(
				BUNDLE_KEY_DEMO_ACTIVITY_ARRAY_LIST_POINTS, listPoints);
	}

	/**
	 * Allows to check if a demo activity has been set never to display again.
	 * 
	 * @param caller
	 *            the activity that is calling the {@link DemoActivity}. Its
	 *            {@link SharedPreferences} will be used internally by the
	 *            {@link DemoActivity}.
	 * @param demoActivityId
	 *            the id that will be used to store the information about the
	 *            'never show again' checkbox.
	 */
	public static boolean isNeverShowAgain(Activity caller, int demoActivityId) {
		return caller.getSharedPreferences(SHARED_PREFERENCE_NAME,
				Activity.MODE_PRIVATE).getBoolean(
				String.valueOf(demoActivityId), false);
	}

	/**
	 * Reset a demo activity to show again.
	 * 
	 * @param caller
	 *            the activity that is calling the {@link DemoActivity}. Its
	 *            {@link SharedPreferences} will be used internally by the
	 *            {@link DemoActivity}.
	 * @param demoActivityId
	 *            the id that will be used to store the information about the
	 *            'never show again' checkbox.
	 */
	public static boolean showAgain(Activity caller, String demoActivityId) {
		return caller
				.getSharedPreferences(SHARED_PREFERENCE_NAME,
						Activity.MODE_PRIVATE).edit().remove(demoActivityId)
				.commit();
	}
}
