package marki.at.Client.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import marki.at.Client.ClientApplication;
import timber.log.Timber;

/**
 * Created by marki on 29.10.13.
 */
public class GetNewDataService extends Service {

    @Inject
    Bus bus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((ClientApplication) getApplication()).inject(this);
        Timber.d("starting get data service");

        try {
            executeTask();
        } catch (Exception e) {
            stopSelf();
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private void executeTask() {
        GetNewDataAsyncTask task = new GetNewDataAsyncTask(bus, this);
        task.execute();
    }

}
