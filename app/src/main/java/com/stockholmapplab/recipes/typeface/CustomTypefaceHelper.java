package com.stockholmapplab.recipes.typeface;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Helper for acquiring font from assets. Lazy loads and keeps fonts cached.
 * 
 * Should be thread-safe since a font is generally a read-only thing.
 */
public class CustomTypefaceHelper {

	private static final HashMap<String, Typeface> TYPEFACE_CACHE = new HashMap<String, Typeface>();

	/** Retrieve a type-face. Does not load twice, uses lazy loading. */
	public static Typeface getTypeface(Context context, String name) {
		// ensure the global context is used. just in case.
		context = context.getApplicationContext();
		if (TYPEFACE_CACHE.containsKey(name)) {
			return TYPEFACE_CACHE.get(name);
		}

		
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), name);

		if (typeface != null) {
			TYPEFACE_CACHE.put(name, typeface);
		}

		return typeface;
	}
}