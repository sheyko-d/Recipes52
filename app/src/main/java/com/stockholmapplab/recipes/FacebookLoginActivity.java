package com.stockholmapplab.recipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

/**
 * Activity helps to login into facebook and also logout from it also you can
 * get friends list, login user data, invite friends for application use
 */
public class FacebookLoginActivity extends ActionBarActivity /*implements StatusCallback*/ {
	//private UiLifecycleHelper uiHelper;
	private boolean isResumed = false;
	private boolean isRequiredNewRequest = false;
	public static final int FACEBOOK_HOME_REQUEST_CODE = 111;
	public static final int FACEBOOK_MY_STAT_REQUEST_CODE = 222;
	public static final int FACEBOOK_ACHIEVEMENT_REQUEST_CODE = 333;

	String[] read_permissions = { "basic_info", "read_stream" };
	String[] write_permissions = { "publish_actions" };
	Context context;
	Handler mHandler = new Handler();
	public static final String extraArticalImages = "articleImages";

	public static final String extraMessage = "extraMessage";
	public static final String extraDrawableID = "drawableID";

	private String message = null;
	private int drawableID = -1;

	/**
	 * onCreate
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*BaseApplication.lockOrientation(this);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			ThreadPolicy tp = ThreadPolicy.LAX;
			StrictMode.setThreadPolicy(tp);
		}
		uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);

		if (!isLoggedIn()) {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				if (bundle.containsKey(extraMessage)) {
					message = bundle.getString(extraMessage);
					drawableID = bundle.getInt(extraDrawableID);
				}
			}
			loginFB();
		}*/

	}

	/**
	 * onSaveInstanceState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//uiHelper.onSaveInstanceState(outState);
	}

	/**
	 * onResume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*uiHelper.onResume();
		isResumed = true;
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}*/
	}

	/**
	 * Function to check user is LoggedIn or not
	 * 
	 * @return boolean true/false
	 */
	public static boolean isLoggedIn() {
		/*Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			return true;
		} else {
			return false;
		}*/
		return true;//TODO:REMOVE
	}

	/**
	 * call
	 */
	/*@Override
	public void call(Session session, SessionState state, Exception exception) {
		onSessionStateChange(session, state, exception);
	}*/

	/**
	 * onPause
	 */
	@Override
	public void onPause() {
		super.onPause();
		//uiHelper.onPause();
		isResumed = false;
	}

	/**
	 * onActivityResult
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * onDestroy
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		//uiHelper.onDestroy();
	}

	/**
	 * onSessionStateChange
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	/*private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Only make changes if the activity is visible
		try {
			if (isResumed) {
				if (state.isOpened()) {
					if (!isRequiredNewRequest) {
						isRequiredNewRequest = true;
						requestReadPermissions();
					} else {
						if (message != null && drawableID != -1) {
							Intent intent = new Intent();
							intent.putExtra(extraMessage, message);
							intent.putExtra(extraDrawableID, drawableID);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							setResult(RESULT_OK);
							finish();
						}

					}

				} else if (state.isClosed()) {
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		} catch (IllegalStateException ies) {
		} catch (IllegalArgumentException iea) {
		} catch (NullPointerException ne) {
		} catch (Exception e) {
		}
	}
*/
	/**
	 * Function to read request permissions
	 */
	void requestReadPermissions() {
		/*try {
			NewPermissionsRequest newPermissionsRequest = new NewPermissionsRequest(
					this, Arrays.asList(write_permissions));
			newPermissionsRequest.setCallback(this);
			newPermissionsRequest
					.setDefaultAudience(SessionDefaultAudience.FRIENDS);
			newPermissionsRequest
					.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			Session.getActiveSession().requestNewPublishPermissions(
					newPermissionsRequest);
		} catch (IllegalStateException ies) {
		} catch (IllegalArgumentException iea) {
		} catch (NullPointerException ne) {
		} catch (Exception e) {
		}*/
	}

	/**
	 * Function to logged into the facebook
	 */
	void loginFB() {
		/*Session.OpenRequest openRequest = null;
		openRequest = new Session.OpenRequest(this);
		if (openRequest != null) {
			openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
			openRequest.setPermissions(Arrays.asList(read_permissions));
			openRequest
					.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			openRequest.setCallback(this);
		}
		Session session = new Session.Builder(this).build();
		Session.setActiveSession(session);
		session.openForRead(openRequest);*/
	}

	/**
	 * Function used to logout from facebook
	 */
	public static void logoutFB() {
		//Session.getActiveSession().closeAndClearTokenInformation();
	}

	/**
	 * FaceBook Post Function
	 */

	public void FacebookPost() {
		/*byte[] data = null;
		Bundle params = new Bundle();

		final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_facebook_icon);

		if (bitmap != null) {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			data = stream.toByteArray();

			params.putString(
					"message",
					getString(R.string.str_facebook_share_message_1)
							+ "\n"
							+ getString(R.string.str_facebook_share_message_2)
							+ "\n\n\n"
							+ getString(R.string.str_facebook_share_message_3)
							+ "\t"
							+ AppRater
									.getPlayStoreURL(FacebookLoginActivity.this));
			params.putByteArray("picture", data);
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					if (response.toString().contains("200")) {
						finish();
						Toast.makeText(FacebookLoginActivity.this,
								"Shared Successfully", Toast.LENGTH_LONG)
								.show();
					} else {
						finish();
						Toast.makeText(FacebookLoginActivity.this,
								"Shared Fail", Toast.LENGTH_LONG).show();
					}
				}
			};
			Request request = new Request(Session.getActiveSession(),
					"me/photos", params, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		} else {
		}*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}