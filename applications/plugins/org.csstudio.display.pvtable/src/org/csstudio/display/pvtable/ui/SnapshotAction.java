/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListModel;

/** Action that takes a snapshot of current values.
 *  @author Kay Kasemir
 */
public class SnapshotAction extends PVListModelAction
{
	public SnapshotAction(PVListModel pv_list)
	{
		super(pv_list);
		setText("Snapshot");
		setToolTipText("Take snapshot of current values");
		setImageDescriptor(Plugin.getImageDescriptor("icons/snapshot.gif")); //$NON-NLS-1$
	}

	@Override
	public void run()
    {
	    PVListModel pv_list = getPVListModel();
	    if (pv_list != null)
            pv_list.takeSnapshot();
    }
}
