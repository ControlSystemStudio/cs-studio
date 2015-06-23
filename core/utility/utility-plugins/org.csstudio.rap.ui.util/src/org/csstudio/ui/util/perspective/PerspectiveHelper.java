/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.perspective;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.internal.localization.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Helper for handling RCP Perspective
 *  @author Xihui Chen - Original org.csstudio.opibuilder.util.E4Utils.showPerspective
 *  @author Kay Kasemir
 */
public class PerspectiveHelper
{
    /** Open perspective, or show if already open.
     *  @param id Perspective ID
     *  @param page Workbench page on which to show the perspective. Typically the 'active' page
     */
    public static void showPerspective(final String id, final IWorkbenchPage page)
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        // workbench.showPerspective() doesn't work for e4, see
        // http://stackoverflow.com/questions/11523187/switch-perspective-in-a-rcp-application-since-eclipse-juno
        // This code works for e3.7 as well as e4 with compatibility layer
        final IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
        page.setPerspective(registry.findPerspectiveWithId(id));
    }

    /** Open perspective, show if already open, or prompt for 'reset' if already visible.
     *  @param id Perspective ID
     *  @param window Workbench window. Perspective will use its 'active' page
     */
    public static void showPerspectiveOrPromptForReset(final String id, final IWorkbenchWindow window)
    {
        final Shell shell = window.getShell();
        try
        {
            final IWorkbenchPage page = window.getActivePage();
            final String current = page.getPerspective().getId();
            if (current.equals(id))
            {
                if (MessageDialog.openQuestion(shell,
                        Messages.OpenPerspectiveReset,
                        Messages.OpenPerspectiveResetQuestion))
                    page.resetPerspective();
            }
            else
                PerspectiveHelper.showPerspective(id, page);
        }
        catch (Exception ex)
        {
            final String message = "Cannot open perspective " + id;
            MessageDialog.openError(shell, "Error", message);
            Logger.getLogger(PerspectiveHelper.class.getName()).log(Level.WARNING, message, ex);
        }
    }
}
