/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

/** A table cell editor for values of type FontData.
 *  <p>
 *  @author Kay Kasemir, Kunal Shroff
 */
public class FontCellEditor extends CellEditor
{
    final private Shell shell;
    private FontData value;

    /** Creates a new font cell editor parented under the given control.
     *  The cell editor value is an SWT FontData value.
     *  @param parent The parent table.
     */
    public FontCellEditor(final Composite parent)
    {
        super(parent, SWT.NONE);
        shell = parent.getShell();
    }

    /** Opens the color dialog. */
    @Override
    public void activate()
    {
        final FontDialog dialog = new FontDialog(shell);
        if (value != null)
            dialog.setFontList(new FontData[] { value });
        value = dialog.open();
        if (value != null)
            fireApplyEditorValue();
    }

    /** NOP */
    @Override
    protected Control createControl(final Composite parent)
    {
        return null;
    }

    /** @return Returns the current FontData value. */
    @Override
    protected Object doGetValue()
    {
        return value;
    }

    @Override
    protected void doSetFocus()
    {
        // Ignore
    }

    /** Called by the framework to initialize the RGB value.
     *  @param value Should be an RGB.
     */
    @Override
    protected void doSetValue(final Object value)
    {
        this.value = (FontData) value;
    }
}
