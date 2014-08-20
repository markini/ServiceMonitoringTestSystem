package marki.at.Client.monitoring;

import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

import marki.at.Client.gcm.GcmBroadcastReceiver;
import marki.at.Client.utils.Settingshandler;
import timber.log.Timber;

/**
 * Created by marki on 06.11.13.
 */
class PingGcm {
	public static boolean performPing(Context context) {
		try {
			Timber.d("MONITOR GCM!!!");
			String url = Settingshandler.getGcmCheckAddress(context);
			HttpRequest request = HttpRequest.get(url).connectTimeout(30000).readTimeout(30000);

			if (!request.ok()) {
				return false;
			} else {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (GcmBroadcastReceiver.receivedPing) {
					GcmBroadcastReceiver.receivedPing = false;
					return true;
				} else {
					return false;
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return false;
	}
}
