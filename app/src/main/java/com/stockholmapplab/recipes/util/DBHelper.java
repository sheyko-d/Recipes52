package com.stockholmapplab.recipes.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase mDB;
	private Integer mMeasurementValue;
	private Integer mMeasurementVolume;

	// Measurements category constants
	private static Integer CATEGORY_VOLUME = 0;
	private static Integer CATEGORY_WEIGHT = 1;
	private static Integer CATEGORY_LENGTH = 2;
	// Measurements metric system constants
	public static Integer METRIC_SYSTEM_ALL = 0;
	public static Integer METRIC_SYSTEM_EU = 1;
	public static Integer METRIC_SYSTEM_US = 2;
	
	private static Boolean USE_IN_ADD_SCREEN = true;
	private static Boolean DO_NOT_USE_IN_ADD_SCREEN = false;

	private static final int DATABASE_VERSION = 2;

	public DBHelper(Context context) {
		super(context, "recipes", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mDB = db;

		db.execSQL("create table recipes_list (id integer, title text, kcal integer, rating text, subcategory integer, category integer, is_favorite integer, language text, status text);");
		db.execSQL("create table subcategories (id integer, title_id text);");
		db.execSQL("create table categories (id integer, title_id text, use_in_add_screen integer);");
		db.execSQL("create table ingredients (id integer primary key autoincrement, title_en text, title_sw text, kcal integer, category integer, measurement_value integer, measurement_volume integer, is_favorite integer);");
		db.execSQL("create table measurements (id integer, title_id text, category integer, measurement_unit integer, metric_system integer);");
		db.execSQL("create table add_ingredients (id integer, title_en text, title_sw text, category integer, kcal integer, measurement_value integer, measurement_volume integer, measurement integer);");

		addCategories();
		addSubcategories();
		addMeasurements();
		parseIngredients();
	}

	private void addMeasurements() {
		Resources res = BaseApplication.getAppContext().getResources();
		insertMeasurementRow(100, res.getResourceEntryName(R.string.measurement_tsp), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_ALL);
		insertMeasurementRow(101, res.getResourceEntryName(R.string.measurement_tbsp), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_ALL);
		insertMeasurementRow(102, res.getResourceEntryName(R.string.measurement_fl_oz), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(103, res.getResourceEntryName(R.string.measurement_gill), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(104, res.getResourceEntryName(R.string.measurement_cup), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_ALL);
		insertMeasurementRow(105, res.getResourceEntryName(R.string.measurement_pint), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(106, res.getResourceEntryName(R.string.measurement_quart), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(107, res.getResourceEntryName(R.string.measurement_gal), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(108, res.getResourceEntryName(R.string.measurement_piece), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_ALL);
		insertMeasurementRow(109, res.getResourceEntryName(R.string.measurement_ml), CATEGORY_VOLUME, 100,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(110, res.getResourceEntryName(R.string.measurement_l), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(111, res.getResourceEntryName(R.string.measurement_dl), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(112, res.getResourceEntryName(R.string.measurement_pinch), CATEGORY_VOLUME, 1,
				METRIC_SYSTEM_ALL);
		insertMeasurementRow(200, res.getResourceEntryName(R.string.measurement_lb), CATEGORY_WEIGHT, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(201, res.getResourceEntryName(R.string.measurement_oz), CATEGORY_WEIGHT, 1,
				METRIC_SYSTEM_US);
		insertMeasurementRow(202, res.getResourceEntryName(R.string.measurement_mcg), CATEGORY_WEIGHT, 100,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(203, res.getResourceEntryName(R.string.measurement_mg), CATEGORY_WEIGHT, 100,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(204, res.getResourceEntryName(R.string.measurement_g), CATEGORY_WEIGHT, 100,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(205, res.getResourceEntryName(R.string.measurement_kg), CATEGORY_WEIGHT, 1,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(300, res.getResourceEntryName(R.string.measurement_mm), CATEGORY_LENGTH, 100,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(301, res.getResourceEntryName(R.string.measurement_cm), CATEGORY_LENGTH, 10,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(302, res.getResourceEntryName(R.string.measurement_m), CATEGORY_LENGTH, 1,
				METRIC_SYSTEM_EU);
		insertMeasurementRow(303, res.getResourceEntryName(R.string.measurement_in), CATEGORY_LENGTH, 1,
				METRIC_SYSTEM_US);
	}

	private void addCategories() {
		Resources res = BaseApplication.getAppContext().getResources();
		insertCategoryRow(1, res.getResourceEntryName(R.string.category_fruit), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(2, res.getResourceEntryName(R.string.category_vegetables), USE_IN_ADD_SCREEN);
		insertCategoryRow(24, res.getResourceEntryName(R.string.category_salad), USE_IN_ADD_SCREEN);
		insertCategoryRow(4, res.getResourceEntryName(R.string.category_eggs), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(17, res.getResourceEntryName(R.string.category_cheese), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(12, res.getResourceEntryName(R.string.category_soup), USE_IN_ADD_SCREEN);
		insertCategoryRow(6, res.getResourceEntryName(R.string.category_meat), USE_IN_ADD_SCREEN);
		insertCategoryRow(7, res.getResourceEntryName(R.string.category_red_meat), USE_IN_ADD_SCREEN);
		insertCategoryRow(8, res.getResourceEntryName(R.string.category_white_meat), USE_IN_ADD_SCREEN);
		insertCategoryRow(9, res.getResourceEntryName(R.string.category_fish), USE_IN_ADD_SCREEN);
		insertCategoryRow(10, res.getResourceEntryName(R.string.category_seafood), USE_IN_ADD_SCREEN);
		insertCategoryRow(11, res.getResourceEntryName(R.string.category_chicken), USE_IN_ADD_SCREEN);
		insertCategoryRow(3, res.getResourceEntryName(R.string.category_rice_and_pasta), USE_IN_ADD_SCREEN);
		insertCategoryRow(13, res.getResourceEntryName(R.string.category_baking), USE_IN_ADD_SCREEN);
		insertCategoryRow(5, res.getResourceEntryName(R.string.category_drinks), USE_IN_ADD_SCREEN);
		insertCategoryRow(23, res.getResourceEntryName(R.string.category_smoothies), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(14, res.getResourceEntryName(R.string.category_hot_drinks), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(15, res.getResourceEntryName(R.string.category_alcohol_drinks), USE_IN_ADD_SCREEN);
		insertCategoryRow(18, res.getResourceEntryName(R.string.category_herbs_and_spices), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(19, res.getResourceEntryName(R.string.category_nuts), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(21, res.getResourceEntryName(R.string.category_oils_and_fat), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(22, res.getResourceEntryName(R.string.category_sweetings), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(16, res.getResourceEntryName(R.string.category_ingredients), DO_NOT_USE_IN_ADD_SCREEN);
		insertCategoryRow(20, res.getResourceEntryName(R.string.category_other), USE_IN_ADD_SCREEN);
		insertCategoryRow(0, res.getResourceEntryName(R.string.category_none), USE_IN_ADD_SCREEN);
	}

	private void addSubcategories() {
		Resources res = BaseApplication.getAppContext().getResources();
		insertSubcategoryRow(1, res.getResourceEntryName(R.string.subcategory_breakfast));
		insertSubcategoryRow(9, res.getResourceEntryName(R.string.subcategory_salad));
		insertSubcategoryRow(6, res.getResourceEntryName(R.string.subcategory_starters));
		insertSubcategoryRow(2, res.getResourceEntryName(R.string.subcategory_lunch));
		insertSubcategoryRow(3, res.getResourceEntryName(R.string.subcategory_dinner));
		insertSubcategoryRow(4, res.getResourceEntryName(R.string.subcategory_supper));
		insertSubcategoryRow(5, res.getResourceEntryName(R.string.subcategory_snack));
		insertSubcategoryRow(7, res.getResourceEntryName(R.string.subcategory_desserts));
		insertSubcategoryRow(8, res.getResourceEntryName(R.string.subcategory_holidays));
		insertSubcategoryRow(10, res.getResourceEntryName(R.string.subcategory_sides));
		insertSubcategoryRow(0, res.getResourceEntryName(R.string.subcategory_none));
	}

	// Insert measurement row into DB
	private void insertMeasurementRow(Integer id, String resEntryName, Integer category, Integer measurementUnit,
			Integer metricSystem) {
		ContentValues cv = new ContentValues();
		cv.put("id", id);
		cv.put("title_id", resEntryName);
		cv.put("category", category);
		cv.put("measurement_unit", measurementUnit);
		cv.put("metric_system", metricSystem);
		mDB.insert("measurements", null, cv);
	}

	// Insert sub category rows into DB
	private void insertCategoryRow(Integer id, String resEntryName, Boolean isUsedInAddScreen) {
		ContentValues cv = new ContentValues();
		cv.put("id", id);
		cv.put("title_id", resEntryName);
		
		if (isUsedInAddScreen){
			cv.put("use_in_add_screen", 1);
		} else {
			cv.put("use_in_add_screen", 0);
		}
		mDB.insert("categories", null, cv);
	}

	// Insert category rows into DB
	private void insertSubcategoryRow(Integer id, String resEntryName) {
		ContentValues cv = new ContentValues();
		cv.put("id", id);
		cv.put("title_id", resEntryName);
		mDB.insert("subcategories", null, cv);
	}

	private void parseIngredients() {
		try {
			InputStream is = BaseApplication.getAppContext().getAssets()
					.open("csv/calorie-menu.csv");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] RowData = line.split(",");
					String titleEn = RowData[0].replace("__", ",").replace(
							(char) 65279 + "", "");
					String titleSw = RowData[6].replace("__", ",").replace(
							(char) 65279 + "", "");
					titleEn = capitalizeFully(titleEn);
					titleSw = capitalizeFully(titleSw);
					if (titleEn.endsWith(",")) {
						titleEn = titleEn.substring(0, titleEn.length() - 1);
					}
					if (titleSw.endsWith(",")) {
						titleSw = titleSw.substring(0, titleSw.length() - 1);
					}
					Integer kcal = Integer.parseInt(RowData[3]);

					mMeasurementValue = Integer.parseInt(RowData[7]);

					Cursor cursor = mDB.query("measurements", null, "id='"
							+ mMeasurementValue + "'", null, null, null, null);
					if (cursor.moveToFirst()) {
						mMeasurementVolume = cursor.getInt(cursor
								.getColumnIndex("measurement_unit"));
					}
					int category = Integer.parseInt(RowData[5]);
					cursor.close();
					ContentValues cv = new ContentValues();
					cv.put("title_en", titleEn);
					cv.put("title_sw", titleSw);
					cv.put("kcal", kcal);
					cv.put("category", category);
					cv.put("measurement_value", mMeasurementValue);
					cv.put("measurement_volume", mMeasurementVolume);
					cv.put("is_favorite", 0);
					mDB.insert("ingredients", null, cv);
				}
			} catch (IOException ex) {
				Log.d("Log", ex + "");
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					Log.d("Log", e + "");
				}
			}
		} catch (IOException e1) {
			Log.d("Log", e1 + "");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS recipes_list");
		db.execSQL("DROP TABLE IF EXISTS subcategories");
		db.execSQL("DROP TABLE IF EXISTS categories");
		db.execSQL("DROP TABLE IF EXISTS ingredients");
		db.execSQL("DROP TABLE IF EXISTS measurements");
		db.execSQL("DROP TABLE IF EXISTS add_ingredients");
		PrefHelper.deletePreference("LastUpdatedDate");
		
		onCreate(db);
	}

	//TODO: Try to find alternative

	private static boolean isDelimiter(final char ch, final char[] delimiters) {
		if (delimiters == null) {
			return Character.isWhitespace(ch);
		}
		for (final char delimiter : delimiters) {
			if (ch == delimiter) {
				return true;
			}
		}
		return false;
	}

	public static String capitalize(final String str, final char... delimiters) {
		final int delimLen = delimiters == null ? -1 : delimiters.length;
		if (TextUtils.isEmpty(str) || delimLen == 0) {
			return str;
		}
		final char[] buffer = str.toCharArray();
		boolean capitalizeNext = true;
		for (int i = 0; i < buffer.length; i++) {
			final char ch = buffer[i];
			if (isDelimiter(ch, delimiters)) {
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer[i] = Character.toTitleCase(ch);
				capitalizeNext = false;
			}
		}
		return new String(buffer);
	}

	public static String capitalize(final String str) {
		return capitalize(str, null);
	}

	public static String capitalizeFully(final String str) {
		return capitalizeFully(str, null);
	}

	public static String capitalizeFully(String str, final char... delimiters) {
		final int delimLen = delimiters == null ? -1 : delimiters.length;
		if (TextUtils.isEmpty(str) || delimLen == 0) {
			return str;
		}
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}
}
