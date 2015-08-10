package com.stockholmapplab.recipes.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stockholmapplab.recipes.AddStep3Fragment;
import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;

public class AddedIngredientsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Integer> mPosWithDeleteModeArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mIdsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mTitlesEnArrayList = new ArrayList<String>();
	private ArrayList<Integer> mCategoriesArrayList = new ArrayList<Integer>();
	private ArrayList<String> mTitlesSvArrayList = new ArrayList<String>();
	private ArrayList<Integer> mKCalsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mMeasurementValuesArrayList = new ArrayList<String>();
	private ArrayList<Integer> mMeasurementArrayList = new ArrayList<Integer>();
	private Context mContext;
	private View mItemView;

	public AddedIngredientsAdapter(Context context) {
		mContext = context;
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("add_ingredients", null, null, null, null,
				null, null);
		Resources resources = context.getResources();
		while (cursor.moveToNext()) {
			mIdsArrayList.add(cursor.getInt(cursor.getColumnIndex("id")));
			mTitlesEnArrayList.add(cursor.getString(cursor
					.getColumnIndex("title_en")));
			mTitlesSvArrayList.add(cursor.getString(cursor
					.getColumnIndex("title_sw")));
			mCategoriesArrayList.add(cursor.getInt(cursor
					.getColumnIndex("category")));
			mKCalsArrayList.add(cursor.getInt(cursor.getColumnIndex("kcal")));
			mMeasurementArrayList.add(cursor.getInt(cursor
					.getColumnIndex("measurement")));
			Cursor measurementCursor = DB.query("measurements", null, "id='"
					+ cursor.getInt(cursor.getColumnIndex("measurement_value"))
					+ "'", null, null, null, null);
			if (measurementCursor.moveToFirst()) {
				mMeasurementValuesArrayList.add(resources.getString(resources
						.getIdentifier(measurementCursor
								.getString(measurementCursor
										.getColumnIndex("title_id")), "string",
								context.getPackageName())));
			}
			measurementCursor.close();
		}
		cursor.close();
		DB.close();

		if (getCount() == 0) {
			AddStep3Fragment.emptyTxt.setVisibility(View.VISIBLE);
		} else {
			AddStep3Fragment.emptyTxt.setVisibility(View.GONE);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		mIdsArrayList.clear();
		mTitlesEnArrayList.clear();
		mTitlesSvArrayList.clear();
		mCategoriesArrayList.clear();
		mKCalsArrayList.clear();
		mMeasurementArrayList.clear();
		mMeasurementValuesArrayList.clear();
		Integer totalKCal = 0;
		Resources resources = mContext.getResources();
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("add_ingredients", null, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			mIdsArrayList.add(cursor.getInt(cursor.getColumnIndex("id")));
			mTitlesEnArrayList.add(cursor.getString(cursor
					.getColumnIndex("title_en")));
			mTitlesSvArrayList.add(cursor.getString(cursor
					.getColumnIndex("title_sw")));
			mCategoriesArrayList.add(cursor.getInt(cursor
					.getColumnIndex("category")));
			Integer kCal = cursor.getInt(cursor.getColumnIndex("kcal"));
			mKCalsArrayList.add(kCal);
			totalKCal += kCal;
			mMeasurementArrayList.add(cursor.getInt(cursor
					.getColumnIndex("measurement")));
			Cursor measurementCursor = DB.query("measurements", null, "id='"
					+ cursor.getInt(cursor.getColumnIndex("measurement_value"))
					+ "'", null, null, null, null);
			Log("measurement_value = "
					+ cursor.getInt(cursor.getColumnIndex("measurement_value")));
			if (measurementCursor.moveToFirst()) {
				mMeasurementValuesArrayList.add(resources.getString(resources
						.getIdentifier(measurementCursor
								.getString(measurementCursor
										.getColumnIndex("title_id")), "string",
								BaseApplication.getAppContext()
										.getPackageName())));
			}
			measurementCursor.close();
		}
		cursor.close();
		DB.close();

		PrefHelper.setInt("AddTotalKCal", totalKCal);
		if (PrefHelper.getBoolean("AddCalculateTotalKCal", true)) {
			AddStep3Fragment.totalKCalTxt.setText(totalKCal + "");
		}

		if (mIdsArrayList.size() == 0) {
			AddStep3Fragment.emptyTxt.setVisibility(View.VISIBLE);
			AddStep3Fragment.headerTitleTxt.setVisibility(View.GONE);
		} else {
			AddStep3Fragment.emptyTxt.setVisibility(View.GONE);
			AddStep3Fragment.headerTitleTxt.setVisibility(View.VISIBLE);
		}
		super.notifyDataSetChanged();
	}

	public int getCount() {
		return mIdsArrayList.size();
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
		mItemView = (View) inflater.inflate(R.layout.item_added_ingredient,
				parent, false);
		mItemView.setTag(position);

		if (BaseApplication.isSwedish()) {
			((TextView) mItemView.findViewById(R.id.addedIngredientTitleTxt))
					.setText(mTitlesSvArrayList.get(position) + ", "
							+ mMeasurementArrayList.get(position) + " "
							+ mMeasurementValuesArrayList.get(position) + ", "
							+ +mKCalsArrayList.get(position) + " kcal");
		} else {
			((TextView) mItemView.findViewById(R.id.addedIngredientTitleTxt))
					.setText(mTitlesEnArrayList.get(position) + ", "
							+ mMeasurementArrayList.get(position) + " "
							+ mMeasurementValuesArrayList.get(position) + ", "
							+ +mKCalsArrayList.get(position) + " kcal");
		}

		mItemView.findViewById(R.id.addedIngredientDeleteImg)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Integer position = Integer.parseInt(((View) v
								.getParent()).getTag() + "");

						View deleteBtn = ((View) v.getParent())
								.findViewById(R.id.addedIngredientDeleteBtn);
						if (mPosWithDeleteModeArrayList.contains(position)) {
							mPosWithDeleteModeArrayList.remove(position);
							Animation rotateAnimation = AnimationUtils
									.loadAnimation(mContext,
											R.anim.rotate_90_back);
							rotateAnimation.setFillAfter(true);
							v.startAnimation(rotateAnimation);
							deleteBtn.setVisibility(View.GONE);
						} else {
							mPosWithDeleteModeArrayList.add(position);
							Animation rotateAnimation = AnimationUtils
									.loadAnimation(mContext, R.anim.rotate_90);
							rotateAnimation.setFillAfter(true);
							v.startAnimation(rotateAnimation);
							deleteBtn.setVisibility(View.VISIBLE);
						}
					}
				});

		mItemView.findViewById(R.id.addedIngredientDeleteBtn)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Integer position = Integer.parseInt(((View) v
								.getParent()).getTag() + "");
						SQLiteDatabase DB = new DBHelper(BaseApplication
								.getAppContext()).getReadableDatabase();
						DB.delete("add_ingredients",
								"id='" + mIdsArrayList.get(position) + "'",
								null);
						DB.close();
						notifyDataSetChanged();
					}
				});

		return mItemView;

	}

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}

}
