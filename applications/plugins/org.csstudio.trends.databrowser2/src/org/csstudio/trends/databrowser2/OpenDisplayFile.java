/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import org.csstudio.openfile.IOpenDisplayAction;
import org.csstudio.trends.databrowser2.editor.DataBrowserEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

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
        final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if (! (resource instanceof IFile))
        	throw new Exception("Cannot locate '" + path + "' in workspace");
        final IFile file = (IFile) resource;
        if (! file.exists())
        	throw new Exception("File  '" + file + "' does not exist in workspace");

    	// Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance(new DataBrowserInput(file.getFullPath()));
        if (editor == null)
            throw new Exception("Cannot create Data Browser");
	}
}
