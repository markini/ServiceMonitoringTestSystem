package marki.at.Client.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class GcmRegistrationReceiver extends BroadcastReceiver {

	private static final String REGISTRATION_ID_INTENT_EXTRA_KEY = "registration_id";
	private static final String ERROR_INTENT_EXTRA_KEY = "error";

	@Override
	public void onReceive(Context context, Intent intent) {
		String regId = intent.getStringExtra(REGISTRATION_ID_INTENT_EXTRA_KEY);
		if (regId != null) { //Handle GCM registration message
			Timber.d("Received Reg Id in GcmRegistrationReceiver: " + regId);
			GCMHandler.storeAndSendGcmIdToBackend(context, regId);
			return;
		}

		String gcmError = intent.getStringExtra(ERROR_INTENT_EXTRA_KEY);
		if (gcmError != null) {
			Timber.e("GCM registration unsuccessful, " + gcmError);
		}
	}
}