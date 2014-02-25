/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import org.epics.pvmanager.DataSourceTypeSupport;
import org.epics.pvmanager.ValueCache;

/**
 * 
 * Given a set of {@link PVATypeAdapter} prepares type support for the 
 * JCA data source.
 *
 * @author carcassi
 */
public class PVATypeSupport extends DataSourceTypeSupport {
    
    private final PVATypeAdapterSet adapters;

    /**
     * A new type support for the pva type support.
     * 
     * @param adapters a set of pva adapters
     */
    public PVATypeSupport(PVATypeAdapterSet adapters) {
        this.adapters = adapters;
    }
    
    /**
     * Returns a matching type adapter for the given
     * cache and channel.
     * 
     * @param cache the cache that will store the data
     * @param channel the pva channel
     * @return the matched type adapter
     */
    protected PVATypeAdapter find(ValueCache<?> cache, PVAChannelHandler channel) {
        return find(adapters.getAdapters(), cache, channel);
    }
    
}
