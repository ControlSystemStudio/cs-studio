/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.gui;


import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provides the Device for a specific Table row */
public class PVProvider implements ILazyContentProvider
{
    final private TableViewer pv_table;
	final private PVUtilModel control;

    public PVProvider(TableViewer pv_table, PVUtilModel control)
    {
        this.pv_table = pv_table;
        this.control = control;
    }

    @Override
    public void updateElement(int row)
    {
        try
        {
            pv_table.replace(control.getPV(row), row);
        }
        catch (Throwable ex)
        {
            // Ignore.
            // When the model changes because of ongoing queries,
            // it's possible to access an invalid element because
            // the table just changed on us.
        }
    }

    @Override
    public void dispose()
    {
        // NOP
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // NOP
    }
}
