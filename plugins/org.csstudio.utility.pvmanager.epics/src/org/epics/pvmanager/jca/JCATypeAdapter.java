/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import org.epics.pvmanager.DataSourceTypeAdapter;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.epics.pvmanager.ValueCache;

/**
 * Type adapter for JCA data source. Will match a channel based on the value
 * type provided and the array flag. Will match the cache based on the type class.
 *
 * @author carcassi
 */
public abstract class JCATypeAdapter implements DataSourceTypeAdapter<JCAConnectionPayload, JCAMessagePayload> {

    private final Class<?> typeClass;
    private final DBRType epicsValueType;
    private final DBRType epicsMetaType;
    private final Boolean array;

    /**
     * Creates a new type adapter.
     * 
     * @param typeClass the java type this adapter will create
     * @param epicsValueType the epics type used for the monitor
     * @param epicsMetaType the epics type for the get at connection time; null if no metadata is needed
     * @param array true whether this will require an array type
     */
    public JCATypeAdapter(Class<?> typeClass, DBRType epicsValueType, DBRType epicsMetaType, Boolean array) {
        this.typeClass = typeClass;
        this.epicsValueType = epicsValueType;
        this.epicsMetaType = epicsMetaType;
        this.array = array;
    }

    @Override
    public int match(ValueCache<?> cache, JCAConnectionPayload connPayload) {
        Channel channel = connPayload.getChannel();
        
        // If the generated type can't be put in the cache, no match
        if (!cache.getType().isAssignableFrom(typeClass))
            return 0;
        
        // If the type of the channel does not match, no match
        if (!dbrTypeMatch(epicsValueType, connPayload.getFieldType()))
            return 0;
        
        // If processes array, but count is 1, no match
        if (array != null &&array && channel.getElementCount() == 1)
            return 0;
        
        // If processes scalar, but the count is not 1, no match
        if (array != null && !array && channel.getElementCount() != 1)
            return 0;
        
        // Everything matches
        return 1;
    }
    
    private static boolean dbrTypeMatch(DBRType aType, DBRType anotherType) {
        if (aType.getClass() == null && anotherType.getClass() != null) {
            return false;
        }
        if (aType.getClass() != null && anotherType.getClass() == null) {
            return false;
        }
        return aType.isBYTE() && anotherType.isBYTE() ||
                aType.isDOUBLE() && anotherType.isDOUBLE() ||
                aType.isENUM() && anotherType.isENUM() ||
                aType.isFLOAT() && anotherType.isFLOAT() ||
                aType.isINT() && anotherType.isINT() ||
                aType.isSHORT() && anotherType.isSHORT() ||
                aType.isSTRING() && anotherType.isSTRING();
    }

    @Override
    public Object getSubscriptionParameter(ValueCache cache, JCAConnectionPayload connPayload) {
        throw new UnsupportedOperationException("Not implemented: JCAChannelHandler is multiplexed, will not use this method");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean updateCache(ValueCache cache, JCAConnectionPayload connPayload, JCAMessagePayload message) {
        Channel channel = connPayload.getChannel();
        
        // If metadata is required and not present, no update
        if (epicsMetaType != null && message.getMetadata() == null)
            return false;
        
        // If value is not present, no update
        if (message.getEvent() == null)
            return false;
        
        Object value = createValue(message.getEvent().getDBR(), message.getMetadata(), connPayload);
        cache.writeValue(value);
        return true;
    }

    /**
     * Given the value and the (optional) metadata, will create the new value.
     * 
     * @param value the value taken from the monitor
     * @param metadata the value taken as metadata
     * @param connPayload the connection payload
     * @return the new value
     */
    public abstract Object createValue(DBR value, DBR metadata, JCAConnectionPayload connPayload);
    
}
