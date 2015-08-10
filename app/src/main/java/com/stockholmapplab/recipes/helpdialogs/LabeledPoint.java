package com.stockholmapplab.recipes.helpdialogs;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * A pojo class that wraps all information needed to display a point on screen.
 * {@link LabeledPoint} embed a position and a text to draw. The
 * {@link Drawable} that illustrates this point will be provided by
 * {@link DrawView#getDrawViewAdapter()}. This class implements
 * {@link Parcelable} so that it can be passed between the Activity to
 * illustrate and the {@link DemoActivity}.
 */
@SuppressLint("NewApi")
public class LabeledPoint extends Point implements Parcelable {

	/** The text associated to this point. */
	private String text;
	private int width;
	private int height;

	/**
	 * Creates an empty {@link LabeledPoint}.
	 */
	public LabeledPoint() {
	}

	/**
	 * Creates a {@link LabeledPoint} at a given location, with a null text.
	 * 
	 * @param x
	 *            the new x coordinate of the point.
	 * @param y
	 *            the new y coordinate of the point.
	 */
	public LabeledPoint(int x, int y) {
		this(x, y, null);
	}

	/**
	 * Creates a {@link LabeledPoint} at a given location, with a given text.
	 * 
	 * @param x
	 *            the new x coordinate of the point.
	 * @param y
	 *            the new y coordinate of the point.
	 * @param text
	 *            then new text of the point.
	 */
	public LabeledPoint(int x, int y, String text) {
		this.x = x;
		this.y = y;
		setText(text);
	}

	/**
	 * Creates a {@link LabeledPoint} at a given location, with a null text.
	 * 
	 * @param src
	 *            a point to get the coordinates from.
	 */
	public LabeledPoint(Point src) {
		this(src, null);
	}

	/**
	 * Creates a {@link LabeledPoint} at a given location, with a given text.
	 * 
	 * @param src
	 *            a point to get the coordinates from.
	 * @param text
	 *            the new text of the point.
	 */
	public LabeledPoint(Point src, String text) {
		super(src);
		setText(text);
	}

	/**
	 * Creates a {@link LabeledPoint} located at the center of a given view,
	 * with a null text.
	 * 
	 * @param view
	 *            the view on which to center the point.
	 */
	public LabeledPoint(View v) {
		this(v, 50, 50, null);
	}

	/**
	 * Creates a {@link LabeledPoint} located at the center of a given view,
	 * with a given text.
	 * 
	 * @param view
	 *            the view on which to center the point.
	 * @param text
	 *            the new text of the point.
	 */
	public LabeledPoint(View v, String text) {
		this(v, 50, 50, text);
	}

	/**
	 * Creates a {@link LabeledPoint} positioned relatively to a given view,
	 * with no given text.
	 * 
	 * @param view
	 *            the view on which to center the point.
	 * @param widthPercent
	 *            the percent of the view width at which to place the new point.
	 * 
	 * @param heightPercent
	 *            the percent of the view height at which to place the new
	 *            point.
	 * 
	 */
	public LabeledPoint(View v, float widthPercent, float heightPercent) {
		this(v, widthPercent, heightPercent, null);
	}

	/**
	 * Creates a {@link LabeledPoint} positioned relatively to a given view,
	 * with a given text.
	 * 
	 * @param view
	 *            the view on which to center the point.
	 * @param widthPercent
	 *            the percent of the view width at which to place the new point.
	 * 
	 * @param heightPercent
	 *            the percent of the view height at which to place the new
	 *            point.
	 * 
	 * @param text
	 *            the new text of the point.
	 */
	public LabeledPoint(View v, float widthPercent, float heightPercent,
			String text) {

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		this.x = location[0]
				+ Math.round(widthPercent * v.getMeasuredWidth() / 100);
		this.y = location[1]
				+ Math.round(heightPercent * v.getMeasuredHeight() / 100);
		setText(text);

		setWidth(v.getMeasuredWidth());
		setHeight(v.getMeasuredHeight());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Write this point to the specified parcel. To restore a point from a
	 * parcel, use readFromParcel()
	 * 
	 * @param out
	 *            The parcel to write the point's coordinates into
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(x);
		out.writeInt(y);
		out.writeString(text);
		out.writeInt(width);
		out.writeInt(height);
	}

	/**
	 * Parcel creator for points.
	 */
	public static final Parcelable.Creator<LabeledPoint> CREATOR = new Parcelable.Creator<LabeledPoint>() {
		/**
		 * Return a new point from the data in the specified parcel.
		 */
		@Override
		public LabeledPoint createFromParcel(Parcel in) {
			LabeledPoint r = new LabeledPoint();
			r.readFromParcel(in);
			return r;
		}

		/**
		 * Return an array of rectangles of the specified size.
		 */
		@Override
		public LabeledPoint[] newArray(int size) {
			return new LabeledPoint[size];
		}
	};

	/**
	 * Set the point's coordinates from the data stored in the specified parcel.
	 * To write a point to a parcel, call writeToParcel().
	 * 
	 * @param in
	 *            The parcel to read the point's coordinates from
	 */
	@Override
	public void readFromParcel(Parcel in) {
		x = in.readInt();
		y = in.readInt();
		text = in.readString();
		width = in.readInt();
		height = in.readInt();

	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}