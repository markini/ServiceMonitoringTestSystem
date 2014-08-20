package marki.at.Client;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import marki.at.Client.dialogs.DeleteDialog;
import marki.at.Client.download.GetNewDataService;
import marki.at.Client.gcm.GCMHandler;
import marki.at.Client.monitoring.MonitorConnectivity;
import marki.at.Client.monitoring.MonitorServerPing;
import marki.at.servicemonitoring.Monitor;
import timber.log.Timber;

public class ClientApplication extends Application {

	private ObjectGraph objectGraph;

	public static Monitor gcmCheckMonitor;
	public static Monitor connectivityMonitor;
	public static Monitor serverMonitor;

	@Override
	public void onCreate() {
		super.onCreate();
		objectGraph = ObjectGraph.create(new MainModule(this));
		Timber.plant(new Timber.DebugTree());
		GCMHandler.with(this).initGcm();
		//Data.createMockMessages(this);

//        if (gcmCheckMonitor == null) {
//            gcmCheckMonitor = new MonitorGcmCheck();
//        }
		if (connectivityMonitor == null) {
			connectivityMonitor = new MonitorConnectivity();
		}
		if (serverMonitor == null) {
			serverMonitor = new MonitorServerPing();
		}

		//startMonitoring();
	}

	public void startMonitoring() {
//        if (!gcmCheckMonitor.isRunning()) {
//            gcmCheckMonitor.executeMonitoring(this, false, 2);
//        }
		if (!connectivityMonitor.isRunning()) {
			connectivityMonitor.executeMonitoring(this, true, 1);
		}
		if (!serverMonitor.isRunning()) {
			serverMonitor.executeMonitoring(this, true, 2);
		}

	}

	public void inject(Object object) {
		objectGraph.inject(object);
	}

	@Module(injects = {
			MainActivity.class, //
			FragmentMain.class, //
			DeleteDialog.class, //
			GetNewDataService.class //
	})
	static class MainModule {
		private final Context appContext;

		MainModule(Context appContext) {
			this.appContext = appContext;
		}

		@Provides
		@Singleton
		Bus provideBus() {
			return new Bus();
		}
	}
}
