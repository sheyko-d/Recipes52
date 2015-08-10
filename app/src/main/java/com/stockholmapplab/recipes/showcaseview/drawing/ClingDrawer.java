package com.stockholmapplab.recipes.showcaseview.drawing;

import android.graphics.Canvas;

import com.stockholmapplab.recipes.util.ShowcaseAreaCalculator;

public interface ClingDrawer extends ShowcaseAreaCalculator {
	void eraseCircle(Canvas canvas, float x, float y, float radius);

	void scale(Canvas canvas, float x, float y, float scaleMultiplier);

	void revertScale(Canvas canvas);

	void drawCling(Canvas canvas);

	int getShowcaseWidth();

	int getShowcaseHeight();

}
