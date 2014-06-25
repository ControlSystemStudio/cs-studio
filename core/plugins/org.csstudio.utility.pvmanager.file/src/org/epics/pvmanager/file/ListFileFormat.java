/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.epics.vtype.VType;
import org.epics.vtype.io.TextIO;

/**
 * A file format for reading and writing lists from a .list file
 * 
 * @author Kunal Shroff
 *
 */
public class ListFileFormat implements FileFormat  {

    @Override
    public Object readValue(InputStream in) throws Exception {
	return TextIO.readList(new InputStreamReader(in));
    }

    @Override
    public void writeValue(Object value, OutputStream out) throws Exception {
	TextIO.writeList((VType) value, new OutputStreamWriter(out));
    }

    @Override
    public boolean isWriteSupported() {
	return true;
    }
}
