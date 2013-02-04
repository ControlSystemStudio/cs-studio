/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.ChannelReadRecipe;
import org.epics.pvmanager.ChannelWriteRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.ReadRecipe;
import org.epics.pvmanager.WriteRecipe;
import org.epics.pvmanager.vtype.DataTypeSupport;
import org.epics.pvmanager.util.FunctionParser;
import org.epics.util.array.ArrayDouble;

/**
 * Data source for locally written data. Each instance of this
 * data source will have its own separate channels and values.
 *
 * @author carcassi
 */
public final class LocalDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public LocalDataSource() {
        super(true);
    }

    private final String CHANNEL_SYNTAX_ERROR_MESSAGE = 
            "Syntax for local channel must be either name, name(Double) or name(String) (e.g \"foo\", \"foo(2.0)\" or \"foo(\"bar\")";
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        // Parse the channel name
        List<Object> parsedTokens = parseName(channelName);
        
        LocalChannelHandler channel = new LocalChannelHandler(parsedTokens.get(0).toString());
        if (parsedTokens.size() > 1) {
            channel.setInitialValue(parsedTokens.get(1));
        } else {
            channel.setInitialValue(0.0);
        }
        return channel;
    }
    
    private List<Object> parseName(String channelName) {
        // Parse the channel name
        List<Object> parsedTokens = FunctionParser.parsePvAndArguments(channelName);
        if (parsedTokens != null && parsedTokens.size() <= 2) {
            return parsedTokens;
        }
        
        if (parsedTokens != null && parsedTokens.size() > 2 && parsedTokens.get(1) instanceof Double) {
            double[] data = new double[parsedTokens.size() - 1];
            for (int i = 1; i < parsedTokens.size(); i++) {
                Object value = parsedTokens.get(i);
                if (value instanceof Double) {
                    data[i-1] = (Double) value;
                } else {
                    throw new IllegalArgumentException(CHANNEL_SYNTAX_ERROR_MESSAGE);
                }
            }
            return Arrays.asList(parsedTokens.get(0), new ArrayDouble(data));
        }
        
        if (parsedTokens != null && parsedTokens.size() > 2 && parsedTokens.get(1) instanceof String) {
            List<String> data = new ArrayList<>();
            for (int i = 1; i < parsedTokens.size(); i++) {
                Object value = parsedTokens.get(i);
                if (value instanceof String) {
                    data.add((String) value);
                } else {
                    throw new IllegalArgumentException(CHANNEL_SYNTAX_ERROR_MESSAGE);
                }
            }
            return Arrays.asList(parsedTokens.get(0), data);
        }
        
        throw new IllegalArgumentException(CHANNEL_SYNTAX_ERROR_MESSAGE);
    }

    @Override
    protected String channelHandlerLookupName(String channelName) {
        List<Object> parsedTokens = parseName(channelName);
        return parsedTokens.get(0).toString();
    }

}
