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
	/** @param selection {@link ISelection}
	 *  @return Scan info from selection <code>null</code>
	 */
	private static ScanInfo checkScanInfo(final ISelection selection)
	{
		if (! (selection instanceof IStructuredSelection))
			return null;
		final IStructuredSelection ssel = (IStructuredSelection) selection;
		final Object object = ssel.getFirstElement();
		if (! (object instanceof ScanInfo))
			return null;
		return (ScanInfo) object;
	}

	/** @param event {@link ExecutionEvent}
	 *  @return Scan info from current context menu or <code>null</code>
	 */
	public static ScanInfo getMenuScanInfo(final ExecutionEvent event)
	{
		return checkScanInfo(HandlerUtil.getActiveMenuSelection(event));
	}

	/** @param event {@link ExecutionEvent}
	 *  @return Currently selected scan or <code>null</code>
	 */
	public static ScanInfo getSelectedScanInfo(final ExecutionEvent event)
	{
		return checkScanInfo(HandlerUtil.getCurrentSelection(event));
	}

	/** @param event {@link ExecutionEvent}
	 *  @return Scan info from context menu or current selection, or <code>null</code>
	 */
	public static ScanInfo getScanInfo(final ExecutionEvent event)
	{
		// Try the menu selection
		// When a context menu was opened, this will be "it".
		// The current selection can actually change while
		// the menu is open and is thus less useful.
		ScanInfo scan = getMenuScanInfo(event);
		if (scan != null)
			return scan;
		return getSelectedScanInfo(event);
	}
}
