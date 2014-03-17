/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.Collection;

/**
 * Represents a list of channel, which can be both read or written.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class ChannelExpressionList<R, W> extends SourceRateReadWriteExpressionListImpl<R, W> {

    /**
     * An expression for a list of channels with the given names, which are expected to
     * provide a read payload of {@code readClass} and accept a write payload
     * of {@code writeClass}.
     * 
     * @param readClass type of the read payload
     * @param writeClass type of the write payload
     * @param channelNames the names of the channels
     */
    public ChannelExpressionList(Collection<String> channelNames, Class<R> readClass, Class<W> writeClass) {
        for (String channelName : channelNames) {
            if (channelName == null) {
                and(new ChannelExpression<R, W>(readClass, writeClass));
            } else {
                and(new ChannelExpression<R, W>(channelName, readClass, writeClass));
            }
        }
    }
    
    /**
     * For writes only, marks that these channels should be written only after the
     * given channels.
     * 
     * @param channelNames preceding channel names
     * @return this
     */
    public ChannelExpressionList<R, W> after(String... channelNames) {
        for (SourceRateReadWriteExpression<R, W> expression : getSourceRateReadWriteExpressions()) {
            ChannelExpression<R, W> channel = (ChannelExpression<R, W>) expression;
            channel.after(channelNames);
        }
        return this;
    }
    
    
}
