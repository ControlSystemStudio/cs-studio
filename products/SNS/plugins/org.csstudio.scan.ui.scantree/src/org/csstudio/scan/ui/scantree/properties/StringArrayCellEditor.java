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

import org.csstudio.java.string.StringSplitter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/** Cell editor for a field that holds a <code>String[]</code>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StringArrayCellEditor extends TextCellEditor
{
    /** Initialize
     *  @param parent Parent control
     */
    public StringArrayCellEditor(final Composite parent)
    {
        super(parent);
    }

    /** @param value Value to display & edit in this cell editor, should be String[] */
    @Override
    protected void doSetValue(final Object value)
    {   // Pass down to TextCellEditor as String
        if (value instanceof String[])
            super.doSetValue(encode((String[]) value));
        else
            super.doSetValue("");
    }

    /** @return Current value of the cell editor, returns String[] */
    @Override
    protected Object doGetValue()
    {
        final String text = (String) super.doGetValue();
        if (text.isEmpty())
            return new String[0];
        try
        {
            return decode(text);
        }
        catch (Exception ex)
        {
            return "";
        }
    }
    
    /** Encode list of items into one String
     *  @param items Array of items
     *  @return Encoded string
     */
    public static String encode(final String[] items)
    {
        final StringBuilder result = new StringBuilder();
        for (String item : items)
        {
            if (result.length() > 0)
                result.append(", ");
            result.append(item);
        }
        return result.toString();
    }

    /** @param encoded_list String that has encoded list of items
     *  @return Array of individual items
     *  @throws Exception on error
     */
    public static String[] decode(final String encoded_list) throws Exception
    {
        return StringSplitter.splitIgnoreInQuotes(encoded_list, ',', true);
    }
    
}
