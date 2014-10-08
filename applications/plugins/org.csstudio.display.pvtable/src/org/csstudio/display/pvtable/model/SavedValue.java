/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import org.epics.pvmanager.PV;
import org.epics.vtype.Scalar;
import org.epics.vtype.VType;

/** Base for saved values of a table item
 * 
 *  <p>Values are always saved as String.
 *  When reading autosave-files, the PV's data type
 *  is unknown until the channel connects,
 *  and a channel may never connect.
 *  To allow reading and writing files without
 *  changing the exact value format, the text
 *  is kept.
 *  
 *  <p>Derived implementations provide support for
 *  scalar (String) and array (List<String>)
 *
 *  @author Kay Kasemir
 */
abstract public class SavedValue
{
    /** @param current_value Current value of PV
     *  @return {@link SavedValue} that contains current value
     *  @throws Exception on error
     */
    public static SavedValue forCurrentValue(VType current_value) throws Exception
    {
        if (current_value instanceof Scalar)
            return new SavedScalarValue(VTypeHelper.getValue(current_value).toString());
        // TODO Handle arrays
        throw new Exception("Cannot handle " + current_value);
    }

    /** @param value_text Text for a scalar value
     *  @return {@link SavedValue}
     */
    public static SavedValue forScalar(final String value_text)
    {
        return new SavedScalarValue(value_text);
    }

    /** Compare saved value against current value of a PV
     *  @param current_value Value to compare against
     *  @param tolerance Tolerance to use for numeric values
     *  @return <code>true</code> if values match within tolerance
     *  @throws Exception on error
     */
    abstract public boolean isEqualTo(final VType current_value, final double tolerance) throws Exception;
    
    /** Restore saved value to PV
     *  @param pv PV to write
     */
    abstract public void restore(final PV<VType, Object> pv);
    
    /** @return String representation for display purpose */
    @Override
    abstract public String toString();
}
