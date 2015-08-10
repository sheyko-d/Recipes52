package com.stockholmapplab.recipes;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.CalorieMenuAdapter;
import com.stockholmapplab.recipes.adapter.ChooseIngredientAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.R;

public class CalorieMenuFragment extends Fragment {
	private static View mView;

	private static ListView mCalorieMenuList;
	public static ArrayList<Integer> mIdsArrayList = new ArrayList<Integer>();
	public static ArrayList<String> mNamesArrayList = new ArrayList<String>();
	public static ArrayList<Integer> mKCalsArrayList = new ArrayList<Integer>();
	public static ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	public static ArrayList<String> mMeasurementValuesArrayList = new ArrayList<String>();
	public static ArrayList<Integer> mMeasurementVolumesArrayList = new ArrayList<Integer>();
	public static ArrayList<Integer> mIsFavoriteArrayList = new ArrayList<Integer>();

	private static CalorieMenuAdapter mCalorieMenuAdapter;


	private String[] mContextArray = { "Select", "Edit", "Delete" };

	public static int selectedPos = -1;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log("create menu fragment " + CalorieMenuActivity.onlyFavorite);

		mView = inflater.inflate(R.layout.fr_choose_ingredient, container,
				false);

		mCalorieMenuAdapter = new CalorieMenuAdapter(
				CalorieMenuActivity.activity, mIdsArrayList, mNamesArrayList,
				mKCalsArrayList, mCategoriesArrayList,
				mMeasurementValuesArrayList, mMeasurementVolumesArrayList,
				mIsFavoriteArrayList);

		((EditText) mView.findViewById(R.id.searchEditTxt))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						CalorieMenuActivity.searchText = s.toString();
						showIngredients();
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

		mCalorieMenuList = (ListView) mView
				.findViewById(R.id.chooseIngredientList);
		mCalorieMenuList.setAdapter(mCalorieMenuAdapter);
		mCalorieMenuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedPos = position;
				registerForContextMenu(mCalorieMenuList);
				getActivity().openContextMenu(mCalorieMenuList);
				unregisterForContextMenu(mCalorieMenuList);
			}
		});

		showIngredients();
		return mView;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.calorie_menu_context, menu);
		if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			menu.findItem(R.id.menu_select).setVisible(false);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_select:
			if (!CalorieMenuAdapter.selectedItemIds.contains(mIdsArrayList
					.get(selectedPos) + "")) {
				CalorieMenuAdapter.selectedItemIds.add(mIdsArrayList
						.get(selectedPos) + "");
			} else {
				CalorieMenuAdapter.selectedItemIds.remove(mIdsArrayList
						.get(selectedPos) + "");
			}
			if (CalorieMenuAdapter.selectedItemIds.size() > 1) {
				((TextView) getActivity().findViewById(
						R.id.addIngredientCheckbox)).setText(BaseApplication
						.getAppContext().getResources()
						.getString(R.string.str_calorie_add_items));
			} else {
				((TextView) getActivity().findViewById(
						R.id.addIngredientCheckbox)).setText(BaseApplication
						.getAppContext().getResources()
						.getString(R.string.str_calorie_add_item));
			}

			mCalorieMenuAdapter.notifyDataSetChanged();
			selectedPos = -1;
			break;
		case R.id.menu_edit:
			startActivity(new Intent(getActivity(),
					CalorieMenuEditActivity.class).putExtra("id",
					mIdsArrayList.get(selectedPos)));
			break;
		case R.id.menu_delete:
			final Dialog errorDialog = new Dialog(getActivity(),
					R.style.DialogTheme);
			errorDialog.setContentView(R.layout.raw_dialog3);
			errorDialog.setCanceledOnTouchOutside(false);

			CustomTextView mTxtErrorTitle = (CustomTextView) errorDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtErrorMessage = (CustomTextView) errorDialog
					.findViewById(R.id.tv_txt_guide2);
			CustomButton mBtnCancel = (CustomButton) errorDialog
					.findViewById(R.id.bt_btn_Cancel);
			CustomButton mBtnOK = (CustomButton) errorDialog
					.findViewById(R.id.bt_btn_ok);

			mTxtErrorTitle.setText(getActivity().getResources().getString(
					R.string.str_dialog_4));
			mTxtErrorMessage.setText(getActivity().getResources().getString(
					R.string.str_dialog_4_1)
					+ " "
					+ mNamesArrayList.get(selectedPos)
					+ " "
					+ getActivity().getResources().getString(
							R.string.str_dialog_4_2));
			mBtnCancel.setText(R.string.str_dialog_5);
			mBtnOK.setText(R.string.str_dialog_2);

			mBtnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					errorDialog.dismiss();
				}
			});

			mBtnOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SQLiteDatabase DB = new DBHelper(BaseApplication
							.getAppContext()).getReadableDatabase();
					DB.delete("ingredients",
							"id='" + mIdsArrayList.get(selectedPos) + "'", null);
					DB.close();

					showIngredients();

					errorDialog.dismiss();
				}
			});
			errorDialog.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private static class GetIngredientsTask extends AsyncTask<Void, Void, Void> {

		private String mFavoriteQuery;
		private String mSearchTextQuery;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();// Set adapter to list and invalidate it

			mIdsArrayList.clear();
			mNamesArrayList.clear();
			mKCalsArrayList.clear();
			mCategoriesArrayList.clear();
			mMeasurementValuesArrayList.clear();
			mMeasurementVolumesArrayList.clear();
			mIsFavoriteArrayList.clear();
			SQLiteDatabase db = new DBHelper(BaseApplication.getAppContext())
					.getReadableDatabase();
			if (CalorieMenuActivity.searchText.equals("")) {
				mSearchTextQuery = "";
			} else {
				if (BaseApplication.isSwedish()) {
					mSearchTextQuery = "title_sw LIKE '%" + CalorieMenuActivity.searchText
							+ "%'  AND ";
				} else {
					mSearchTextQuery = "title_en LIKE '%" + CalorieMenuActivity.searchText
							+ "%'  AND ";
				}
			}
			if (CalorieMenuActivity.onlyFavorite) {
				mFavoriteQuery = "is_favorite=1";
			} else {
				mFavoriteQuery = "(is_favorite=0 OR is_favorite=1)";
			}
			Cursor cursor = db.query("ingredients", null, mSearchTextQuery
					+ mFavoriteQuery, null, null, null,
					CalorieMenuActivity.sortBy);
			while (cursor.moveToNext()) {
				mIdsArrayList.add(cursor.getInt(cursor.getColumnIndex("id")));
				if (BaseApplication.isSwedish()) {
					mNamesArrayList.add(cursor.getString(cursor
							.getColumnIndex("title_sw")));
				} else {
					mNamesArrayList.add(cursor.getString(cursor
							.getColumnIndex("title_en")));
				}
				mKCalsArrayList
						.add(cursor.getInt(cursor.getColumnIndex("kcal")));
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
		}

		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mCalorieMenuAdapter.notifyDataSetChanged();
			Log("added "+mIdsArrayList.size()+" items to calorie menu");
			Log("added to "+mCalorieMenuList);
			if (mIdsArrayList.size() > 0) {
				CalorieMenuActivity.activity.findViewById(R.id.noResultsTxt)
						.setVisibility(View.GONE);
				mCalorieMenuList.setVisibility(View.VISIBLE);
			} else {
				CalorieMenuActivity.activity.findViewById(R.id.noResultsTxt)
						.setVisibility(View.VISIBLE);
				mCalorieMenuList.setVisibility(View.GONE);
			}
			mCalorieMenuAdapter.notifyDataSetChanged();
		}
	}

	public static void showIngredients() {
		new GetIngredientsTask().execute();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}