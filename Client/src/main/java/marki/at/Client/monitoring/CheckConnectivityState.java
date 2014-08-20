package marki.at.Client.monitoring;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import timber.log.Timber;

/**
 * Created by marki on 06.11.13.
 */
class CheckConnectivityState {
	public static boolean performConnectivityCheck(Context context) {
		try {
			Timber.d("MONITOR CONNECTIVITY!!!");

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			return netInfo != null && netInfo.isConnectedOrConnecting();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
