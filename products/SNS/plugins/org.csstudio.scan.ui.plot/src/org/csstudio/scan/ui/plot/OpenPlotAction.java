/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Action that opens a (new) plot for scan data
 *  @author Kay Kasemir
 */
public class OpenPlotAction extends Action
{
    final private ScanInfo info;
    
    /** Initialize
     *  @param info Scan info
     */
    public OpenPlotAction(final ScanInfo info)
    {
        super(Messages.Plot, Activator.getImageDescriptor("icons/plot.gif")); //$NON-NLS-1$
        this.info = info;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final String secondary = ScanPlotView.getNextViewID();
            final ScanPlotView view = (ScanPlotView) page.showView(ScanPlotView.ID, secondary, IWorkbenchPage.VIEW_ACTIVATE);
            view.selectScan(info.getName(), info.getId());
        }
        catch (Exception ex)
        {
            new ErrorDetailDialog(null, Messages.Error, Messages.OpenPlotError, ex.getMessage()).open();
        }
    }
}
