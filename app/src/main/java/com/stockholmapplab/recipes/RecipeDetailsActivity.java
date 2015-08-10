package com.stockholmapplab.recipes;

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
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
import com.stockholmapplab.recipes.typeface.CustomEditText;
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

public class RecipeDetailsActivity extends ActionBarActivity {

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
	public static RecipeDetailsActivity activity;
	private static ArrayList<String> mIngTitlesArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngMVArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngMArrayList = new ArrayList<String>();
	private static ArrayList<String> mIngKcalArrayList = new ArrayList<String>();
	private static final String GET_RECIPE_DETAILS_URL = "http://dev.core.stockholmapplab.com/Webservice/Recipe/GetRecipeDetails";
	private static final String RATE_RECIPE_URL = "http://dev.core.stockholmapplab.com/Webservice/Recipe/RateRecipe";
	private static final String REVIEW_RECIPE_URL = "http://dev.core.stockholmapplab.com/Webservice/Recipe/ReviewRecipe";
	private JSONArray mIngredientsArray;
	private ViewGroup.LayoutParams params;
	// private ImageView mFlagImg;
	private ViewPager mPager;
	private MyPagerAdapter mAdapter;
	public JSONArray mScreenshotsArray = new JSONArray();
	private int mLangIconRes = 0;
	private MenuItem mLangItem;
	public static String language;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_recipe_details);

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
		new GetRecipeDetailsTask().execute();

		if (BaseApplication.isAdmin()) {
			params = (ViewGroup.LayoutParams) findViewById(R.id.adminBtnsLayout)
					.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			findViewById(R.id.adminBtnsLayout).setLayoutParams(params);
		}

		mPager = (ViewPager) findViewById(R.id.detailsViewPager);
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager.setAdapter(mAdapter);

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
				bundle.putString(
						"link",
						mScreenshotsArray.getJSONObject(position)
								.getString("ImagePath").replace("dev.", ""));
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
		mName = getIntent().getStringExtra("Name");
		SpannableString s = new SpannableString(mName);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IngredientsAdapter.layout = R.layout.item_ingredient;
	}

	private class GetRecipeDetailsTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private JSONObject mResponseJSON;
		private JSONObject mDataJSON;
		private Object mStatus;
		private String mDescription;
		private int mCurrentRating;
		private double mRating;
		private int mRatingNum;
		private String mAuthorName;
		private String mAuthorEmail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(GET_RECIPE_DETAILS_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("TokenKey",
						PrefHelper.getString("TokenKey", "")));
				nameValuePairs
						.add(new BasicNameValuePair("RecipeId", mId + ""));

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
							mDataJSON = mResponseJSON.getJSONObject("data");
							// Detect language of recipe
							if (RecipesFragment.languageCode.equals("all")) {
								if (new JSONObject(
										mDataJSON.getString("Description"))
										.has("en")) {
									language = "en";
								} else if (new JSONObject(
										mDataJSON.getString("Description"))
										.has("sv")) {
									language = "sv";
								} else if (new JSONObject(
										mDataJSON.getString("Description"))
										.has("no")) {
									language = "no";
								}
							} else {
								language = RecipesFragment.languageCode;
							}
							mDescription = new JSONObject(
									mDataJSON.getString("Description"))
									.getString(language);

							mCurrentRating = mDataJSON.getJSONObject("Ratings")
									.getInt("CurrentUserRating");
							mRating = mDataJSON.getJSONObject("Ratings")
									.getDouble("Rate");
							mRatingNum = mDataJSON.getJSONObject("Ratings")
									.getInt("NoOfVotes");
							mAuthorName = new JSONObject(
									mDataJSON.getString("Owner"))
									.getString("Name");
							mAuthorEmail = new JSONObject(
									mDataJSON.getString("Owner"))
									.getString("Email");
							mTime = formatTime(mDataJSON.getInt("CookingTime"));
							mKCal = mDataJSON.getInt("KCal");
							mScreenshotsArray = mDataJSON
									.getJSONArray("Screenshots");

							if (mScreenshotsArray.length() > 0) {
								mScreenshotUrl = (mDataJSON
										.getJSONArray("Screenshots"))
										.getJSONObject(0)
										.getString("ImagePath");
							} else {
								mScreenshotUrl = "";
							}

							if (mScreenshotUrl.contains("dev.")) {
								mScreenshotUrl = mScreenshotUrl.replace("dev.",
										"");
							}

							mIngredientsArray = new JSONArray(
									mDataJSON.getString("Ingredients"));
							mIngTitlesArrayList.clear();
							mIngMVArrayList.clear();
							mIngMArrayList.clear();
							mIngKcalArrayList.clear();
							SQLiteDatabase DB = new DBHelper(
									BaseApplication.getAppContext())
									.getReadableDatabase();
							Log("mIngredientsArray size = "
									+ mIngredientsArray.length());
							for (int i = 0; i < mIngredientsArray.length(); i++) {
								Log("ingredient loop #" + i);
								JSONObject ingredientJSON = mIngredientsArray
										.getJSONObject(i);
								Log("language = " + language);
								mIngTitlesArrayList.add(new JSONObject(
										ingredientJSON.getString("t"))
										.getString(language));
								Cursor cursor = DB.query("measurements", null,
										"id='" + ingredientJSON.getString("m")
												+ "'", null, null, null, null);
								mIngMVArrayList.add(ingredientJSON
										.getString("mv"));
								if (cursor.moveToFirst()) {
									mIngMArrayList
											.add(getResources()
													.getString(
															getResources()
																	.getIdentifier(
																			cursor.getString(cursor
																					.getColumnIndex("title_id")),
																			"string",
																			BaseApplication
																					.getAppContext()
																					.getPackageName())));
								}
								cursor.close();
								mIngKcalArrayList.add(ingredientJSON
										.getInt("c") + "");
							}
							DB.close();
						} catch (JSONException e) {
							Log(e);
						}
					} else {
						try {
							mErrorMessage = mResponseJSON
									.getString("ErrorMessage");
							if (mResponseJSON.getString("ErrorType").equals(
									"validation")) {
								RecipeDetailsActivity.this
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														RecipeDetailsActivity.this,
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
								RecipeDetailsActivity.this
										.startActivity(new Intent(
												RecipeDetailsActivity.this,
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
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

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
				// Set description
				mDescTxt.setText(mDescription);
				// Set rating to ratingBar
				mRatingBar.setRating(mCurrentRating);
				// Add ending to "vote" string, if it's >1, or =0
				// Set time text
				mTimeTxt.setText(mTime);
				// Set kcal text
				mKcalTxt.setText(mKCal + " kcal");
				// Set author text
				if (mAuthorName == null || mAuthorName.equals("null")) {
					mAuthorNameTxt.setText("Anonymous");
				} else if (mAuthorEmail.equals("null")) {
					mAuthorNameTxt.setText(mAuthorName);
				} else {
					mAuthorNameTxt.setText(mAuthorName + " (" + mAuthorEmail
							+ ")");
				}
				// Set rating text
				mRatingTxt
						.setText(getResources()
								.getString(R.string.str_rating_1)
								+ " "
								+ mRating
								+ " ("
								+ mRatingNum
								+ " "
								+ getResources().getString(
										R.string.str_rating_2) + ")");

				mRatingBar.setRating((float) mRating);

				// Set flag image in action bar
				if (language.equals("en")) {
					mLangIconRes = R.drawable.ic_flag_en;
				} else if (language.equals("sv")) {
					mLangIconRes = R.drawable.ic_flag_sv;
				}
				if (mLangItem != null) {
					mLangItem.setIcon(mLangIconRes);
				}

				// Hide progress bar
				findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
				// Show main layout
				findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);

				// Show 1st screenshot image
				mImgLoader.displayImage(mScreenshotUrl, mScreenshotImg,
						mOptions, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								findViewById(R.id.screenshotProgressBar)
										.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								Log("loading failed: " + failReason.toString());
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

				mAdapter.notifyDataSetChanged();

				ingredientsAdapter.notifyDataSetChanged();
			}
		}
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
		private boolean mValidate = false;

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
								mValidate = true;
							}
						} catch (JSONException e) {
						}
						mError = true;
					}
				}
			} catch (ClientProtocolException e) {
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

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
				if (mValidate) {
					Toast.makeText(RecipeDetailsActivity.this,
							"Session expired", Toast.LENGTH_LONG).show();
					PreferenceManager
							.getDefaultSharedPreferences(
									BaseApplication.getAppContext()).edit()
							.clear().commit();
					RecipeDetailsActivity.this.startActivity(new Intent(
							RecipeDetailsActivity.this, LoginActivity.class)
							.putExtra("logout", true));
					RecipesActivity.activity.finish();
					HomeActivity.activity.finish();
				} else {
					showErrorDialog("Server error", mErrorMessage);
				}
			} else {
				Toast.makeText(RecipeDetailsActivity.this,
						"Thanks for your feedback!", Toast.LENGTH_SHORT).show();

				// Show progress bar
				findViewById(R.id.mainProgressBar).setVisibility(View.VISIBLE);

				new GetRecipeDetailsTask().execute();
			}
		}

	}

	private static void showErrorDialog(String title, String message) {
		final Dialog errorDialog = new Dialog(RecipeDetailsActivity.activity,
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
		finish();
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
									RecipeDetailsActivity.this, session, params))
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
																	RecipeDetailsActivity.this,
																	"Story is posted",
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															// User clicked the
															// Cancel button
															Toast.makeText(
																	RecipeDetailsActivity.this
																			.getApplicationContext(),
																	"Publish cancelled",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													} else if (error instanceof FacebookOperationCanceledException) {
														// User clicked the "x"
														// button
														Toast.makeText(
																RecipeDetailsActivity.this
																		.getApplicationContext(),
																"Publish cancelled",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														// Generic, ex: network
														// error
														Toast.makeText(
																RecipeDetailsActivity.this
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
					Toast.makeText(RecipeDetailsActivity.this,
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
		startActivity(new Intent(RecipeDetailsActivity.this,
				IngredientsActivity.class));
	}

	public void reject(View v) {
		final Dialog rejectDialog = new Dialog(RecipeDetailsActivity.activity,
				R.style.DialogTheme);
		rejectDialog.setContentView(R.layout.raw_joien_dialog);
		rejectDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtTitle = (CustomTextView) rejectDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtMessage = (CustomTextView) rejectDialog
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnCancel = (CustomButton) rejectDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOk = (CustomButton) rejectDialog
				.findViewById(R.id.bt_btn_ok);
		final CustomEditText mReasonEdittxt = (CustomEditText) rejectDialog
				.findViewById(R.id.et_edtTitle);

		mTxtTitle.setText(R.string.str_reject_title);
		mTxtMessage.setText(R.string.str_reject_msg);
		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnOk.setText(R.string.str_reject_btn);
		mReasonEdittxt.setHint(R.string.str_reject_edittxt);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectDialog.dismiss();
			}
		});
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mReasonEdittxt.getText().toString().replace(" ", "")
						.equals("")) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.review_approved_empty, Toast.LENGTH_SHORT)
							.show();
				} else {
					rejectDialog.dismiss();
					// Show progress bar
					findViewById(R.id.mainProgressBar).setVisibility(
							View.VISIBLE);
					// Hide main layout
					findViewById(R.id.mainLayout).setVisibility(View.GONE);
					new ReviewRecipeTask().execute(new String[] { "3",
							mReasonEdittxt.getText().toString() });
				}
			}
		});
		rejectDialog.show();
	}

	public void approve(View v) {
		final Dialog approvingDialog = new Dialog(
				RecipeDetailsActivity.activity, R.style.DialogTheme);
		approvingDialog.setContentView(R.layout.raw_dialog3);
		approvingDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtTitle = (CustomTextView) approvingDialog
				.findViewById(R.id.tv_txt_guide1);
		CustomTextView mTxtMessage = (CustomTextView) approvingDialog
				.findViewById(R.id.tv_txt_guide2);
		CustomButton mBtnCancel = (CustomButton) approvingDialog
				.findViewById(R.id.bt_btn_Cancel);
		CustomButton mBtnOk = (CustomButton) approvingDialog
				.findViewById(R.id.bt_btn_ok);

		mTxtTitle.setText(R.string.str_approve_title);
		mTxtMessage.setText(R.string.str_approve_msg);
		mBtnCancel.setText(R.string.str_dialog_5);
		mBtnOk.setText(R.string.str_approve_btn);

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				approvingDialog.dismiss();
			}
		});
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				approvingDialog.dismiss();
				// Show progress bar
				findViewById(R.id.mainProgressBar).setVisibility(View.VISIBLE);
				// Hide main layout
				findViewById(R.id.mainLayout).setVisibility(View.GONE);
				new ReviewRecipeTask().execute(new String[] { "2" });
			}
		});
		approvingDialog.show();
	}

	private class ReviewRecipeTask extends AsyncTask<String, Void, String> {

		private boolean mError;
		private String mErrorMessage;
		private JSONObject mResponseJSON;
		private Object mStatus;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... input) {
			HttpPost httpPost = new HttpPost(REVIEW_RECIPE_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("TokenKey",
						PrefHelper.getString("TokenKey", "")));
				nameValuePairs
						.add(new BasicNameValuePair("RecipeId", mId + ""));
				nameValuePairs.add(new BasicNameValuePair("RecipeStatus",
						input[0]));
				// If administrator is rejecting recipe - attach reason
				if (input[0].equals("3")) {
					nameValuePairs.add(new BasicNameValuePair("Comment",
							input[1]));
				} else {
					nameValuePairs.add(new BasicNameValuePair("Comment", ""));
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
						if (input[0].equals("2")) {
							updateDB(mId, "Approved");
						} else {
							updateDB(mId, "Rejected");
						}
					} else {
						try {
							mErrorMessage = mResponseJSON
									.getString("ErrorMessage");
							if (mResponseJSON.getString("ErrorType").equals(
									"validation")) {
								RecipeDetailsActivity.this
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												Toast.makeText(
														RecipeDetailsActivity.this,
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
								RecipeDetailsActivity.this
										.startActivity(new Intent(
												RecipeDetailsActivity.this,
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
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				RecipeDetailsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Connection error",
								"Check your internet connection.");
					}

				});
			}
			return input[0];
		}

		@Override
		protected void onPostExecute(String status) {
			super.onPostExecute(status);
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				if (status.equals("2")) {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.review_approved_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BaseApplication.getAppContext(),
							R.string.review_rejected_success,
							Toast.LENGTH_SHORT).show();
				}
				finish();
				RecipesFragment.clearList();
				RecipesFragment.downloadRecipes();
			}
		}

		private void updateDB(Integer id, String status) {
			SQLiteDatabase db = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("id", id + "");
			cv.put("status", status);
			db.update("recipes_list", cv, "id='" + id + "'", null);
			db.close();
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}