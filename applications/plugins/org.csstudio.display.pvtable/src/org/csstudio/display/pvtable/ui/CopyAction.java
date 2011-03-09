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
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Copy an entry.
 *
 *  @author Kay Kasemir
 */
public class CopyAction extends Action
{
	protected PVTableViewerHelper helper;

	CopyAction(PVTableViewerHelper helper)
	{
		this.helper = helper;
		setText("Copy");
		setToolTipText("Copy PV to Clipboard");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		// Conditionally enable this action
        setEnabled(false);
		helper.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener()
				{
					@Override
                    public void selectionChanged(SelectionChangedEvent event)
					{
						boolean anything = !event.getSelection().isEmpty();
						CopyAction.this.setEnabled(anything);
					}
				});
	}

	@Override
	public void run()
	{
        PVListEntry entries[] = helper.getSelectedEntries();
		if (entries == null || entries.length < 1)
			return;
		// TODO: Place more then one selected PV on the clipboard?
		helper.getClipboard().setContents(new Object[]
		{ entries[0].getPV().getName() }, new Transfer[]
		{ TextTransfer.getInstance() });
	}
}
