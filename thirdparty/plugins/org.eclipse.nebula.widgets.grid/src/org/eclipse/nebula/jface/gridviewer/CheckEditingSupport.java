/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

/**
 * .
 */
public abstract class CheckEditingSupport extends EditingSupport
{
    /**
     * Checkbox editing support.
     * 
     * @param viewer column to add check box support for.
     */
    public CheckEditingSupport(ColumnViewer viewer)
    {
        super(viewer);
    }

    /** {@inheritDoc} */
    protected boolean canEdit(Object element)
    {
        return false;
    }

    /** {@inheritDoc} */
    protected CellEditor getCellEditor(Object element)
    {
        return null;
    }

    /** {@inheritDoc} */
    protected Object getValue(Object element)
    {
        return null;
    }

    /** {@inheritDoc} */
    public abstract void setValue(Object element, Object value);
}
