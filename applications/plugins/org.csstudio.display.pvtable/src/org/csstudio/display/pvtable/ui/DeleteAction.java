/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;


import org.csstudio.display.pvtable.model.PVListEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Remove selected entries in table from the model.
 *
 *  @author Kay Kasemir
 */
public class DeleteAction extends Action
{
    private PVTableViewerHelper helper;

	public DeleteAction(PVTableViewerHelper helper)
	{
        this.helper = helper;
		setText("Delete");
		setToolTipText("Delete selected PV");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
		// Conditionally enable this action
		helper.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener()
				{
					@Override
                    public void selectionChanged(SelectionChangedEvent event)
					{
						boolean anything = !event.getSelection().isEmpty();
						setEnabled(anything);
					}
				});
	}

	@Override
	public void run()
	{
        PVListEntry entries[] = helper.getSelectedEntries();
		if (entries == null)
			return;
		for (int i = 0; i < entries.length; i++)
			helper.getPVListModel().removeEntry(entries[i]);
	}
}
