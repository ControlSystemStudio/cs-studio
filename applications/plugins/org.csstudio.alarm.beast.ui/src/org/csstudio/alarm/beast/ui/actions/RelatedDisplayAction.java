/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.net.URL;

import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.csstudio.alarm.beast.client.GDCDataStructure;
import org.csstudio.alarm.beast.ui.AlarmTreeActionIcon;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.openfile.DisplayUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/** Action that displays a related display
 *  @author Kay Kasemir, Xihui Chen
 */
public class RelatedDisplayAction extends AbstractExecuteAction
{
    /** Initialize
     *  @param shell Shell to use for displayed dialog
     *  @param tree_position Origin of this display in alarm tree
     *  @param related_display Related display
     */
    public RelatedDisplayAction(final Shell shell,
            final AlarmTreePosition tree_position,
            final GDCDataStructure related_display)
    {
    	super(shell,
              AlarmTreeActionIcon.createIcon("icons/related_display.gif",  //$NON-NLS-1$
                        tree_position),
              related_display.getTeaser(), related_display.getDetails());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public void run()
    {
        // Is command to be handled by open-display extension point mechanism?
        try
        {
            final DisplayReference display = new DisplayReference(command);
            if (display.isValid())
            {
                DisplayUtil.getInstance().openDisplay(display.getFilename(), display.getData());
                return;
            }
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(shell,
                Messages.Error,
                NLS.bind("Error opening {0}:\n{1}", command, ex.getMessage()));
            return;
        }

        // Open workspace file in default editor?
        if (command.startsWith("file:"))
        {
            // Use text after "file:" as filename
            final String name = command.substring(5).trim();

            final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try
            {
                // Copied from org.eclipse.ui.actions.OpenFileAction
                // in org.eclipse.ui.ide
                final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(name);
                if (resource == null)
                    throw new Exception(NLS.bind("Cannot find {0} in workspace", name));
                if (!(resource instanceof IFile))
                    throw new Exception(NLS.bind("{0} is not a workspace file", name));
                IDE.openEditor(page, (IFile) resource);
            }
            catch (Throwable ex)
            {
                MessageDialog.openError(page.getWorkbenchWindow().getShell(),
                    Messages.Error,
                    NLS.bind("Error opening {0}:\n{1}", name, ex.getMessage()));
            }
            return;
        }

        // Open URL in browser?
        if (command.startsWith("http:") ||
            command.startsWith("https:"))
        {
            try
            {
                PlatformUI.getWorkbench().getBrowserSupport()
                    .createBrowser(null).openURL(new URL(command));
            }
            catch (Exception ex)
            {
                MessageDialog.openError(shell, Messages.RelatedDisplayHTTP_Error,
                    NLS.bind(Messages.RelatedDisplayErrorFmt,
                            command, ex.getMessage()));
            }
            return;
        }

        // else: Execute command as received
        super.run();
    }
}












































