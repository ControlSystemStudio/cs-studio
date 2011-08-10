/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/** Context menu command handler for editing a launch configuration
 *  @author Kay Kasemir
 */
public class EditLaunchConfiguration extends AbstractHandler
{
	@Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final IStructuredSelection selection =
			(IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty())
			return null;
		final Object element = selection.getFirstElement();
		if (! (element instanceof IFile))
			return null;
		final IFile file = (IFile) element;
		
		LaunchConfig config;
		try
		{
			config = new LaunchConfig(file.getContents());
		}
		catch (Exception ex)
		{
			config = new LaunchConfig();
		}
		
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final LaunchConfigDialog dlg = new LaunchConfigDialog(shell, config);
		if (dlg.open() != Window.OK)
			return null;
		
		// Update file
		try
        {
	        file.setContents(
	        	new ByteArrayInputStream(dlg.getConfig().getXML().getBytes()),
	        	IResource.FORCE, null);
        }
        catch (CoreException ex)
        {
        	MessageDialog.openError(shell,
        			Messages.Error,
        			NLS.bind(Messages.LaunchConfigUpdateErrorFmt,
        					ex.getMessage()));
        }
		
	    return null;
    }
}
