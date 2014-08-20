package marki.at.Client.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Random;

import at.marki.Client.BuildConfig;
import marki.at.Client.MainActivity;
import timber.log.Timber;

public class GCMHandler {

	public static final String SENDER_ID = "380505122106";
	private static final String PROPERTY_REG_ID = "property_gcm_registration_id";
	private static final String PROPERTY_APP_VERSION = "property_app_version";

	private static final int MAX_ATTEMPTS = 7;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random sRandom = new Random();

	private static GCMHandler sSingleton = null;

	private final Context mContext;
	private GoogleCloudMessaging gcm;
	private String regId;

	private GCMHandler(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null.");
		}
		mContext = context.getApplicationContext();
	}

	/**
	 * The global default Cloud instance.
	 */
	public static GCMHandler with(Context context) {
		if (sSingleton == null) {
			sSingleton = new GCMHandler(context);
		}
		return sSingleton;
	}

	/**
	 * Initializes the GCM service.
	 * <p/>
	 * Checks if the device has the proper dependencies installed. <br />
	 * Checks that the application manifest is properly configured. <br />
	 * Registers the device with the GCM Service.
	 */
	public GCMHandler initGcm() {
		gcm = GoogleCloudMessaging.getInstance(mContext);
		regId = getRegistrationId(mContext);

		if (regId.isEmpty()) {
			registerInBackground();
		}

		return this;
	}

	public String returnRegistrationId(){
		return getRegistrationId(mContext);
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p/>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 * registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Timber.e("Registration ID not found");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = BuildConfig.VERSION_CODE;
		if (registeredVersion != currentVersion) {
			Timber.e("App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p/>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				long backoff = BACKOFF_MILLI_SECONDS + sRandom.nextInt(1000);
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					Timber.d("Attempt #" + i + " to register");
					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging.getInstance(mContext);
						}
						regId = gcm.register(SENDER_ID);
						Timber.d("Device registered, registration ID = " + regId);

						// No need to register again.
						// Call to persist the registration ID is in the GcmBroadcastReceiver
						break;
					} catch (IOException e) {
						Timber.d(e, "Failed to register on attempt " + i);
						if (i == MAX_ATTEMPTS) {
							break;
						}
						if (e.getMessage()
								.contains(GoogleCloudMessaging.ERROR_SERVICE_NOT_AVAILABLE)) {
							Timber.d("Retry to register GCM");
							// According to http://blog.pushbullet
							// .com/2014/02/12/keeping-google-cloud-messaging-for-android-working
							// -reliably-techincal-post/ this is a recoverable error
							try {
								Timber.d("Sleeping for " + backoff + " ms before retry");
								Thread.sleep(backoff);
							} catch (InterruptedException e1) {
								// Activity finished before we complete - exit.
								Timber.d("Thread interrupted: abort remaining retries!");
								Thread.currentThread().interrupt();
								return null;
							}
							// increase backoff exponentially
							backoff *= 2;
						} else {
							// TODO handle unrecoverable errors
							break;
						}
					}
				}
				return null;
			}
		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId   registration ID
	 */
	private static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = BuildConfig.VERSION_CODE;
		Timber.d("Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.apply();
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}. Also sends the registration ID to the backend.
	 *
	 * @param context  application's context.
	 * @param gcmRegId registration ID
	 */
	static void storeAndSendGcmIdToBackend(Context context, String gcmRegId) {
		storeRegistrationId(context, gcmRegId);
		// TODO send to backend
	}
}
