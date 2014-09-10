/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A way to read and write an object with a particular file format.
 *
 * @author carcassi
 */
public interface FileFormat {
    
    /**
     * Reads the value from the given stream.
     * 
     * @param in a stream; not null
     * @return the value de-serialized
     * @throws java.lang.Exception any error
     */
    public Object readValue(InputStream in) throws Exception;
    
    /**
     * Write the value to the given stream.
     * 
     * @param value the value to write; not null
     * @param out the output stream; not null
     * @throws java.lang.Exception any error
     */
    public void writeValue(Object value, OutputStream out) throws Exception;
    
    /**
     * Whether writes are supported.
     * 
     * @return true if can serialize objects
     */
    public boolean isWriteSupported();
}
