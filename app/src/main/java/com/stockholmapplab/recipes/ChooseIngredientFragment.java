package com.stockholmapplab.recipes;

import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import com.stockholmapplab.recipes.adapter.ChooseIngredientAdapter;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.R;

public class ChooseIngredientFragment extends Fragment {
	private static View mView;

	private static ListView mChooseIngredientList;
	private static ArrayList<Integer> mIdsArrayList = new ArrayList<Integer>();
	private static ArrayList<String> mNamesArrayList = new ArrayList<String>();
	private static ArrayList<Integer> mKCalsArrayList = new ArrayList<Integer>();
	private static ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private static ArrayList<String> mMeasurementValuesArrayList = new ArrayList<String>();
	private static ArrayList<Integer> mMeasurementVolumesArrayList = new ArrayList<Integer>();
	private static ArrayList<Integer> mIsFavoriteArrayList = new ArrayList<Integer>();

	private static ChooseIngredientAdapter mChooseIngredientAdapter;

	public static String searchText = "";
	public static boolean onlyFavorite = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.fr_choose_ingredient, container,
				false);
		ChooseIngredientActivity.addIngredientCheckbox.setEnabled(false);

		mChooseIngredientAdapter = new ChooseIngredientAdapter(
				ChooseIngredientActivity.activity, mIdsArrayList,
				mNamesArrayList, mKCalsArrayList, mCategoriesArrayList,
				mMeasurementValuesArrayList, mMeasurementVolumesArrayList,
				mIsFavoriteArrayList);

		// Set adapter to list and invalidate it
		mChooseIngredientList = (ListView) mView
				.findViewById(R.id.chooseIngredientList);

		if (savedInstanceState != null) {
			Log("restore id");
			// Restore value of members from saved state
			ChooseIngredientActivity.selectedItemId = savedInstanceState
					.getInt("ChooseIngredientActivity.selectedItemId", -1);
		}
		Log("Fragment is created");
		Log("ChooseIngredientActivity.selectedItemId = "
				+ ChooseIngredientActivity.selectedItemId);
		Log("ChooseIngredientAdapter.selectedItemId = "
				+ ChooseIngredientAdapter.selectedItemId);

		if (ChooseIngredientActivity.selectedItemId != -1) {
			ChooseIngredientAdapter.selectedItemId = ChooseIngredientActivity.selectedItemId;
			mChooseIngredientAdapter.notifyDataSetChanged();
			BaseApplication.fadeIn(ChooseIngredientActivity.addIngredientCheckbox);
		} else {
			BaseApplication.fadeOut(ChooseIngredientActivity.addIngredientCheckbox);
		}

		mChooseIngredientList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mIdsArrayList.get(position) == ChooseIngredientAdapter.selectedItemId) {
					ChooseIngredientAdapter.selectedItemId = -1;
					ChooseIngredientActivity.selectedItemId = -1;
				} else {
					ChooseIngredientAdapter.selectedItemId = mIdsArrayList
							.get(position);
					ChooseIngredientActivity.selectedItemId = mIdsArrayList
							.get(position);
				}
				if (ChooseIngredientAdapter.selectedItemId == -1) {
					BaseApplication.fadeOut(ChooseIngredientActivity.addIngredientCheckbox);
				} else {
					BaseApplication.fadeIn(ChooseIngredientActivity.addIngredientCheckbox);
				}
				mChooseIngredientAdapter.notifyDataSetChanged();
			}
		});

		((EditText) mView.findViewById(R.id.searchEditTxt))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						searchText = s.toString();
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

		showIngredients();

		return mView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("ChooseIngredientActivity.selectedItemId",
				ChooseIngredientActivity.selectedItemId);
		super.onSaveInstanceState(savedInstanceState);
	}

	private static String mFavoriteQuery;
	private static String mSearchTextQuery;

	public static void showIngredients() {
		SQLiteDatabase db = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		if (searchText.equals("")) {
			mSearchTextQuery = "";
		} else {
			mSearchTextQuery = "title_en LIKE '%" + searchText + "%'  AND ";
		}
		if (onlyFavorite) {
			mFavoriteQuery = "is_favorite=1";
		} else {
			mFavoriteQuery = "(is_favorite=0 OR is_favorite=1)";
		}
		Cursor cursor = db.query("ingredients", null, mSearchTextQuery
				+ mFavoriteQuery, null, null, null,
				ChooseIngredientActivity.sortBy);
		mIdsArrayList.clear();
		mNamesArrayList.clear();
		mKCalsArrayList.clear();
		mCategoriesArrayList.clear();
		mMeasurementValuesArrayList.clear();
		mMeasurementVolumesArrayList.clear();
		mIsFavoriteArrayList.clear();
		while (cursor.moveToNext()) {
			mIdsArrayList.add(cursor.getInt(cursor.getColumnIndex("id")));
			if (BaseApplication.isSwedish()) {
				mNamesArrayList.add(cursor.getString(cursor
						.getColumnIndex("title_sw")));
			} else {
				mNamesArrayList.add(cursor.getString(cursor
						.getColumnIndex("title_en")));
			}
			mKCalsArrayList.add(cursor.getInt(cursor.getColumnIndex("kcal")));
			mCategoriesArrayList.add(cursor.getString(cursor
					.getColumnIndex("category")));
			mMeasurementValuesArrayList.add(cursor.getString(cursor
					.getColumnIndex("measurement_value")));
			mMeasurementVolumesArrayList.add(cursor.getInt(cursor
					.getColumnIndex("measurement_volume")));
			mIsFavoriteArrayList.add(cursor.getInt(cursor
					.getColumnIndex("is_favorite")));
		}
		cursor.close();
		db.close();
		if (mIdsArrayList.size() > 0) {
			ChooseIngredientActivity.activity.findViewById(R.id.noResultsTxt)
					.setVisibility(View.GONE);
			mChooseIngredientList.setAdapter(mChooseIngredientAdapter);
			mChooseIngredientAdapter.notifyDataSetChanged();
			mChooseIngredientList.setVisibility(View.VISIBLE);
		} else {
			ChooseIngredientActivity.activity.findViewById(R.id.noResultsTxt)
					.setVisibility(View.VISIBLE);
			mChooseIngredientList.setVisibility(View.GONE);
			mChooseIngredientList.setAdapter(null);
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}