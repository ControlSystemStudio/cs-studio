/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.File;
import java.net.URI;
import java.util.concurrent.Executors;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.vtype.DataTypeSupport;
import org.epics.util.time.TimeDuration;

/**
 * Data source for locally written data. Each instance of this
 * data source will have its own separate channels and values.
 *
 * @author carcassi
 */
public final class FileDataSource extends DataSource {
    private final static FileFormatRegistry register = FileFormatRegistry.getDefault();
    static {
	// Install type support for the types it generates.
	DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public FileDataSource() {
        super(true);
    }
    
    private final FileWatcherService fileWatchService =
            new FileWatcherFileSystemService(Executors.newSingleThreadScheduledExecutor(org.epics.pvmanager.util.Executors.namedPool("diirt - file watch")),
                    TimeDuration.ofSeconds(1.0));

    FileWatcherService getFileWatchService() {
        return fileWatchService;
    }
    
    @Override
    protected ChannelHandler createChannel(String channelName) {	
	if (channelName.contains(".")) {
	    String fileExt = channelName.substring(
		    channelName.lastIndexOf('.') + 1, channelName.length());
	    if (register.contains(fileExt)) {
		return new FileChannelHandler(this, channelName, new File(
			URI.create("file://" + channelName)),
			register.getFileFormatFor(fileExt));
	    }
	}
	return new FileChannelHandler(this, channelName, new File(
		URI.create("file://" + channelName)), new CSVFileFormat());
    }
    
}
