package marki.at.Client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by marki on 14.11.13.
 */
public class Message {
    public final String id;
    public final String message;
    public final long date;
	public transient final String dateString;

    public Message(String id, String message, long date) {
        this.id = id;
        this.message = message;
        this.date = date;
	    this.dateString = getDate(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) {
            return false;
        }else{
	        return true;
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getDate(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMANY);
        Date resultTime = new Date(date);

        return sdf.format(resultTime);
    }
}
