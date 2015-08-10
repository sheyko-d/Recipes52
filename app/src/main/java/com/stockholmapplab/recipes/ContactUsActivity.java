package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomEditText;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.ConnectionUtility;
import com.stockholmapplab.recipes.util.ResourceUtil;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.R;

/**
 * ContactUsActivity used to suscribe user to get news letter regarding Health
 * diet .
 */
public class ContactUsActivity extends ActionBarActivity {

	private CustomTextView mTxtInfo;
	private CustomButton mBtnSendRequest;
	private CustomEditText mEdtTitle, mEdtEmail, mEdtDescription;
	private ProgressBar mProgressBar;
	private Dialog mDialog;
	private int TITLE_VALIDATE_DIALOG = 1, EMAIL_VALIDATE_DIALOG = 2,
			FEEDBACK_VALIDATE_DIALOG = 3, SEND_REQUEST_DIALOG = 4,
			INTERNET_CONNECTION_DIALOG = 5;

	class SendFeedbackTask extends AsyncTask<String, Void, Void> {

		private boolean mError = true;
		private String mErrorMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar = (ProgressBar) findViewById(R.id.pb_sending);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		protected Void doInBackground(String... files) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(BaseConstant.FEEDBACK_URL);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("Title", mEdtTitle
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("Content",
						mEdtDescription.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("Email", mEdtEmail
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("AppId",
						"com.sweekend.HealthDiet.recipes"));
				nameValuePairs.add(new BasicNameValuePair("TypeId", "2"));
				nameValuePairs.add(new BasicNameValuePair("UDID", Utility
						.getDeviceUDID()));
				nameValuePairs.add(new BasicNameValuePair("UserInfo", Utility
						.getUserInfo()));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String responseString = EntityUtils.toString(response
						.getEntity());
				Log.d("Log", responseString);
				JSONObject responseJSON = new JSONObject(responseString);
				if (responseJSON.getString("status").equals("ok")) {
					mError = false;
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.INVISIBLE);
			if (mError) {
				Toast.makeText(
						BaseApplication.getAppContext(),
						getResources().getString(
								R.string.str_contact_send_error)+": "+mErrorMessage,
						Toast.LENGTH_SHORT).show();
			} else {
				showValidationDialog(SEND_REQUEST_DIALOG);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);

		setContentView(R.layout.ac_contact_us);

		initActionBar();

		init();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_contact_us));
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE:
			if (requestCode == FacebookLoginActivity.FACEBOOK_ACHIEVEMENT_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					Utility.FacebookPostOnAchievements(
							this,
							data.getExtras().getString(
									FacebookLoginActivity.extraMessage),
							data.getExtras().getInt(
									FacebookLoginActivity.extraDrawableID));
				}
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		if (BaseConstant.mDietCycle == null) {
			BaseConstant.mDietCycle = DietCycle.getDietCycle();
		}
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		Utility.showFastingAndNonFastingDays(ContactUsActivity.this);
		Utility.checkLastLaunchApp();
		Utility.checkAchievemtnDialog(this);
		if (Utility.isDietCycleFinish()) {
			finish();
		} else {
			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
		}
		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onStop() {
		DietCycle.setDietCycle();

		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {

		mTxtInfo = GenericView.findViewById(this, R.id.tv_txt_Info);
		mBtnSendRequest = GenericView.findViewById(this,
				R.id.bt_btn_send_request);

		mEdtTitle = GenericView.findViewById(this, R.id.et_edt_title);
		mEdtEmail = GenericView.findViewById(this, R.id.et_edt_email);

		mEdtDescription = GenericView.findViewById(this,
				R.id.et_edt_description);
		mProgressBar = GenericView.findViewById(this, R.id.pb_sending);

		mBtnSendRequest.setText(R.string.str_request_message_1);

		mEdtTitle.setHintTextColor(ResourceUtil.getColor(R.color.hint_color));
		mEdtEmail.setHintTextColor(ResourceUtil.getColor(R.color.hint_color));

		mEdtDescription.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				int length = mEdtDescription.getText().toString().length();

				if (length == 0) {
					mTxtInfo.setVisibility(View.VISIBLE);

				} else {
					mTxtInfo.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public void clearTitle(View v) {
		mEdtTitle.setText("");
	}

	public void clearEmail(View v) {
		mEdtEmail.setText("");
	}

	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.bt_btn_send_request:
			if (ConnectionUtility.isNetAvailable()) {
				if (mEdtTitle.getText().toString().equals("")) {
					showValidationDialog(TITLE_VALIDATE_DIALOG);
				} else if (mEdtEmail.getText().toString().equals("")) {
					showValidationDialog(EMAIL_VALIDATE_DIALOG);
				} else if (mEdtDescription.getText().toString().equals("")) {
					showValidationDialog(FEEDBACK_VALIDATE_DIALOG);
				} else {
					if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							mEdtEmail.getText().toString()).matches() == true) {
						mBtnSendRequest.setText(R.string.str_sending_message);
						sendFeedback();
					} else {
						showValidationDialog(EMAIL_VALIDATE_DIALOG);
					}
				}
			} else {
				showValidationDialog(INTERNET_CONNECTION_DIALOG);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * showValidationDialog method is use for showing validation dialog.
	 * 
	 * @param id
	 */
	private void showValidationDialog(int id) {
		mDialog = new Dialog(ContactUsActivity.this, R.style.DialogTheme);
		mDialog.setContentView(R.layout.raw_dialog2);
		mDialog.setCanceledOnTouchOutside(false);
		CustomTextView mTxtValidate = (CustomTextView) mDialog
				.findViewById(R.id.tv_txtCalendarValidation);

		CustomButton mBtnOK = (CustomButton) mDialog
				.findViewById(R.id.bt_btn_ok);

		mBtnOK.setText(R.string.str_dialog_2);

		if (id == TITLE_VALIDATE_DIALOG) {
			mTxtValidate.setText(R.string.str_dialog_13);
		} else if (id == EMAIL_VALIDATE_DIALOG) {
			mTxtValidate.setText(R.string.str_dialog_14);
		} else if (id == FEEDBACK_VALIDATE_DIALOG) {
			mTxtValidate.setText(R.string.str_dialog_15);
		} else if (id == SEND_REQUEST_DIALOG) {
			mTxtValidate.setText(R.string.str_dialog_16);
			mBtnSendRequest.setText(R.string.str_request_message_1);
		} else if (id == INTERNET_CONNECTION_DIALOG) {
			mTxtValidate.setText(R.string.str_internet_connection);
		}

		mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	/**
	 * sendFeedback() method is use for sending user feedback to server.
	 */
	private void sendFeedback() {
		new SendFeedbackTask().execute();
	}

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(ContactUsActivity.this);
		}
	};
}
