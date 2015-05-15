/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.visualparts.PVNameTextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * The property descriptor for PV Name which supports auto complete.
 *
 * @author Xihui Chen
 *
 */
public class PVNamePropertyDescriptor extends TextPropertyDescriptor {


    /**
     * @param id
     *            id of the property
     * @param displayName
     *            the display name in property sheet entry
     * @param detailedDescription
     *            the detailed description on tooltip and status line.
     */
    public PVNamePropertyDescriptor(Object id, String displayName, String detailedDescription) {
        super(id, displayName);
        setDescription(detailedDescription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {

        final PVNameTextCellEditor editor = new PVNameTextCellEditor(parent);
        editor.getControl().setToolTipText(getDescription());
        return editor;
    }


}
