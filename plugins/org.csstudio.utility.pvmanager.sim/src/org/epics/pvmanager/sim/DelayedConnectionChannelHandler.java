/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.util.FunctionParser;
import org.epics.vtype.ValueFactory;

/**
 *
 * @author carcassi
 */
class DelayedConnectionChannelHandler extends MultiplexedChannelHandler<Object, Object> {
    
    private final Object initialValue;
    private final double delayInSeconds;
    private final ScheduledExecutorService exec;

    DelayedConnectionChannelHandler(String channelName, ScheduledExecutorService exec) {
        super(channelName);
        String errorMessage = "Incorrect syntax. Must match delayedConnectionChannel(delayInSeconds, value)";
        List<Object> tokens = FunctionParser.parseFunctionAnyParameter(channelName);
        if (tokens == null || tokens.size() <= 1) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (tokens.size() == 2) {
            initialValue = "Initial value";
        } else {
            Object value = FunctionParser.asScalarOrList(tokens.subList(2, tokens.size()));
            if (value == null) {
                throw new IllegalArgumentException(errorMessage);
            }
            initialValue = ValueFactory.wrapValue(value);
        }
        delayInSeconds = (Double) tokens.get(1);
        this.exec = exec;
    }

    @Override
    public void connect() {
        exec.schedule(new Runnable() {

            @Override
            public void run() {
                synchronized(DelayedConnectionChannelHandler.this) {
                    if (getUsageCounter() > 0) {
                        processConnection(new Object());
                        processMessage(initialValue);
                    }
                }
            }
        }, (long) delayInSeconds * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disconnect() {
        processConnection(null);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> result = new HashMap<>();
        result.put("delayInSeconds", delayInSeconds);
        result.put("initialValue", initialValue);
        return result;
    }
    
    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        throw new UnsupportedOperationException("Can't write to simulation channel.");
    }
    
}
