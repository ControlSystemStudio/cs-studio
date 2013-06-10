/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    DelayedConnectionChannelHandler(String channelName) {
        super(channelName);
        String errorMessage = "Incorrect syntax. Must match delayedConnectionChannel(delayInSeconds, value)";
        List<Object> tokens = FunctionParser.parseFunctionAnyParameter(channelName);
        if (tokens == null || tokens.size() <= 1) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (tokens.size() == 2) {
            initialValue = "Initial Value";
        } else {
            Object value = FunctionParser.asScalarOrList(tokens.subList(2, tokens.size()));
            if (value == null) {
                throw new IllegalArgumentException(errorMessage);
            }
            initialValue = ValueFactory.wrapValue(value);
        }
        delayInSeconds = (Double) tokens.get(1);
    }

    @Override
    public void connect() {
        try {
            Thread.sleep((long) (delayInSeconds * 1000));
        } catch(Exception ex) {
        }
        
        processConnection(new Object());
        processMessage(initialValue);
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
