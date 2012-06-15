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
 * @author carcassi
 */
public class JCATypeSupport extends DataSourceTypeSupport {
    
    private final JCATypeAdapterSet adapters;

    public JCATypeSupport(JCATypeAdapterSet adapters) {
        this.adapters = adapters;
    }
    
    protected JCATypeAdapter find(ValueCache<?> cache, Channel channel) {
        return find(adapters.getAdapters(), cache, channel);
    }
    
}
