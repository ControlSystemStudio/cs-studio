/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Paste an entry
 *
 *  @author Kay Kasemir
 */
public class PasteAction extends Action
{
	private PVTableViewerHelper helper;

	PasteAction(PVTableViewerHelper helper)
	{
		this.helper = helper;
		setText("Paste");
		setToolTipText("Paste new PV");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	@Override
	public void run()
	{
		TextTransfer transfer = TextTransfer.getInstance();
		String name = (String) helper.getClipboard().getContents(transfer);
		if (name == null)
			System.out.println("Empty Clipboard");
		else
            helper.getPVListModel().addPV(name);
	}
}
