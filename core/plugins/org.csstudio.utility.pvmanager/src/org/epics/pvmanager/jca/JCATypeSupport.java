/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.Channel;
import org.epics.pvmanager.DataSourceTypeSupport;
import org.epics.pvmanager.ValueCache;

/**
 * 
 * Given a set of {@link JCATypeAdapter} prepares type support for the 
 * JCA data source.
 *
 * @author carcassi
 */
public class JCATypeSupport extends DataSourceTypeSupport {
    
    private final JCATypeAdapterSet adapters;

    /**
     * A new type support for the jca type support.
     * 
     * @param adapters a set of jca adapters
     */
    public JCATypeSupport(JCATypeAdapterSet adapters) {
        this.adapters = adapters;
    }
    
    /**
     * Returns a matching type adapter for the given
     * cache and channel.
     * 
     * @param cache the cache that will store the data
     * @param channel the jca channel
     * @return the matched type adapter
     */
    protected JCATypeAdapter find(ValueCache<?> cache, Channel channel) {
        return find(adapters.getAdapters(), cache, channel);
    }
    
}
