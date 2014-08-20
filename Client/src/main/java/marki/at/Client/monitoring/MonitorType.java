package marki.at.Client.monitoring;

/**
 * Created by marki on 26.12.13.
 */
public class MonitorType {
	private final String name;

	private MonitorType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public static final MonitorType TYPE_OK = new MonitorType("OK");
	public static final MonitorType TYPE_SERVER = new MonitorType("Server");
	public static final MonitorType TYPE_GCM = new MonitorType("GCM");
	public static final MonitorType TYPE_CONNECTION = new MonitorType("Connection");
}
