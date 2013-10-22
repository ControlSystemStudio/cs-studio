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

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

/** Cell editor for a <code>Double</code> typed field
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DoubleCellEditor extends TextCellEditor
{
    /** When the user presses 'return' in the text box,
     *  <code>handleDefaultSelection</code>
     *  will fire a property update.
     *
     *  <p>While in 'live' mode, this can open
     *  a confirmation message dialog ("Change property of live scan?").
     *  When that dialog opens, the text field looses focus,
     *  which by default would trigger another property update,
     *  and in 'live' mode result in another message dialog...
     *
     *  <p>This flag tracks if we're in <code>handleDefaultSelection</code>
     *  to avoid the extra update from loss-of-focus.
     */
    private boolean handling_default_selection = false;

    /** Initialize
     *  @param parent Parent control
     */
    public DoubleCellEditor(final Composite parent)
    {
        super(parent);
    }

    /** @param value Value to display & edit in this cell editor, should be Double */
    @Override
    protected void doSetValue(final Object value)
    {   // Pass down to TextCellEditor as String
        if (value==null)
            super.doSetValue("0");
        else
            super.doSetValue(value.toString());
    }

    /** @return Current text of editor */
    protected String getStringValue()
    {
        return (String) super.doGetValue();
    }

    /** @return Current value of the cell editor, will be Double */
    @Override
    protected Object doGetValue()
    {   // Return String of TextCellEditor as Double
        final String text = getStringValue();
        try
        {
            return Double.valueOf(text.trim());
        }
        catch (NumberFormatException ex)
        {
            return Double.valueOf(0.0);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleDefaultSelection(final SelectionEvent event)
    {
        handling_default_selection = true;
        super.handleDefaultSelection(event);
        handling_default_selection = false;
    }

    /** {@inheritDoc} */
    @Override
    protected void focusLost()
    {
        if (handling_default_selection)
            return;
        super.focusLost();
    }
}
