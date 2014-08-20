package marki.at.servicemonitoring;

import android.app.IntentService;
import android.content.Intent;

public class MonitorThisService extends IntentService {

	public MonitorThisService() {
		super("MonitorThisService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("Monitor this from intent service - from alarmmanager");
		Monitor monitor = intent.getExtras().getParcelable("monitor");
		if (!monitor.observeThis(this)) {
			monitor.handleEvent(this);
		}
	}
}