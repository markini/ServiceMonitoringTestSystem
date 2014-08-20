package marki.at.Client.monitoring;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marki on 26.12.13.
 */
public class ApplicationState {
	private final String name;

	private ApplicationState(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public static final ApplicationState TYPE_DEFAULT = new ApplicationState("Default");
	public static final ApplicationState TYPE_HTTP = new ApplicationState("Http");
	public static final ApplicationState TYPE_SMS = new ApplicationState("SMS");

	public static Map<String, ApplicationState> VALUES = new HashMap<String, ApplicationState>() {
		{
			put(TYPE_DEFAULT.getName(), TYPE_DEFAULT);
			put(TYPE_HTTP.getName(), TYPE_HTTP);
			put(TYPE_SMS.getName(), TYPE_SMS);
		}

		;
	};
}
