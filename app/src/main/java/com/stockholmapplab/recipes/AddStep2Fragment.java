package com.stockholmapplab.recipes;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class AddStep2Fragment extends Fragment {

	private View mRoot;
	public static TextView mCategoryTxt;
	public static TextView mTypeTxt;
	public static TextView mTimeTxt;
	public static ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	public static ArrayList<String> mSubcategoriesArrayList = new ArrayList<String>();
	private ArrayList<Integer> mCategoriesIdsArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mSubcategoriesIdsArrayList = new ArrayList<Integer>();

	public static AddStep2Fragment newInstance() {
		AddStep2Fragment f = new AddStep2Fragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fr_add_recipe_2, container, false);

		mCategoryTxt = (TextView) mRoot.findViewById(R.id.addCategoryTxt);
		mCategoryTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openCategoryDialog();
			}
		});

		mTypeTxt = (TextView) mRoot.findViewById(R.id.addTypeTxt);
		mTypeTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openTypeDialog();
			}
		});

		mTimeTxt = (TextView) mRoot.findViewById(R.id.addTimeTxt);
		mTimeTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openTimeDialog();
			}
		});
		// Get categories array
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("categories", null, "use_in_add_screen=1",
				null, null, null, null);
		mCategoriesArrayList.clear();
		mCategoriesIdsArrayList.clear();
		mSubcategoriesArrayList.clear();
		mSubcategoriesIdsArrayList.clear();
		Resources resources = BaseApplication.getAppContext().getResources();
		while (cursor.moveToNext()) {
			mCategoriesArrayList
					.add(resources.getString(resources.getIdentifier(cursor
							.getString(cursor.getColumnIndex("title_id")),
							"string", BaseApplication.getAppContext()
									.getPackageName())));
			mCategoriesIdsArrayList.add(cursor.getInt(cursor
					.getColumnIndex("id")));
		}
		cursor = DB.query("subcategories", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			mSubcategoriesArrayList
					.add(resources.getString(resources.getIdentifier(cursor
							.getString(cursor.getColumnIndex("title_id")),
							"string", BaseApplication.getAppContext()
									.getPackageName())));
			mSubcategoriesIdsArrayList.add(cursor.getInt(cursor
					.getColumnIndex("id")));
		}
		cursor.close();
		DB.close();

		mCategoryTxt.setText(mCategoriesArrayList.get(PrefHelper.getInt(
				"AddCategoryPos", mCategoriesArrayList.size() - 1)));

		mTypeTxt.setText(mSubcategoriesArrayList.get(PrefHelper.getInt(
				"AddTypePos", mSubcategoriesArrayList.size() - 1)));

		String hours;
		if (PrefHelper.getInt("AddH", 0) == 0) {
			hours = "";
		} else {
			hours = PrefHelper.getInt("AddH", 0) + "h, ";
		}
		String min = PrefHelper.getInt("AddMin", 0) + "min";
		mTimeTxt.setText(hours + min);
		return mRoot;
	}

	private void openCategoryDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity());

		// Set adapter to list in dialog
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				BaseApplication.getAppContext(),
				R.layout.item_single_selection, mCategoriesArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter, PrefHelper.getInt(
				"AddCategoryPos", mCategoriesArrayList.size() - 1),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mCategoryTxt.setText(arrayAdapter.getItem(which));
						// Dismiss dialog
						dialog.dismiss();
						PrefHelper.setInt("AddCategoryPos", which);
						PrefHelper.setInt("AddCategoryId",
								mCategoriesIdsArrayList.get(which));
					}
				});
		dialogBuilder.show();
	}

	private void openTypeDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity());

		// Set adapter to list in dialog
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				BaseApplication.getAppContext(),
				R.layout.item_single_selection, mSubcategoriesArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter, PrefHelper.getInt(
				"AddTypePos", mSubcategoriesArrayList.size() - 1),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mTypeTxt.setText(arrayAdapter.getItem(which));
						// Dismiss dialog
						dialog.dismiss();
						PrefHelper.setInt("AddTypePos", which);
						PrefHelper.setInt("AddTypeId",
								mSubcategoriesIdsArrayList.get(which));
					}
				});
		dialogBuilder.show();
	}

	private void openTimeDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity());

		View dialogView = getActivity().getLayoutInflater().inflate(
				R.layout.raw_dialog_time, null);
		final TimePicker timePicker = (TimePicker) dialogView
				.findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(PrefHelper.getInt("AddH", 0));
		timePicker.setCurrentMinute(PrefHelper.getInt("AddMin", 0));
		dialogBuilder.setView(dialogView);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		// Add "OK" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String hours;
						if (timePicker.getCurrentHour() == 0) {
							hours = "";
						} else {
							hours = timePicker.getCurrentHour() + "h, ";
						}
						String min = timePicker.getCurrentMinute() + "min";
						mTimeTxt.setText(hours + min);
						PrefHelper.setInt("AddH", timePicker.getCurrentHour());
						PrefHelper.setInt("AddMin",
								timePicker.getCurrentMinute());
					}
				});
		dialogBuilder.show();
	}
}