/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms;

import org.epics.pvmanager.DataSourceTypeSupport;
import org.epics.pvmanager.ValueCache;

/**
 *
 * Given a set of {@link PVATypeAdapter} prepares type support for the JCA data
 * source.
 *
 * @author carcassi
 */
public class JMSTypeSupport extends DataSourceTypeSupport {

    private final JMSTypeAdapterSet adapters;

    /**
     * A new type support for the pva type support.
     *
     * @param adapters a set of pva adapters
     */
    public JMSTypeSupport(JMSTypeAdapterSet adapters) {
        this.adapters = adapters;
    }

    /**
     * Returns a matching type adapter for the given cache and channel.
     *
     * @param cache the cache that will store the data
     * @param channel the pva channel
     * @return the matched type adapter
     */
    protected JMSTypeAdapter find(ValueCache<?> cache, JMSChannelHandler channel) {
        return find(adapters.getAdapters(), cache, channel);
    }
}
