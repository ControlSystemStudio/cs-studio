/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
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
public class LocalDataSource extends DataSource {

    private final boolean zeroInitialization;

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public LocalDataSource() {
        this(false);
    }
    
    /**
     * Zero initialization is deprecated. Will be removed in a future release.
     * 
     * @param zeroInitialization whether to initialize variable to 0
     * @deprecated do not use zero initialization of local variable: does not work for non numeric variables
     */
    @Deprecated
    public LocalDataSource(boolean zeroInitialization) {
        super(true);
        this.zeroInitialization = zeroInitialization;
    }

    private final String CHANNEL_SYNTAX_ERROR_MESSAGE = 
            "Syntax for local channel must be either name, name(Double) or name(String) (e.g \"foo\", \"foo(2.0)\" or \"foo(\"bar\")";
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        // Parse the channel name
        List<Object> parsedTokens = parseName(channelName);
        
        LocalChannelHandler channel = new LocalChannelHandler(parsedTokens.get(0).toString());
        return channel;
    }
    
    private List<Object> parseName(String channelName) {
        List<Object> tokens = FunctionParser.parseFunctionWithScalarOrArrayArguments(".+", channelName, CHANNEL_SYNTAX_ERROR_MESSAGE);
        String nameAndType = tokens.get(0).toString();
        String name = nameAndType;
        String type = null;
        int index = nameAndType.lastIndexOf('<');
        if (nameAndType.endsWith(">") && index != -1) {
            name = nameAndType.substring(0, index);
            type = nameAndType.substring(index + 1, nameAndType.length() - 1);
        }
        List<Object> newTokens = new ArrayList<>();
        newTokens.add(name);
        newTokens.add(type);
        if (tokens.size() > 1) {
            newTokens.addAll(tokens.subList(1, tokens.size()));
        }
        return newTokens;
    }

    @Override
    protected String channelHandlerLookupName(String channelName) {
        List<Object> parsedTokens = parseName(channelName);
        return parsedTokens.get(0).toString();
    }
    
    private void initialize(String channelName) {
        List<Object> parsedTokens = parseName(channelName);

        LocalChannelHandler channel = (LocalChannelHandler) getChannels().get(channelHandlerLookupName(channelName));
        channel.setType((String) parsedTokens.get(1));
        if (parsedTokens.size() > 2) {
            if (channel != null) {
                channel.setInitialValue(parsedTokens.get(2));
            }
        } else if (zeroInitialization) {
            Logger.getLogger(this.getClass().getName()).warning("Using zero initialization for channel " + channelName);
            channel.setInitialValue(0);
        }
    }

    @Override
    public void connectRead(ReadRecipe readRecipe) {
        super.connectRead(readRecipe);
        
        // Initialize all values
        for (ChannelReadRecipe channelReadRecipe : readRecipe.getChannelReadRecipes()) {
            initialize(channelReadRecipe.getChannelName());
        }
    }

    @Override
    public void connectWrite(WriteRecipe writeRecipe) {
        super.connectWrite(writeRecipe);
        
        // Initialize all values
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            initialize(channelWriteRecipe.getChannelName());
        }
    }

}
