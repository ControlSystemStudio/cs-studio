/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui;

import org.csstudio.scan.server.ScanInfo;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** HandlerUtil-type methods for scan UI
 *  @author Kay Kasemir
 */
public class ScanHandlerUtil
{
	/** @param event {@link ExecutionEvent}
	 *  @return Currently selected scan or <code>null</code>
	 */
	public static ScanInfo getSelectedScanInfo(final ExecutionEvent event)
	{
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (! (selection instanceof IStructuredSelection))
			return null;
		final IStructuredSelection ssel = (IStructuredSelection) selection;
		final Object object = ssel.getFirstElement();
		if (! (object instanceof ScanInfo))
			return null;
		return (ScanInfo) object;
	}
}
