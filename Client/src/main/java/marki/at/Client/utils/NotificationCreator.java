package marki.at.Client.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import at.marki.Client.R;
import marki.at.Client.MainActivity;

/**
 * Created by marki on 14.12.13.
 */
public class NotificationCreator {
	public static void makeNotification(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("New Message")
				.setContentText("A new message arrived").setContentIntent(pendingIntent).setLargeIcon(largeIcon);

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		builder.setSound(alarmSound);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = builder.build();

		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(R.id.notification_id, notification);
	}
}
