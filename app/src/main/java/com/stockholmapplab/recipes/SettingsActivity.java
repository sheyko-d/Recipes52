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
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.stickylistview.HeaderListView;
import com.stockholmapplab.recipes.stickylistview.SectionAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.util.Utility;
import com.stockholmapplab.recipes.wheel.widget.adapters.ArrayWheelAdapter;
import com.stockholmapplab.recipes.wheel.widget.adapters.NumericWheelAdapter;
import com.stockholmapplab.recipes.widget.OnWheelClickedListener;
import com.stockholmapplab.recipes.widget.OnWheelScrollListener;
import com.stockholmapplab.recipes.widget.WheelView;
import com.stockholmapplab.recipes.R;

/**
 * This class stores App Settings. It sets Morning Alarm as well as Evening
 * Alarm when reminder is on. Help dialogues are active/Inactive using toggle.
 * Reset all the Help dialogues.
 * 
 */
public class SettingsActivity extends ActionBarActivity {
	private TextView mTxtReminder, mTxtMorningReminder, mTxtEveningReminder,
			mTxtActive;
	private ToggleButton mToggle1, mToggle2;
	private LinearLayout mLinear;
	private CustomButton mBtnMorningTime, mBtnEveningTime;
	private Dialog mDialog1, mDialog2;
	private WheelView hours, mins;
	private int[] hour = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	private Animation mScaleAnimation, mReverseScaleAnimation;
	private Animation mBtnScaleAnimation, mBtnReverseScaleAnimation;
	private HeaderListView mList;

	private String[] itemsSections;
	private String[] itemsSection2;
	private String[] itemsSection3;
	private String[] itemsSection5;
	private String[] itemsSection6;

	private String mAccessToken;
	private String mEmail;
	private Dialog mErrorDialog;
	private CompoundButton mButtonView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);

		setContentView(R.layout.ac_settings);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mList = (HeaderListView) findViewById(R.id.settingsListView);
		} else {
			mList = (HeaderListView) findViewById(R.id.settingsListView2);
		}

		itemsSections = getResources().getStringArray(
				R.array.array_settings_sections);
		itemsSection2 = getResources().getStringArray(
				R.array.array_settings_section_2);
		itemsSection3 = getResources().getStringArray(
				R.array.array_settings_section_3);
		itemsSection5 = getResources().getStringArray(
				R.array.array_settings_section_5);
		itemsSection6 = getResources().getStringArray(
				R.array.array_settings_section_6);

		initActionBar();

		mScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
		mScaleAnimation.setFillEnabled(true);
		mScaleAnimation.setFillAfter(true);
		mReverseScaleAnimation = AnimationUtils.loadAnimation(this,
				R.anim.reverse_scale);
		mReverseScaleAnimation.setFillEnabled(true);
		mReverseScaleAnimation.setFillAfter(true);
		mBtnScaleAnimation = AnimationUtils.loadAnimation(this,
				R.anim.btn_scale);
		mBtnScaleAnimation.setFillEnabled(true);
		mBtnScaleAnimation.setFillAfter(true);
		mBtnReverseScaleAnimation = AnimationUtils.loadAnimation(this,
				R.anim.btn_reverse_scale);
		mBtnReverseScaleAnimation.setFillEnabled(true);
		mBtnReverseScaleAnimation.setFillAfter(true);

		// init();

		mList.setAdapter(new SectionAdapter() {

			@Override
			public int numberOfSections() {
				return 4;
			}

			@Override
			public int numberOfRows(int section) {
				if (section == 0) {
					if (PrefHelper.getBoolean(
							getString(R.string.KEY_IS_REMINDER_ON), true)) {
						return 2;
					} else {
						return 0;
					}
				} else if (section == 2) {
					return 1;
				} else {
					return 2;
				}
			}

			@Override
			public Object getRowItem(int section, int row) {
				return null;
			}

			@Override
			public boolean hasSectionHeaderView(int section) {
				return true;
			}

			@Override
			public View getRowView(int section, int row, View convertView,
					ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							getResources().getLayout(R.layout.item_settings),
							null);
				}

				convertView.findViewById(R.id.settingsCheckbox).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.settingsTimeBtn).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.settingsBtn).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.settingsTxt).setVisibility(
						View.GONE);
				if (section == 0 & row == 0) {
					mTxtMorningReminder = (TextView) convertView
							.findViewById(R.id.text);
					mBtnMorningTime = (CustomButton) convertView
							.findViewById(R.id.settingsTimeBtn);

					mBtnMorningTime.setVisibility(View.VISIBLE);

					String morningTime = PrefHelper.getString(
							getString(R.string.KEY_MORNING_REMINDER), "08:00");
					mBtnMorningTime.setText(morningTime.subSequence(0, 5));
					mBtnMorningTime.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showTimePicker(true);
						}
					});
				} else if (section == 0 & row == 1) {
					mTxtEveningReminder = (TextView) convertView
							.findViewById(R.id.text);
					mBtnEveningTime = (CustomButton) convertView
							.findViewById(R.id.settingsTimeBtn);
					mBtnEveningTime.setVisibility(View.VISIBLE);
					String eveningTime = PrefHelper.getString(
							getString(R.string.KEY_EVENING_REMINDER), "22:00");
					mBtnEveningTime.setText(eveningTime.substring(0, 5));
					mBtnEveningTime.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showTimePicker(false);
						}
					});
				} else if (section == 1 & row == 0) {
					convertView.findViewById(R.id.settingsCheckbox)
							.setVisibility(View.VISIBLE);

					Session session = BaseApplication.session;
					if (session != null) {
						if (session.isOpened() & !session.isClosed()) {
							((CheckBox) convertView
									.findViewById(R.id.settingsCheckbox))
									.setChecked(true);
						}
					}
					((CheckBox) convertView.findViewById(R.id.settingsCheckbox))
							.setOnCheckedChangeListener(facebookCheckedChangeListener);
				} else if (section == 1 & row == 1) {
					convertView.findViewById(R.id.settingsBtn).setVisibility(
							View.VISIBLE);
					if (!PrefHelper.getString("Email", "").equals("")) {
						((Button) convertView.findViewById(R.id.settingsBtn))
								.setText(R.string.str_btn_signout);
					} else {
						((Button) convertView.findViewById(R.id.settingsBtn))
								.setText(R.string.str_btn_signin);
					}

					((Button) convertView.findViewById(R.id.settingsBtn))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (!PrefHelper.getString("Email", "")
											.equals("")) {
										PreferenceManager
												.getDefaultSharedPreferences(
														BaseApplication
																.getAppContext())
												.edit().clear().commit();
										startActivity(new Intent(
												SettingsActivity.this,
												LoginActivity.class)
												.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
														| Intent.FLAG_ACTIVITY_CLEAR_TOP));
										finish();
									} else {
										startActivity(new Intent(
												SettingsActivity.this,
												LoginActivity.class));
									}
								}
							});
				} else if (section == 2 & row == 0) {
					convertView.findViewById(R.id.settingsTxt).setVisibility(
							View.VISIBLE);
					try {
						PackageInfo pInfo = getPackageManager().getPackageInfo(
								getPackageName(), 0);

						((TextView) convertView.findViewById(R.id.settingsTxt))
								.setText("v" + pInfo.versionName + " ("
										+ pInfo.versionCode + ")");
					} catch (NameNotFoundException e) {
						((TextView) convertView.findViewById(R.id.settingsTxt))
								.setText("n\\a");
					}
				} else if (section == 3 & row == 0) {
					convertView.findViewById(R.id.settingsCheckbox)
							.setVisibility(View.VISIBLE);
					final TextView text = (TextView) convertView
							.findViewById(R.id.text);
					((CheckBox) convertView.findViewById(R.id.settingsCheckbox))
							.setChecked(PrefHelper.getBoolean(
									getString(R.string.KEY_IS_DIALOG_ACTIVE),
									false));
					((CheckBox) convertView.findViewById(R.id.settingsCheckbox))
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									PrefHelper
											.setBoolean(
													getString(R.string.KEY_IS_DIALOG_ACTIVE),
													isChecked);
									if (isChecked) {
										text.setText(R.string.str_dialog_active);
									} else {
										text.setText(R.string.str_dialog_inactive);
									}
								}
							});
				} else if (section == 3 & row == 1) {
					convertView.findViewById(R.id.settingsBtn).setVisibility(
							View.VISIBLE);
					((Button) convertView.findViewById(R.id.settingsBtn))
							.setText(R.string.str_btn_reset);

					((Button) convertView.findViewById(R.id.settingsBtn))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									resetDialog();
								}
							});
				}

				if (section == 0) {
					((TextView) convertView.findViewById(R.id.text))
							.setText(itemsSection2[row]);
				} else if (section == 1) {
					if (row == 1
							& !PrefHelper.getString("Email", "").equals("")) {
						((TextView) convertView.findViewById(R.id.text))
								.setText(PrefHelper.getString("Email", ""));
					} else {
						((TextView) convertView.findViewById(R.id.text))
								.setText(itemsSection3[row]);
					}
				} else if (section == 2) {
					((TextView) convertView.findViewById(R.id.text))
							.setText(itemsSection5[row]);
				} else if (section == 3) {
					if (row == 0) {
						final TextView text = (TextView) convertView
								.findViewById(R.id.text);
						if (PrefHelper
								.getBoolean(
										getString(R.string.KEY_IS_DIALOG_ACTIVE),
										false)) {
							text.setText(R.string.str_dialog_active);
						} else {
							text.setText(R.string.str_dialog_inactive);
						}
					} else {
						((TextView) convertView.findViewById(R.id.text))
								.setText(itemsSection6[row]);
					}
				}
				return convertView;
			}

			@Override
			public int getSectionHeaderViewTypeCount() {
				return 2;
			}

			@Override
			public int getSectionHeaderItemViewType(int section) {
				return section % 2;
			}

			@Override
			public View getSectionHeaderView(int section, View convertView,
					ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							getResources().getLayout(
									R.layout.item_settings_header), null);
				}
				convertView.findViewById(R.id.settingsCheckbox).setVisibility(
						View.GONE);
				if (section == 0) {
					convertView.findViewById(R.id.settingsCheckbox)
							.setVisibility(View.VISIBLE);
					((CheckBox) convertView.findViewById(R.id.settingsCheckbox))
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									PrefHelper
											.setBoolean(
													getString(R.string.KEY_IS_REMINDER_ON),
													isChecked);
									notifyDataSetChanged();
								}
							});
					((CheckBox) convertView.findViewById(R.id.settingsCheckbox))
							.setChecked(PrefHelper.getBoolean(
									getString(R.string.KEY_IS_REMINDER_ON),
									true));
				}

				((TextView) convertView.findViewById(R.id.text))
						.setText(itemsSections[section]);

				return convertView;
			}

			@Override
			public void onRowItemClick(AdapterView<?> parent, View view,
					int section, int row, long id) {
				super.onRowItemClick(parent, view, section, row, id);
				Toast.makeText(SettingsActivity.this,
						"Section " + section + " Row " + row,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	OnCheckedChangeListener facebookCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mButtonView = buttonView;
			buttonView.setChecked(!isChecked);
			if (isChecked) {
				BaseApplication.openActiveSession(SettingsActivity.this, true,
						new Session.StatusCallback() {

							@Override
							public void call(Session session,
									SessionState state, Exception exception) {
								Log.d("Log", "session changed = " + state
										+ " (" + exception + ")");
								if (session.isOpened()) {
									GraphUserCallback request = new Request.GraphUserCallback() {

										@Override
										public void onCompleted(GraphUser user,
												Response response) {
											Log.d("Log", "user = " + user);
											if (user != null) {
												mEmail = (String) user.asMap()
														.get("email");
												mAccessToken = Session
														.getActiveSession()
														.getAccessToken();
												new FacebookLoginTask()
														.execute();
											}
										}
									};

									Request.executeMeRequestAsync(session,
											request);
								} else {
									if (exception != null) {
										Toast.makeText(
												BaseApplication.getAppContext(),
												"Login failed: " + exception,
												Toast.LENGTH_LONG).show();
										/*
										 * mFacebookTxt . setText(R.string .
										 * str_facebook) ; mFacebookTxt .
										 * setTextColor( getResources () .
										 * getColorStateList ( R.drawable.
										 * ac_login_text_color_selector ));
										 */
									}
								}
							}
						});
			} else {
				try {
					BaseApplication.session.close();
				} catch (Exception e) {
				}
				buttonView.setChecked(false);
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log("onActivityResult");
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	class FacebookLoginTask extends AsyncTask<String, Void, Void> {

		private boolean mError;
		private String mErrorMessage;
		private static final String LOGIN_SOCIAL_URL = "http://dev.core.stockholmapplab.com/webservice/user/loginsocial";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
				} else {
					mErrorMessage = responseJSON.getString("ErrorMessage");
					mError = true;
				}
				Log("facebook response = " + responseString);
			} catch (ClientProtocolException e) {
				Log.d("Log", e + "");
				SettingsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showErrorDialog("Client error",
								"Please contact developer.");
					}

				});
			} catch (IOException e) {
				SettingsActivity.this.runOnUiThread(new Runnable() {

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
				mButtonView.setOnCheckedChangeListener(null);
				mButtonView.setChecked(true);
				mButtonView
						.setOnCheckedChangeListener(facebookCheckedChangeListener);
			}
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	private void showErrorDialog(String title, String message) {
		mErrorDialog = new Dialog(SettingsActivity.this, R.style.DialogTheme);
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

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		// Hide title from action bar
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0);
		// Set title text
		SpannableString s = new SpannableString(getResources().getString(
				R.string.str_setting));
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		finish();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * init() method is use for getting reference of controls and populate view
	 * on screen.
	 */
	public void init() {
		mLinear = GenericView.findViewById(this, R.id.ll_linear1);
		mTxtReminder.setText(R.string.str_reminder_off);
		mTxtMorningReminder.setText(R.string.str_morning_reminder);
		mTxtEveningReminder.setText(R.string.str_evening_reminder);
		mTxtActive.setText(R.string.str_dialog_inactive);

		// Toggle Button Settings....
		mToggle1.setChecked(PrefHelper.getBoolean(
				getString(R.string.KEY_IS_REMINDER_ON), true));
		mToggle2.setChecked(PrefHelper.getBoolean(
				getString(R.string.KEY_IS_DIALOG_ACTIVE), false));
		String morningTime = PrefHelper.getString(
				getString(R.string.KEY_MORNING_REMINDER), "08:00");
		String eveningTime = PrefHelper.getString(
				getString(R.string.KEY_EVENING_REMINDER), "22:00");
		mBtnMorningTime.setText(morningTime.subSequence(0, 5));
		mBtnEveningTime.setText(eveningTime.substring(0, 5));
		// Toggle1...
		if (PrefHelper.getBoolean(getString(R.string.KEY_IS_REMINDER_ON), true)) {
			mTxtReminder.setText(R.string.str_reminder_on);
			mLinear.setVisibility(View.VISIBLE);
		} else {
			mTxtReminder.setText(R.string.str_reminder_off);
			mLinear.setVisibility(View.GONE);
		}

		// Toggle2...
		if (PrefHelper.getBoolean(getString(R.string.KEY_IS_DIALOG_ACTIVE),
				false)) {
			mTxtActive.setText(R.string.str_dialog_active);
		} else {
			mTxtActive.setText(R.string.str_dialog_inactive);
		}
		resetDialog();
	}

	@Override
	protected void onStop() {
		Utility.setAlarm();
		DietCycle.setDietCycle();
		try {
			unregisterReceiver(mHandleMessageReceiver);
		} catch (IllegalArgumentException e) {
		}
		super.onStop();
	}

	/**
	 * SHow Time Picker Dialog.
	 * 
	 * @param flag
	 *            - If true set Morning Alarm. - If false set Evening Alarm.
	 */
	public void showTimePicker(final boolean flag) {
		mDialog1 = new Dialog(SettingsActivity.this, R.style.CustomDialogTheme);
		mDialog1.setContentView(R.layout.ac_time_pick);
		mDialog1.setCanceledOnTouchOutside(true);
		Window window = mDialog1.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		CustomTextView mTxtTap = (CustomTextView) mDialog1
				.findViewById(R.id.tv_txtTap);
		RelativeLayout mRelative = (RelativeLayout) mDialog1
				.findViewById(R.id.rl_relative1);
		hours = (WheelView) mDialog1.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 1, 12,
				"%02d");
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
		hours.setCyclic(true);
		mins = (WheelView) mDialog1.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 59,
				"%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);
		final WheelView ampm = (WheelView) mDialog1.findViewById(R.id.ampm);
		ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
				this, new String[] { "AM", "PM" });
		ampmAdapter.setItemResource(R.layout.wheel_text_item);
		ampmAdapter.setItemTextResource(R.id.text);
		ampm.setViewAdapter(ampmAdapter);
		if (flag) {
			mTxtMorningReminder.startAnimation(mScaleAnimation);
			mBtnMorningTime.startAnimation(mBtnScaleAnimation);
			String moringTime = PrefHelper.getString(
					getString(R.string.KEY_MORNING_REMINDER), "08:00:0");

			if (moringTime.substring(6, 7).equals("1")) {
				int hour = Integer.parseInt(moringTime.substring(0, 2)) - 1;
				hours.setCurrentItem(hour - 12);
				mins.setCurrentItem(Integer.parseInt(moringTime.substring(3, 5)));
				ampm.setCurrentItem(Integer.parseInt(moringTime.substring(6, 7)));
			} else {
				hours.setCurrentItem(Integer.parseInt(moringTime
						.substring(0, 2)) - 1);
				mins.setCurrentItem(Integer.parseInt(moringTime.substring(3, 5)));
				ampm.setCurrentItem(Integer.parseInt(moringTime.substring(6, 7)));
			}
		} else {
			mTxtEveningReminder.startAnimation(mScaleAnimation);
			mBtnEveningTime.startAnimation(mBtnScaleAnimation);
			String eveningTime = PrefHelper.getString(
					getString(R.string.KEY_EVENING_REMINDER), "22:00:1");
			hours.setCurrentItem(Integer.parseInt(eveningTime.substring(0, 2)) - 1);
			mins.setCurrentItem(Integer.parseInt(eveningTime.substring(3, 5)));
			ampm.setCurrentItem(Integer.parseInt(eveningTime.substring(6, 7)));
		}
		mTxtTap.setText(R.string.str_tap_anywhere);
		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		hours.addClickingListener(click);
		mins.addClickingListener(click);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {
				if (flag) {
					mBtnMorningTime.setText(Utility.change(
							hour[hours.getCurrentItem()],
							mins.getCurrentItem(), ampm.getCurrentItem()));
					PrefHelper.setString(
							getString(R.string.KEY_MORNING_REMINDER),
							mBtnMorningTime.getText().toString() + ":"
									+ String.valueOf(ampm.getCurrentItem()));
				} else {
					mBtnEveningTime.setText(Utility.change(
							hour[hours.getCurrentItem()],
							mins.getCurrentItem(), ampm.getCurrentItem()));
					PrefHelper.setString(
							getString(R.string.KEY_EVENING_REMINDER),
							mBtnEveningTime.getText().toString() + ":"
									+ String.valueOf(ampm.getCurrentItem()));
				}
			}
		};
		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
		ampm.addScrollingListener(scrollListener);
		mDialog1.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (flag) {
					mTxtMorningReminder.startAnimation(mReverseScaleAnimation);
					mBtnMorningTime.startAnimation(mBtnReverseScaleAnimation);
					mTxtEveningReminder.clearAnimation();
					mBtnEveningTime.clearAnimation();
				} else {
					mTxtEveningReminder.startAnimation(mReverseScaleAnimation);
					mBtnEveningTime.startAnimation(mBtnReverseScaleAnimation);
					mTxtMorningReminder.clearAnimation();
					mBtnMorningTime.clearAnimation();
				}
				mDialog1.dismiss();
			}
		});
		mRelative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog1.dismiss();
			}
		});
		mDialog1.show();
	}

	/**
	 * Reset Dialog to reset Help dialog Preferences to true.
	 */
	public void resetDialog() {
		if (PrefHelper.getBoolean(getString(R.string.KEY_IS_DIALOG_ACTIVE),
				false)) {
			Utility.setALLPreferencesToTrue(SettingsActivity.this);
		}
		mDialog2 = new Dialog(SettingsActivity.this, R.style.DialogTheme);
		mDialog2.setContentView(R.layout.raw_dialog2);
		mDialog2.setCanceledOnTouchOutside(false);
		CustomTextView mTxtReset = (CustomTextView) mDialog2
				.findViewById(R.id.tv_txtCalendarValidation);
		CustomButton mBtnOk = (CustomButton) mDialog2
				.findViewById(R.id.bt_btn_ok);
		mTxtReset.setText(getString(R.string.str_reset_hints));
		mBtnOk.setText(R.string.str_dialog_2);
		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog2.dismiss();
			}
		});
		mDialog2.show();
	}

	/*
	 * @Override protected void onResume() { if (BaseConstant.mDietCycle ==
	 * null) { BaseConstant.mDietCycle = DietCycle.getDietCycle(); }
	 * registerReceiver(mHandleMessageReceiver, new IntentFilter(
	 * BaseConstant.DISPLAY_1_HOUR_NOTIFICATION_ACTION));
	 * Utility.showFastingAndNonFastingDays(SettingsActivity.this);
	 * Utility.checkLastLaunchApp(); Utility.checkAchievemtnDialog(this); if
	 * (Utility.isDietCycleFinish()) { finish(); } super.onResume(); }
	 */

	/**
	 * This Broadcast Receiver is use for show how much calories left.
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.leftCaloryDialog(SettingsActivity.this);
		}
	};

	public void back(View v) {
		finish();
	}

	public void logOut() {
		PreferenceManager
				.getDefaultSharedPreferences(BaseApplication.getAppContext())
				.edit().clear().commit();
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getWritableDatabase();
		DB.execSQL("delete from recipes_list");
		DB.close();
		HomeActivity.activity.finish();
		finish();
		// Start login activity, with logout extra (it will kill facebook
		// session later)
		startActivity(new Intent(SettingsActivity.this, LoginActivity.class)
				.putExtra("logout", true));
	}

}