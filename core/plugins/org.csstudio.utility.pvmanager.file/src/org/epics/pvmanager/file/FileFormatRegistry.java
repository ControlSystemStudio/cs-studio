/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Place to registers file formats so that the file datasource can use them.
 *
 * @author carcassi
 */
public class FileFormatRegistry {
    
    private final static FileFormatRegistry registry = new FileFormatRegistry();
    
    private static final Map<String, FileFormat> fileFormatRegistry = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(FileFormatRegistry.class.getName());
    
    /**
     * The default registry. This registry is the one used by the framework
     * to look for currently supported file format.
     * 
     * @return the default registry; not null
     */
    public static FileFormatRegistry getDefault() {
        return registry;
    }
    
    static {
        // Find file formats to register using the ServiceLoader
        ServiceLoader<FileFormat> sl = ServiceLoader.load(FileFormat.class);
        for (FileFormat fileFormat : sl) {
            registry.registerFileFormat(fileFormat);
        }
    }
    
    /**
     * Register a new FileFormat for a given file extension
     *  
     * @param extension the file extension
     * @param format the FileFormat
     */
    public void registerFileFormat(String extension, FileFormat format) {
        log.log(Level.FINE, "File datasource: registering extension {0}", extension);
	fileFormatRegistry.put(extension, format);
    }
    
    /**
     * Register a new FileFormat for the extensions declared by the format
     * itself.
     *  
     * @param format the FileFormat
     */
    public void registerFileFormat(FileFormat format) {
        for (String extension : format.getFileExtensions()) {
            registerFileFormat(extension, format);
        }
    }
    
    /**
     * Find the registered FileFormat for the given file extension
     * 
     * @param extension the file extension to register the file format for
     * @return the FileFormat registered for this extension
     */
    public FileFormat getFileFormatFor(String extension) {
        return fileFormatRegistry.get(extension);
    }

    /**
     * Returns true if there is a FileFormat registered for the given file extension
     * 
     * @param extension the file extension to register the file format for
     * @return true if there is a FileFormat registered for this file extension
     */
    public boolean contains(String extension) {
	return fileFormatRegistry.containsKey(extension);
    }
}
