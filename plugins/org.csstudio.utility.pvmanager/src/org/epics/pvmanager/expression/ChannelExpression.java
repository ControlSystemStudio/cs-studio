/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.Arrays;
import org.epics.pvmanager.ValueCacheImpl;
import org.epics.pvmanager.WriteCache;

/**
 * Represents a channel, which can be both read or written.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class ChannelExpression<R, W> extends SourceRateReadWriteExpressionImpl<R, W> {

    /**
     * An expression for a channel with the given name, which is expected to
     * provide a read payload of {@code readClass} and accept a write payload
     * of {@code writeClass}.
     * 
     * @param channelName the name of the channel
     * @param readClass type of the read payload
     * @param writeClass type of the write payload
     */
    public ChannelExpression(String channelName, Class<R> readClass, Class<W> writeClass) {
        super(new SourceRateExpressionImpl<R>(channelName, readClass), new WriteExpressionImpl<W>(channelName));
        if (channelName == null) {
            throw new NullPointerException("Channel name can't be null");
        }
    }
    
    /**
     * Constructor for the null channel.
     * 
     * @param readClass type of the read payload
     * @param writeClass type of the write payload
     */
    public ChannelExpression(Class<R> readClass, Class<W> writeClass) {
        super(new SourceRateExpressionImpl<R>(new SourceRateExpressionListImpl<Object>(), new ValueCacheImpl<R>(readClass), "null"),
                new WriteExpressionImpl<W>(new WriteExpressionListImpl<Object>(), new WriteCache<W>(), "null"));
    }
    
    /**
     * For writes only, marks that this channel should be written only after the
     * given channels.
     * 
     * @param channelNames preceding channel names
     * @return this
     */
    public ChannelExpression<R, W> after(String... channelNames) {
        WriteCache<W> cache = (WriteCache<W>) getWriteFunction();
        if (!cache.getPrecedingChannels().isEmpty()) {
            throw new IllegalArgumentException("Preceding channels were already set to " + cache.getPrecedingChannels());
        }
        cache.setPrecedingChannels(Arrays.asList(channelNames));
        return this;
    }
    
    
}
