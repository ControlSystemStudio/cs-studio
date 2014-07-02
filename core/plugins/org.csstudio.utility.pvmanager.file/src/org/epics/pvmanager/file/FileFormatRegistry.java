/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Place to registers file formats so that the file datasource can use them.
 *
 * @author carcassi
 */
public class FileFormatRegistry {
    
    private final static FileFormatRegistry registry = new FileFormatRegistry();
    
    private static final Map<String, FileFormat> fileFormatRegistry = new ConcurrentHashMap<>();
    
    public static FileFormatRegistry getDefault() {
        return registry;
    }
    
    /**
     * Register a new FileFormat for a given file extension
     *  
     * @param extension the file extension
     * @param format the FileFormat
     */
    public void registerFileFormat(String extension, FileFormat format) {
	fileFormatRegistry.put(extension, format);
    }
    
    /**
     * Find the registered FileFormat for the given file extension
     * 
     * @param extension
     * @return the FileFormat registered for this extension
     */
    public FileFormat getFileFormatFor(String extension) {
        return fileFormatRegistry.get(extension);
    }

    /**
     * Returns true if there is a FileFormat registered for the given file extension
     * 
     * @param extension
     * @return true if there is a FileFormat registered for this file extension
     */
    public boolean contains(String extension) {
	return fileFormatRegistry.containsKey(extension);
    }
}
