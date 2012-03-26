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
import org.eclipse.swt.widgets.Shell;

/** Action that removes all completed scans
 *  @author Kay Kasemir
 */
public class RemoveCompletedAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell Parent shell
     *  @param model
     */
    public RemoveCompletedAction(final Shell shell, final ScanInfoModel model)
    {
        super(shell, model, null, Messages.RemoveCompleted, Activator.getImageDescriptior("icons/remove_completed.gif")); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        model.getServer().removeCompletedScans();
    }
}
