package marki.at.servicemonitoring;

import android.content.Context;
import android.os.Parcel;

/**
 * Created by marki on 02.01.14.
 */
public class MonitorExecutorService1 extends Monitor {

    @Override
    protected boolean observeThis(Context context) {
        System.out.println("OBSERVE THIS - MonitorExecutorService1");
        TestNonStickyMonitors.observeThisCounter++;
        return true;
    }

    @Override
    protected boolean handleEvent(Context context) {
        System.out.println("HANDLE EVENT - MonitorExecutorService1");
        TestNonStickyMonitors.handleEventCounter++;
        return true;
    }

    public static final Creator<Monitor> CREATOR
            = new Creator<Monitor>() {
        public Monitor createFromParcel(Parcel in) {
            return new MonitorExecutorService1();
        }

        public Monitor[] newArray(int size) {
            return new MonitorExecutorService1[size];
        }
    };
}

