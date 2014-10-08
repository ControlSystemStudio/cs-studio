/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.util.List;

import org.epics.pvmanager.PV;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;

/** Saved value of an array table item
 *  @author Kay Kasemir
 */
public class SavedArrayValue extends SavedValue
{
    final private List<String> saved_value;
    
    /** Initialize from text
     *  @param value_text
     */
    public SavedArrayValue(final List<String> value_texts)
    {
        saved_value = value_texts;
    }
    
    /** @return Number of array elements */
    public int size()
    {
        return saved_value.size();
    }
    
    /** @param index Array index, <code>0 .. size()-1</code>
     *  @return Array element
     */
    public String get(final int index)
    {
        return saved_value.get(index);
    }
    
    /** {@inheritDoc} */
    public boolean isEqualTo(final VType current_value, final double tolerance) throws Exception
    {
        if (current_value == null)
            return true;
        if (current_value instanceof VNumberArray)
        {
            final ListNumber values = ((VNumberArray)current_value).getData();
            final int N = values.size();
            if (N != saved_value.size())
                return false;
            for (int i=0; i<N; ++i)
            {
                final double v1 = values.getDouble(i);
                final double v2 = Double.parseDouble(saved_value.get(i));
                if (Math.abs(v2 - v1) > tolerance)
                    return false;
            }
            return true;
        }
        if (current_value instanceof VStringArray)
            return ((VStringArray)current_value).getData().equals(saved_value);
        // No VEnumArray support at this time..
        throw new Exception("Cannot compare against unhandled type " + current_value.getClass().getName());
    }
    
    /** {@inheritDoc} */
    public void restore(final PV<VType, Object> pv)
    {
        // TODO Implement
//        // Determine what type to write based on current value of the PV
//        final VType pv_type = pv.getValue();
//        if (pv_type instanceof VDouble)
//            pv.write(Double.parseDouble(saved_value));
//        else if (pv_type instanceof VNumber)
//            pv.write(Long.parseLong(saved_value));
//        else // Write as text
//            pv.write(saved_value);
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (String value : saved_value)
        {
            if (first)
                first = false;
            else
                buf.append(", ");
            buf.append(value);
        }
        return buf.toString();
    }
}
