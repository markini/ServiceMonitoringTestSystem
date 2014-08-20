package marki.at.Client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import at.marki.Client.R;
import marki.at.Client.utils.Parser;
import timber.log.Timber;

/**
 * Created by marki on 14.12.13.
 */
public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		String smsString = Parser.parseSmsToString(context, intent);
		if(smsString == null){
			Timber.e("Not one of the sms from the Server.");
			return;
		}

		Intent broadCastIntent = new Intent(context.getString(R.string.intent_filter_sms_receive));
		broadCastIntent.putExtra("message",smsString);
		context.sendOrderedBroadcast(broadCastIntent,null);
		abortBroadcast();
	}
}