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
import org.csstudio.scan.ui.scanmonitor.Activator;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that displays scan server info
 *  @author Kay Kasemir
 */
public class InfoAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell
     *  @param model
     *  @param info
     */
    public InfoAction(final Shell shell, final ScanInfoModel model)
    {
        super(shell, model, null, Messages.Info, Activator.getImageDescriptior("icons/information.gif")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
    	MessageDialog.openInformation(shell, Messages.InfoTitle,
    			model.getServerInfoText());
    }
}
