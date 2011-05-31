/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;

/** A 'saved' snapshot value.
 *
 *  Internally either a Double or a String.
 *  @author Kay Kasemir
 */
public class SavedValue
{
    private Object saved;

    /** Construct empty saved value. */
    public SavedValue()
    {   saved = null; }

    /** Construct saved value with initial number. */
    public SavedValue(Double d)
    {   saved = d; }

    /** Construct saved value with initial text. */
    public SavedValue(String s)
    {   saved = s; }

    /** Create saved value from string. */
    public static SavedValue fromString(String text)
    {
        if (text.length() < 0)
            return new SavedValue();
        try // to get a number from the string
        {
            return new SavedValue(Double.parseDouble(text));
        }
        catch (Exception e)
        {
            // Not fatal
        }
        // Fall back to String.
        return new SavedValue(text);
    }

    /** Save the current value of the PV into this SavedValue. */
    public void readFromPV(PV pv)
    {
        if (pv == null  ||  ! pv.isConnected())
        {
            saved = null;
            return;
        }
        IValue current = pv.getValue();
        double num = ValueUtil.getDouble(current);
        if (Double.isInfinite(num) || Double.isNaN(num))
            saved = current.format();
        else
            saved = new Double(num);
    }

    /** Restore this SavedValue to the given PV. */
    public void restoreToPV(PV pv) throws Exception
    {
        if (pv == null || saved == null  ||  !pv.isConnected())
            return;
        pv.setValue(saved);
    }

    /** @return Current value, never <code>null</code>. */
    @Override
    public String toString()
    {
        if (saved == null)
            return ""; //$NON-NLS-1$
        return saved.toString();
    }

    /** @return <code>true</code> if we have a current and saved value,
     *  and they differ beyond the given tolerance.
     */
    public boolean differ(PV pv, double tolerance)
    {
        if (pv == null  ||  saved == null)
            return false;
        if (! pv.isConnected())
            return false;
        IValue value = pv.getValue();
        // Compare strings as strings
        if (saved instanceof String)
            return value.format().equals(saved) == false;
        // Compare rest as double
        double current = ValueUtil.getDouble(value);
        double saved_num = ((Double)saved).doubleValue();
        return Math.abs(current - saved_num) > tolerance;
    }
}
