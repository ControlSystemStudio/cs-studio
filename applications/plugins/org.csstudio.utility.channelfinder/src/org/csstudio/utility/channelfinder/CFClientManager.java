package org.csstudio.utility.channelfinder;

import gov.bnl.channelfinder.api.ChannelFinderClient;

import java.security.Provider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CFClientManager {

	public static final String DEFAULT_CLIENT = "composite_client";

	private final static Map<String, ChannelFinderClient> cfClients = new ConcurrentHashMap<String, ChannelFinderClient>();

	private CFClientManager() {

	}

	static void registerDefaultClient(ChannelFinderClient client) {
		CFClientManager.registerClient(DEFAULT_CLIENT, client);
	}

	static void registerClient(String name, ChannelFinderClient client) {
		CFClientManager.cfClients.put(name, client);
	}

	/**
	 * Returns the default {@link ChannelFinderClient}.
	 * @return
	 */
	public static ChannelFinderClient getClient() {
		return cfClients.get(DEFAULT_CLIENT);
	}

	/**
	 * Returns a {@link ChannelFinderClient} registered with the name <tt>name</tt>
	 * @param name
	 * @return
	 */
	public static ChannelFinderClient getClient(String name) {
		return cfClients.get(name);
	}

}
