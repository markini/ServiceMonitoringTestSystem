package marki.at.Client.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by marki on 15.12.13.
 */
public class Parser {

	public static Message parseSms(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		if (extras != null) {
			Object[] smsExtras = (Object[]) extras.get("pdus");

			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) smsExtras[0]);

			String message = smsMessage.getMessageBody().toString();
			String sender = smsMessage.getOriginatingAddress();

			Timber.i("message: " + message + " from: " + sender);

			if (message.contains("at.marki")) {//message is from our server
				String messageString = null;

				//get message and save it on database
				if (message.length() > 8) {
					messageString = message.substring(8);
				} else {
					//no real message
				}

				Timber.i("message string: " + messageString);

				if (messageString != null) {
					return new Message(UUID.randomUUID().toString(), messageString, System.currentTimeMillis());
				}
			}
		}
		return null;
	}

	public static String parseSmsToString(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		if (extras != null) {
			Object[] smsExtras = (Object[]) extras.get("pdus");

			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) smsExtras[0]);

			String message = smsMessage.getMessageBody().toString();
			String sender = smsMessage.getOriginatingAddress();

			Timber.i("message: " + message + " from: " + sender);

			if (message.contains("at.marki")) {//message is from our server
				String messageString = null;

				//get message and save it on database
				if (message.length() > 8) {
					messageString = message.substring(8);
				} else {
					//no real message
				}

				Timber.i("message string: " + messageString);

				return messageString;
			}
		}
		return null;
	}


	public static Message parseMessage(Intent intent) {
		String message = null;
		String messageId = null;
		if (intent != null) {
			try {
				message = intent.getExtras().getString("message");
				messageId = intent.getExtras().getString("messageId");
			} catch (Exception e) {
				Timber.e("no extras in on receive of broadcast");
			}
		} else {
			message = null;
			messageId = null;
		}
		if (message == null) {
			message = "defaultMessage";
		}
		if (messageId == null) {
			messageId = "defaultId";
		}

		return new Message(messageId, message, System.currentTimeMillis());
	}
}
