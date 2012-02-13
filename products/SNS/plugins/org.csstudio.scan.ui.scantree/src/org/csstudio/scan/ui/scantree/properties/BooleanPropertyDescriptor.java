/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** Descriptor for a property that holds a <code>Boolean</code>
 *  @author Kay Kasemir
 */
public class BooleanPropertyDescriptor extends PropertyDescriptor
{
    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     */
    public BooleanPropertyDescriptor(final String id, final String label)
    {
        super(id, label);

        // Default would display true/false
        setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                if (element instanceof Boolean)
                    return ((Boolean)element).booleanValue() ? Messages.BoolEdit_True : Messages.BoolEdit_False;
                return super.getText(element);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        final CellEditor editor = new CheckboxCellEditor(parent);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
