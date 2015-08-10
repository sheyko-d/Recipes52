package com.stockholmapplab.recipes.adapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.ChooseIngredientFragment;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.util.DBHelper;

public class ChooseIngredientAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Integer> mIdsArrayList;
	private ArrayList<String> mTitlesArrayList;
	private ArrayList<Integer> mKCalsArrayList;
	private ArrayList<String> mCategoriesArrayList;
	private ArrayList<String> mMeasurmentValuesArrayList;
	private ArrayList<Integer> mMeasurmentVolumesArrayList;
	private ArrayList<Integer> mIsFavoriteArrayList;
	private Context mContext;
	private View mItemView;
	private SQLiteDatabase mDB;
	private String mMeasurement;
	public static int selectedItemId = -1;

	public ChooseIngredientAdapter(Context context, ArrayList<Integer> ids,
			ArrayList<String> titles, ArrayList<Integer> kcals,
			ArrayList<String> categories, ArrayList<String> measurmentValues,
			ArrayList<Integer> measurmentVolumes, ArrayList<Integer> isFavorites) {
		mContext = context;
		mIdsArrayList = ids;
		mTitlesArrayList = titles;
		mKCalsArrayList = kcals;
		mCategoriesArrayList = categories;
		mMeasurmentValuesArrayList = measurmentValues;
		mMeasurmentVolumesArrayList = measurmentVolumes;
		mIsFavoriteArrayList = isFavorites;

		mDB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		selectedItemId = -1;
	}

	public int getCount() {
		return mTitlesArrayList.size();
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
		mItemView = (View) inflater.inflate(R.layout.item_choose_ingredient,
				parent, false);
		if ((position % 2) == 0) {
			mItemView.setBackgroundResource(R.drawable.list_light_selector);
		} else {
			mItemView.setBackgroundResource(R.drawable.list_dark_selector);
		}

		if (selectedItemId == mIdsArrayList.get(position)) {
			mItemView.setBackgroundResource(R.drawable.list_focused);
			((CheckBox) mItemView.findViewById(R.id.ingredientCheckbox))
					.setChecked(true);
		}

		((TextView) mItemView.findViewById(R.id.ingredientTitleTxt))
				.setText(mTitlesArrayList.get(position));
		((TextView) mItemView.findViewById(R.id.ingredientKCalTxt))
				.setText(mKCalsArrayList.get(position) + "");
		Cursor cursor = mDB.query("categories", null, "id='"
				+ mCategoriesArrayList.get(position) + "'", null, null, null,
				null);

		Resources resources = BaseApplication.getAppContext().getResources();

		if (cursor.moveToFirst()) {
			((TextView) mItemView.findViewById(R.id.ingredientCategoryTxt))
					.setText(resources.getString(resources.getIdentifier(cursor
							.getString(cursor.getColumnIndex("title_id")),
							"string", BaseApplication.getAppContext()
									.getPackageName())));
		}
		cursor.close();
		Cursor measurementCursor = mDB.query("measurements", null, "id='"
				+ mMeasurmentValuesArrayList.get(position) + "'", null, null,
				null, null);
		if (measurementCursor.moveToFirst()) {
			mMeasurement = resources.getString(resources.getIdentifier(
					measurementCursor.getString(measurementCursor
							.getColumnIndex("title_id")), "string",
					BaseApplication.getAppContext().getPackageName()));
		}
		((TextView) mItemView.findViewById(R.id.ingredientMeasurmentUnitTxt))
				.setText(mMeasurmentVolumesArrayList.get(position)
						+ mMeasurement);

		mItemView.findViewById(R.id.starImg)
				.setTag(mIdsArrayList.get(position));

		mItemView.findViewById(R.id.starImg).setOnClickListener(
				starClickListener);

		if (mIsFavoriteArrayList.get(position) == 1) {
			((ImageView) mItemView.findViewById(R.id.starImg))
					.setImageResource(R.drawable.ic_star_enabled);
		}

		return mItemView;

	}

	OnClickListener starClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SQLiteDatabase db = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			Cursor c = db.query("ingredients", null, "id='" + v.getTag() + "'",
					null, null, null, null);
			if (c.moveToFirst()) {
				// If ingredient is marked as favorite
				ContentValues cv = new ContentValues();
				if (c.getInt(c.getColumnIndex("is_favorite")) > 0) {
					cv.put("is_favorite", "0");
					if (!ChooseIngredientFragment.onlyFavorite) {
						((ImageView) v)
								.setImageResource(R.drawable.ic_star_disabled);
					}
				} else {
					cv.put("is_favorite", "1");
					((ImageView) v)
							.setImageResource(R.drawable.ic_star_enabled);
				}

				db.update("ingredients", cv, "id='" + v.getTag() + "'", null);
			} else {

			}
			c.close();
			db.close();
			if (ChooseIngredientFragment.onlyFavorite) {
				ChooseIngredientFragment.showIngredients();
			}
		}
	};

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}

}
