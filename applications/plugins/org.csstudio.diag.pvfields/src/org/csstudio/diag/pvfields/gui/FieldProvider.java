/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.gui;

import org.csstudio.diag.pvfields.model.PVFieldsModel;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provides the PVField for a specific Table row */
public class FieldProvider implements ILazyContentProvider
{
    final private TableViewer fields_table;
    final private PVFieldsModel model;

    public FieldProvider(TableViewer fields_table, PVFieldsModel control)
    {
        this.fields_table = fields_table;
        this.model = control;
    }

    @SuppressWarnings("nls")
    @Override
    public void updateElement(int row)
    {
    	try
        {
    		if (model.getPVInfoListCount() <= row)
    			fields_table.replace(new PVInfo("","","","","","","",""), row);
    		else
            	fields_table.replace(model.getPVInfoRow(row), row);
        }
        catch (Throwable ex)
        {
            // Ignore.
            // When the model changes because of ongoing queries,
            // it's possible to access an invalid element because
            // the table just changed on us.

            // TODO remove when it all works
            ex.printStackTrace();
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
