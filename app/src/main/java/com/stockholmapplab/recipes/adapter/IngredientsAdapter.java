package com.stockholmapplab.recipes.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stockholmapplab.recipes.R;

public class IngredientsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<String> mTitlesArrayList;
	private ArrayList<String> mIngMVArrayList;
	private ArrayList<String> mIngMArrayList;
	private ArrayList<String> mIngKcalArrayList;
	private Context mContext;
	private String mKCal;
	private TextView mItemView;
	public static int layout = R.layout.item_ingredient;

	public IngredientsAdapter(Context context, ArrayList<String> titles,
			ArrayList<String> measurementValues,
			ArrayList<String> measurements, ArrayList<String> calories) {
		mContext = context;
		mTitlesArrayList = titles;
		mIngMVArrayList = measurementValues;
		mIngMArrayList = measurements;
		mIngKcalArrayList = calories;
	}

	public int getCount() {
		// If list is expanded
		if (layout == R.layout.item_ingredient) {
			return mTitlesArrayList.size() + 1;
		} else {
			return mTitlesArrayList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// If last item in list - add "Show full list" text
		if (position == (getCount() - 1) & layout == R.layout.item_ingredient) {
			mItemView = (TextView) inflater.inflate(
					R.layout.item_ingredient_expand, parent, false);
		} else {
			mItemView = (TextView) inflater.inflate(layout, parent, false);

			if (mIngKcalArrayList.get(position).equals("0")) {
				mKCal = "";
			} else {
				mKCal = ", " + mIngKcalArrayList.get(position) + " kcal";
			}
			try {
				mItemView.setText(mTitlesArrayList.get(position) + ", "
						+ mIngMVArrayList.get(position) + " "
						+ mIngMArrayList.get(position) + mKCal);
			} catch (Exception e) {
				mItemView.setText("n/a");
				Log(e);
			}
		}
		if (position % 2 == 1) {
			mItemView.setBackgroundResource(R.color.cream);
		} else {
			mItemView.setBackgroundResource(R.color.list_green_dark);
		}
		return mItemView;
	}

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}
}
