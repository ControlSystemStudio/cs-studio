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

/** Action that resumes a paused scan
 *  @author Kay Kasemir
 */
public class ResumeAction extends AbstractGUIAction
{
    /** Initialize
     *  @param model
     *  @param info
     */
    public ResumeAction(final ScanInfoModel model, final ScanInfo info)
    {
        super(model, info, Messages.Resume, "icons/resume.gif"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        model.getServer().resume(info == null ? -1 : info.getId());
    }
}
