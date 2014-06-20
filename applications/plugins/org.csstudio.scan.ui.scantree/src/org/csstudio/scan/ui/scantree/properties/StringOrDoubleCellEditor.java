/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.eclipse.swt.widgets.Composite;

/** Cell editor for a field that's either a <code>String</code> or a <code>Double</code>
 *  @author Kay Kasemir
 */
public class StringOrDoubleCellEditor extends DoubleCellEditor
{
    /** Initialize
     *  @param parent Parent control
     */
    public StringOrDoubleCellEditor(final Composite parent)
    {
        super(parent);
    }

    /** @param value Value to display & edit in this cell editor, should be String or Double */
    @Override
    protected void doSetValue(final Object value)
    {   // DoubleCellEditor can also handle String
        super.doSetValue(value);
    }

    /** @return Current value of the cell editor, will be Double if possible, otherwise String */
    @Override
    protected Object doGetValue()
    {   // Try to turn String of TextCellEditor into Double
        final String text = getStringValue();
        if (text.isEmpty())
            return Double.valueOf(0);
        try
        {
            return Double.valueOf(text.trim());
        }
        catch (NumberFormatException ex)
        {   // Use String
            return text;
        }
    }
}
