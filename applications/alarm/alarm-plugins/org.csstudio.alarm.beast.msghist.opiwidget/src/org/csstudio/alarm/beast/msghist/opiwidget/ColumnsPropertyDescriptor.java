/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 *
 * <code>ColumnsPropertyDescriptor</code> is the descriptor for column property of the message history widget.
 *
 * @author Borut Terpinc
 */
public class ColumnsPropertyDescriptor extends PropertyDescriptor {

    /**
     * Creates a property descriptor with the given id and display name.
     *
     * @param id
     *            the id of the property
     * @param displayName
     *            the display name of the property
     */
    public ColumnsPropertyDescriptor(final Object id, final String displayName) {
        super(id, displayName);
    }

    /*
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        CellEditor editor = new ColumnsPropertyEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }
}
