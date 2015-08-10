package com.stockholmapplab.recipes.util;

import android.graphics.Rect;

/**
 * Class responsible for calculating where the Showcase should position itself
 */
public interface ShowcaseAreaCalculator {

	boolean calculateShowcaseRect(float showcaseX, float showcaseY,
			float originalWidth, float originalHeight);

	Rect getShowcaseRect();
}
