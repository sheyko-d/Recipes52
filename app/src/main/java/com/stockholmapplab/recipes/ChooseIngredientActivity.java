package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.ChooseIngredientAdapter;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class ChooseIngredientActivity extends ActionBarActivity {
	private ListView mIngredientsList;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private Button mSortBtn;
	public static CheckBox ingredientCheckbox;
	public static ChooseIngredientActivity activity;

	private ArrayList<String> mSortArrayList;
	private boolean mOnlyFavorite = false;
	public static TextView addIngredientCheckbox;

	public static String sortBy = "";
	public static final String SORT_TYPE_TITLE_EN = "title_en ASC";
	public static final String SORT_TYPE_TITLE_SW = "title_sw ASC";
	public static final String SORT_TYPE_KCAL = "kcal ASC";

	public static int selectedItemId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		
		selectedItemId = -1;
		ChooseIngredientAdapter.selectedItemId = -1;

		activity = this;

		setContentView(R.layout.ac_choose_ingredient);

		ingredientCheckbox = (CheckBox) findViewById(R.id.editIngredientCheckbox);
		addIngredientCheckbox = (TextView) findViewById(R.id.addIngredientCheckbox);

		initActionBar();

		mSortBtn = (Button) findViewById(R.id.sortBtn);
		setSortType();

		if (savedInstanceState == null) {
			mFragmentManager = getSupportFragmentManager();
			mFragmentManager.popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.add(R.id.recipesFragmentLayout,
					new ChooseIngredientFragment());
			mFragmentTransaction.commit();
		}

		registerForContextMenu(mSortBtn);
		mSortArrayList = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.ingredient_sort_array)));
		mSortBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						ChooseIngredientActivity.this);

				// Set adapter to list in dialog
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						ChooseIngredientActivity.this,
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
						PrefHelper.getInt("IngredientSortType", 0),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PrefHelper.setInt("IngredientSortType", which);
								setSortType();
								ChooseIngredientFragment.showIngredients();

								// Dismiss dialog
								dialog.dismiss();
							}
						});
				dialogBuilder.show();
			}
		});

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mOnlyFavorite = savedInstanceState.getBoolean("mOnlyFavorite",
					false);
			if (mOnlyFavorite) {
				// Reset search text
				ChooseIngredientFragment.searchText = "";
				ChooseIngredientFragment.onlyFavorite = true;

				// Change tabs
				findViewById(R.id.tabsFavorite).setVisibility(View.VISIBLE);
				findViewById(R.id.tabsAll).setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_calorie_menu));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

	public void add() {
		startActivity(new Intent(ChooseIngredientActivity.this,
				EditIngredientActivity.class));
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	public void showFavorites(View v) {
		// Reset search text
		ChooseIngredientFragment.searchText = "";
		ChooseIngredientFragment.onlyFavorite = true;

		// Change tabs
		findViewById(R.id.tabsFavorite).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsAll).setVisibility(View.INVISIBLE);

		// Replace fragment
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		// ChooseIngredientFragment.isFavorite = true;
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new ChooseIngredientFragment());
		mFragmentTransaction.commit();

		mOnlyFavorite = true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("mOnlyFavorite", mOnlyFavorite);
		super.onSaveInstanceState(savedInstanceState);
	}

	public void showAll(View v) {
		// Reset search text
		ChooseIngredientFragment.searchText = "";
		ChooseIngredientFragment.onlyFavorite = false;

		// Change tabs
		findViewById(R.id.tabsAll).setVisibility(View.VISIBLE);
		findViewById(R.id.tabsFavorite).setVisibility(View.INVISIBLE);

		// Replace fragment
		// ChooseIngredientFragment.isFavorite = false;
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.recipesFragmentLayout,
				new ChooseIngredientFragment());
		mFragmentTransaction.commit();

		mOnlyFavorite = false;
	}

	private void setSortType() {
		if (PrefHelper.getInt("IngredientSortType", 0) == 0) {
			sortBy = SORT_TYPE_TITLE_EN;
			mSortBtn.setText(getResources().getString(R.string.str_sort_title));
		} else if (PrefHelper.getInt("IngredientSortType", 0) == 1) {
			sortBy = SORT_TYPE_KCAL;
			mSortBtn.setText(getResources().getString(R.string.str_sort_kcal));
		}
	}

	public void edit(View v) {
		startActivity(new Intent(ChooseIngredientActivity.this,
				EditIngredientActivity.class).putExtra("Id",
				ChooseIngredientAdapter.selectedItemId));
	}

}
