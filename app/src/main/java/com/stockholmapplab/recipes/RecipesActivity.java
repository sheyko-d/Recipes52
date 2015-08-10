package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.http.util.TextUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class RecipesActivity extends ActionBarActivity {

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private CheckBox mShowCategoryCheckBox;
	private ArrayList<String> mSortArrayList;
	private ArrayList<String> mLangArrayList;
	private ArrayList<String> mFilterArrayList;
	public static boolean mRefreshBtnIsTransparent = false;
	public static String filter = "";
	public static Button mRefreshBtn;
	public static int mSubcategory = -1;
	public static int mCategory = -1;
	public static String mCalorie;
	private static final int REQUEST_CODE = 0;
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_recipes);

		BaseApplication.lockOrientation(this);

		Log("mSubcategory = " + mSubcategory);

		initActionBar();

		activity = this;

		mShowCategoryCheckBox = (CheckBox) findViewById(R.id.showCategoryCheckBox);
		mShowCategoryCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						RecipesFragment.showCategories = isChecked;
						RecipesFragment.clearList();
						RecipesFragment.showRecipes();
					}
				});

		mRefreshBtn = (Button) findViewById(R.id.recipesRefreshBtn);

		/*
		 * // Show context menu on show button click
		 * registerForContextMenu(findViewById(R.id.sortBtn)); // Show context
		 * menu on language button click
		 * registerForContextMenu(findViewById(R.id.langBtn)); // Show context
		 * menu on filter button click
		 * registerForContextMenu(findViewById(R.id.filterBtn));
		 */

		mSortArrayList = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.sort_array)));
		findViewById(R.id.sortBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						RecipesActivity.this);

				// Set adapter to list in dialog
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						RecipesActivity.this,
						android.R.layout.select_dialog_singlechoice,
						mSortArrayList);

				// Add "Cancel" button to dialog
				dialogBuilder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				dialogBuilder.setSingleChoiceItems(arrayAdapter,
						PrefHelper.getInt("SortType", 0),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_LATEST;
								} else if (which == 1) {
									RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_RATING;
								} else if (which == 2) {
									RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_TITLE;
								} else if (which == 0) {
									RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_KCAL;
								}
								PrefHelper.setInt("SortType", which);
								RecipesFragment.setSortType();
								RecipesFragment.showRecipes();

								// Dismiss dialog
								dialog.dismiss();
							}
						});
				dialogBuilder.show();
			}
		});

		mLangArrayList = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.lang_array)));
		findViewById(R.id.langBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						RecipesActivity.this);

				// Set adapter to list in dialog
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						RecipesActivity.this,
						android.R.layout.select_dialog_singlechoice,
						mLangArrayList);

				// Add "Cancel" button to dialog
				dialogBuilder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				dialogBuilder.setSingleChoiceItems(arrayAdapter,
						PrefHelper.getInt("Language", 0),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PrefHelper.setInt("Language", which);
								RecipesFragment.setLanguage();
								RecipesFragment.showRecipes();

								// Dismiss dialog
								dialog.dismiss();
							}
						});
				dialogBuilder.show();
			}
		});

		mFilterArrayList = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.filter_array)));
		findViewById(R.id.filterBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						RecipesActivity.this);

				// Set adapter to list in dialog
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						RecipesActivity.this,
						android.R.layout.select_dialog_singlechoice,
						mFilterArrayList);

				// Add "Cancel" button to dialog
				dialogBuilder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				int pos = 0;
				if (filter.equals("Pending")) {
					pos = 0;
				} else if (filter.equals("Rejected")) {
					pos = 1;
				} else if (filter.equals("Approved")) {
					pos = 2;
				}

				dialogBuilder.setSingleChoiceItems(arrayAdapter, pos,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									filter = "Pending";
								} else if (which == 1) {
									filter = "Rejected";
								} else if (which == 2) {
									filter = "Approved";
								}
								RecipesFragment.downloadRecipes();

								// Dismiss dialog
								dialog.dismiss();
							}
						});
				dialogBuilder.show();
			}
		});

		if (RecipesFragment.isFavorite) {
			showFavorites(new View(this));
		}

		if (mCategory != -1 || mSubcategory != -1 || mCalorie != null) {
			findViewById(R.id.clearAdvSearchBtn).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.clearAdvSearchBtn).setVisibility(View.GONE);
		}

		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.add(R.id.recipesFragmentLayout,
				new RecipesFragment());
		mFragmentTransaction.addToBackStack(null);
		mFragmentTransaction.commit();

	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_recipes));
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		switch (v.getId()) {
		case R.id.sortBtn:
			inflater.inflate(R.menu.recipes_sort_context, menu);
			if (PrefHelper.getInt("SortType", 0) == 0) {
				menu.findItem(R.id.menu_latest).setChecked(true);
			} else if (PrefHelper.getInt("SortType", 0) == 1) {
				menu.findItem(R.id.menu_rating).setChecked(true);
			} else if (PrefHelper.getInt("SortType", 0) == 2) {
				menu.findItem(R.id.menu_title).setChecked(true);
			} else if (PrefHelper.getInt("SortType", 0) == 3) {
				menu.findItem(R.id.menu_kcal).setChecked(true);
			}
			break;
		case R.id.langBtn:
			inflater.inflate(R.menu.recipes_lang_context, menu);
			if (PrefHelper.getInt("Language", 0) == 0) {
				menu.findItem(R.id.menu_all).setChecked(true);
			} else if (PrefHelper.getInt("Language", 0) == 1) {
				menu.findItem(R.id.menu_en).setChecked(true);
			} else if (PrefHelper.getInt("Language", 0) == 2) {
				menu.findItem(R.id.menu_sv).setChecked(true);
			}
			break;
		case R.id.filterBtn:
			inflater.inflate(R.menu.recipes_filter_context, menu);
			if (filter.equals("")) {
				menu.findItem(R.id.menu_all_filter).setChecked(true);
			} else if (filter.equals("Pending")) {
				menu.findItem(R.id.menu_pending).setChecked(true);
			} else if (filter.equals("Rejected")) {
				menu.findItem(R.id.menu_rejected).setChecked(true);
			} else if (filter.equals("Approved")) {
				menu.findItem(R.id.menu_apprived).setChecked(true);
			}
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_all:
			PrefHelper.setInt("Language", 0);
			RecipesFragment.setLanguage();
			break;
		case R.id.menu_en:
			PrefHelper.setInt("Language", 1);
			RecipesFragment.setLanguage();
			break;
		case R.id.menu_sv:
			PrefHelper.setInt("Language", 2);
			RecipesFragment.setLanguage();
			break;
		case R.id.menu_latest:
			RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_LATEST;
			PrefHelper.setInt("SortType", 0);
			RecipesFragment.setSortType();
			break;
		case R.id.menu_rating:
			RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_RATING;
			PrefHelper.setInt("SortType", 1);
			RecipesFragment.setSortType();
			break;
		case R.id.menu_title:
			RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_TITLE;
			PrefHelper.setInt("SortType", 2);
			RecipesFragment.setSortType();
			break;
		case R.id.menu_kcal:
			RecipesFragment.sortBy = RecipesFragment.SORT_TYPE_KCAL;
			PrefHelper.setInt("SortType", 3);
			break;
		case R.id.menu_all_filter:
			filter = "";
			break;
		case R.id.menu_pending:
			filter = "Pending";
			break;
		case R.id.menu_rejected:
			filter = "Rejected";
			break;
		case R.id.menu_apprived:
			filter = "Approved";
			break;
		}
		RecipesFragment.downloadRecipes();
		return super.onOptionsItemSelected(item);
	}

	public void refresh(View v) {
		if (!mRefreshBtnIsTransparent) {
			mRefreshBtn.setText(R.string.str_refreshing);
			BaseApplication.fadeOut(mRefreshBtn);
			mRefreshBtnIsTransparent = true;
		}
		RecipesFragment.downloadRecipes();
	}

	public void showFavorites(View v) {
		// Reset search text
		RecipesFragment.searchText = "";

		// Change tabs
		findViewById(R.id.tabsFavorite).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsAll).setVisibility(View.INVISIBLE);

		// Replace fragment
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		RecipesFragment.isFavorite = true;
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new RecipesFragment());
		mFragmentTransaction.addToBackStack(null);
		mFragmentTransaction.commit();
	}

	public void showAll(View v) {
		// Reset search text
		RecipesFragment.searchText = "";

		// Change tabs
		findViewById(R.id.tabsAll).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsFavorite).setVisibility(View.INVISIBLE);

		// Replace fragment
		RecipesFragment.isFavorite = false;
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new RecipesFragment());
		mFragmentTransaction.addToBackStack(null);
		mFragmentTransaction.commit();
	}

	public void clear(View v) {
		((EditText) findViewById(R.id.searchEditTxt)).setText("");
		RecipesFragment.downloadRecipes();
	}

	public void sort(View v) {
		openContextMenu(findViewById(R.id.sortBtn));
	}

	public void lang(View v) {
		openContextMenu(findViewById(R.id.langBtn));
	}

	public void filter(View v) {
		openContextMenu(findViewById(R.id.filterBtn));
	}

	public void back(View v) {
		finish();
	}

	public void clearAdvancedSearch(View v) {
		mSubcategory = -1;
		mCategory = -1;
		mCalorie = null;
		PrefHelper.setInt("CategoryPos", 0);
		PrefHelper.setInt("SubCategoryPos", 0);
		PrefHelper.setInt("CaloriePos", 0);
		RecipesFragment.showRecipes();
		v.setVisibility(View.GONE);
	}

	public void add() {
		startActivity(new Intent(RecipesActivity.this, AddRecipeActivity.class)
				.putExtra("showDialog", true));
	}

	public void advSearch(View v) {
		startActivityForResult(new Intent(RecipesActivity.this,
				AdvSearchActivity.class), REQUEST_CODE);
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == REQUEST_CODE) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				mSubcategory = data.getIntExtra("Category", -1);
				mCategory = data.getIntExtra("Type", -1);
				mCalorie = data.getStringExtra("Calorie");
				Log("mCalorie = " + mCalorie);
				if (mCategory != -1 || mSubcategory != -1
						|| !TextUtils.isEmpty(mCalorie)) {
					findViewById(R.id.clearAdvSearchBtn).setVisibility(
							View.VISIBLE);
				} else {
					findViewById(R.id.clearAdvSearchBtn).setVisibility(
							View.GONE);
				}
				RecipesFragment.showRecipes();
			}
		}
	}

}
