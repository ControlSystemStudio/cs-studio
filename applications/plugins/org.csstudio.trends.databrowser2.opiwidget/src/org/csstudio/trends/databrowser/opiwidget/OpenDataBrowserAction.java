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
import org.csstudio.utility.singlesource.PathEditorInput;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Action for context menu object contribution that opens
 *  the full Data Browser for the model in the Data Browser widget
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenDataBrowserAction extends Action
{
    final private DataBrowserWidgedEditPart edit_part;

    public OpenDataBrowserAction(final DataBrowserWidgedEditPart edit_part)
    {
        super(Messages.OpenDataBrowser,
              AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/databrowser.png"));
        this.edit_part = edit_part;
    }

    @Override
    public void run()
    {
        final DataBrowserWidgedModel model = edit_part.getWidgetModel();
        IPath filename = model.getExpandedFilename();
        if (!filename.isAbsolute())
            filename = ResourceUtil.buildAbsolutePath(model, filename);
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            final IEditorInput input = new  PathEditorInput(filename);
            final DataBrowserModelEditorInput model_input =
                    new DataBrowserModelEditorInput(input, model.createDataBrowserModel());
            page.openEditor(model_input, DataBrowserEditor.ID, true);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(page.getActivePart().getSite().getShell(),
                Messages.Error,
                NLS.bind(Messages.OpenDataBrowserErrorFmt, filename.toString()),
                ex);
        }
    }
}
