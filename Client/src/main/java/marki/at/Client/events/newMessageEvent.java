package marki.at.Client.events;

import marki.at.Client.utils.Message;

/**
 * Created by marki on 29.10.13.
 */
public class newMessageEvent {

    public final Message message;

    public newMessageEvent(Message message) {
        this.message = message;
    }
}
