/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ChannelHandlerReadSubscription;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ChannelHandlerWriteSubscription;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.*;
import static org.epics.vtype.ValueFactory.*;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.io.CSVIO;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class FileChannelHandler extends MultiplexedChannelHandler<File, Object> {
    
    private File file;

    FileChannelHandler(String channelName, File file) {
        super(channelName);
        this.file = file;
    }
    
    private CSVIO io = new CSVIO();

    @Override
    public void connect() {
        processConnection(file);
        try {
            Object value = readValueFromFile(file);
            processMessage(value);
        } catch (Exception ex) {
            reportExceptionToAllReadersAndWriters(ex);
        }
    }
    
    protected Object readValueFromFile(File file) throws Exception {
        FileReader fileReader = new FileReader(file);
        VTable value = io.importVTable(fileReader);
        return value;
    }

    @Override
    public void disconnect() {
        processConnection(null);
    }

    @Override
    protected boolean isConnected(File payload) {
        return payload.exists() && payload.isFile();
    }

    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
