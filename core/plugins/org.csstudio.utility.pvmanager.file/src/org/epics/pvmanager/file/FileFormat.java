/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.file;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author carcassi
 */
public interface FileFormat {
    
    /**
     * Reads the value from the given stream.
     * 
     * @param in a stream; not null
     * @return the value de-serialized
     */
    public Object readValue(InputStream in);
    
    /**
     * Write the value to the given stream.
     * 
     * @param value the value to write; not null
     * @param out the output stream; not null
     */
    public void writeValue(Object value, OutputStream out);
    
    /**
     * Whether writes are supported.
     * 
     * @return true if can serialize objects
     */
    public boolean isWriteSupported();
}
