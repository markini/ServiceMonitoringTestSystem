package marki.at.Client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {
    public final String id;
    public String logMessage;
    public final long date;

    public Log(String id, String logMessage, long date) {
        this.id = id;
        this.logMessage = logMessage;
        this.date = date;
    }

    public String getDate(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY);
        Date resultTime = new Date(date);

        return sdf.format(resultTime);
    }
}
