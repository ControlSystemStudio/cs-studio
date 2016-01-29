/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableItemListener;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** 'Virtual' content provider, input is {@link PVTableModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableModelContentProvider implements ILazyContentProvider
{
    /** 'Magic' table item added to the end of the actual model
     *  to allow adding entries.
     *  Setting the name of this item is handled as adding a new
     *  item for that name.
     */
    final public static PVTableItem NEW_ITEM = new PVTableItem("", 0.0, null, new PVTableItemListener() {
        @Override
        public void tableItemSelectionChanged(PVTableItem item) {
            // NOP
        }

        @Override
        public void tableItemChanged(PVTableItem item) {
            // NOP
        }
    }, null);

    static {
        NEW_ITEM.setSelected(false);
    }

    private TableViewer viewer;
    private PVTableModel model;

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        this.viewer = (TableViewer) viewer;
        model = (PVTableModel) newInput;
        if (viewer != null   &&  model != null) {
        	if(model.getConfig() != null && model.getConfig().getMesures().size() > 0){
        		this.viewer.setItemCount(model.getItemCount());
        	}else{
        		this.viewer.setItemCount(model.getItemCount() + 1);
        	}
        }
        else {
            this.viewer.setItemCount(0);
        }
    }


    @Override
    public void updateElement(final int index) {
        if (index < model.getItemCount()) {
            viewer.replace(model.getItem(index), index);
        }
        else {
        	

        	
            viewer.replace(NEW_ITEM, index);
        }
    }

    @Override
    public void dispose() {
        viewer = null;
        model = null;
    }
}
