package edu.msu.nscl.olog.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.msu.nscl.olog.api.OlogClient;

public class OlogClientManager {

	public static final String DEFAULT_CLIENT = "default_client";

	private final static Map<String, OlogClient> ologClients = new ConcurrentHashMap<String, OlogClient>();

	private OlogClientManager() {

	}

	public static void registerDefaultClient(OlogClient client) {
		OlogClientManager.registerClient(DEFAULT_CLIENT, client);
	}

	static void registerClient(String name, OlogClient client) {
		OlogClientManager.ologClients.put(name, client);
	}

	/**
	 * Returns the default {@link OlogClient}.
	 * @return
	 */
	public static OlogClient getClient() {
		return ologClients.get(DEFAULT_CLIENT);
	}

	/**
	 * Returns a {@link OlogClient} registered with the name <tt>name</tt>
	 * @param name
	 * @return
	 */
	public static OlogClient getClient(String name) {
		return ologClients.get(name);
	}
}
