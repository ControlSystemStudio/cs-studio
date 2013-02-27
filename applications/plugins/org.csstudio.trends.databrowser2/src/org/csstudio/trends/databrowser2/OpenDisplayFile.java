/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import java.io.File;
import java.io.FileInputStream;

import org.csstudio.openfile.IOpenDisplayAction;
import org.csstudio.trends.databrowser2.editor.DataBrowserEditor;
import org.csstudio.trends.databrowser2.editor.DataBrowserModelEditorInput;
import org.csstudio.trends.databrowser2.editor.EmptyEditorInput;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.utility.singlesource.PathEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorInput;

/** Support opening Data Browser configurations from
 *  the command-line.
 *  plugin.xml registers this {@link IOpenDisplayAction}
 *  for Data Browser "plt" files.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenDisplayFile implements IOpenDisplayAction
{
	/** {@inheritDoc} */
    @Override
	public void openDisplay(final String path, final String data) throws Exception
	{
        final IEditorInput input;
        
        // Try workspace resource
        final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (resource instanceof IFile)
        {
            final IFile file = (IFile) resource;
            if (! file.exists())
            	throw new Exception("File  '" + file + "' does not exist in workspace");
            input = new PathEditorInput(file.getFullPath());
        }
        else
        {   // Try plain file
            final File file = new File(path);
            if (!file.exists())
                throw new Exception("Cannot locate file '" + path + "'");
            // Read model from file, but don't associate a (Workspace) file
            // with it because can't save outside of workspace
            final Model model = new Model();
            model.read(new FileInputStream(file));
            input = new DataBrowserModelEditorInput(new EmptyEditorInput(), model);
        }

        // Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance(input);
        if (editor == null)
            throw new Exception("Cannot create Data Browser");
	}
}
