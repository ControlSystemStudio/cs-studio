/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.workbench;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** An action that opens or resets a perspective.
 *  @author Kay Kasemir
 */
public class OpenPerspectiveAction extends Action
{
    /** ID of the Perspective to open */
    final private String ID;
    
    /** Construct the action for opening a perspective.
     *  @param icon Icon to use for the action.
     *  @param name Name to use for the action.
     *  @param ID The ID of the Perspective to open.
     */
    public OpenPerspectiveAction(final ImageDescriptor icon,
                                 final String name, final String ID)
    {
        super(name);
        setImageDescriptor(icon);
        this.ID = ID;
    }
    
    @Override
    public void run()
    {
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
        final Shell shell = window.getShell();
        try
        {
            final IWorkbenchPage page = window.getActivePage();
            final String current = page.getPerspective().getId();
            if (current.equals(ID))
            {
                if (MessageDialog.openQuestion(shell,
                        Messages.OpenPerspectiveReset,
                        Messages.OpenPerspectiveResetQuestion))
                    page.resetPerspective();
            }
            else
                wb.showPerspective(ID, wb.getActiveWorkbenchWindow());
        }
        catch (Exception e)
        {
            MessageDialog.openError(shell,
                    "Error", "Cannot open perspective " + ID); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
