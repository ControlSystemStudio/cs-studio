/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.ScanHandlerUtil;
import org.csstudio.scan.ui.ScanUIActivator;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler that opens a (new) plot for scan data
 *  @author Kay Kasemir
 */
public class OpenPlotHandler extends AbstractHandler
{
    /** {@inheritDoc} */
	@Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final ScanInfo info = ScanHandlerUtil.getScanInfo(event);
		if (info == null)
			return null;

		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        try
        {
            final IWorkbenchPage page = window.getActivePage();
            final String secondary = ScanPlotView.getNextViewID();
            final ScanPlotView view = (ScanPlotView) page.showView(ScanUIActivator.ID_SCAN_PLOT_VIEW, secondary, IWorkbenchPage.VIEW_ACTIVATE);
            view.selectScan(info.getName(), info.getId());
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(window.getShell(), Messages.Error, Messages.OpenPlotError, ex);
        }
	    return null;
    }
}
