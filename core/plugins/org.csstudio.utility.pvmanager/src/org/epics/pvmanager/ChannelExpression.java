/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.Arrays;

/**
 * Represents a channel, which can be both read or written.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class ChannelExpression<R, W> extends ReadWriteExpression<R, W> {

    ChannelExpression(String channelName, Class<R> readClass, Class<W> writeClass) {
        super(new SourceRateExpressionImpl<R>(channelName, readClass), new WriteExpressionImpl<W>(channelName));
        if (channelName == null) {
            throw new NullPointerException("Channel name can't be null");
        }
    }
    
    /**
     * For writes only, marks that this channel should be written only after the
     * given channels.
     * 
     * @param channelNames
     * @return
     */
    public ChannelExpression<R, W> after(String... channelNames) {
        WriteCache<W> cache = (WriteCache<W>) getWriteExpressionImpl().getWriteFunction();
        if (!cache.getPrecedingChannels().isEmpty()) {
            throw new IllegalArgumentException("Preceding channels were already set to " + cache.getPrecedingChannels());
        }
        cache.setPrecedingChannels(Arrays.asList(channelNames));
        return this;
    }
    
    
}
