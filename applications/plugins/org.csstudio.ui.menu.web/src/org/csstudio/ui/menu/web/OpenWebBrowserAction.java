/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.web;

import java.net.URL;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/** Action that opens a web browser.
 *  @author Kay Kasemir, Xihui Chen
 */
public class OpenWebBrowserAction extends Action
{
    final private String url;

    /** Create action
     *  @param title Title of action in menu
     *  @param url URL to open in the browser
     */
    public OpenWebBrowserAction(final String title,
            final String url)
    {
        super(title);
        this.url = url;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        try
        {
            final IWebBrowser browser =
                workbench.getBrowserSupport().createBrowser(
                		IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR,
                		Messages.BrowserID, null, null);
            browser.openURL(new URL(url));
        }
        catch (Exception ex)
        {
            MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(),
                    Messages.Error,
                    NLS.bind(Messages.BrowserErrorFmt, url, ex.getMessage()));
        }
    }
}
