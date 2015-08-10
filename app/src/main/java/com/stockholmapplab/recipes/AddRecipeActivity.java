package com.stockholmapplab.recipes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.views.ControllableViewPager;
import com.stockholmapplab.recipes.R;

@SuppressLint("InlinedApi")
public class AddRecipeActivity extends ActionBarActivity {

	private ControllableViewPager mPager;
	private MyPagerAdapter mAdapter;
	public static int pagerPos = 0;
	public static AddRecipeActivity activity;
	private ContextMenu mMenu;
	private Dialog mErrorDialog;
	private Uri mPhotoUri;
	private boolean mDialogIsShowed = false;
	private MenuItem mAddItem;
	private MenuItem mAddLang;
	private MenuItem mLangItem;
	private MenuItem mLangEnItem;
	private MenuItem mLangSvItem;
	protected int mPagerPos;
	public static final int RESULT_CODE_GALLERY = 0;
	public static final int RESULT_CODE_CAMERA = 1;
	public static final int RESULT_CODE_SUMMARY = 2;
	private static final String GET_PROFILE_URL = "http://dev.core.stockholmapplab.com/webservice/user/getuserprofile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_add_recipe);

		initActionBar();

		activity = this;

		mPager = (ControllableViewPager) findViewById(R.id.addViewPager);
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager.setAdapter(mAdapter);

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				ImageView pageIndicator1 = (ImageView) findViewById(R.id.addPageIndicator1);
				ImageView pageIndicator2 = (ImageView) findViewById(R.id.addPageIndicator2);
				ImageView pageIndicator3 = (ImageView) findViewById(R.id.addPageIndicator3);
				ImageView pageIndicator4 = (ImageView) findViewById(R.id.addPageIndicator4);
				pageIndicator1
						.setImageResource(R.drawable.ic_page_indicator_disabled);
				pageIndicator2
						.setImageResource(R.drawable.ic_page_indicator_disabled);
				pageIndicator3
						.setImageResource(R.drawable.ic_page_indicator_disabled);
				pageIndicator4
						.setImageResource(R.drawable.ic_page_indicator_disabled);
				mPagerPos = pos;
				if (pos == 0) {
					pageIndicator1
							.setImageResource(R.drawable.ic_page_indicator_enabled);
					setTitle(getString(R.string.str_step1));
					if (mAddItem != null) {
						mAddItem.setVisible(false);
						mLangItem.setVisible(true);
					}
				} else if (pos == 1) {
					pageIndicator2
							.setImageResource(R.drawable.ic_page_indicator_enabled);
					setTitle(getString(R.string.str_step2));
					if (mAddItem != null) {
						mAddItem.setVisible(false);
						mLangItem.setVisible(false);
					}
				} else if (pos == 2) {
					pageIndicator3
							.setImageResource(R.drawable.ic_page_indicator_enabled);
					setTitle(getString(R.string.str_step3));
					if (mAddItem != null) {
						mAddItem.setVisible(true);
						mLangItem.setVisible(false);
					}
				} else {
					pageIndicator4
							.setImageResource(R.drawable.ic_page_indicator_enabled);
					setTitle(getString(R.string.str_step4));
					if (mAddItem != null) {
						mAddItem.setVisible(false);
						mLangItem.setVisible(false);
					}
				}

				changeArrowsVisibility(pos);
				pagerPos = pos;
			}

			private void changeArrowsVisibility(int pos) {
				if (pos == 0) {
					findViewById(R.id.addArrowPrevious)
							.setVisibility(View.GONE);
					findViewById(R.id.addArrowNext).setVisibility(View.VISIBLE);
				} else if (pos == 3) {
					// TODO: DONEDOENODEODEODNEODNOEN
				} else {
					findViewById(R.id.addArrowPrevious).setVisibility(
							View.VISIBLE);
					findViewById(R.id.addArrowNext).setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// TODO: Add other items
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("add_ingredients", null, null, null, null,
				null, null);
		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mDialogIsShowed = savedInstanceState.getBoolean("mDialogIsShowed",
					false);
		}
		if (!mDialogIsShowed) {
			if (!PrefHelper.getString("AddTitle", "").equals("")
					|| PrefHelper.getInt("AddSignature", 1) != 1
					|| !PrefHelper.getString("AddPhotos", "").equals("")
					|| PrefHelper.getInt("AddH", -1) != -1
					|| PrefHelper.getInt("AddMin", -1) != -1
					|| PrefHelper.getInt("AddTotalKCal", -1) != -1
					|| !PrefHelper.getString("AddGuide", "").equals("")
					|| cursor.moveToFirst()) {
				showContinueUnsavedDialog();
			}
			AddStep1Fragment.dialogClicked = false;
		}
		cursor.close();
		DB.close();

		new GetProfileTask().execute();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("mDialogIsShowed", mDialogIsShowed);
		super.onSaveInstanceState(savedInstanceState);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getString(R.string.str_step1));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);
	}

	private void setTitle(String title) {
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.recipes_add, menu);
		mAddItem = menu.findItem(R.id.action_add);
		mLangItem = menu.findItem(R.id.action_lang);
		mLangEnItem = menu.findItem(R.id.action_lang_en);
		mLangSvItem = menu.findItem(R.id.action_lang_sv);
		if (mPagerPos == 0) {
			if (mAddItem != null) {
				mAddItem.setVisible(false);
				mLangItem.setVisible(true);
			}
		} else if (mPagerPos == 1) {
			if (mAddItem != null) {
				mAddItem.setVisible(false);
				mLangItem.setVisible(false);
			}
		} else if (mPagerPos == 2) {
			if (mAddItem != null) {
				mAddItem.setVisible(true);
				mLangItem.setVisible(false);
			}
		} else {
			if (mAddItem != null) {
				mAddItem.setVisible(false);
				mLangItem.setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			close();
			return true;
		case R.id.action_add:
			add();
			return true;
		case R.id.action_lang_en:
			mLangEnItem.setChecked(true);
			mLangSvItem.setChecked(false);
			PrefHelper.setInt("AddLanguage", 0);
			mLangItem.setIcon(R.drawable.ic_flag_en);
			return true;
		case R.id.action_lang_sv:
			mLangSvItem.setChecked(true);
			mLangEnItem.setChecked(false);
			PrefHelper.setInt("AddLanguage", 1);
			mLangItem.setIcon(R.drawable.ic_flag_sv);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class GetProfileTask extends AsyncTask<String, Void, Void> {

		private String mErrorMessage;
		private JSONObject mResponseJSON;
		private Object mStatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(GET_PROFILE_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("TokenKey",
						PrefHelper.getString("TokenKey", "")));
				nameValuePairs.add(new BasicNameValuePair("UserId", PrefHelper
						.getInt("UserId", 0) + ""));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				Log(responseString);

				try {
					mResponseJSON = new JSONObject(responseString);

					mStatus = mResponseJSON.getString("status");
					if (mStatus.equals("ok")) {
						PrefHelper.setString("FirstName", mResponseJSON
								.getJSONObject("data").getString("FirstName"));
						PrefHelper.setString("LastName", mResponseJSON
								.getJSONObject("data").getString("LastName"));
						PrefHelper.setString("Email", mResponseJSON
								.getJSONObject("data").getString("Email"));
						PrefHelper.setString(
								"PhotoUpdateDate",
								mResponseJSON.getJSONObject("data").getString(
										"PhotoUpdateDate"));
					} else {
						mErrorMessage = mResponseJSON.getString("ErrorMessage");
						if (mResponseJSON.getString("ErrorType").equals(
								"validation")) {
							AddRecipeActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(
													AddRecipeActivity.this,
													"Error: " + mErrorMessage,
													Toast.LENGTH_LONG).show();
										}

									});
							PreferenceManager
									.getDefaultSharedPreferences(
											BaseApplication.getAppContext())
									.edit().clear().commit();
							AddRecipeActivity.this
									.startActivity(new Intent(
											AddRecipeActivity.this,
											LoginActivity.class).putExtra(
											"logout", true));
							RecipesActivity.activity.finish();
							HomeActivity.activity.finish();
						}
					}
				} catch (Exception e) {
				}
			} catch (ClientProtocolException e) {
				AddRecipeActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				AddRecipeActivity.this.runOnUiThread(new Runnable() {

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
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.acFlagImg) {
			mMenu = menu;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.add_recipe_lang_context, menu);
			if (PrefHelper.getInt("AddLanguage", 0) == 0) {
				MenuItem menuEn = menu.findItem(R.id.menu_en);
				menuEn.setChecked(true);
			} else if (PrefHelper.getInt("AddLanguage", 0) == 1) {
				MenuItem menuSv = menu.findItem(R.id.menu_sv);
				menuSv.setChecked(true);
			}
		} else {
			mMenu = menu;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.add_recipe_photo_context, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_gallery:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, RESULT_CODE_GALLERY);
			break;
		case R.id.menu_camera:
			intent = new Intent("android.media.action.IMAGE_CAPTURE");
			File photo = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".jpg");

			Log(Environment.getExternalStorageDirectory());
			mPhotoUri = Uri.fromFile(photo);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
			startActivityForResult(intent, RESULT_CODE_CAMERA);
			break;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == AddRecipeActivity.RESULT_CODE_GALLERY) {
				Uri selectedImageUri = data.getData();
				JSONArray oldPhotosJSON;
				try {
					oldPhotosJSON = new JSONArray(PrefHelper.getString(
							"AddPhotos", "[]"));
					PrefHelper.setString("AddPhotos",
							oldPhotosJSON.put(selectedImageUri.toString())
									.toString());
				} catch (JSONException e) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.str_error_photos, Toast.LENGTH_SHORT)
							.show();
				}
				View photoFrame = AddRecipeActivity.activity
						.getLayoutInflater().inflate(R.layout.photo, null);
				ImageView photoImage = (ImageView) photoFrame
						.findViewById(R.id.photoImg);
				AddStep1Fragment.photoLayout.addView(photoFrame);
				AddStep1Fragment.imgLoader.displayImage(
						selectedImageUri.toString(), photoImage,
						AddStep1Fragment.options);
				AddStep1Fragment.horizontalScrollView
						.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			} else if (requestCode == AddRecipeActivity.RESULT_CODE_CAMERA) {
				JSONArray oldPhotosJSON;
				try {
					oldPhotosJSON = new JSONArray(PrefHelper.getString(
							"AddPhotos", "[]"));
					PrefHelper.setString("AddPhotos",
							oldPhotosJSON.put(mPhotoUri.toString()).toString());
				} catch (JSONException e) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.str_error_photos, Toast.LENGTH_SHORT)
							.show();
				}
				View photoFrame = AddRecipeActivity.activity
						.getLayoutInflater().inflate(R.layout.photo, null);
				ImageView photoImage = (ImageView) photoFrame
						.findViewById(R.id.photoImg);
				AddStep1Fragment.photoLayout.addView(photoFrame);
				AddStep1Fragment.imgLoader.displayImage(mPhotoUri.toString(),
						photoImage, AddStep1Fragment.options);
				AddStep1Fragment.horizontalScrollView
						.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			} else if (requestCode == RESULT_CODE_SUMMARY) {
				Log("result = " + data.getIntExtra("edit", 3));
				mPager.setCurrentItem(data.getIntExtra("edit", 3));
			}

		}

	}

	private void showContinueUnsavedDialog() {
		final Dialog continueDialog = new Dialog(this, R.style.DialogTheme);
		continueDialog.setContentView(R.layout.raw_dialog_continue);
		continueDialog.setCanceledOnTouchOutside(false);

		CustomButton mBtnNew = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnContinue = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_ok);

		mBtnNew.setText(R.string.str_dialog_continue_btn_new);
		mBtnContinue.setText(R.string.str_dialog_continue_btn_continue);

		mBtnNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeSavedRecipe();
				continueDialog.dismiss();
				AddStep1Fragment.dialogClicked = true;
				mDialogIsShowed = true;
			}

		});

		mBtnContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Set old title
				AddStep1Fragment.titleEdittxt.setText(PrefHelper.getString(
						"AddTitle", ""));

				// Set old signature
				AddStep1Fragment.setButtonBg(AddStep1Fragment.button2,
						R.drawable.add_btn_2_selector);
				if (PrefHelper.getInt("AddSignature", 1) == 2) {
					AddStep1Fragment.setButtonBg(AddStep1Fragment.button1,
							R.drawable.add_btn_1_selected_patch);
				} else if (PrefHelper.getInt("AddSignature", 1) == 1) {
					AddStep1Fragment.setButtonBg(AddStep1Fragment.button2,
							R.drawable.add_btn_2_selected_patch);
				} else if (PrefHelper.getInt("AddSignature", 1) == 0) {
					AddStep1Fragment.setButtonBg(AddStep1Fragment.button3,
							R.drawable.add_btn_3_selected_patch);
				}
				Log.d("Log", "add photos3");
				AddStep1Fragment.AddPhotos();

				AddStep1Fragment.dialogClicked = true;

				continueDialog.dismiss();
				mDialogIsShowed = true;
			}

		});
		continueDialog.show();
	}

	private void showClosingDialog() {
		final Dialog continueDialog = new Dialog(this, R.style.DialogTheme);
		continueDialog.setContentView(R.layout.raw_dialog_continue);
		continueDialog.setCanceledOnTouchOutside(false);

		CustomButton mBtnCancel = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOK = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_ok);

		((TextView) continueDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_closing_title);
		((TextView) continueDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_closing_msg);

		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnOK.setText(R.string.str_dialog_2);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				continueDialog.dismiss();
			}

		});

		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				continueDialog.dismiss();
			}

		});
		continueDialog.show();
	}

	private void removeSavedRecipe() {
		// TODO: ADD OTHER ITEMS
		PrefHelper.deletePreference("AddTitle");
		PrefHelper.deletePreference("AddSignature");
		PrefHelper.deletePreference("AddPhotos");
		PrefHelper.deletePreference("AddH");
		PrefHelper.deletePreference("AddMin");
		PrefHelper.deletePreference("AddTotalKCal");
		PrefHelper.deletePreference("AddGuide");
		PrefHelper.setBoolean("AddCalculateTotalKCal", true);
		PrefHelper.deletePreference("AddLanguage");
		PrefHelper.deletePreference("AddCategoryPos");
		PrefHelper.deletePreference("AddCategoryId");
		PrefHelper.deletePreference("AddTypePos");
		PrefHelper.deletePreference("AddTypeId");
		PrefHelper.deletePreference("AddMetricSystem");
		AddStep2Fragment.mTimeTxt.setText("0min");
		AddStep2Fragment.mCategoryTxt
				.setText(AddStep2Fragment.mCategoriesArrayList
						.get(AddStep2Fragment.mCategoriesArrayList.size() - 1));
		AddStep2Fragment.mTypeTxt
				.setText(AddStep2Fragment.mSubcategoriesArrayList
						.get(AddStep2Fragment.mSubcategoriesArrayList.size() - 1));

		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getWritableDatabase();
		DB.delete("add_ingredients", null, null);
		DB.close();
	}

	public void previous(View v) {
		if (pagerPos != 0) {
			pagerPos--;
			mPager.setCurrentItem(pagerPos);
		}
	}

	public void next(View v) {
		boolean showNextPage = false;
		if (pagerPos == 0) {
			try {
				if (PrefHelper.getString("AddTitle", "").equals("")) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.str_error_title_empty, Toast.LENGTH_SHORT)
							.show();
					showNextPage = false;
				} else if (new JSONArray(
						PrefHelper.getString("AddPhotos", "[]")).length() == 0) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.str_error_photos_empty, Toast.LENGTH_SHORT)
							.show();
					showNextPage = false;
				} else {
					showNextPage = true;
				}
			} catch (Exception e) {
			}
		} else if (pagerPos == 1) {
			if (PrefHelper.getInt("AddCategoryPos", 14) == 14) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_category_empty, Toast.LENGTH_SHORT)
						.show();
				showNextPage = false;
			} else if (PrefHelper.getInt("AddTypePos", 10) == 10) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_type_empty, Toast.LENGTH_SHORT)
						.show();
				showNextPage = false;
			} else if (PrefHelper.getInt("AddH", 0) == 0
					& PrefHelper.getInt("AddMin", 0) == 0) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_time_empty, Toast.LENGTH_SHORT)
						.show();
				showNextPage = false;
			} else {
				showNextPage = true;
			}
		} else if (pagerPos == 2) {
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getReadableDatabase();
			Cursor cursor = DB.query("add_ingredients", null, null, null, null,
					null, null);
			if (cursor.getCount() == 0) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_ingredients_empty,
						Toast.LENGTH_SHORT).show();
				showNextPage = false;
				cursor.close();
				DB.close();
			} else {
				showNextPage = true;
			}
		} else if (pagerPos == 3) {
			showNextPage = false;
			if (PrefHelper.getString("AddGuide", "").equals("")) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_description, Toast.LENGTH_SHORT)
						.show();
			} else {
				startActivityForResult(new Intent(AddRecipeActivity.this,
						SummaryActivity.class), RESULT_CODE_SUMMARY);
			}
			// new AddRecipeTask().execute();
		}
		if (showNextPage) {
			pagerPos++;
			mPager.setCurrentItem(pagerPos);
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return AddStep1Fragment.newInstance();
			} else if (position == 1) {
				return AddStep2Fragment.newInstance();
			} else if (position == 2) {
				return AddStep3Fragment.newInstance();
			} else {
				return AddStep4Fragment.newInstance();
			}
		}
	}

	private void showErrorDialog(String title, String message) {
		mErrorDialog = new Dialog(AddRecipeActivity.this, R.style.DialogTheme);
		mErrorDialog.setContentView(R.layout.raw_dialog_error);
		mErrorDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtErrorTitle = (CustomTextView) mErrorDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtErrorMessage = (CustomTextView) mErrorDialog
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnCancel = (CustomButton) mErrorDialog
				.findViewById(R.id.bt_btn_Cancel);

		if (title == null) {
			mTxtErrorTitle.setVisibility(View.GONE);
		} else {
			mTxtErrorTitle.setText(title);
		}
		mTxtErrorMessage.setText(message);
		mBtnCancel.setText(R.string.str_dialog_5);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mErrorDialog.dismiss();
			}
		});
		mErrorDialog.show();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	public void close() {
		// TODO: Add other items
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("add_ingredients", null, null, null, null,
				null, null);
		if (getIntent().getBooleanExtra("showDialog", false)) {
			if (!PrefHelper.getString("AddTitle", "").equals("")
					|| PrefHelper.getInt("AddSignature", 1) != 1
					|| !PrefHelper.getString("AddPhotos", "").equals("")
					|| PrefHelper.getInt("AddH", -1) != -1
					|| PrefHelper.getInt("AddMin", -1) != -1
					|| PrefHelper.getInt("AddTotalKCal", -1) != -1
					|| !PrefHelper.getString("AddGuide", "").equals("")
					|| cursor.moveToFirst()) {
				showClosingDialog();
			} else {
				finish();
			}
		} else {
			finish();
		}
		cursor.close();
		DB.close();
	}

	public void add() {
		startActivity(new Intent(AddRecipeActivity.this,
				ChooseIngredientActivity.class));
	}

	public void showAutocalculationDialog(View v) {
		final Dialog continueDialog = new Dialog(this, R.style.DialogTheme);
		continueDialog.setContentView(R.layout.raw_dialog_continue);
		continueDialog.setCanceledOnTouchOutside(false);

		CustomButton mBtnCancel = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOK = (CustomButton) continueDialog
				.findViewById(R.id.bt_btn_ok);

		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnOK.setText(R.string.str_dialog_2);

		((TextView) continueDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_dialog_autocalculation_title);
		((TextView) continueDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_dialog_autocalculation_msg);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				continueDialog.dismiss();
			}

		});

		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PrefHelper.setBoolean("AddCalculateTotalKCal", false);
				AddStep3Fragment.totalKCalTxt.setText(PrefHelper.getInt(
						"AddTotalKCalCustom", 0) + "");
				AddStep3Fragment.totalKCalTxt.setCursorVisible(true);
				AddStep3Fragment.totalKCalView.setVisibility(View.GONE);
				AddStep3Fragment.calculateCheckbox.setChecked(false);
				continueDialog.dismiss();
			}

		});
		continueDialog.show();
	}

	@Override
	public void onBackPressed() {
		close();
	}

	public void back(View v) {
		close();
	}
}
