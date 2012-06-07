/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.jca;

import org.epics.pvmanager.DataSourceTypeAdapter;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.epics.pvmanager.ValueCache;

/**
 *
 * @author carcassi
 */
public abstract class JCATypeAdapter implements DataSourceTypeAdapter<Channel, JCAMessagePayload> {

    private final Class<?> typeClass;
    private final DBRType epicsValueType;
    private final DBRType epicsMetaType;
    private final boolean array;

    public JCATypeAdapter(Class<?> typeClass, DBRType epicsValueType, DBRType epicsMetaType, boolean array) {
        this.typeClass = typeClass;
        this.epicsValueType = epicsValueType;
        this.epicsMetaType = epicsMetaType;
        this.array = array;
    }

    @Override
    public int match(ValueCache<?> cache, Channel channel) {
        
        // If the generated type can't be put in the cache, no match
        if (!cache.getType().isAssignableFrom(typeClass))
            return 0;
        
        // If the type of the channel does not match, no match
        if (!dbrTypeMatch(epicsValueType, channel.getFieldType()))
            return 0;
        
        // If processes array, but count is 1, no match
        if (array && channel.getElementCount() == 1)
            return 0;
        
        // If processes scalar, but the count is not 1, no match
        if (!array && channel.getElementCount() != 1)
            return 0;
        
        // Everything matches
        return 1;
    }
    
    private static boolean dbrTypeMatch(DBRType aType, DBRType anotherType) {
        return aType.isBYTE() && anotherType.isBYTE() ||
                aType.isDOUBLE() && anotherType.isDOUBLE() ||
                aType.isENUM() && anotherType.isENUM() ||
                aType.isFLOAT() && anotherType.isFLOAT() ||
                aType.isINT() && anotherType.isINT() ||
                aType.isSHORT() && anotherType.isSHORT() ||
                aType.isSTRING() && anotherType.isSTRING();
    }

    @Override
    public JCASubscriptionParameters getSubscriptionParameter(ValueCache cache, Channel channel) {
        int count = 1;
        if (array)
            count = channel.getElementCount();
        return new JCASubscriptionParameters(epicsValueType, epicsMetaType, count);
    }

    @Override
    public boolean updateCache(ValueCache cache, Channel channel, JCAMessagePayload message) {
        // If metadata is required and not present, no update
        if (epicsMetaType != null && message.getMetadata() == null)
            return false;
        
        // If value is not present, no update
        if (message.getEvent() == null)
            return false;
        
        Object value = createValue(message.getEvent().getDBR(), message.getMetadata(), !JCAChannelHandler.isConnected(channel));
        cache.setValue(value);
        return true;
    }
    
    public abstract Object createValue(DBR value, DBR metadata, boolean disconnected);
    
}
