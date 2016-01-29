/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

/** Base for {@link Action} on PVTableModel
 *  @author Kay Kasemir
 */
public class PVTableAction extends Action
{
    protected TableViewer viewer;

    public PVTableAction(final String title, final String icon_path, final TableViewer viewer)
    {
        super(title, Plugin.getImageDescriptor(icon_path));
        setViewer(viewer);
    }

    public void setViewer(final TableViewer viewer)
    {
        this.viewer = viewer;
        setEnabled(viewer != null);
    }

	public TableViewer getViewer() {
		return viewer;
	}
    
    
}
