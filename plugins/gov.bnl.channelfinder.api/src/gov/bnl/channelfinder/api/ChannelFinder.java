/**
 * Copyright (C) 2010-2012 Brookhaven National Laboratory
 * Copyright (C) 2010-2012 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms.
 */
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
     * 
     * @return returns the default {@link ChannelFinderClient}.
     */
    public static ChannelFinderClient getClient() {
        if (client == null) {
            ChannelFinder.client = serviceURL().withHTTPAuthentication(false).create();
        }
        return client;
    }

}
