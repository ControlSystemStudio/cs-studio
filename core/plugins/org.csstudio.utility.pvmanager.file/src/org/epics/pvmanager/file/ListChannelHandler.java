/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.vtype.VType;
import org.epics.vtype.io.TextIO;

/**
 * Implementation for file channel that reads a list
 *
 * @author carcassi
 */
class ListChannelHandler extends FileChannelHandler {

    ListChannelHandler(FileDataSource dataSource, String channelName, File file) {
        super(dataSource, channelName, file);
    }
    
    @Override
    protected Object readValueFromFile(File file) throws Exception {
        return TextIO.readList(new FileReader(file));
    }

    @Override
    protected boolean isWriteConnected(File payload) {
        return payload != null && payload.canWrite();
    }

    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        File file = getConnectionPayload();
        if (file == null) {
            callback.channelWritten(new RuntimeException("Channel is closed"));
        }
        
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            TextIO.writeList((VType) newValue, out);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }

}
