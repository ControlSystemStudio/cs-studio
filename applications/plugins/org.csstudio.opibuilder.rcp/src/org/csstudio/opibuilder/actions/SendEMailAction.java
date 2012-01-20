/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.email.ui.AbstractSendEMailAction;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action for e-mailing snapshot of plot
 *  @author Kay Kasemir, Xihui Chen
 */
public class SendEMailAction extends AbstractSendEMailAction
{
    final private IOPIRuntime opiRuntime;
    public static final String ID = "org.csstudio.opibuilder.actions.sendEmail";
    /** Initialize
     *  @param shell
     *  @param graph
     */
    public SendEMailAction(final IOPIRuntime part)
    {
        super(part.getSite().getShell(), "opi@css",
              part.getDisplayModel().getName(),
              "See attached OPI screenshot");
        opiRuntime = part;
        setId(ID);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getImage()
    {
        try
        {
            return ResourceUtil.getScreenshotFile(
            		(GraphicalViewer) opiRuntime.getAdapter(GraphicalViewer.class));
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, "error", ex.getMessage());
            return null;
        }
    }
}
