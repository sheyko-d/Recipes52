package com.stockholmapplab.recipes.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.stockholmapplab.recipes.BaseApplication;

/**
 * <p>
 * The <b>ConnectionUtility</b> class provides the primary API for managing all
 * aspects of connectivity like wi-fi, Mobile network, and Wi-max.
 * </p>
 * &lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> <br>
 * &lt;uses-permission android:name="android.permission.INTERNET"/>
 * 
 */
public class ConnectionUtility {

	/**
	 * The boolean <strong>isWifiConnected</strong> is used to get the state of
	 * the Wifi adapter. <br>
	 * 
	 * @param context
	 *            Current state of the application/object.
	 * @return true, if is wifi connected
	 * @see <a
	 *      href="http://developer.android.com/reference/android/content/Context.html">Context</a>
	 */
	public static boolean isWifiConnected(final Context context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null)
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	/**
	 * The boolean <strong>isMobileNetworkConnected</strong> is used to get the
	 * state of the Mobile Network <strong>true</strong>. All data traffic will
	 * use this connection by default <br>
	 * 
	 * @param context
	 *            Current state of the application/object.
	 * @return true, if is mobile network connected
	 * @see <a
	 *      href="http://developer.android.com/reference/android/content/Context.html">Context</a>
	 */
	public static boolean isMobileNetworkConnected(final Context context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null)
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	/**
	 * The boolean <strong>isWiMaxConnected</strong> is used to get the state of
	 * the Wi max connection. <br>
	 * 
	 * @param context
	 *            Current state of the application/object.
	 * @return true, if is wi max connected
	 * @see <a
	 *      href="http://developer.android.com/reference/android/content/Context.html">Context</a>
	 */
	public static boolean isWiMaxConnected(final Context context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null)
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	/**
	 * The boolean <strong>isNetAvailable</strong> is used to get the all state
	 * of the connection or all networks. <br>
	 * 
	 * @param context
	 *            Current state of the application/object.
	 * @return true, if is net available
	 * @see <a
	 *      href="http://developer.android.com/reference/android/content/Context.html">Context</a>
	 */

	public static boolean isNetAvailable() {

		try {
			boolean isNetAvailable = false;
			if (BaseApplication.getAppContext() != null) {
				final ConnectivityManager mgr = (ConnectivityManager) BaseApplication
						.getAppContext().getSystemService(
								Context.CONNECTIVITY_SERVICE);
				if (mgr != null) {
					boolean mobileNetwork = false;
					boolean wifiNetwork = false;
					boolean wiMaxNetwork = false;

					boolean mobileNetworkConnecetd = false;
					boolean wifiNetworkConnecetd = false;
					boolean wiMaxNetworkConnected = false;

					final NetworkInfo mobileInfo = mgr
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					final NetworkInfo wifiInfo = mgr
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					final NetworkInfo wiMaxInfo = mgr
							.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

					if (mobileInfo != null)
						mobileNetwork = mobileInfo.isAvailable();

					if (wifiInfo != null)
						wifiNetwork = wifiInfo.isAvailable();

					if (wiMaxInfo != null)
						wiMaxNetwork = wiMaxInfo.isAvailable();

					if (wifiNetwork == true)
						wifiNetworkConnecetd = wifiInfo
								.isConnectedOrConnecting();
					if (mobileNetwork == true)
						mobileNetworkConnecetd = mobileInfo
								.isConnectedOrConnecting();
					if (wiMaxNetwork == true)
						wiMaxNetworkConnected = wiMaxInfo
								.isConnectedOrConnecting();

					isNetAvailable = (mobileNetworkConnecetd
							|| wifiNetworkConnecetd || wiMaxNetworkConnected);
				}
			}
			return isNetAvailable;
		} catch (final NullPointerException e) {
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

}
