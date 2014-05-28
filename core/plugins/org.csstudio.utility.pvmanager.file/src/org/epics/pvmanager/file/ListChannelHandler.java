/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.File;
import java.io.FileReader;
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

}
