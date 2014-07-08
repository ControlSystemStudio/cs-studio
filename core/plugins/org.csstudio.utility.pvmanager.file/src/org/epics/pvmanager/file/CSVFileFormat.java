/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.epics.vtype.VTable;
import org.epics.vtype.io.CSVIO;

/**
 * A FileFormat for reading .csv files into VTables
 * 
 * @author Kunal Shroff
 *
 */
public class CSVFileFormat implements FileFormat {

    private CSVIO io = new CSVIO();
    
    @Override
    public Object readValue(InputStream in) {
	VTable value = io.importVTable(new InputStreamReader(in));
	return value;
    }

    @Override
    public void writeValue(Object value, OutputStream out) {
	io.export(value, new OutputStreamWriter(out));
    }

    @Override
    public boolean isWriteSupported() {
	return true;
    }

}
