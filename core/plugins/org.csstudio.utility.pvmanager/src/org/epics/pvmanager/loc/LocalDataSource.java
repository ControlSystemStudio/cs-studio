/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import java.util.List;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.data.DataTypeSupport;
import org.epics.pvmanager.util.FunctionParser;

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
        List<Object> parsedTokens = FunctionParser.parsePvAndArguments(channelName);
        if (parsedTokens == null || parsedTokens.size() > 2)
            throw new IllegalArgumentException(CHANNEL_SYNTAX_ERROR_MESSAGE);
        
        // If the channel was already created, return it (no matter what the argument is)
        ChannelHandler handler = getChannels().get(parsedTokens.get(0).toString());
        if (handler != null)
            return handler;
        
        if (parsedTokens.size() == 1) {
            return new LocalChannelHandler(parsedTokens.get(0).toString(), 0.0);
        } else {
            return new LocalChannelHandler(parsedTokens.get(0).toString(), parsedTokens.get(1));
        }
    }

}
