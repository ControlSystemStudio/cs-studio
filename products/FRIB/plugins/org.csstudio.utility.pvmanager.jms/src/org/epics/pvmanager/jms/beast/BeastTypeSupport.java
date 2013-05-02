/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jms.beast;

import org.epics.pvmanager.jms.beast.*;
import org.epics.pvmanager.DataSourceTypeSupport;
import org.epics.pvmanager.ValueCache;

/**
 *
 * Given a set of {@link PVATypeAdapter} prepares type support for the Beast data
 * source.
 *
 * @author carcassi
 */
public class BeastTypeSupport extends DataSourceTypeSupport {

    private final BeastTypeAdapterSet adapters;

    /**
     * A new type support for the Beast type support.
     *
     * @param adapters a set of Beast adapters
     */
    public BeastTypeSupport(BeastTypeAdapterSet adapters) {
        this.adapters = adapters;
    }

    /**
     * Returns a matching type adapter for the given cache and channel.
     *
     * @param cache the cache that will store the data
     * @param beastChannelHandler the Beast channel
     * @return the matched type adapter
     */
    protected BeastTypeAdapter find(ValueCache<?> cache, BeastChannelHandler beastChannelHandler) {
        return find(adapters.getAdapters(), cache, beastChannelHandler);
    }
}
