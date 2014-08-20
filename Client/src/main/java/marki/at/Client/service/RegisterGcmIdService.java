package marki.at.Client.service;

import android.app.IntentService;
import android.content.Intent;

import com.github.kevinsawicki.http.HttpRequest;

import marki.at.Client.utils.Settingshandler;
import timber.log.Timber;

/**
 * Created by marki on 14.11.13.
 */
public class RegisterGcmIdService extends IntentService {

    // No-arg constructor is required
    public RegisterGcmIdService() {
        super("StateHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String gcmId = intent.getExtras().getString("id");

        try {
            String url = Settingshandler.getRegisterGcmIdAddress(this);
            HttpRequest request = HttpRequest.get(url + "?id=" + gcmId).connectTimeout(30000).readTimeout(30000);

            if (request.ok()) {
                Timber.d("successfull registered gcm on server");
                return;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


    }
}