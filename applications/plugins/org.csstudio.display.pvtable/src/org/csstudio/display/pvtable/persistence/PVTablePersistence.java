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
    // TODO static PVTablePersistence forFilename(XML or SAV file)
    // TODO Update PVTableXMLPersistence to use this
    // TODO Update 'editor' to use this
    
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
        // Try to parse as number
        try
        {
            // Integer?
            if (value_text.contains("."))
            {
                final double value = Double.parseDouble(value_text);
                return ValueFactory.newVDouble(value);
            }
            else
            {
                final long value = Long.parseLong(value_text);
                return ValueFactory.newVLong(value, ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());
            }
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
