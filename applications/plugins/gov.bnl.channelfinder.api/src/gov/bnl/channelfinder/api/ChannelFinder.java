package gov.bnl.channelfinder.api;

import static gov.bnl.channelfinder.api.ChannelFinderClientImpl.CFCBuilder.serviceURL;

public class ChannelFinder {

	public static final String DEFAULT_CLIENT = "composite_client";
	private static volatile ChannelFinderClient client;

	private ChannelFinder() {

	}

	public static void setClient(ChannelFinderClient client) {
		ChannelFinder.client = client;
	}

	/**
	 * Returns the default {@link ChannelFinderClient}.
	 * @return
	 */
	public static ChannelFinderClient getClient() {
		if(client == null){
			ChannelFinder.client = serviceURL().withHTTPAuthentication(false).create();
		}
		return client;
	}

}
