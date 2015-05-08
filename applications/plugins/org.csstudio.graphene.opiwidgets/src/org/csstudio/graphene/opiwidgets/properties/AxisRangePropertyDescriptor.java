/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.graphene.opiwidgets.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Description for AxisRange property.
 */
public class AxisRangePropertyDescriptor extends PropertyDescriptor {

    public AxisRangePropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        AxisRangeCellEditor editor = new AxisRangeCellEditor(parent, "Choose range for axis...");
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }


}
