package marki.at.Client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import marki.at.Client.MainActivity;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Message;
import marki.at.Client.utils.NotificationCreator;
import timber.log.Timber;

/**
 * Created by marki on 14.12.13.
 */
public class StaticSmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Message messageObject = new Message(UUID.randomUUID().toString(),intent.getExtras().getString("message"),System.currentTimeMillis());
		Timber.i("Static sms Receiver");
		if(messageObject != null){
			MainActivity.newMessage = true;
			Data.addMessage(context, messageObject);
			Data.getMessages(context).add(messageObject);
			NotificationCreator.makeNotification(context);
			abortBroadcast();
		}
	}
}