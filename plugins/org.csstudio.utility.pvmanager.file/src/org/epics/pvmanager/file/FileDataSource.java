/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.File;
import java.net.URI;
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
public final class FileDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public FileDataSource() {
        super(false);
    }
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        if (channelName.endsWith(".png") || channelName.endsWith(".bmp")) {
            return new ImageChannelHandler(channelName, new File(URI.create("file://" + channelName)));
        }
        return new FileChannelHandler(channelName, new File(URI.create("file://" + channelName)));
    }

}
