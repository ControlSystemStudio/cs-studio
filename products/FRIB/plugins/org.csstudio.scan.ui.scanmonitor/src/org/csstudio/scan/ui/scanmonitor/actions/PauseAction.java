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
import org.csstudio.scan.ui.ScanUIActivator;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.eclipse.swt.widgets.Shell;

/** Action that pauses a scan
 *  @author Kay Kasemir
 */
public class PauseAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell Parent shell
     *  @param model
     *  @param infos
     */
    public PauseAction(final Shell shell, final ScanInfoModel model, final ScanInfo[] infos)
    {
        super(shell, model, infos, Messages.Pause, ScanUIActivator.getImageDescriptor("icons/pause.gif")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        if (infos == null)
            model.getServer().pause(-1);
        else
            for (ScanInfo info : infos)
                model.getServer().pause(info.getId());
    }
}
