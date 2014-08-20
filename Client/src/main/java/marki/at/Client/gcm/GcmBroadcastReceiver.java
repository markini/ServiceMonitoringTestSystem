package marki.at.Client.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import at.marki.Client.R;
import timber.log.Timber;

public class GcmBroadcastReceiver extends BroadcastReceiver {

	public static boolean receivedPing = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Timber.d("GCM Message received");

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);

		switch (messageType) {
			case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
				// TODO
				break;
			case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
				// TODO
				// Maybe in the future
				break;
			case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
				Bundle bundle = intent.getExtras();

				String pingString = bundle.getString("ping");
				if(pingString != null){
					Timber.d("test ping gcm from server");
					receivedPing = true;
					return;
				}

				String message = bundle.getString("message");
				String messageId = bundle.getString("messageId");
				if (message == null) {
					message = "no message";
				}
				if (messageId == null) {
					messageId = "default id";
				}

				Timber.d("new message from server: " + message);

				Intent broadCastIntent = new Intent(context.getString(R.string.intent_filter_message_receive));
				broadCastIntent.putExtra("message",message);
				broadCastIntent.putExtra("messageId",messageId);
				context.sendOrderedBroadcast(broadCastIntent, "at.marki.Client.BROADCASTNOTIFY");
				break;
		}
	}
}
