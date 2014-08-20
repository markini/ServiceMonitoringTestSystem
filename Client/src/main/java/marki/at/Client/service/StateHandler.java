package marki.at.Client.service;

import android.app.IntentService;
import android.content.Intent;

import marki.at.Client.utils.Settingshandler;

/**
 * Created by marki on 14.11.13.
 */
public class StateHandler extends IntentService {

    // No-arg constructor is required
    public StateHandler() {
        super("StateHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // CONNECTIVITY ------------------------------------------------------
        boolean connectivityState = Settingshandler.getConnectivityState(this);

        if(!connectivityState){ //no internet
            //TODO switch to sms
            return;
        }

        // SERVER -------------------------------------------------------------
        boolean serverState = Settingshandler.getServerState(this);

        if(!serverState){ //no internet
            //TODO switch to sms
            return;
        }

        // GCM -------------------------------------------------------------
        boolean gcmState = Settingshandler.getGcmState(this);

        if(!gcmState){ //no internet
            //activate polling
            return;
        }


    }
}