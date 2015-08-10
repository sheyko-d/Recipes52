package com.stockholmapplab.recipes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.stockholmapplab.recipes.adapter.RecipesAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class RecipesFragment extends Fragment {
	private static View mView;

	private static ListView mRecipesList;
	private static ArrayList<Integer> mIdsArrayList = new ArrayList<Integer>();
	private static ArrayList<String> mNamesArrayList = new ArrayList<String>();
	private static ArrayList<Boolean> mIsTitlesArrayList = new ArrayList<Boolean>();
	private static ArrayList<Integer> mCaloriesArrayList = new ArrayList<Integer>();
	private static ArrayList<String> mRatingsArrayList = new ArrayList<String>();
	private static ArrayList<Integer> mCategoriesArrayList = new ArrayList<Integer>();
	private static ArrayList<Integer> mIsFavoriteArrayList = new ArrayList<Integer>();
	private static ArrayList<String> mStatusesArrayList = new ArrayList<String>();

	private static ArrayList<Integer> mIdsArrayListFull = new ArrayList<Integer>();
	private static ArrayList<String> mNamesArrayListFull = new ArrayList<String>();
	private static ArrayList<Integer> mCategoriesArrayListFull = new ArrayList<Integer>();
	private static ArrayList<Integer> mCaloriesArrayListFull = new ArrayList<Integer>();
	private static ArrayList<String> mRatingsArrayListFull = new ArrayList<String>();
	private static ArrayList<Integer> mIsFavoriteArrayListFull = new ArrayList<Integer>();
	private static ArrayList<String> mStatusesArrayListFull = new ArrayList<String>();

	private static ArrayList<Integer> mCategoriesTitlesArrayList = new ArrayList<Integer>();
	private static ArrayList<Integer> mCategoriesTitlesAddedArrayList = new ArrayList<Integer>();
	private static int mRecipesNum = 0;
	private static RecipesAdapter mRecipesAdapter;
	private static DBHelper mDBHelper;
	private static final String GET_RECIPES_URL = "http://dev.core.stockholmapplab.com/Webservice/Recipe/GetRecipeList";
	public static boolean showCategories = true;
	public static String ORDER_BY = "CASE category WHEN 4 THEN 0 WHEN 2 THEN 1 WHEN 24 THEN 2 WHEN 24 THEN 3 WHEN 17 THEN 4 WHEN 12 THEN 5 WHEN 6 THEN 6 WHEN 7 THEN 7 WHEN 8 THEN 8 WHEN 9 THEN 9 WHEN 10 THEN 10 WHEN 11 THEN 11 WHEN 3 THEN 12 WHEN 13 THEN 13 WHEN 5 THEN 14 WHEN 23 THEN 15 WHEN 14 THEN 16 WHEN 15 THEN 17 WHEN 18 THEN 18 WHEN 19 THEN 19 WHEN 21 THEN 20 WHEN 22 THEN 21 WHEN 16 THEN 22 WHEN 20 THEN 23 WHEN 0 THEN 24 END, ";
	public static String sortBy = "";

	// Current language of recipes ("en"-english, "sv"-swedish, "no"-norwegian)
	public static String languageCode = "all";

	// Sort type constants
	public static final String SORT_TYPE_LATEST = "id DESC";
	public static final String SORT_TYPE_RATING = "rating DESC";
	public static final String SORT_TYPE_TITLE = "title ASC";
	public static final String SORT_TYPE_KCAL = "kcal ASC";
	private static Cursor mCursor;
	public static boolean isFavorite = false;
	public static String searchText = "";
	private static Button mSortBtn;
	private static Button mLangBtn;
	private static String mIsFavoriteText;
	private static String mSearchText = "";
	private static String mCalorieText;
	private static String mLanguageCode;
	private static String mSubcategoryText;
	private static String mCategoryText;
	private static boolean mIsUpdatedFromServer = false;
	private static boolean mShowRecept;

	private boolean mClearBtnIsTransparent = false;

	private static SwipeRefreshLayout mRecipesRefreshLayout;

	public static boolean updatedFromServer;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fr_recipes, container, false);

		mDBHelper = new DBHelper(getActivity());

		((EditText) mView.findViewById(R.id.searchEditTxt))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						searchText = s.toString();
						if (searchText.length() > 0) {
							if (mClearBtnIsTransparent) {
								BaseApplication.fadeIn(mView
										.findViewById(R.id.recipesClearBtn));
								mClearBtnIsTransparent = false;
							}
							downloadRecipes();
							clearList();
							mRecipesAdapter.notifyDataSetChanged();
						} else {
							if (!mClearBtnIsTransparent) {
								BaseApplication.fadeOut(mView
										.findViewById(R.id.recipesClearBtn));
								mClearBtnIsTransparent = true;
							}
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

		((EditText) mView.findViewById(R.id.searchEditTxt))
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							InputMethodManager imm = (InputMethodManager) getActivity()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(((EditText) mView
									.findViewById(R.id.searchEditTxt))
									.getWindowToken(), 0);
							return true;
						}
						return false;
					}
				});

		mSortBtn = (Button) getActivity().findViewById(R.id.sortBtn);
		mLangBtn = (Button) getActivity().findViewById(R.id.langBtn);
		setSortType();
		setLanguage();

		mRecipesList = (ListView) mView.findViewById(R.id.recipesList);
		mRecipesRefreshLayout = (SwipeRefreshLayout) mView
				.findViewById(R.id.recipesRefreshLayout);
		mRecipesRefreshLayout
				.setColorSchemeResources(new int[] { R.color.green });
		mRecipesAdapter = new RecipesAdapter(RecipesActivity.activity,
				mIdsArrayListFull, mNamesArrayListFull, mCaloriesArrayListFull,
				mRatingsArrayListFull, mCategoriesArrayListFull,
				mIsTitlesArrayList, mIsFavoriteArrayListFull,
				mStatusesArrayListFull);
		mIsUpdatedFromServer = false;
		showRecipes();
		downloadRecipes();

		// If admin - show filter button
		if (BaseApplication.isAdmin()) {
			getActivity().findViewById(R.id.filterBtn).setVisibility(
					View.VISIBLE);
		}
		return mView;
	}

	public static void setSortType() {
		if (PrefHelper.getInt("SortType", 0) == 0) {
			sortBy = SORT_TYPE_LATEST;
			mSortBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_sort_latest));
		} else if (PrefHelper.getInt("SortType", 0) == 1) {
			sortBy = SORT_TYPE_RATING;
			mSortBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_sort_rating));
		} else if (PrefHelper.getInt("SortType", 0) == 2) {
			sortBy = SORT_TYPE_TITLE;
			mSortBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_sort_title));
		} else if (PrefHelper.getInt("SortType", 0) == 3) {
			sortBy = SORT_TYPE_KCAL;
			mSortBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_sort_kcal));
		}
	}

	public static void setLanguage() {
		if (PrefHelper.getInt("Language", 0) == 0) {
			languageCode = "all";
		} else if (PrefHelper.getInt("Language", 0) == 1) {
			languageCode = "en";
		} else if (PrefHelper.getInt("Language", 0) == 2) {
			languageCode = "sv";
		} else if (PrefHelper.getInt("Language", 0) == 3) {
			languageCode = "no";
		}
	}

	@SuppressLint("SimpleDateFormat")
	private static String getUTCDate() {
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			Date date = new Date();
			date.setTime(System.currentTimeMillis() - 2 * 1000 * 60);
			return dateformat.format(date);
		} catch (ParseException e) {
			return "";
		}
	}

	public static void downloadRecipes() {
		new DownloadRecipesTask().execute();
	}

	public static void clearList() {
		mIdsArrayList.clear();
		mNamesArrayList.clear();
		mCategoriesArrayList.clear();
		mCaloriesArrayList.clear();
		mRatingsArrayList.clear();
		mStatusesArrayList.clear();
		mIsFavoriteArrayList.clear();

		mIdsArrayListFull.clear();
		mNamesArrayListFull.clear();
		mCategoriesArrayListFull.clear();
		mCaloriesArrayListFull.clear();
		mRatingsArrayListFull.clear();
		mStatusesArrayListFull.clear();
		mIsFavoriteArrayListFull.clear();
		mCategoriesTitlesAddedArrayList.clear();

		mIsTitlesArrayList.clear();

		if (mCategoriesTitlesArrayList != null)
			mCategoriesTitlesArrayList.clear();
		if (mRecipesAdapter != null)
			mRecipesAdapter.notifyDataSetChanged();

		// Hide placeholder
		RecipesActivity.activity.findViewById(R.id.noRecipesLayout)
				.setVisibility(View.GONE);
	}

	public static void showRecipes() {

		mRecipesList.setVisibility(View.VISIBLE);

		mRecipesRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				downloadRecipes();
			}
		});

		getRecipesFromDB();

		// Set adapter to list and invalidate it
		mRecipesList.setAdapter(mRecipesAdapter);
		mRecipesAdapter.notifyDataSetChanged();

		if (showCategories) {
			mCategoriesTitlesArrayList = new ArrayList<Integer>();
			mCategoriesTitlesArrayList = getTitles();
		} else {
			mCategoriesTitlesArrayList = new ArrayList<Integer>();
		}
	}

	// Get old recipes from database
	public static void getRecipesFromDB() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		mCursor = getCursor(db, searchText, null);
		clearList();
		while (mCursor.moveToNext()) {
			if (!RecipesActivity.filter.equals("")) {
				mShowRecept = mCursor.getString(
						mCursor.getColumnIndex("status")).equals(
						RecipesActivity.filter);
			} else {
				mShowRecept = true;
			}
			if (mShowRecept) {
				mIdsArrayList.add(mCursor.getInt(mCursor.getColumnIndex("id")));
				mNamesArrayList.add(mCursor.getString(mCursor
						.getColumnIndex("title")));
				mCaloriesArrayList.add(mCursor.getInt(mCursor
						.getColumnIndex("kcal")));
				mRatingsArrayList.add(mCursor.getString(mCursor
						.getColumnIndex("rating")));
				mCategoriesArrayList.add(mCursor.getInt(mCursor
						.getColumnIndex("category")));
				mIsFavoriteArrayList.add(mCursor.getInt(mCursor
						.getColumnIndex("is_favorite")));
				mStatusesArrayList.add(mCursor.getString(mCursor
						.getColumnIndex("status")));
			}
		}
		mCursor.close();
		db.close();
		addTitles();

	}

	private static ArrayList<Integer> getTitles() {
		if (showCategories) {
			mCategoriesTitlesArrayList = new ArrayList<Integer>();
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			Log(searchText);
			mCursor = getCursor(db, searchText, "category");
			while (mCursor.moveToNext()) {
				mCategoriesTitlesArrayList.add(mCursor.getInt(mCursor
						.getColumnIndex("category")));
				Log(mCursor.getInt(mCursor.getColumnIndex("category")));
			}
			mCursor.close();
			db.close();
		}
		return null;
	}

	private static Cursor getCursor(SQLiteDatabase db, String searchText,
			String groupBy) {
		if (languageCode.equals("all")) {
			mLanguageCode = "(language='en' OR language='sv' OR language='no')";
		} else {
			mLanguageCode = "language='" + languageCode + "'";
		}
		if (isFavorite) {
			mIsFavoriteText = " AND is_favorite='1'";
		} else {
			mIsFavoriteText = " AND (is_favorite='1' OR is_favorite='0')";
		}
		if (searchText.equals("")) {
			mSearchText = "";
		} else {
			mSearchText = " AND title LIKE '%" + searchText + "%'";
		}
		Log("category = " + RecipesActivity.mSubcategory + ", subcategory = "
				+ RecipesActivity.mCategory);
		if (RecipesActivity.mSubcategory == -1) {
			mSubcategoryText = "";
		} else {
			mSubcategoryText = " AND subcategory="
					+ RecipesActivity.mSubcategory;
		}
		if (RecipesActivity.mCategory == -1) {
			mCategoryText = "";
		} else {
			mCategoryText = " AND category=" + RecipesActivity.mCategory;
		}
		if (RecipesActivity.mCalorie == null) {
			mCalorieText = "";
		} else {
			mCalorieText = " AND kcal " + RecipesActivity.mCalorie;
		}
		if (!showCategories) {
			mCursor = db.query("recipes_list", null, mLanguageCode
					+ mIsFavoriteText + mSearchText + mCalorieText
					+ mCategoryText + mSubcategoryText, null, groupBy, null,
					sortBy, null);
		} else {
			mCursor = db.query("recipes_list", null, mLanguageCode
					+ mIsFavoriteText + mSearchText + mCalorieText
					+ mCategoryText + mSubcategoryText, null, groupBy, null,
					ORDER_BY + sortBy, null);
		}

		Log("WHERE " + mLanguageCode + mIsFavoriteText + mSearchText
				+ mCalorieText + mCategoryText + mSubcategoryText
				+ " GROUP BY " + groupBy + ", ORDER BY " + ORDER_BY
				+ ", SORT BY " + sortBy);

		return mCursor;
	}

	private static void addTitles() {
		Resources resources = RecipesActivity.activity.getResources();
		mRecipesNum = 0;
		if (showCategories) {
			for (int i = 0; i < mCategoriesArrayList.size(); i++) {
				Integer category = mCategoriesArrayList.get(i);
				Integer id = mIdsArrayList.get(i);
				String title = mNamesArrayList.get(i);
				Integer kcal = mCaloriesArrayList.get(i);
				String rating = mRatingsArrayList.get(i);
				Integer isFavorite = mIsFavoriteArrayList.get(i);
				String status = mStatusesArrayList.get(i);
				if (!mCategoriesTitlesAddedArrayList.contains(category)) {
					// Mark header title as added
					mCategoriesTitlesAddedArrayList.add(category);

					// Add values to header item
					mIdsArrayListFull.add(-1);
					SQLiteDatabase DB = mDBHelper.getReadableDatabase();
					Cursor cursor = DB.query("categories", null, "id='"
							+ category + "'", null, null, null, null);
					if (cursor.moveToFirst()) {
						mNamesArrayListFull.add(resources.getString(resources
								.getIdentifier(cursor.getString(cursor
										.getColumnIndex("title_id")), "string",
										RecipesActivity.activity
												.getPackageName())));
					}
					cursor.close();
					mCategoriesArrayListFull.add(category);
					mCaloriesArrayListFull.add(-1);
					mRatingsArrayListFull.add("");
					mIsFavoriteArrayListFull.add(-1);
					mStatusesArrayListFull.add("");

					// Mark title as header
					mIsTitlesArrayList.add(true);
				}
				// Add values to recipe item
				mIdsArrayListFull.add(id);
				mNamesArrayListFull.add(title);
				mCategoriesArrayListFull.add(category);
				mCaloriesArrayListFull.add(kcal);
				mRatingsArrayListFull.add(rating);
				mIsFavoriteArrayListFull.add(isFavorite);
				mStatusesArrayListFull.add(status);

				// Mark title as recipe name
				mIsTitlesArrayList.add(false);

				// Increment recipes number
				mRecipesNum++;
			}
		} else {
			// Add values to header item
			mIdsArrayListFull.add(-1);
			mNamesArrayListFull.add("Title");
			mCaloriesArrayListFull.add(-1);
			mRatingsArrayListFull.add("");
			mCategoriesArrayListFull.add(-1);
			mIsFavoriteArrayListFull.add(-1);
			mStatusesArrayListFull.add("");
			// Mark title as header
			mIsTitlesArrayList.add(true);

			for (int i = 0; i < mCategoriesArrayList.size(); i++) {
				Integer id = mIdsArrayList.get(i);
				Integer subcategory = mCategoriesArrayList.get(i);
				String title = mNamesArrayList.get(i);
				Integer kcal = mCaloriesArrayList.get(i);
				String rating = mRatingsArrayList.get(i);
				Integer isFavorite = mIsFavoriteArrayList.get(i);
				String status = mStatusesArrayList.get(i);
				// Add values to recipe item
				mNamesArrayListFull.add(title);
				mCategoriesArrayListFull.add(subcategory);
				mCaloriesArrayListFull.add(kcal);
				mRatingsArrayListFull.add(rating);
				mIdsArrayListFull.add(id);
				mIsFavoriteArrayListFull.add(isFavorite);
				mStatusesArrayListFull.add(status);

				// Mark title as recipe name
				mIsTitlesArrayList.add(false);

				// Increment recipes number
				mRecipesNum++;
			}

		}

		if (mIdsArrayListFull.size() > 0 & updatedFromServer) {
			PrefHelper.setString("LastUpdatedDate", getUTCDate());
			updatedFromServer = false;
		}

		mRecipesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RecipesActivity.activity.startActivity(new Intent(
						RecipesActivity.activity, RecipeDetailsActivity.class)
						.putExtra("Id", mIdsArrayListFull.get(position - 1))
						.putExtra("Name", mNamesArrayListFull.get(position - 1)));
			}
		});

		// Change language button text (with recipes counter)
		if (PrefHelper.getInt("Language", 0) == 0) {
			mLangBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_all)
					+ " (" + mRecipesNum + ")");
		} else if (PrefHelper.getInt("Language", 0) == 1) {
			mLangBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_lang_en)
					+ " (" + mRecipesNum + ")");
		} else if (PrefHelper.getInt("Language", 0) == 2) {
			mLangBtn.setText(RecipesActivity.activity.getResources().getString(
					R.string.str_lang_sv)
					+ "  (" + mRecipesNum + ")");
		}

		// Reset text of refresh button
		RecipesActivity.mRefreshBtn.setText(R.string.str_refresh);
		RecipesActivity.mRefreshBtn
				.setBackgroundResource(R.drawable.button_selector);
		if (RecipesActivity.mRefreshBtnIsTransparent) {
			BaseApplication.fadeIn(RecipesActivity.mRefreshBtn);
			RecipesActivity.mRefreshBtnIsTransparent = false;
		}

		// Hide pull to refresh header
		mRecipesRefreshLayout.setRefreshing(false);

		if (mIsUpdatedFromServer) {
			// Hide progress par
			RecipesActivity.activity.findViewById(R.id.recipesProgressBar)
					.setVisibility(View.GONE);

			// If there is no recipes - show text placeholder, and hide list
			if (mRecipesNum == 0) {
				RecipesActivity.activity.findViewById(R.id.noRecipesLayout)
						.setVisibility(View.VISIBLE);
				mRecipesList.setVisibility(View.GONE);
			} else {
				RecipesActivity.activity.findViewById(R.id.noRecipesLayout)
						.setVisibility(View.GONE);
				mRecipesList.setVisibility(View.VISIBLE);
			}
		} else {
			if (mRecipesNum != 0) {
				// Hide progress par
				RecipesActivity.activity.findViewById(R.id.recipesProgressBar)
						.setVisibility(View.GONE);
			}
		}
	}

	public static class DownloadRecipesTask extends
			AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private JSONObject mResponseJSON;
		private Object mStatus;
		private JSONArray mDataJSON;
		private String mTitle;
		private String mLanguage;
		private boolean mShowRecept;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log("login token key = " + PrefHelper.getString("TokenKey", ""));
			updatedFromServer = true;
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(GET_RECIPES_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				if (!PrefHelper.getString("TokenKey", "").equals("")) {
					nameValuePairs.add(new BasicNameValuePair("TokenKey",
							PrefHelper.getString("TokenKey", "")));
				}
				Log("LastUpdatedDate = "
						+ PrefHelper.getString("LastUpdatedDate", ""));

				if (!PrefHelper.getString("LastUpdatedDate", "").equals("")) {
					nameValuePairs.add(new BasicNameValuePair(
							"LastUpdatedDate", PrefHelper.getString(
									"LastUpdatedDate", "")));
				} else {
					nameValuePairs.add(new BasicNameValuePair(
							"LastUpdatedDate", null));
				}

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				Log(responseString);
				try {
					mResponseJSON = new JSONObject(responseString);
					mError = false;
				} catch (JSONException e) {
					mError = true;
				}
				if (!mError) {
					try {
						mStatus = mResponseJSON.getString("status");
					} catch (JSONException e) {
					}
					if (mStatus.equals("ok")) {
						mError = false;
						try {
							mDataJSON = mResponseJSON.getJSONArray("data");
						} catch (JSONException e) {

						}
						for (int i = 0; i < mDataJSON.length(); i++) {
							try {
								String id = mDataJSON.getJSONObject(i)
										.getString("RecipeId");
								mTitle = "";
								if (languageCode.equals("all")) {
									if (new JSONObject(mDataJSON.getJSONObject(
											i).getString("Title")).has("en")) {
										mTitle = new JSONObject(mDataJSON
												.getJSONObject(i).getString(
														"Title"))
												.getString("en");
										mLanguage = "en";
									} else if (new JSONObject(mDataJSON
											.getJSONObject(i)
											.getString("Title")).has("sv")) {
										mTitle = new JSONObject(mDataJSON
												.getJSONObject(i).getString(
														"Title"))
												.getString("sv");
										mLanguage = "sv";
									} else if (new JSONObject(mDataJSON
											.getJSONObject(i)
											.getString("Title")).has("no")) {
										mTitle = new JSONObject(mDataJSON
												.getJSONObject(i).getString(
														"Title"))
												.getString("no");
										mLanguage = "no";
									}
								} else {
									mTitle = new JSONObject(mDataJSON
											.getJSONObject(i)
											.getString("Title"))
											.getString(languageCode);
									mLanguage = languageCode;
								}
								if (!mTitle.equals("")) {
									if (BaseApplication.isAdmin()) {
										// Add recipe to list, if it's approved,
										// pending, or rejected (administrator
										// mode)
										mShowRecept = mDataJSON
												.getJSONObject(i)
												.getString("Status")
												.equals("Approved")
												|| mDataJSON.getJSONObject(i)
														.getString("Status")
														.equals("Pending")
												|| mDataJSON.getJSONObject(i)
														.getString("Status")
														.equals("Rejected");
									} else {
										// Add recipe to list, if it's approved
										mShowRecept = mDataJSON
												.getJSONObject(i)
												.getString("Status")
												.equals("Approved");
									}
									if (mShowRecept) {
										String calorie = mDataJSON
												.getJSONObject(i).getString(
														"Calorie");
										String rating = mDataJSON
												.getJSONObject(i).getString(
														"Rating");
										Integer subcategory = mDataJSON
												.getJSONObject(i).getInt(
														"SubCategory");
										Integer category = mDataJSON
												.getJSONObject(i).getInt(
														"Category");
										String status = mDataJSON
												.getJSONObject(i).getString(
														"Status");
										Integer isFavorite = 0;
										insertToDB(id, mTitle, calorie, rating,
												subcategory, category,
												isFavorite, mLanguage, status);
									}
								}
							} catch (JSONException e) {
								Log(e);
							}
						}
					} else {
						try {
							mErrorMessage = mResponseJSON
									.getString("ErrorMessage");
							if (mResponseJSON.getString("ErrorType").equals(
									"validation")) {
								RecipesActivity.activity
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														RecipesActivity.activity,
														"Error: "
																+ mErrorMessage,
														Toast.LENGTH_LONG)
														.show();
											}

										});
								PreferenceManager
										.getDefaultSharedPreferences(
												BaseApplication.getAppContext())
										.edit().clear().commit();
								RecipesActivity.activity
										.startActivity(new Intent(
												RecipesActivity.activity,
												LoginActivity.class).putExtra(
												"logout", true));
								RecipesActivity.activity.finish();
								HomeActivity.activity.finish();
							}
						} catch (JSONException e) {
						}
						mError = true;
					}
				}
			} catch (ClientProtocolException e) {
				RecipesActivity.activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				RecipesActivity.activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Connection error",
								"Check your internet connection.");
					}

				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				mIsUpdatedFromServer = true;
				showRecipes();
			}
		}
	}

	private static void insertToDB(String id, String title, String kcal,
			String rating, Integer subcategory, Integer category,
			Integer isFavorite, String language, String status) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		Cursor c = db.query("recipes_list", null, "id='" + id + "'", null,
				null, null, null);
		cv.put("id", id);
		cv.put("title", title);
		cv.put("kcal", kcal);
		cv.put("rating", rating);
		cv.put("subcategory", subcategory);
		cv.put("category", category);
		cv.put("is_favorite", isFavorite);
		cv.put("language", language);
		cv.put("status", status);
		if (!c.moveToFirst()) {
			db.insert("recipes_list", null, cv);
		} else {
			db.update("recipes_list", cv, "id='" + id + "'", null);
		}
		c.close();
		db.close();
	}

	private static void showErrorDialog(String title, String message) {
		if (RecipesActivity.activity != null) {
			try {
				final Dialog errorDialog = new Dialog(RecipesActivity.activity,
						R.style.DialogTheme);
				errorDialog.setContentView(R.layout.raw_dialog_error);
				errorDialog.setCanceledOnTouchOutside(false);

				CustomTextView mTxtErrorTitle = (CustomTextView) errorDialog
						.findViewById(R.id.tv_txt_guide1);
				CustomTextView mTxtErrorMessage = (CustomTextView) errorDialog
						.findViewById(R.id.tv_txt_guide2);
				CustomButton mBtnCancel = (CustomButton) errorDialog
						.findViewById(R.id.bt_btn_Cancel);

				mTxtErrorTitle.setText(title);
				mTxtErrorMessage.setText(message);
				mBtnCancel.setText(R.string.str_dialog_5);

				mBtnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						errorDialog.dismiss();
					}
				});
				errorDialog.show();
			} catch (Exception e) {
			}
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}