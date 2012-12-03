/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** 'Virtual' content provider, input is {@link PVTableModel}
 *  @author Kay Kasemir
 */
public class PVTableModelContentProvider implements ILazyContentProvider
{
	private TableViewer viewer;
	private PVTableModel model;
	
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
		this.viewer = (TableViewer) viewer;
		model = (PVTableModel) newInput;
		if (viewer != null   &&  model != null)
			this.viewer.setItemCount(model.getItemCount());
	}

	
	@Override
	public void updateElement(final int index)
	{
		viewer.replace(model.getItem(index), index);
	}


	@Override
	public void dispose()
	{
		viewer = null;
		model = null;
	}
}
