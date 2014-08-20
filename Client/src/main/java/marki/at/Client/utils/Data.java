package marki.at.Client.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import marki.at.Client.database.DatabaseHelper;

/**
 * Created by marki on 30.10.13.
 */
public class Data {
    private static List<Message> messages;
    private static DatabaseHelper databaseHelper;

    public static void sortMessages(Context context) {
        if (messages == null) {
            getMessages(context);
        }
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message entry1, Message entry2) {
                if (entry1.date == entry2.date) {
                    return 0;
                }

                if (entry1.date > entry2.date) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public static void updateMessage(Context context, Message message) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.updateMessage(message);
    }

    public static void addMessage(Context context, Message message) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.createMessage(message);
    }

    public static void deleteMessage(Context context, Message message) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.deleteMessage(message);
        getMessages(context).remove(message);
    }

    public static void deleteAllMessages(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.clearAllMessages();
    }

    public static List<Message> getMessages(Context context) {
        if (messages == null) {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(context);
            }
            messages = databaseHelper.getMessages();
        }
        return messages;
    }

    public static void createMockMessages(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.clearAllMessages();
        List<Message> messages = new ArrayList<Message>();
        messages.add(new Message(UUID.randomUUID().toString(), "If you don't like something, change it. If you can't change it, change your attitude", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "The only thing that interferes with my learning is my education", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "You only live once, but if you do it right, once is enough.", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "A friend is someone who knows all about you and still loves you.", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Always forgive your enemies; nothing annoys them so much.", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Without music, life would be a mistake.", Long.parseLong("1376841600000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Do you want to tell me what's bothering you or would you like to break some more furniture?", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Rumors of my assimilation are greatly exaggerated.", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Spock, I do not know too much about these little Tribbles yet, but there is one thing that I have discovered. I like them … better than I like you.", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Our neural pathways have become accustomed to your sensory input patterns.", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Please, Captain, not in front of the Klingons.", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "It can be argued that a human is ultimately the sum of his experiences.", Long.parseLong("1377446400000")));
        messages.add(new Message(UUID.randomUUID().toString(), "Everybody remember where we parked.", Long.parseLong("1377964800000")));
        messages.add(new Message(UUID.randomUUID().toString(), "The bureaucratic mentality is the only constant in the universe.", Long.parseLong("1377964800000")));

        for (Message message : messages) {
            databaseHelper.createMessage(message);
        }
        Data.messages = messages;
    }

    //LOG ----------------------------------------------
    // -------------------------------------------------

    public static void addLogEntry(Context context, Log logEntry) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        databaseHelper.createLogEntry(logEntry);
    }

    public static List<Log> getLogs(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper.getLogs();
    }
}
