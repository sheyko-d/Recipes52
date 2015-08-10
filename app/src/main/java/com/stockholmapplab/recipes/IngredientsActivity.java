package com.stockholmapplab.recipes;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.IngredientsAdapter;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.R;

public class IngredientsActivity extends ActionBarActivity {
	private ListView mIngredientsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_ingredients);

		initActionBar();

		// Set title
		if (RecipeDetailsActivity.mName != null) {
			((TextView) findViewById(R.id.ingredientsTitleTxt))
					.setText(RecipeDetailsActivity.mName);
		} else if (SummaryActivity.mName != null) {
			((TextView) findViewById(R.id.ingredientsTitleTxt))
					.setText(SummaryActivity.mName);
		}

		// Add items to list
		mIngredientsList = (ListView) findViewById(R.id.ingredientsListLarge);
		if (RecipeDetailsActivity.ingredientsAdapter != null) {
			mIngredientsList
					.setAdapter(RecipeDetailsActivity.ingredientsAdapter);
		} else if (SummaryActivity.ingredientsAdapter != null) {
			mIngredientsList.setAdapter(SummaryActivity.ingredientsAdapter);
		}

		IngredientsAdapter.layout = R.layout.item_ingredient_large;
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_ingredients));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		default:
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}
