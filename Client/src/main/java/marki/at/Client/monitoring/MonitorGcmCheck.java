package marki.at.Client.monitoring;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import marki.at.Client.EventHandler;
import marki.at.Client.utils.Settingshandler;
import marki.at.servicemonitoring.Monitor;
import timber.log.Timber;

/**
 * Created by marki on 06.11.13.
 */
public class MonitorGcmCheck extends Monitor {

    @Override
    public boolean observeThis(Context context) {
        if(!CheckConnectivityState.performConnectivityCheck(context)){
            Settingshandler.setConnectivityState(context, false);
            return false;
        }else{
            Settingshandler.setConnectivityState(context,true);
        }

        if(!PingServer.performPing(context)){
            Settingshandler.setServerState(context,false);
            return false;
        }else{
            Settingshandler.setServerState(context,true);
        }

        if(PingGcm.performPing(context)){
            Settingshandler.setGcmState(context, true);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean handleEvent(Context context) {
        Settingshandler.setGcmState(context, false);
        Timber.e("in handleEvent");
	    EventHandler.calculateApplicationEvent(context);
        return false;
    }

    public static final Parcelable.Creator<Monitor> CREATOR
            = new Parcelable.Creator<Monitor>() {
        public Monitor createFromParcel(Parcel in) {
            return new MonitorGcmCheck();
        }

        public Monitor[] newArray(int size) {
            return new MonitorGcmCheck[size];
        }
    };
}
