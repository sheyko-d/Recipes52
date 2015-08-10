package com.stockholmapplab.recipes.views;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.stockholmapplab.recipes.AddRecipeActivity;
import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.SummaryActivity;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;

public class ControllableViewPager extends ViewPager {
	private boolean mShowToast;
	private float mDownX = 0;
	private boolean mOpenSummary;
	private boolean mTapped = false;

	public ControllableViewPager(Context context) {
		super(context);
	}

	public ControllableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mOpenSummary = true;
			mDownX = event.getX();
			mTapped = true;
			return super.dispatchTouchEvent(event);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float currentX = event.getX();
			if (Math.abs(mDownX - currentX) > 100) {
				if (mTapped) {
					mShowToast = true;
					mTapped = false;
				}
			}
			if (mDownX < currentX) {
				mShowToast = false;
				return super.dispatchTouchEvent(event);
			} else {
				if (AddRecipeActivity.pagerPos == 0) {
					try {
						if (PrefHelper.getString("AddTitle", "").equals("")) {
							if (mShowToast) {
								Toast.makeText(BaseApplication.getAppContext(),
										R.string.str_error_title_empty,
										Toast.LENGTH_SHORT).show();
								mShowToast = false;
							}
							return false;
						} else if (new JSONArray(PrefHelper.getString(
								"AddPhotos", "[]")).length() == 0) {
							if (mShowToast) {
								Toast.makeText(BaseApplication.getAppContext(),
										R.string.str_error_photos_empty,
										Toast.LENGTH_SHORT).show();
								mShowToast = false;
							}
							return false;
						} else {
							return super.dispatchTouchEvent(event);
						}
					} catch (Exception e) {
						return super.dispatchTouchEvent(event);
					}
				} else if (AddRecipeActivity.pagerPos == 1) {
					if (PrefHelper.getInt("AddCategoryPos", 14) == 14) {
						if (mShowToast) {
							Toast.makeText(BaseApplication.getAppContext(),
									R.string.str_error_category_empty,
									Toast.LENGTH_SHORT).show();
							mShowToast = false;
						}
						return false;
					} else if (PrefHelper.getInt("AddTypePos", 10) == 10) {
						if (mShowToast) {
							Toast.makeText(BaseApplication.getAppContext(),
									R.string.str_error_type_empty,
									Toast.LENGTH_SHORT).show();
							mShowToast = false;
						}
						return false;
					} else if (PrefHelper.getInt("AddH", 0) == 0
							& PrefHelper.getInt("AddMin", 0) == 0) {
						if (mShowToast) {
							Toast.makeText(BaseApplication.getAppContext(),
									R.string.str_error_time_empty,
									Toast.LENGTH_SHORT).show();
							mShowToast = false;
						}
						return false;
					} else {
						return super.dispatchTouchEvent(event);
					}
				} else if (AddRecipeActivity.pagerPos == 2) {
					SQLiteDatabase DB = new DBHelper(
							BaseApplication.getAppContext())
							.getReadableDatabase();
					Cursor cursor = DB.query("add_ingredients", null, null,
							null, null, null, null);
					if (cursor.getCount() == 0) {
						if (mShowToast) {
							Toast.makeText(BaseApplication.getAppContext(),
									R.string.str_error_ingredients_empty,
									Toast.LENGTH_SHORT).show();
							mShowToast = false;
						}
						cursor.close();
						DB.close();
						return false;
					} else {
						cursor.close();
						DB.close();
						return super.dispatchTouchEvent(event);
					}
				} else if (AddRecipeActivity.pagerPos == 3) {
					if (PrefHelper.getString("AddGuide", "").equals("")) {
						if (mShowToast) {
							Toast.makeText(BaseApplication.getAppContext(),
									R.string.str_error_description,
									Toast.LENGTH_SHORT).show();
							mShowToast = false;
						}
						return false;
					} else {
						if (mOpenSummary & Math.abs(mDownX - currentX) > 100
								& mDownX > currentX) {
							mOpenSummary = false;
							BaseApplication
									.getAppContext()
									.startActivity(
											new Intent(BaseApplication
													.getAppContext(),
													SummaryActivity.class)
													.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						}
						return super.dispatchTouchEvent(event);
					}
				} else {
					return super.dispatchTouchEvent(event);
				}
			}
		} else {
			return super.dispatchTouchEvent(event);
		}
	}

}