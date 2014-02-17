/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Structure;
import org.epics.pvmanager.DataSourceTypeAdapter;
import org.epics.pvmanager.ValueCache;

/**
 * Type adapter for PVA data source. Will match a channel based on the value
 * type provided and the array flag. Will match the cache based on the type class.
 *
 * @author msekoranja
 */
public abstract class PVATypeAdapter implements DataSourceTypeAdapter<PVAChannelHandler, PVStructure> {

	// e.g. VDouble.class
    private final Class<?> typeClass;
    
    // PVStructure requirements
    private final String[] ntIds;
    private final Field[] valueFieldTypes;

    /**
     * Creates a new type adapter.
     * 
     * @param typeClass the java type this adapter will create
     * @param ntIds array of IDs this adapter is able convert, <code>null</code> allowed
     */
    public PVATypeAdapter(Class<?> typeClass, String[] ntIds) {
    	this(typeClass, ntIds, (Field[])null);
    }

    /**
     * Creates a new type adapter.
     * 
     * @param typeClass the java type this adapter will create
     * @param ntIds array of IDs this adapter is able convert, <code>null</code> allowed
     * @param fieldType <code>Field</code> instance this adapter is able convert
     */
    public PVATypeAdapter(Class<?> typeClass, String[] ntIds, Field fieldType) {
    	this(typeClass, ntIds, new Field[] { fieldType });
    }

    /**
     * Creates a new type adapter.
     * 
     * @param typeClass the java type this adapter will create
     * @param ntIds array of IDs this adapter is able convert, <code>null</code> allowed
     * @param fieldTypes <code>Field</code> instances this adapter is able convert, <code>null</code> allowed
     */
    public PVATypeAdapter(Class<?> typeClass, String[] ntIds, Field[] fieldTypes) {
        this.typeClass = typeClass;
        this.ntIds = ntIds;
        this.valueFieldTypes = fieldTypes;
    }

    @Override
    public int match(ValueCache<?> cache, PVAChannelHandler channel) {

    	// If the generated type can't be put in the cache, no match
        if (!cache.getType().isAssignableFrom(typeClass))
            return 0;

        // If one of the IDs does not match, no match
        if (ntIds != null)
        {
        	boolean match = false;
        	String ntId = channel.getChannelType().getID();
        	// TODO "structure" ID ??
        	for (String id : ntIds)
        		if (ntId.equals(id))
        		{
        			match = true;
        			break;
        		}
        	
        	if (!match)
        		return 0;
        }
        
        // If the type of the channel does not match, no match
        if (valueFieldTypes != null)
        {
        	boolean match = false;
        	// we assume Structure here
        	Field channelValueType = ((Structure)channel.getChannelType()).getField("value");
        	if (channelValueType != null)
    		{
            	for (Field vf : valueFieldTypes)
            		if (channelValueType.equals(vf))
            		{
            			match = true;
            			break;
            		}
            	
            	if (!match)
            		return 0;
    		}
        }

        // Everything matches
        return 1;
    }
    
    @Override
    public Object getSubscriptionParameter(ValueCache<?> cache, PVAChannelHandler channel) {
        throw new UnsupportedOperationException("Not implemented: PVAChannelHandler is multiplexed, will not use this method");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean updateCache(ValueCache cache, PVAChannelHandler channel, PVStructure message) {
        Object value = createValue(message, channel.getChannelType(), !channel.isConnected());
        cache.writeValue(value);
        return true;
    }

    /**
     * Given the value create the new value.
     * 
     * @param message the value taken from the monitor
     * @param valueType the value introspection data
     * @param disconnected true if the value should report the channel is currently disconnected
     * @return the new value
     */
    public abstract Object createValue(PVStructure message, Field valueType, boolean disconnected);
}
