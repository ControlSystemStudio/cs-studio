/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;

/** {@link Action} to save value snapshots
 *  @author Kay Kasemir
 */
public class RestoreAction extends Action
{
	final private TableViewer viewer;
	
	public RestoreAction(final TableViewer viewer)
	{
		super("Restore", Plugin.getImageDescriptor("icons/restore.png"));
		this.viewer = viewer;
	}
	
	public void run()
	{
		final PVTableModel model = (PVTableModel) viewer.getInput();
		if (model == null)
			return;
		model.restore();
		viewer.setSelection(null);
	}
}
