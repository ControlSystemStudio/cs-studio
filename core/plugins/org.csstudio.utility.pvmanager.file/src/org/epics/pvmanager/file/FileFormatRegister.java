/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

/**
 *
 * @author carcassi
 */
public class FileFormatRegister {
    
    private final static FileFormatRegister registry = new FileFormatRegister();
    
    public static FileFormatRegister getDefault() {
        return registry;
    }
    
    public void registerFileFormat(String extension, FileFormat format) {
        // TODO fill in!
    }
    
    public FileFormat getFileFormatFor(String extension) {
        // TODO fill in!
        return null;
    }
}
