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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/** Handler that displays data for selected {@link ScanInfo}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenScanData  extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final ScanInfo scan = ScanHandlerUtil.getScanInfo(event);
		if (scan == null)
			return null;

		// Open ScanDataEditor for this scan
		final IEditorInput input = new ScanInfoEditorInput(scan);
    	try
        {
    		final IWorkbench workbench = PlatformUI.getWorkbench();
    		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    		final IWorkbenchPage page = window.getActivePage();
	        page.openEditor(input, ScanDataEditor.ID);
        }
        catch (PartInitException ex)
        {
        	throw new ExecutionException("Cannot open scan data editor", ex);
        }

		return null;
    }
}
