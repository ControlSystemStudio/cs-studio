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
import org.csstudio.trends.databrowser2.editor.DataBrowserModelEditorInput;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.utility.singlesource.PathEditorInput;
import org.csstudio.utility.singlesource.ResourceHelper;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.IPath;
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
        final Model model = new Model();

        // Read file
        final ResourceHelper resources = SingleSourcePlugin.getResourceHelper();
        final IPath ipath = resources.newPath(path);
        model.read(resources.getInputStream(ipath));

        final IEditorInput input = new DataBrowserModelEditorInput(new PathEditorInput(ipath), model);

        // Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance(input);
        if (editor == null)
            throw new Exception("Cannot create Data Browser");
	}
}
