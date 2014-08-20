package marki.at.Client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import marki.at.Client.MainActivity;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Message;
import marki.at.Client.utils.NotificationCreator;
import marki.at.Client.utils.Parser;

/**
 * Created by marki on 29.10.13.
 */
public class StaticReceiverMessages extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.newMessage = true;
        Message messageObject = Parser.parseMessage(intent);
        Data.addMessage(context, messageObject);
        Data.getMessages(context).add(messageObject);
        NotificationCreator.makeNotification(context);
        abortBroadcast();
    }


}