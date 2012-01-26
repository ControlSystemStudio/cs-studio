/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Descriptor for a property that holds a <code>String[]</code>
 *  @author Kay Kasemir
 */
public class StringArrayPropertyDescriptor extends TextPropertyDescriptor
{
    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     */
    public StringArrayPropertyDescriptor(final String id, final String label)
    {
        super(id, label);
        
        // Set label provider for String[], because default
        // would use String[].toString() and show "LString@..."
        setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                if (element instanceof String[])
                    return StringArrayCellEditor.encode((String[]) element);
                return super.getText(element);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        final CellEditor editor = new StringArrayCellEditor(parent);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
