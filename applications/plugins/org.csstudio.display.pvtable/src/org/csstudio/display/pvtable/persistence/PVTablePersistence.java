/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.persistence;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.csstudio.display.pvtable.model.PVTableModel;
import org.epics.vtype.VDouble;
import org.epics.vtype.VLong;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** Base for persisting PVTableModel to/from file
 *  @author Kay Kasemir
 */
abstract public class PVTablePersistence
{
    /** Create a persistance helper based on file extension
     *  @param filename File name
     *  @return {@link PVTablePersistence}
     */
    public static PVTablePersistence forFilename(final String filename)
    {
        // Use Autosave for *.sav files
        if (filename.endsWith(PVTableAutosavePersistence.FILE_EXTENSION))
            return new PVTableAutosavePersistence();
        // Use XML for anything else, because at some time "css-pvtable" was used
        return new PVTableXMLPersistence();
    }

    /** @return Preferred file extension (without '.') */
    abstract public String getFileExtension();
    
    /** Read {@link PVTableModel} from file
     *  @param filename Filename
     *  @return PV table model
     *  @throws Exception on error
     */
    public PVTableModel read(final String filename) throws Exception
    {
        return read(filename);
    }

    /** Read {@link PVTableModel} from stream
     *  @param stream Stream
     *  @return PV table model
     *  @throws Exception on error
     */
    abstract public PVTableModel read(final InputStream stream) throws Exception;
    
    /** Write {@link PVTableModel} to file
     *  @param model Model
     *  @param filename Filename
     *  @throws Exception on error
     */
    public void write(final PVTableModel model, final String filename) throws Exception
    {
        write(model, new FileOutputStream(filename));
    }

    /** Write {@link PVTableModel} to stream
     *  @param model Model
     *  @param stream Stream
     *  @throws Exception on error
     */
    abstract public void write(final PVTableModel model, final OutputStream stream) throws Exception;
    
    /** Helper for creating {@link VType} from a saved value
     *  @param value_text Text of a value
     *  @return VType for that text, either {@link VNumber} ({@link VDouble}) or {@link VString}, or <code>null</code>
     */
    protected VType createValue(final String value_text)
    {
        if (value_text.isEmpty())
            return null;
        try
        {   // Try to parse as number
            final double value = Double.parseDouble(value_text);
            if (value == (long) value)
                return ValueFactory.newVLong((long) value, ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
            return ValueFactory.newVDouble(value);
        }
        catch (NumberFormatException ex)
        {
            // Not a number, fall through to return VString
        }
        return ValueFactory.newVString(value_text, ValueFactory.alarmNone(), ValueFactory.timeNow());
    }

    /** Format the value (without alarm, timestamp) as a string
     *  @param value VType returned by <code>createValue</code>
     *  @return Text for the value
     */
    protected String formatValue(final VType value)
    {
        if (value instanceof VLong)
            return Long.toString(((VLong) value).getValue());
        if (value instanceof VDouble)
            return Double.toString(((VDouble) value).getValue());
        if (value instanceof VString)
            return ((VString) value).getValue();
        return value.toString();
    }
}
