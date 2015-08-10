package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.CalorieMenuAdapter;
import com.stockholmapplab.recipes.pojo.FoodItem;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

public class CalorieMenuActivity extends ActionBarActivity {
	private android.support.v4.app.FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private Button mSortBtn;
	private Integer cal;
	public static TextView ingredientTextView;
	public static Activity activity;
	public static boolean onlyFavorite = false;

	public static String sortBy = "";
	public static final String SORT_TYPE_TITLE_EN = "title_en COLLATE NOCASE ASC";
	public static final String SORT_TYPE_TITLE_SW = "title_sw COLLATE NOCASE ASC";
	public static final String SORT_TYPE_KCAL = "kcal COLLATE NOCASE ASC";

	private ArrayList<Long> mWeekFastingDates = new ArrayList<Long>();
	private Dialog mNoLeftCaloriesDialog;
	public static String searchText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);

		activity = this;

		setContentView(R.layout.ac_calorie_menu);

		mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
		Collections.sort(mWeekFastingDates);

		ingredientTextView = (TextView) findViewById(R.id.addIngredientCheckbox);

		initActionBar();

		mSortBtn = (Button) findViewById(R.id.sortBtn);
		Log("mSortBtn = " + mSortBtn);
		setSortType();

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.add(R.id.recipesFragmentLayout,
				new CalorieMenuFragment());
		mFragmentTransaction.commit();

		registerForContextMenu(mSortBtn);
		mSortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openContextMenu(v);
			}
		});

		if (!BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			ingredientTextView.setVisibility(View.INVISIBLE);
			ingredientTextView.setEnabled(false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		CalorieMenuAdapter.selectedItemIds = new ArrayList<String>();
		CalorieMenuAdapter.selectedItemId = -1;
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_calorie_menu));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.recipes, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_add:
	        add();
	        return true;
	    default:
	    	finish();
	        return super.onOptionsItemSelected(item);
	    }
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (onlyFavorite) {
			showFavorites(new View(this));
		} else {
			showAll(new View(this));
		}
	}

	public void showFavorites(View v) {
		Log("showFavorites");

		// Reset search text
		searchText = "";
		onlyFavorite = true;

		// Change tabs
		findViewById(R.id.tabsFavorite).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsAll).setVisibility(View.INVISIBLE);

		// Replace fragment
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new CalorieMenuFragment());
		mFragmentTransaction.addToBackStack(null);
		mFragmentTransaction.commit();
	}

	public void showAll(View v) {
		Log("showAll");

		// Reset search text
		searchText = "";
		onlyFavorite = false;

		// Change tabs
		findViewById(R.id.tabsAll).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsFavorite).setVisibility(View.INVISIBLE);

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new CalorieMenuFragment());
		mFragmentTransaction.addToBackStack(null);
		mFragmentTransaction.commit();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.choose_ingredient_sort_context, menu);
		if (PrefHelper.getInt("IngredientSortType", 0) == 0) {
			menu.findItem(R.id.menu_title).setChecked(true);
		} else if (PrefHelper.getInt("IngredientSortType", 0) == 1) {
			menu.findItem(R.id.menu_kcal).setChecked(true);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_title:
			PrefHelper.setInt("IngredientSortType", 0);
			setSortType();
			CalorieMenuFragment.showIngredients();
			break;
		case R.id.menu_kcal:
			PrefHelper.setInt("IngredientSortType", 1);
			setSortType();
			CalorieMenuFragment.showIngredients();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setSortType() {
		if (PrefHelper.getInt("IngredientSortType", 0) == 0) {
			if (BaseApplication.isSwedish()) {
				sortBy = SORT_TYPE_TITLE_SW;
			} else {
				sortBy = SORT_TYPE_TITLE_EN;
			}
			mSortBtn.setText(getResources().getString(R.string.str_sort_title));
		} else if (PrefHelper.getInt("IngredientSortType", 0) == 1) {
			sortBy = SORT_TYPE_KCAL;
			mSortBtn.setText(getResources().getString(R.string.str_sort_kcal));
		}
	}

	public void addToConsumeScreen(View v) {
		if (CalorieMenuAdapter.selectedItemIds.size() > 1) {
			final Dialog infoDialog = new Dialog(this,
					R.style.CustomDialogTheme);
			infoDialog.setContentView(R.layout.raw_dialog_consume);
			((TextView) infoDialog.findViewById(R.id.tv_txt_guide1))
					.setText(R.string.str_dialog_title);
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			int totalKCal = 0;
			StringBuilder itemsBuilder = new StringBuilder();
			for (int i = 0; i < CalorieMenuAdapter.selectedItemIds.size(); i++) {
				Cursor c = DB.query("ingredients", null, "id='"
						+ CalorieMenuAdapter.selectedItemIds.get(i) + "'",
						null, null, null, null);
				if (c.moveToFirst()) {
					String title;
					if (BaseApplication.isSwedish()) {
						title = c.getString(c.getColumnIndex("title_sw"));
					} else {
						title = c.getString(c.getColumnIndex("title_en"));
					}
					totalKCal += c.getInt(c.getColumnIndex("kcal"));
					itemsBuilder.append(title + " ("
							+ c.getInt(c.getColumnIndex("kcal")) + ")");
					if (i != (CalorieMenuAdapter.selectedItemIds.size() - 1)) {
						itemsBuilder.append("\n");
					}
				}
				c.close();
			}
			DB.close();
			((TextView) infoDialog.findViewById(R.id.tv_txt_guide2))
					.setText(getResources().getString(
							R.string.str_dialog_consume_1)
							+ " "
							+ totalKCal
							+ " kcal. "
							+ getResources().getString(
									R.string.str_dialog_consume_2)
							+ ": "
							+ itemsBuilder.toString());
			Button btnOk = (Button) infoDialog.findViewById(R.id.bt_btn_ok);
			Button btnCancel = (Button) infoDialog
					.findViewById(R.id.bt_btn_Cancel);
			btnOk.setText(R.string.str_dialog_18);
			btnCancel.setText(R.string.str_dialog_5);
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					infoDialog.dismiss();
					updateIngredientDB();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					infoDialog.dismiss();
				}
			});

			infoDialog.show();
		} else {
			updateIngredientDB();
		}
	}

	private void updateIngredientDB() {
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getWritableDatabase();
		for (int i = 0; i < CalorieMenuAdapter.selectedItemIds.size(); i++) {
			Cursor c = DB.query("ingredients", null, "id='"
					+ CalorieMenuAdapter.selectedItemIds.get(i) + "'", null,
					null, null, null);
			if (c.moveToFirst()) {
				String title;
				if (BaseApplication.isSwedish()) {
					title = c.getString(c.getColumnIndex("title_sw"));
				} else {
					title = c.getString(c.getColumnIndex("title_en"));
				}
				int kcal = c.getInt(c.getColumnIndex("kcal"));
				Set<String> keySet = BaseConstant.mDietCycle.getUsedCal()
						.keySet();
				String key = Utility.getTimeInddMMyy(Calendar.getInstance());
				if (keySet.contains(key)) {
					cal = BaseConstant.mDietCycle.getUsedCal().get(key);
					cal = cal + kcal;
					BaseConstant.mDietCycle.setConsume_food_item(new FoodItem(
							title, kcal, Utility.getTimeInddMMyy(Calendar
									.getInstance())));
					BaseConstant.mDietCycle.getUsedCal().put(key, cal);
					BaseConstant.mDietCycle
							.setmConsumeFoodPoints(BaseConstant.mDietCycle
									.getmConsumeFoodPoints() + 1);
				} else {
					cal = kcal;

					if (todayIsFastingDay()) {
						if (BaseConstant.mDietCycle.getCalories_consumption() < cal) {
							showNoLeftCaloriesDialog();
						} else {
							BaseConstant.mDietCycle
									.setConsume_food_item(new FoodItem(title,
											kcal, Utility
													.getTimeInddMMyy(Calendar
															.getInstance())));
							BaseConstant.mDietCycle.getUsedCal().put(key, cal);
							BaseConstant.mDietCycle
									.setmConsumeFoodPoints(BaseConstant.mDietCycle
											.getmConsumeFoodPoints() + 1);
						}
					} else {
						BaseConstant.mDietCycle
								.setConsume_food_item(new FoodItem(title, kcal,
										Utility.getTimeInddMMyy(Calendar
												.getInstance())));
						BaseConstant.mDietCycle.getUsedCal().put(key, cal);
					}
				}
			}
			c.close();
		}
		DB.close();
		startActivity(new Intent(CalorieMenuActivity.this,
				ConsumedTodayActivity.class));
		finish();
	}

	public void add() {
		startActivity(new Intent(CalorieMenuActivity.this,
				CalorieMenuEditActivity.class));
	}

	private void showNoLeftCaloriesDialog() {
		final String title = CalorieMenuFragment.mNamesArrayList
				.get(CalorieMenuFragment.selectedPos);
		final int kcal = CalorieMenuFragment.mKCalsArrayList
				.get(CalorieMenuFragment.selectedPos);
		final String key = Utility.getTimeInddMMyy(Calendar.getInstance());
		if (mNoLeftCaloriesDialog == null) {
			mNoLeftCaloriesDialog = new Dialog(CalorieMenuActivity.this,
					R.style.DialogTheme);
			mNoLeftCaloriesDialog.setContentView(R.layout.raw_dialog3);
			mNoLeftCaloriesDialog.setCanceledOnTouchOutside(false);
			CustomTextView mTxtRemoveItem = (CustomTextView) mNoLeftCaloriesDialog
					.findViewById(R.id.tv_txt_guide1);
			CustomTextView mTxtRemove = (CustomTextView) mNoLeftCaloriesDialog
					.findViewById(R.id.tv_txt_guide2);
			CustomButton mBtnOk = (CustomButton) mNoLeftCaloriesDialog
					.findViewById(R.id.bt_btn_ok);
			CustomButton mBtnCancel = (CustomButton) mNoLeftCaloriesDialog
					.findViewById(R.id.bt_btn_Cancel);

			mTxtRemoveItem.setText(R.string.str_no_more_calories_left);
			mTxtRemove.setText(R.string.str_miss_goal);
			mBtnOk.setText(R.string.str_dialog_21);
			mBtnCancel.setText(R.string.str_no);

			mBtnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mNoLeftCaloriesDialog.dismiss();
				}
			});
			mBtnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BaseConstant.mDietCycle.setConsume_food_item(new FoodItem(
							title, kcal, Utility.getTimeInddMMyy(Calendar
									.getInstance())));
					BaseConstant.mDietCycle.getUsedCal().put(key, cal);
					if (todayIsFastingDay()) {
						BaseConstant.mDietCycle
								.setmConsumeFoodPoints(BaseConstant.mDietCycle
										.getmConsumeFoodPoints() - 1);

					}
					mNoLeftCaloriesDialog.dismiss();
				}
			});
		}
		mNoLeftCaloriesDialog.show();
	}

	private boolean todayIsFastingDay() {
		if (BaseConstant.mDietCycle.isDiet_cycle_is_on()) {
			if (mWeekFastingDates.size() == 0) {
				mWeekFastingDates = Utility.getDateOfFastingDayInThisWeek();
				Collections.sort(mWeekFastingDates);
			}
			return Utility.todayIsFastingDay(mWeekFastingDates);
		}
		return false;
	}

}
