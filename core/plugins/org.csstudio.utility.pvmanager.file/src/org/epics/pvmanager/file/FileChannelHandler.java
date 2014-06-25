/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.vtype.VTable;
import org.epics.vtype.io.CSVIO;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class FileChannelHandler extends MultiplexedChannelHandler<File, Object> {
    
    private final File file;
    private final FileDataSource dataSource;
    private final Runnable updateTask = new Runnable() {

        @Override
        public void run() {
            update();
        }
    };
        
    private final FileFormat format;

    FileChannelHandler(FileDataSource dataSource, String channelName, File file, FileFormat format) {
        super(channelName);
        this.file = file;
        this.dataSource = dataSource;
        this.format = format;
    }
    
    @Override
    public void connect() {
        processConnection(file);
        update();
        dataSource.getFileWatchService().addWatcher(file, updateTask);
    }
    
    private void update() {
        try {
            Object value = readValueFromFile(file);
            processMessage(value);
        } catch (Exception ex) {
            reportExceptionToAllReadersAndWriters(ex);
        }
    }
    
    protected Object readValueFromFile(File file) {
	try {	    
	    FileInputStream in = new FileInputStream(file);
	    Object value = format.readValue(in);
	    in.close();
	    return value;
	} catch (Exception e) {
	    reportExceptionToAllReadersAndWriters(e);
	}
	return null;
    }

    @Override
    public void disconnect() {
        dataSource.getFileWatchService().removeWatcher(file, updateTask);
        processConnection(null);
    }

    @Override
    protected boolean isConnected(File payload) {
        return payload != null && payload.exists() && payload.isFile();
    }

    @Override
    protected boolean isWriteConnected(File payload) {
        return isConnected(payload) && format.isWriteSupported();
    }

    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        File file = getConnectionPayload();
        if (file == null) {
            callback.channelWritten(new RuntimeException("Channel is closed"));
        }
        
        try (OutputStream out = new FileOutputStream(file)) {
            format.writeValue(newValue, out);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
