package com.stockholmapplab.recipes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomEditText;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.PrefHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {

	private TextView mVersionText;
	private String mPassword;
	private String mAccessToken;
	private String mEmail;
	private static final String LOGIN_SOCIAL_URL = "http://dev.core.stockholmapplab.com/webservice/user/loginsocial";
	private static final String LOGIN_CREDENTIALS_URL = "http://dev.core.stockholmapplab.com/webservice/user/login";
	private static final String REGISTRATION_URL = "http://dev.core.stockholmapplab.com/webservice/user/register";
	private static final String RECOVER_URL = "http://dev.core.stockholmapplab.com/webservice/user/recover";
	private Dialog mErrorDialog;
	private Dialog mPasswordDialog;
	private CustomEditText mEditEmail;
	private View mFacebookLayout;
	private View mLoginLayout;
	private View mRegistrationLayout;
	private TextView mFacebookTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login);

		BaseApplication.lockOrientation(this);

		mVersionText = (TextView) findViewById(R.id.versionText);
		// Set version text to TextView
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			mVersionText.setText("v" + pInfo.versionName + " ("
					+ pInfo.versionCode + ")");
		} catch (NameNotFoundException e) {
		}

		mFacebookLayout = findViewById(R.id.facebookLayout);
		mLoginLayout = findViewById(R.id.loginLayout);
		mRegistrationLayout = findViewById(R.id.registrationLayout);

		mFacebookTxt = (TextView) findViewById(R.id.loginFacebookText);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	class FacebookLoginTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PreferenceManager
					.getDefaultSharedPreferences(
							BaseApplication.getAppContext()).edit().clear()
					.commit();
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(LOGIN_SOCIAL_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				Log.d("Log", "email = " + mEmail);
				nameValuePairs.add(new BasicNameValuePair("accesstoken",
						mAccessToken));
				nameValuePairs.add(new BasicNameValuePair("provider",
						"facebook"));
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				nameValuePairs.add(new BasicNameValuePair("AppVersion",
						pInfo.versionName));
				nameValuePairs.add(new BasicNameValuePair("AppBundle",
						"com.stockholmapplab.recipes"));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				JSONObject responseJSON = new JSONObject(responseString);
				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
					JSONObject dataJSON = responseJSON.getJSONObject("data");
					PrefHelper.setString("TokenKey",
							dataJSON.getString("TokenKey"));
					PrefHelper.setInt("UserId", dataJSON.getInt("UserId"));
					PrefHelper.setString("Email", mEmail);
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
				Log("facebook response = " + responseString);
			} catch (ClientProtocolException e) {
				Log.d("Log", e + "");
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Connection error",
								"Check your internet connection.");
					}

				});
			} catch (JSONException e) {
				Log(e);
			} catch (NameNotFoundException e) {
				Log(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				startActivity(new Intent(LoginActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			}
		}
	}

	class CredentialsLoginTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private TextView mLoginCredentialsText;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PreferenceManager
					.getDefaultSharedPreferences(
							BaseApplication.getAppContext()).edit().clear()
					.commit();
			mLoginCredentialsText = ((TextView) findViewById(R.id.loginCredentialsText));
			mLoginCredentialsText.setText("Signing In...");
			mLoginCredentialsText.setTextColor(getResources().getColor(
					R.color.blue));
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(LOGIN_CREDENTIALS_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs
						.add(new BasicNameValuePair("password", mPassword));
				nameValuePairs.add(new BasicNameValuePair("apnstoken",
						PrefHelper.getString("TokenKey", "")));
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				nameValuePairs.add(new BasicNameValuePair("AppVersion",
						pInfo.versionName));
				nameValuePairs.add(new BasicNameValuePair("AppBundle",
						"com.stockholmapplab.recipes"));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				JSONObject responseJSON = new JSONObject(responseString);
				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
					JSONObject dataJSON = responseJSON.getJSONObject("data");
					PrefHelper.setString("TokenKey",
							dataJSON.getString("TokenKey"));
					PrefHelper.setInt("UserId", dataJSON.getInt("UserId"));
					PrefHelper.setString("Email", mEmail);
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
				Log(responseString);
			} catch (ClientProtocolException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Connection error",
								"Check your internet connection.");
					}

				});
			} catch (JSONException e) {
				Log(e);
			} catch (NameNotFoundException e) {
				Log(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLoginCredentialsText.setText(R.string.str_credentials);
			/*TODO:mLoginCredentialsText
					.setTextColor(getResources().getColorStateList(
							R.drawable.ac_login_text_color_selector));*/
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				startActivity(new Intent(LoginActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			}
		}
	}

	class RegistrationTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private TextView mLoginCredentialsText;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PreferenceManager
					.getDefaultSharedPreferences(
							BaseApplication.getAppContext()).edit().clear()
					.commit();
			mLoginCredentialsText = ((TextView) findViewById(R.id.loginCredentialsText));
			mLoginCredentialsText.setText("Registration...");
			mLoginCredentialsText.setTextColor(getResources().getColor(
					R.color.blue));
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(REGISTRATION_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				JSONObject responseJSON = new JSONObject(responseString);
				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
					JSONObject dataJSON = responseJSON.getJSONObject("data");
					mPassword = dataJSON.getString("password");
					PrefHelper.setString("Email", mEmail);
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
				Log(responseString);
			} catch (ClientProtocolException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLoginCredentialsText.setText(R.string.str_credentials);
			/*TODO:mLoginCredentialsText
					.setTextColor(getResources().getColorStateList(
							R.drawable.ac_login_text_color_selector));*/
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				if (mEmail == null || mEmail.replaceAll(" ", "").equals("")
						|| !isEmailValid()) {
					showErrorDialog(null,
							getResources()
									.getString(R.string.str_invalid_email));
				} else if (mPassword == null
						|| mPassword.replaceAll(" ", "").equals("")) {
					showErrorDialog(
							null,
							getResources().getString(
									R.string.str_empty_password));
				} else {
					new CredentialsLoginTask().execute();
				}
			}
		}
	}

	private boolean isEmailValid() {
		boolean isValid = false;

		String expression = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
		CharSequence inputStr = mEmail;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			Log.d("Log", mEmail + " matches");
			isValid = true;
		} else {
			Log.d("Log", mEmail + " doesn't match");
		}
		return isValid;
	}

	class RecoverTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mEmail = mEditEmail.getText().toString();
		}

		protected Void doInBackground(String... files) {
			HttpPost httpPost = new HttpPost(RECOVER_URL);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httpPost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				JSONObject responseJSON = new JSONObject(responseString);
				Log(responseString);

				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
				Log(responseString);
			} catch (ClientProtocolException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				LoginActivity.this.runOnUiThread(new Runnable() {

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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPasswordDialog.dismiss();
			if (mError) {
				showErrorDialog("Server error", mErrorMessage);
			} else {
				showInfoDialog("New password was sent to your email.");
			}
		}
	}

	private void showErrorDialog(String title, String message) {
		mErrorDialog = new Dialog(LoginActivity.this, R.style.DialogTheme);
		if (title == null) {
			mErrorDialog.setContentView(R.layout.raw_dialog_error_single);
		} else {
			mErrorDialog.setContentView(R.layout.raw_dialog_error);
		}
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

	private void showInfoDialog(String message) {
		mErrorDialog = new Dialog(LoginActivity.this, R.style.DialogTheme);
		mErrorDialog.setContentView(R.layout.raw_dialog2);
		mErrorDialog.setCanceledOnTouchOutside(false);

		CustomTextView mTxtErrorMessage = (CustomTextView) mErrorDialog
				.findViewById(R.id.tv_txtCalendarValidation);
		CustomButton mBtnOk = (CustomButton) mErrorDialog
				.findViewById(R.id.bt_btn_ok);
		mTxtErrorMessage.setText(message);
		mBtnOk.setText(R.string.str_dialog_2);

		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mErrorDialog.dismiss();
			}
		});
		mErrorDialog.show();
	}

	public void facebook(View v) {
		mFacebookTxt.setText(R.string.str_signing_in);
		mFacebookTxt.setTextColor(getResources().getColor(R.color.blue));
	}

	public void credentials(View v) {
		Animation slideAnimIn = AnimationUtils
				.loadAnimation(BaseApplication.getAppContext(),
						R.anim.pull_from_right_to_left);
		Animation slideAnimOut = AnimationUtils
				.loadAnimation(BaseApplication.getAppContext(),
						R.anim.push_from_right_to_left);
		slideAnimOut.setFillAfter(true);
		mFacebookLayout.setVisibility(View.VISIBLE);
		mFacebookLayout.startAnimation(slideAnimIn);
		mLoginLayout.startAnimation(slideAnimOut);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mLoginLayout.setVisibility(View.GONE);
			}
		}, 150);
	}

	public void credentialsLogin(View v) {
		mEmail = ((EditText) findViewById(R.id.loginEmailEditText)).getText()
				.toString();
		mPassword = ((EditText) findViewById(R.id.loginPasswordEditText))
				.getText().toString();
		if (mEmail == null || mEmail.replaceAll(" ", "").equals("")) {
			showErrorDialog(null,
					getResources().getString(R.string.str_required_email));
		} else if (!isEmailValid()) {
			showErrorDialog(null,
					getResources().getString(R.string.str_invalid_email));
		} else if (mPassword == null
				|| mPassword.replaceAll(" ", "").equals("")) {
			showErrorDialog(null,
					getResources().getString(R.string.str_empty_password));
		} else {
			new CredentialsLoginTask().execute();
		}
	}

	public void registration(View v) {
		Animation slideAnimIn = AnimationUtils
				.loadAnimation(BaseApplication.getAppContext(),
						R.anim.pull_from_right_to_left);
		Animation slideAnimOut = AnimationUtils
				.loadAnimation(BaseApplication.getAppContext(),
						R.anim.push_from_right_to_left);
		slideAnimOut.setFillAfter(true);
		mRegistrationLayout.setVisibility(View.VISIBLE);
		mRegistrationLayout.startAnimation(slideAnimIn);
		mLoginLayout.startAnimation(slideAnimOut);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mLoginLayout.setVisibility(View.GONE);
			}
		}, 150);
	}

	public void registrationComplete(View v) {
		mEmail = ((EditText) findViewById(R.id.registrationEmailEditText))
				.getText().toString();
		if (mEmail == null || mEmail.replaceAll(" ", "").equals("")) {
			showErrorDialog(null,
					getResources().getString(R.string.str_required_email));
		} else if (!isEmailValid()) {
			showErrorDialog(null,
					getResources().getString(R.string.str_invalid_email));
		} else {
			new RegistrationTask().execute();
		}
	}

	public void password(View v) {
		// Show restore password dialog
		mPasswordDialog = new Dialog(LoginActivity.this, R.style.DialogTheme);
		mPasswordDialog.setContentView(R.layout.raw_dialog_recovery);
		mPasswordDialog.setCanceledOnTouchOutside(false);
		CustomButton mBtnCancel = (CustomButton) mPasswordDialog
				.findViewById(R.id.bt_btn_cancel);
		CustomButton mBtnOk = (CustomButton) mPasswordDialog
				.findViewById(R.id.bt_btn_ok);
		mEditEmail = (CustomEditText) mPasswordDialog
				.findViewById(R.id.et_edtEmail);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPasswordDialog.dismiss();
			}
		});
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RecoverTask().execute();
			}
		});
		mPasswordDialog.show();
	}

	public void back(View v) {
		if (mFacebookLayout.getVisibility() == View.VISIBLE) {
			Animation slideAnimIn = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.pull_from_right_to_left_backward);
			Animation slideAnimOut = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.push_from_right_to_left_backward);
			mLoginLayout.setVisibility(View.VISIBLE);
			mFacebookLayout.startAnimation(slideAnimIn);
			mLoginLayout.startAnimation(slideAnimOut);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mFacebookLayout.setVisibility(View.GONE);
				}
			}, 150);
		} else if (mRegistrationLayout.getVisibility() == View.VISIBLE) {
			Animation slideAnimIn = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.pull_from_right_to_left_backward);
			Animation slideAnimOut = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.push_from_right_to_left_backward);
			mLoginLayout.setVisibility(View.VISIBLE);
			mRegistrationLayout.startAnimation(slideAnimIn);
			mLoginLayout.startAnimation(slideAnimOut);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mRegistrationLayout.setVisibility(View.GONE);
				}
			}, 150);
		}
	}

	@Override
	public void onBackPressed() {
		if (mFacebookLayout.getVisibility() == View.VISIBLE) {
			Animation slideAnimIn = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.pull_from_right_to_left_backward);
			Animation slideAnimOut = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.push_from_right_to_left_backward);
			mLoginLayout.setVisibility(View.VISIBLE);
			mFacebookLayout.startAnimation(slideAnimIn);
			mLoginLayout.startAnimation(slideAnimOut);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mFacebookLayout.setVisibility(View.GONE);
				}
			}, 150);
		} else if (mRegistrationLayout.getVisibility() == View.VISIBLE) {
			Animation slideAnimIn = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.pull_from_right_to_left_backward);
			Animation slideAnimOut = AnimationUtils.loadAnimation(
					BaseApplication.getAppContext(),
					R.anim.push_from_right_to_left_backward);
			mLoginLayout.setVisibility(View.VISIBLE);
			mRegistrationLayout.startAnimation(slideAnimIn);
			mLoginLayout.startAnimation(slideAnimOut);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					mRegistrationLayout.setVisibility(View.GONE);
				}
			}, 150);
		} else {
			super.onBackPressed();
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}
