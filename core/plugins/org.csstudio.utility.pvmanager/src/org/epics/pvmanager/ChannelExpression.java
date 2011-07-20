/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Arrays;

/**
 *
 * @author carcassi
 */
public class ChannelExpression<T> extends WriteExpression<T> {

    public ChannelExpression(String channelName) {
        super(channelName);
    }
    
    public WriteExpression<T> after(String... channelNames) {
        ((WriteCache<T>) getWriteFunction()).setPrecedingChannels(Arrays.asList(channelNames));
        return this;
    }
    
    
}
