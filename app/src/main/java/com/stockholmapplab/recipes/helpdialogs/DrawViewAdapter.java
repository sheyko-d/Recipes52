package com.stockholmapplab.recipes.helpdialogs;

import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.widget.BaseAdapter;

/**
 * Base class for creating a {@link DrawViewAdapter}. It holds a collection of
 * {@link LabeledPoint} to render. This adapter will provide a {@link DrawView}
 * with all informations needed to render a given {@link LabeledPoint}. It is
 * responsible for associating a {@link Drawable}, a {@link TextPaint} and every
 * other data needed at rendering time.
 * 
 * This class must be subclassed and works more or less like an Android
 * {@link BaseAdapter}.
 * 
 * @see DrawView#setDrawViewAdapter(DrawViewAdapter)
 * 
 */
public interface DrawViewAdapter {

	/**
	 * @return the number of {@link LabeledPoint} to draw.
	 */
	public int getPointsCount();

	/**
	 * Give the {@link Drawable} of the {@link LabeledPoint} at a given
	 * position.
	 * 
	 * @param position
	 *            the position of the {@link LabeledPoint} to render.
	 * @return the {@link Drawable} of the {@link LabeledPoint} at a given
	 *         position.
	 */
	public Drawable getDrawableAt(int position);

}