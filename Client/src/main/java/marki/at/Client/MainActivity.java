package marki.at.Client;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Bus;

import java.util.UUID;

import javax.inject.Inject;

import at.marki.Client.R;
import marki.at.Client.events.newMessageEvent;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Message;
import marki.at.Client.utils.Parser;
import timber.log.Timber;

public class MainActivity extends Activity {

	private BroadcastReceiver messageReceiver;
	private BroadcastReceiver smsReceiver;

	private final static int REQUEST_PLAY_SERVICES = 1;

	public static boolean newMessage = false;

	@Inject
	Bus bus;

	protected static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		((ClientApplication) getApplication()).inject(this);
		Fragment fragmentMain = getFragmentManager().findFragmentByTag(FragmentMain.TAG);
		if (fragmentMain == null) {
			fragmentMain = new FragmentMain();
		}

		startTransaction(R.id.fragment_frame, fragmentMain, FragmentMain.TAG, false);
	}

	public void startTransaction(int id, Fragment fragment, String tag, boolean addToBackStack) {
		FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
		fragTransaction.replace(id, fragment, tag);
		if (addToBackStack) {
			fragTransaction.addToBackStack(null);
		}
		fragTransaction.commit();
	}

	//--------------------------------------------------------------------------------------------------
	//PAUSE - RESUME -----------------------------------------------------------------------------------

	@Override
	protected void onResume() {
		super.onResume();
		Timber.d("on resume called");

		messageReceiver = new MessageReceiver();
		smsReceiver = new SmsDynamicReceiver();

		//filter for normal messages (gcm receiver)
		IntentFilter filterMessage = new IntentFilter(getString(R.string.intent_filter_message_receive));
		filterMessage.setPriority(30);

		//filter for sms messages
		IntentFilter filterSmsMessage = new IntentFilter(getString(R.string.intent_filter_sms_receive));
		filterSmsMessage.setPriority(30);

		this.registerReceiver(messageReceiver, filterMessage);
		this.registerReceiver(smsReceiver, filterSmsMessage);
	}

	@Override
	protected void onPause() {
		Timber.d("onPause called");
		this.unregisterReceiver(messageReceiver);
		this.unregisterReceiver(smsReceiver);
		super.onPause();
	}

	//--------------------------------------------------------------------------------------------------
	//RECEIVER -----------------------------------------------------------------------------------------

	private class MessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
			final Message sendMessage = Parser.parseMessage(intent);
			Data.addMessage(context, sendMessage);
			MAIN_THREAD.post(new Runnable() {
				@Override
				public void run() {
					bus.post(new newMessageEvent(sendMessage));
				}
			});
			abortBroadcast();
		}
	}

	private class SmsDynamicReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Timber.i("Dynamic sms Receiver");
			Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
			final Message sendMessage = new Message(UUID.randomUUID().toString(), intent.getExtras().getString("message"), System.currentTimeMillis());
			Data.addMessage(context, sendMessage);
			MAIN_THREAD.post(new Runnable() {
				@Override
				public void run() {
					bus.post(new newMessageEvent(sendMessage));
				}
			});
			abortBroadcast();
		}
	}

	private void checkForGooglePlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_PLAY_SERVICES,
						new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								Toast.makeText(MainActivity.this,
										"Play services error",
										Toast.LENGTH_LONG).show();
								finish();
							}
						}
				).show(); // Throws NullPointerException
			} else {
				Toast.makeText(this, "Play services unsupported",
						Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}

