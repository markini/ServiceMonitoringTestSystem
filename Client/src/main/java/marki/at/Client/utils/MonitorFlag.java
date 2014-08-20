package marki.at.Client.utils;

/**
 * Created by marki on 18.11.13.
 */
public class MonitorFlag {
    private final String name;

    private MonitorFlag(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static final MonitorFlag OK = new MonitorFlag("ok");
    public static final MonitorFlag SERVER_DOWN = new MonitorFlag("server down");
    public static final MonitorFlag GCM_DOWN = new MonitorFlag("gcm down");
}
