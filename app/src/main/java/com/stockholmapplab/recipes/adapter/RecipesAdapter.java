package com.stockholmapplab.recipes.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.RecipesFragment;
import com.stockholmapplab.recipes.util.DBHelper;

import java.util.ArrayList;

public class RecipesAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Integer> mIdsArrayList;
	private ArrayList<String> mTitlesArrayList;
	private ArrayList<Integer> mCaloriesArrayList;
	private ArrayList<String> mRatingsArrayList;
	private ArrayList<Integer> mCategoriesIdsArrayList;
	private ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private ArrayList<Boolean> mIsTitlesArrayList;
	private ArrayList<Integer> mIsFavoriteArrayList;
	private ArrayList<String> mStatusesArrayList;
	private Context mContext;
	private View convertView;
	private SQLiteDatabase mDB;

	public RecipesAdapter(Context context, ArrayList<Integer> ids,
			ArrayList<String> titles, ArrayList<Integer> calories,
			ArrayList<String> ratings, ArrayList<Integer> categories,
			ArrayList<Boolean> isTitles, ArrayList<Integer> isFavorite,
			ArrayList<String> statuses) {
		mContext = context;
		mIdsArrayList = ids;
		mTitlesArrayList = titles;
		mCaloriesArrayList = calories;
		mRatingsArrayList = ratings;
		mIsTitlesArrayList = isTitles;
		mIsFavoriteArrayList = isFavorite;
		mStatusesArrayList = statuses;
		mCategoriesIdsArrayList = categories;
	}

	@Override
	public void notifyDataSetChanged() {
		mCategoriesArrayList.clear();

		Resources resources = mContext.getResources();
		mDB = new DBHelper(mContext).getWritableDatabase();
		for (Integer category : mCategoriesIdsArrayList) {
			Cursor cursor = mDB.query("categories", null, "id='" + category
					+ "'", null, null, null, null);
			if (cursor.moveToFirst()) {
				mCategoriesArrayList.add(resources.getString(resources
						.getIdentifier(cursor.getString(cursor
								.getColumnIndex("title_id")), "string",
								mContext.getPackageName())));
			} else {
				mCategoriesArrayList.add("");
			}
			cursor.close();
		}
		mDB.close();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mIdsArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log("get position " + position + " (" + mTitlesArrayList.get(position)
				+ ", " + mIsTitlesArrayList.get(position) + ")");
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		HeaderViewHolder headerHolder = null;
		ChildViewHolder childHolder = null;
		if (mIsTitlesArrayList.get(position)) {
			// view is creating
			if (convertView == null
					|| !(convertView.getTag() instanceof HeaderViewHolder)) {
				headerHolder = new HeaderViewHolder();
				convertView = (View) inflater.inflate(R.layout.item_header,
						null);

				headerHolder.header = (TextView) convertView
						.findViewById(R.id.recipesHeaderTitleTxt);

				convertView.setTag(headerHolder);
			} else {
				headerHolder = (HeaderViewHolder) convertView.getTag();
			}

			headerHolder.header.setText(mTitlesArrayList.get(position));
		} else {
			if (convertView == null
					|| !(convertView.getTag() instanceof ChildViewHolder)) {
				childHolder = new ChildViewHolder();
				convertView = (View) inflater.inflate(R.layout.item_recipes,
						null);

				childHolder.starImg = (ImageView) convertView
						.findViewById(R.id.starImg);
				childHolder.title = (TextView) convertView
						.findViewById(R.id.recipesTitleTxt);
				childHolder.kcal = (TextView) convertView
						.findViewById(R.id.recipesKcalTxt);
				childHolder.rating = (TextView) convertView
						.findViewById(R.id.recipesRatingTxt);
				childHolder.subcategory = (TextView) convertView
						.findViewById(R.id.recipesSubcategoryTxt);

				convertView.setTag(childHolder);
			} else {
				childHolder = (ChildViewHolder) convertView.getTag();
			}

			if ((position % 2) == 0) {
				convertView
						.setBackgroundResource(R.drawable.list_light_selector);
			} else {
				convertView
						.setBackgroundResource(R.drawable.list_dark_selector);
			}
			childHolder.starImg.setTag(mIdsArrayList.get(position));

			childHolder.title.setText(mTitlesArrayList.get(position));

			if (mIsFavoriteArrayList.get(position) == 1) {
				childHolder.starImg
						.setImageResource(R.drawable.ic_star_enabled);
			} else {
				childHolder.starImg
						.setImageResource(R.drawable.ic_star_disabled);
			}
			childHolder.starImg.setOnClickListener(starClickListener);
			if (mStatusesArrayList.get(position).equals("Approved")) {
				childHolder.kcal.setVisibility(View.VISIBLE);
				childHolder.kcal.setText(mCaloriesArrayList.get(position) + "");

				childHolder.rating.setVisibility(View.VISIBLE);
				childHolder.rating.setText(String.format("%.2f",
						Float.parseFloat(mRatingsArrayList.get(position)))
						.replaceAll(",", "."));
			} else if (mStatusesArrayList.get(position).equals("Rejected")) {
				childHolder.rating.setVisibility(View.GONE);
				childHolder.kcal.setVisibility(View.VISIBLE);
				childHolder.kcal.setText(Html.fromHtml("<font color='#da0000'>"
						+ mStatusesArrayList.get(position) + "</font>"));
			} else {
				childHolder.kcal.setVisibility(View.VISIBLE);
				childHolder.rating.setVisibility(View.GONE);
				childHolder.kcal.setText(Html.fromHtml("<font color='#65a303'>"
						+ mStatusesArrayList.get(position) + "</font>"));
			}
			childHolder.subcategory.setText(mCategoriesArrayList.get(position));
		}

		return convertView;
	}

	public static class HeaderViewHolder {
		TextView header;
	}

	public static class ChildViewHolder {
		TextView title;
		TextView kcal;
		TextView rating;
		TextView subcategory;
		ImageView starImg;
		Boolean isTitle;
	}

	OnClickListener starClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((ImageView) v).setImageResource(R.drawable.ic_star_enabled);
			mDB = new DBHelper(mContext).getWritableDatabase();
			Cursor c = mDB.query("recipes_list", null, "id='" + v.getTag()
					+ "'", null, null, null, null);
			if (c.moveToFirst()) {
				ContentValues cv = new ContentValues();
				if (c.getInt(c.getColumnIndex("is_favorite")) > 0) {
					Log("set not favorite");
					// If recipe is marked as favorite
					cv.put("is_favorite", "0");
				} else {
					Log("set favorite");
					cv.put("is_favorite", "1");
				}
				mDB.update("recipes_list", cv, "id='" + v.getTag() + "'", null);
			}
			c.close();
			mDB.close();
			RecipesFragment.clearList();
			RecipesFragment.searchText = "";
			RecipesFragment.getRecipesFromDB();
			notifyDataSetChanged();
		}
	};

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}

}
