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
package org.csstudio.scan.ui.scanmonitor.actions;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/** Base for action that calls the model, displaying errors in dialog
 *  @author Kay Kasemir
 */
public abstract class AbstractGUIAction extends Action
{
	final protected Shell shell;
    final protected ScanInfoModel model;
    final protected ScanInfo[] infos;

    /** Initialize
     *  @param shell Parent shell
     *  @param model {@link ScanInfoModel}
     *  @param infos {@link ScanInfo}s for scan on which this action acts
     *  @param label Label
     *  @param icon Icon image
     */
    public AbstractGUIAction(final Shell shell, final ScanInfoModel model, final ScanInfo[] infos, final String label, final ImageDescriptor icon)
    {
        super(label, icon);
        this.shell = shell;
        this.model = model;
        this.infos = infos;
    }

    /** Invoke the 'real' action, handling errors */
    @Override
    public void run()
    {
        try
        {
            runModelAction();
        }
        catch (Exception ex)
        {
        	ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
        }
    }

    /** To be implemented by derived class:
     *  Perform action on <code>model</code> and currently selected <code>info</code>
     *  @throws Exception Error that will be displayed in message box
     */
    abstract protected void runModelAction() throws Exception;
}
