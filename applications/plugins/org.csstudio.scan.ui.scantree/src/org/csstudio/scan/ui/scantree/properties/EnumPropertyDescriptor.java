/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** Descriptor for a property that holds an <code>Enum</code>
 *  @author Kay Kasemir
 */
public class EnumPropertyDescriptor extends PropertyDescriptor
{
    final Class<? extends Enum<?>> enum_type;
    
    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     *  @param enum_type Enum type to show, defines the values to provide
     */
    public EnumPropertyDescriptor(final String id, final String label,
            final Class<? extends Enum<?>> enum_type)
    {
        super(id, label);
        this.enum_type = enum_type;
    }
    
    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        final CellEditor editor = new EnumCellEditor(parent, enum_type);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
