package marki.at.servicemonitoring;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public abstract class Monitor extends BroadcastReceiver implements Parcelable {

	private ScheduledFuture mHandler;

	/**
	 * @return true if no problem and false if monitoring found a problem
	 */
	protected abstract boolean observeThis(Context context);

	protected abstract boolean handleEvent(Context context);

	public void executeMonitoring(Context context, boolean startSticky, float intervalInMinutes) {
		long interval = (long) (intervalInMinutes * 60 * 1000);
		System.out.println("INTERVAL: " + interval);
		if (startSticky) {
			startAlarmTask(context, interval);
		} else {
			startExecutionService(context, interval);
		}
	}

	public void stopMonitoring(Context context) {
		System.out.println("Abort monitoring");

		//kill alarm manager
		Intent intent = new Intent(context, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		//kill execution process
		if (mHandler != null) {
			mHandler.cancel(false); //will not cancel thread instantaneously / will wait for thread to finish and don't start again
		}
		mHandler = null;
	}

	private void startExecutionService(final Context context, long interval) {

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		Thread runner = new Thread(new Runnable() {
			public void run() {
				System.out.println("Monitor this from executorservice");
				if (!observeThis(context)) {
					handleEvent(context);
				}
			}
		});

		mHandler = scheduler.scheduleAtFixedRate(runner, 1000, interval, MILLISECONDS); //starts in one second
	}

	/**
	 * Sets the AlarmManager to start the monitor
	 * continuously in the given interval.
	 * The monitor is started after one second.
	 *
	 * @param context  the application context
	 * @param interval the interval in which the
	 *                 monitor executes (in milliseconds)
	 */
	private void startAlarmTask(Context context, long interval) {
		Intent intent = new Intent(context, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(
				Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
				+ 1000, interval, pendingIntent);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		Intent serviceIntent = new Intent(context.getApplicationContext(), MonitorThisService.class);
		serviceIntent.putExtra("monitor", this);
		context.getApplicationContext().startService(serviceIntent);
	}

	public boolean isRunning() {
		return mHandler != null;
	}

	//PARCELABLE ------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
	}
}


