/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.ScanHandlerUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/** Handler that displays data for selected {@link ScanInfo}
 *  @author Kay Kasemir
 */
public class OpenScanData  extends AbstractHandler
{
	@Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final ScanInfo scan = ScanHandlerUtil.getSelectedScanInfo(event);
		if (scan == null)
			return null;

		final Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		MessageDialog.openInformation(shell, "TODO", "Should show data of scan " + scan);

	    // TODO Open display, connect to ScanDataModel, ...
	    return null;
    }
}
