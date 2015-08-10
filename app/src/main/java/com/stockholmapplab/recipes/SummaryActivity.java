package com.stockholmapplab.recipes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.stockholmapplab.recipes.adapter.IngredientsAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.stockholmapplab.recipes.R;

public class SummaryActivity extends ActionBarActivity {

	private int mId;
	private TextView mDescTxt;
	private RatingBar mRatingBar;
	private ImageView mScreenshotImg;
	private ImageLoader mImgLoader;
	private DisplayImageOptions mOptions;
	public static IngredientsAdapter ingredientsAdapter;
	private ListView mIngredientsList;
	private TextView mRatingTxt;
	private TextView mAuthorNameTxt;
	private TextView mTimeTxt;
	private TextView mKcalTxt;
	public String mTime;
	public int mKCal;
	private int mRateValue = 0;
	private String mScreenshotUrl;
	public static String mName;
	public static SummaryActivity activity;
	private static ArrayList<String> mIngTitlesArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngMVArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngMArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngKcalArrayList = new ArrayList<String>();
	private static final String RATE_RECIPE_URL = "http://dev.core.stockholmapplab.com/Webservice/Recipe/RateRecipe";
	private ViewPager mPager;
	private MyPagerAdapter mAdapter;
	public JSONArray mScreenshotsArray = new JSONArray();
	private boolean mEditMode = false;
	private MenuItem mLangItem;
	private int mLangIconRes;
	private static final String ADD_URL = "http://dev.core.stockholmapplab.com/webservice/recipe/createrecipe";

	public static Integer language;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_summary);

		initActionBar();

		activity = this;

		mDescTxt = (TextView) findViewById(R.id.detailsDescTxt);
		mRatingBar = (RatingBar) findViewById(R.id.detailsRatingBar);
		mScreenshotImg = (ImageView) findViewById(R.id.detailsScreenshotImg);
		mRatingTxt = (TextView) findViewById(R.id.recipesRatingTxt);
		mAuthorNameTxt = (TextView) findViewById(R.id.detailsAuthorTxt);
		mTimeTxt = (TextView) findViewById(R.id.detailsTimeTxt);
		mKcalTxt = (TextView) findViewById(R.id.detailsKcalTxt);

		// Add adapter to ingredients list
		mIngredientsList = (ListView) findViewById(R.id.ingredientsList);
		ingredientsAdapter = new IngredientsAdapter(this, mIngTitlesArrayList,
				mIngMVArrayList, mIngMArrayList, mIngKcalArrayList);
		IngredientsAdapter.layout = R.layout.item_ingredient;

		mIngredientsList.setAdapter(ingredientsAdapter);

		mId = getIntent().getIntExtra("Id", -1);

		// initialise ImageLoader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).build();
		mImgLoader = ImageLoader.getInstance();
		mImgLoader.init(config);
		mOptions = new DisplayImageOptions.Builder().displayer(
				new FadeInBitmapDisplayer(300)).build();

		mPager = (ViewPager) findViewById(R.id.detailsViewPager);
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager.setAdapter(mAdapter);

		// Set description
		mDescTxt.setText(PrefHelper.getString("AddGuide", ""));
		// Set rating to ratingBar
		mRatingBar.setRating(0);
		// Add ending to "vote" string, if it's >1, or =0
		// Set time text
		mTimeTxt.setText(mTime);
		// Set kcal text
		if (PrefHelper.getBoolean("AddCalculateTotalKCal", true)) {
			mKCal = PrefHelper.getInt("AddTotalKCal", 0);
		} else {
			mKCal = PrefHelper.getInt("AddTotalKCalCustom", -1);
		}
		mKcalTxt.setText(mKCal + " kcal");
		String hours;
		if (PrefHelper.getInt("AddH", 0) == 0) {
			hours = "";
		} else {
			hours = PrefHelper.getInt("AddH", 0) + "h, ";
		}
		String min = PrefHelper.getInt("AddMin", 0) + "min";
		mTimeTxt.setText(hours + min);

		// Set author text
		if (PrefHelper.getInt("AddSignature", 1) == 0) {
			mAuthorNameTxt.setText("You");
		} else if (PrefHelper.getInt("AddSignature", 1) == 1) {
			if (PrefHelper.getString("FirstName", null) != null
					& !PrefHelper.getString("FirstName", "").equals("null")) {
				mAuthorNameTxt.setText(PrefHelper.getString("FirstName", ""));
			}
		} else if (PrefHelper.getInt("AddSignature", 1) == 2) {
			if (PrefHelper.getString("FirstName", null) != null
					& !PrefHelper.getString("FirstName", "").equals("null")) {
				mAuthorNameTxt.setText(PrefHelper.getString("FirstName", ""));
			} else if (PrefHelper.getString("Email", null) != null
					& !PrefHelper.getString("Email", "").equals("null")) {
				findViewById(R.id.detailsAuthorImg).setVisibility(View.VISIBLE);
			}
		}

		// Set rating text
		mRatingTxt.setText(getResources().getString(R.string.str_rating_1)
				+ " " + 0 + " (" + 0 + " "
				+ getResources().getString(R.string.str_rating_2) + ")");

		mRatingBar.setRating((float) 0);

		language = PrefHelper.getInt("AddLanguage", 0);
		// Set flag image in action bar
		if (language == 1) {
			mLangIconRes = R.drawable.ic_flag_sv;
		} else {
			mLangIconRes = R.drawable.ic_flag_en;
		}
		if (mLangItem != null)
			mLangItem.setIcon(mLangIconRes);

		try {
			mScreenshotsArray = new JSONArray(PrefHelper.getString("AddPhotos",
					"[]"));
			mAdapter.notifyDataSetChanged();
			mScreenshotUrl = mScreenshotsArray.getString(0);
			// Show 1st screenshot image
			mImgLoader.displayImage(mScreenshotUrl, mScreenshotImg, mOptions,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							findViewById(R.id.screenshotProgressBar)
									.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							findViewById(R.id.screenshotProgressBar)
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
						}
					});
		} catch (JSONException e) {
		}

		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		Cursor cursor = DB.query("add_ingredients", null, null, null, null,
				null, null);
		mIngTitlesArrayList.clear();
		mIngMVArrayList.clear();
		mIngMArrayList.clear();
		mIngKcalArrayList.clear();
		while (cursor.moveToNext()) {
			if (BaseApplication.isSwedish()) {
				mIngTitlesArrayList.add(cursor.getString(cursor
						.getColumnIndex("title_sw")));
			} else {
				mIngTitlesArrayList.add(cursor.getString(cursor
						.getColumnIndex("title_en")));
			}
			Cursor cursor_measurements = DB.query("measurements", null, "id='"
					+ cursor.getInt(cursor.getColumnIndex("measurement_value"))
					+ "'", null, null, null, null);
			mIngMVArrayList.add(cursor.getInt(cursor
					.getColumnIndex("measurement")) + "");
			if (cursor_measurements.moveToFirst()) {
				mIngMArrayList.add(getResources().getString(
						getResources().getIdentifier(
								cursor_measurements
										.getString(cursor_measurements
												.getColumnIndex("title_id")),
								"string",
								BaseApplication.getAppContext()
										.getPackageName())));
			}
			cursor_measurements.close();
			mIngKcalArrayList.add(cursor.getInt(cursor.getColumnIndex("kcal"))
					+ "");
		}
		cursor.close();
		DB.close();
		ingredientsAdapter.notifyDataSetChanged();

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mEditMode = savedInstanceState.getBoolean("mEditMode", false);
		}
		if (mEditMode) {
			findViewById(R.id.summaryEditLayout).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.editBtn))
					.setText(R.string.str_summary_finish_editing);
		}
	}

	public void email(View v) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { PrefHelper.getString("Email", "") });
		Intent mailer = Intent.createChooser(intent, null);
		startActivity(mailer);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("mEditMode", mEditMode);
		super.onSaveInstanceState(savedInstanceState);
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
			return mScreenshotsArray.length();
		}

		@Override
		public Fragment getItem(int position) {
			LargePhotoFragment photoFragment = new LargePhotoFragment();
			Bundle bundle = new Bundle();
			try {
				bundle.putString("link", mScreenshotsArray.getString(position));
			} catch (Exception e) {
			}
			photoFragment.setArguments(bundle);
			return photoFragment;
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		mName = PrefHelper.getString("AddTitle", "");
		SpannableString s = new SpannableString(mName);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.recipe_details, menu);
		mLangItem = menu.findItem(R.id.action_lang);
		if (mLangIconRes != 0)
			mLangItem.setIcon(mLangIconRes);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_lang:
			return true;
		default:
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		IngredientsAdapter.layout = R.layout.item_ingredient;
	}

	// Format number of minutes to hours and minutes string
	private String formatTime(int minutes) {
		int hours = (minutes / 60);
		minutes = minutes - hours * 60;
		if (hours == 0) {
			return minutes + "m";
		} else if (minutes == 0) {
			return hours + "h";
		} else {
			return hours + "h, " + minutes + "m";
		}
	}

	private class RateRecipeTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private JSONObject mResponseJSON;
		private Object mStatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(RATE_RECIPE_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("TokenKey",
						PrefHelper.getString("TokenKey", "")));
				nameValuePairs
						.add(new BasicNameValuePair("RecipeId", mId + ""));
				nameValuePairs.add(new BasicNameValuePair("RateValue",
						mRateValue + ""));

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
					} else {
						try {
							mErrorMessage = mResponseJSON
									.getString("ErrorMessage");
							if (mResponseJSON.getString("ErrorType").equals(
									"validation")) {
								SummaryActivity.this
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														SummaryActivity.this,
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
								SummaryActivity.this.startActivity(new Intent(
										SummaryActivity.this,
										LoginActivity.class).putExtra("logout",
										true));
								RecipesActivity.activity.finish();
								HomeActivity.activity.finish();
							}
						} catch (JSONException e) {
						}
						mError = true;
					}
				}
			} catch (ClientProtocolException e) {
				SummaryActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				SummaryActivity.this.runOnUiThread(new Runnable() {

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
				Toast.makeText(SummaryActivity.this,
						"Thanks for your feedback!", Toast.LENGTH_SHORT).show();

				// Show progress bar
				findViewById(R.id.mainProgressBar).setVisibility(View.VISIBLE);

			}
		}

	}

	private static void showErrorDialog(String title, String message) {
		final Dialog errorDialog = new Dialog(SummaryActivity.activity,
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
	}

	public void back(View v) {
		showClosingDialog();
	}

	public void share(View v) {
		BaseApplication.openActiveSession(this, true,
				new Session.StatusCallback() {

					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
						if (session.isOpened()) {
							Bundle params = new Bundle();
							params.putString("name",
									getResources().getString(R.string.app_name));
							params.putString("caption", getResources()
									.getString(R.string.str_facebook_caption));
							params.putString("description", mName);
							params.putString("link",
									"http://stockholmapplab.com/");
							params.putString("picture", mScreenshotUrl);

							WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
									SummaryActivity.this, session, params))
									.setOnCompleteListener(
											new OnCompleteListener() {

												@Override
												public void onComplete(
														Bundle values,
														FacebookException error) {
													if (error == null) {
														// When the story is
														// posted, echo the
														// success
														final String postId = values
																.getString("post_id");
														if (postId != null) {
															Toast.makeText(
																	SummaryActivity.this,
																	"Story is posted",
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															// User clicked the
															// Cancel button
															Toast.makeText(
																	SummaryActivity.this
																			.getApplicationContext(),
																	"Publish cancelled",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													} else if (error instanceof FacebookOperationCanceledException) {
														// User clicked the "x"
														// button
														Toast.makeText(
																SummaryActivity.this
																		.getApplicationContext(),
																"Publish cancelled",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														// Generic, ex: network
														// error
														Toast.makeText(
																SummaryActivity.this
																		.getApplicationContext(),
																"Error posting story",
																Toast.LENGTH_SHORT)
																.show();
													}
												}

											}).build();
							feedDialog.show();
						}
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public void rate(View v) {
		mRateValue = 0;
		final Dialog rateDialog = new Dialog(this, R.style.DialogTheme);
		rateDialog.setContentView(R.layout.raw_dialog_rate);
		rateDialog.setCanceledOnTouchOutside(false);

		CustomButton mBtnCancel = (CustomButton) rateDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOk = (CustomButton) rateDialog
				.findViewById(R.id.bt_btn_ok);
		RatingBar mRatingBar = (RatingBar) rateDialog
				.findViewById(R.id.detailsRatingBar);

		mRatingBar
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						mRateValue = (int) rating;
					}
				});

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rateDialog.dismiss();
			}
		});

		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRateValue == 0) {
					Toast.makeText(SummaryActivity.this,
							R.string.str_rate_directions_dialog,
							Toast.LENGTH_SHORT).show();
				} else {
					new RateRecipeTask().execute();
					rateDialog.dismiss();
				}
			}
		});
		rateDialog.show();
	}

	public void fullScreen(View v) {
		findViewById(R.id.detailsLargeImgBg).setVisibility(View.VISIBLE);
		findViewById(R.id.detailsLargeImgBg).startAnimation(
				AnimationUtils.loadAnimation(this, R.anim.fade_in));
	}

	public void closeFullscreen(View v) {
		Animation animFadeOut = AnimationUtils.loadAnimation(this,
				R.anim.fade_out);
		animFadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				findViewById(R.id.detailsLargeImgBg).setVisibility(View.GONE);
			}
		});
		findViewById(R.id.detailsLargeImgBg).startAnimation(animFadeOut);
	}

	@Override
	public void onBackPressed() {
		if (findViewById(R.id.detailsLargeImgBg).getVisibility() == View.VISIBLE) {
			Animation animFadeOut = AnimationUtils.loadAnimation(this,
					R.anim.fade_out);
			animFadeOut.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					findViewById(R.id.detailsLargeImgBg).setVisibility(
							View.GONE);
				}
			});
			findViewById(R.id.detailsLargeImgBg).startAnimation(animFadeOut);
		} else
			super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		mName = null;
		ingredientsAdapter = null;
		super.onDestroy();
	}

	public void largeIngList(View v) {
		// Launch ingredients screen
		startActivity(new Intent(SummaryActivity.this,
				IngredientsActivity.class));
	}

	public void edit(View v) {
		if (findViewById(R.id.summaryEditLayout).getVisibility() == View.GONE) {
			findViewById(R.id.summaryEditLayout).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.editBtn))
					.setText(R.string.str_summary_finish_editing);
			mEditMode = true;
		} else {
			findViewById(R.id.summaryEditLayout).setVisibility(View.GONE);
			((Button) findViewById(R.id.editBtn))
					.setText(R.string.str_summary_editing);
			mEditMode = false;
		}
	}

	public void review(View v) {
		final Dialog rejectDialog = new Dialog(SummaryActivity.activity,
				R.style.DialogTheme);
		rejectDialog.setContentView(R.layout.raw_dialog_terms);
		rejectDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtTitle = (CustomTextView) rejectDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtMessage = (CustomTextView) rejectDialog
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnCancel = (CustomButton) rejectDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOk = (CustomButton) rejectDialog
				.findViewById(R.id.bt_btn_ok);

		mTxtTitle.setText(R.string.str_summary_terms_title);
		mTxtMessage.setText(R.string.str_summary_terms_txt);
		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnOk.setText(R.string.str_dialog_2);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectDialog.dismiss();
			}
		});
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectDialog.dismiss();
				new AddRecipeTask().execute();
			}
		});
		rejectDialog.show();
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
				AddRecipeActivity.activity.finish();
				continueDialog.dismiss();
			}

		});
		continueDialog.show();
	}

	class AddRecipeTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.mainProgressBar).setVisibility(View.VISIBLE);
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(ADD_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				MultipartEntityBuilder entity = MultipartEntityBuilder.create();

				JSONArray oldPhotoJSON = new JSONArray(PrefHelper.getString(
						"AddPhotos", "[]"));
				for (int i = 0; i < oldPhotoJSON.length(); i++) {
					InputStream iStream = getContentResolver().openInputStream(
							Uri.parse(oldPhotoJSON.getString(i)));
					byte[] data = getBytes(iStream);
					ByteArrayBody bab = new ByteArrayBody(data, "image.jpg");
					entity.addPart("Image[" + i + "]", bab);
				}

				JSONObject titleJSON = new JSONObject();
				entity.addTextBody("TokenKey",
						PrefHelper.getString("TokenKey", ""));
				if (language == 0) {
					titleJSON.put("en", PrefHelper.getString("AddTitle", ""));
				} else {
					titleJSON.put("sv", PrefHelper.getString("AddTitle", ""));
				}
				entity.addTextBody("Title", titleJSON.toString(),
						ContentType.APPLICATION_JSON);

				JSONObject descriptionJSON = new JSONObject();
				if (language == 0) {
					descriptionJSON.put("en",
							PrefHelper.getString("AddGuide", ""));
				} else {
					descriptionJSON.put("sv",
							PrefHelper.getString("AddGuide", ""));
				}

				entity.addTextBody("Description", descriptionJSON.toString(),
						ContentType.APPLICATION_JSON);
				entity.addTextBody(
						"CookingTime",

						(PrefHelper.getInt("AddH", 0) * 60 + PrefHelper.getInt(
								"AddMin", 0)) + "");

				if (PrefHelper.getBoolean("AddCalculateTotalKCal", true)) {
					entity.addTextBody("KCal",
							PrefHelper.getInt("AddTotalKCal", 0) + "");
				} else {
					entity.addTextBody("KCal",
							"" + PrefHelper.getInt("AddTotalKCalCustom", -1));
				}
				entity.addTextBody("Category",
						PrefHelper.getInt("AddCategoryId", 0) + "");

				entity.addTextBody("SubCategory",
						PrefHelper.getInt("AddTypeId", 0) + "");

				entity.addTextBody("VisibilityId", "3");

				entity.addTextBody("CreatorSignatureId",
						PrefHelper.getInt("AddSignature", 0) + "");
				JSONArray ingredientJSONArray = new JSONArray();
				SQLiteDatabase DB = new DBHelper(
						BaseApplication.getAppContext()).getReadableDatabase();
				Cursor cursor = DB.query("add_ingredients", null, null, null,
						null, null, null);
				while (cursor.moveToNext()) {
					JSONObject addedIngredientJSON = new JSONObject();

					// Create JSON with localized title
					JSONObject addedIngredientTitleJSON = new JSONObject();
					addedIngredientTitleJSON
							.put("en", cursor.getString(cursor
									.getColumnIndex("title_en")));
					addedIngredientTitleJSON
							.put("sv", cursor.getString(cursor
									.getColumnIndex("title_sw")));
					addedIngredientJSON.put("t", addedIngredientTitleJSON);

					// Put category to JSON
					addedIngredientJSON.put("ct",
							cursor.getInt(cursor.getColumnIndex("category")));
					// Put measurement value to JSON
					addedIngredientJSON
							.put("mv", cursor.getInt(cursor
									.getColumnIndex("measurement")));
					// Sub category isn't used
					addedIngredientJSON.put("sct", "");
					// Put total KCal value to JSON
					addedIngredientJSON.put("c",
							cursor.getInt(cursor.getColumnIndex("kcal")));
					// Put measurement id to JSON
					addedIngredientJSON.put("m", cursor.getInt(cursor
							.getColumnIndex("measurement_value")));

					ingredientJSONArray.put(addedIngredientJSON);
				}
				cursor.close();
				DB.close();

				entity.addTextBody("Ingredient",
						ingredientJSONArray.toString(),
						ContentType.APPLICATION_JSON);

				httpPost.setEntity(entity.build());
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				JSONObject responseJSON = new JSONObject(responseString);
				Log("added recipe response = " + responseString);
				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
					JSONObject dataJSON = responseJSON.getJSONObject("data");
				} else {
					try {
						mErrorMessage = responseJSON.getString("ErrorMessage");
						if (responseJSON.getString("ErrorType").equals(
								"validation")) {
							SummaryActivity.activity
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(
													RecipesActivity.activity,
													"Error: " + mErrorMessage,
													Toast.LENGTH_LONG).show();
										}

									});
							SummaryActivity.activity
									.startActivity(new Intent(
											RecipesActivity.activity,
											LoginActivity.class)
											.setFlags(
													Intent.FLAG_ACTIVITY_NEW_TASK
															| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK)
											.putExtra("logout", true));
							SummaryActivity.activity.finish();
							HomeActivity.activity.finish();
						}
						mError = true;
					} catch (JSONException e) {
						mError = true;
					}
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
			} catch (IOException e) {
				SummaryActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Connection error",
								"Check your internet connection.");
					}

				});
			} catch (JSONException e) {
				Log(e);
			}
			return null;
		}

		public byte[] getBytes(InputStream inputStream) throws IOException {
			ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
			return byteBuffer.toByteArray();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				Toast.makeText(getApplicationContext(), "Recipe is posted",
						Toast.LENGTH_SHORT).show();
				removeSavedRecipe();
				startActivity(new Intent(SummaryActivity.this,
						RecipesActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK));
				finish();
			}
		}
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

	public void edit1(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 0);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit2(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 2);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit3(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 0);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit4(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 1);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit5(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 1);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit6(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 0);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public void edit7(View v) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("edit", 3);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}