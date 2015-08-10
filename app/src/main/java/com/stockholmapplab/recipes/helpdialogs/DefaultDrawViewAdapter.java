package com.stockholmapplab.recipes.helpdialogs;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.stockholmapplab.recipes.R;

/**
 * Sample DrawViewAdapter.
 */
public class DefaultDrawViewAdapter implements DrawViewAdapter {
	private Drawable drawable;
	private List<LabeledPoint> listPoints;
	private Context context;

	/**
	 * Constructor
	 * 
	 * 
	 * @param context
	 *            - Adapter context
	 * @param listPoints
	 *            - List of Label Points
	 */
	public DefaultDrawViewAdapter(Context context, List<LabeledPoint> listPoints) {
		this.context = context;
		this.drawable = context.getResources().getDrawable(
				R.drawable.showcase_transparent);
		this.listPoints = listPoints;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - Adapter context
	 * @param drawable
	 *            - drawable
	 * @param textPaint
	 *            - paint Extension that leaves room for some extra data used
	 *            during text measuring and drawing.
	 * @param listPoints
	 *            - List of Label Points
	 */
	public DefaultDrawViewAdapter(Context context, Drawable drawable,
			TextPaint textPaint, List<LabeledPoint> listPoints) {
		this.context = context;
		this.drawable = drawable;
		this.listPoints = listPoints;
	}

	/**
	 * context
	 * 
	 * @return context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * counts label points.
	 */
	@Override
	public int getPointsCount() {
		return listPoints.size();
	}

	/**
	 * Get Drawable at specified position.
	 */
	@Override
	public Drawable getDrawableAt(int position) {

		Point point = listPoints.get(position);
		int width = listPoints.get(position).getWidth();
		int height = listPoints.get(position).getHeight();

		drawable.setBounds(point.x - width / 2, point.y - height / 2, point.x
				+ width / 2, point.y + height / 2);
		return drawable;
	}
}