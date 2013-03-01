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
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;

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
