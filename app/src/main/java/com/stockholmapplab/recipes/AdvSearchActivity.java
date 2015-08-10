package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class AdvSearchActivity extends ActionBarActivity {

	public static Activity activity;
	protected String mCalorie;
	private ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private ArrayList<Integer> mCategoriesIdsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mSubcategoriesArrayList = new ArrayList<String>();
	private ArrayList<Integer> mSubcategoriesIdsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mCaloriesArrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_adv_search);

		initActionBar();

		Resources resources = BaseApplication.getAppContext().getResources();

		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		// Get categories array
		Cursor cursor = DB.query("categories", null, "use_in_add_screen=1",
				null, null, null, null);
		while (cursor.moveToNext()) {
			mCategoriesArrayList.add(getResources()
					.getString(
							resources.getIdentifier(cursor.getString(cursor
									.getColumnIndex("title_id")), "string",
									BaseApplication.getAppContext()
											.getPackageName())));
			mCategoriesIdsArrayList.add(cursor.getInt(cursor
					.getColumnIndex("id")));
		}
		// Get subcategories array
		cursor = DB.query("subcategories", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Log("title = " + cursor.getInt(cursor.getColumnIndex("title_id")));
			mSubcategoriesArrayList
					.add(resources.getString(resources.getIdentifier(
							cursor.getString(cursor.getColumnIndex("title_id")),
							"string", getPackageName())));
			mSubcategoriesIdsArrayList.add(cursor.getInt(cursor
					.getColumnIndex("id")));
		}
		cursor.close();
		DB.close();

		// Add "-" placeholder at the 1st place of categories array
		mCategoriesArrayList.add(0, "-");

		// Add "-" placeholder at the 1st place of subcategories array
		mSubcategoriesArrayList.add(0, "-");

		// Get calories array, and add "-" placeholder at the 1st place
		mCaloriesArrayList = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.calorie)));
		mCaloriesArrayList.add(0, "-");

		// If user already changed spinners, set saved texts to them
		((TextView) findViewById(R.id.spinnerTxt1))
				.setText(mCategoriesArrayList.get(PrefHelper.getInt(
						"CategoryPos", 0)));
		((TextView) findViewById(R.id.spinnerTxt2))
				.setText(mSubcategoriesArrayList.get(PrefHelper.getInt(
						"SubCategoryPos", 0)));
		((TextView) findViewById(R.id.spinnerTxt3)).setText(mCaloriesArrayList
				.get(PrefHelper.getInt("CaloriePos", 0)));

		if (PrefHelper.getInt("CaloriePos", 0) == 0) {
			mCalorie = null;
		} else {
			mCalorie = mCaloriesArrayList.get(PrefHelper
					.getInt("CaloriePos", 0));
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		// Hide title from action bar
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0);
		// Set title text
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_adv_search_title));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		finish();
		return super.onOptionsItemSelected(item);
	}

	public void recipeCategory(View v) {
		Log("category");
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				AdvSearchActivity.this);

		// Set adapter to list in dialog
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				AdvSearchActivity.this,
				android.R.layout.select_dialog_singlechoice,
				mCategoriesArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter,
				PrefHelper.getInt("CategoryPos", 0),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							RecipesActivity.mCategory = -1;
						} else {
							RecipesActivity.mCategory = mCategoriesIdsArrayList
									.get(which - 1);
						}
						((TextView) findViewById(R.id.spinnerTxt1))
								.setText(arrayAdapter.getItem(which));
						// Save position of selected item
						PrefHelper.setInt("CategoryPos", which);
						// Dismiss dialog
						dialog.dismiss();
					}
				});
		dialogBuilder.show();
	}

	public void recipeType(View v) {
		Log("subcategory");
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				AdvSearchActivity.this);

		// Set adapter to list in dialog
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				AdvSearchActivity.this,
				android.R.layout.select_dialog_singlechoice,
				mSubcategoriesArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter,
				PrefHelper.getInt("SubCategoryPos", 0),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							RecipesActivity.mSubcategory = -1;
						} else {
							RecipesActivity.mSubcategory = mSubcategoriesIdsArrayList
									.get(which - 1);
						}

						((TextView) findViewById(R.id.spinnerTxt2))
								.setText(arrayAdapter.getItem(which));
						// Save position of selected item
						PrefHelper.setInt("SubCategoryPos", which);
						// Dismiss dialog
						dialog.dismiss();
					}
				});
		dialogBuilder.show();
	}

	public void calorieLimit(View v) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				AdvSearchActivity.this);

		// Set adapter to list in dialog
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				AdvSearchActivity.this,
				android.R.layout.select_dialog_singlechoice, mCaloriesArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter,
				PrefHelper.getInt("CaloriePos", 0),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							mCalorie = null;
							((TextView) findViewById(R.id.spinnerTxt3))
									.setText("-");
						} else {
							mCalorie = arrayAdapter.getItem(which);
							((TextView) findViewById(R.id.spinnerTxt3))
									.setText(mCalorie);// Save position of
														// selected item
						}

						PrefHelper.setInt("CaloriePos", which);

						// Dismiss dialog
						dialog.dismiss();
					}
				});
		dialogBuilder.show();
	}

	public void search(View v) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("Category", RecipesActivity.mSubcategory);
		returnIntent.putExtra("Type", RecipesActivity.mCategory);
		returnIntent.putExtra("Calorie", mCalorie);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	public void back(View v) {
		RecipesActivity.mSubcategory = -1;
		RecipesActivity.mCategory = -1;
		PrefHelper.setInt("CategoryPos", 0);
		PrefHelper.setInt("SubCategoryPos", 0);
		PrefHelper.setInt("CaloriePos", 0);
		finish();
	}

	@Override
	public void onBackPressed() {
		RecipesActivity.mSubcategory = -1;
		RecipesActivity.mCategory = -1;
		PrefHelper.setInt("CategoryPos", 0);
		PrefHelper.setInt("SubCategoryPos", 0);
		PrefHelper.setInt("CaloriePos", 0);
		super.onBackPressed();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}
