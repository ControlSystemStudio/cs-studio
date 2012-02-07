/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.trends.databrowser2.editor.DataBrowserEditor;
import org.csstudio.trends.databrowser2.editor.DataBrowserModelEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/** Action for context menu object contribution that opens
 *  the full Data Browser for the model in the Data Browser widget
 *  @author Kay Kasemir
 */
public class OpenDataBrowserAction extends DataBrowserWidgetAction
{
    /** Open Data Browser */
    @Override
    protected void doRun(final IWorkbenchPage page, final DataBrowserWidgedEditPart edit_part)
    {
        // In run mode, we always seem to receive the absolute path.
        // In edit mode, a relative path it not resolved
        // unless it's first converted to the absolute path.
        final DataBrowserWidgedModel model = edit_part.getWidgetModel();
        IPath filename = model.getExpandedFilename();
        if(!filename.isAbsolute())
            filename = ResourceUtil.buildAbsolutePath(model, filename);
        try
        {
            final IFile file_input = getIFileFromIPath(filename);
            final DataBrowserModelEditorInput model_input =
                    new DataBrowserModelEditorInput(new FileEditorInput(file_input), model.createDataBrowserModel());
            IDE.openEditor(page, model_input, DataBrowserEditor.ID, true);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(page.getActivePart().getSite().getShell(),
                Messages.Error,
                NLS.bind(Messages.OpenDataBrowserErrorFmt, filename.toString()),
                ex);
        }
    }
	/**Get the IFile from IPath.
	 * @param path Path to file in workspace
	 * @return the IFile. <code>null</code> if no IFile on the path, file does not exist, internal error.
	 */
	public static IFile getIFileFromIPath(final IPath path)
	{
	    try
	    {
    		final IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
    				path, false);
    		if (r!= null && r instanceof IFile)
		    {
    		    final IFile file = (IFile) r;
    		    if (file.exists())
    		        return file;
		    }
	    }
	    catch (Exception ex)
	    {
	        // Ignored
	    }
	    return null;
	}
}
