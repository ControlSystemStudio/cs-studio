/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.trends.databrowser.editor.DataBrowserEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

/** Action for context menu object contribution that opens
 *  the full Data Browser for the model in the Data Browser widget
 *  @author Kay Kasemir
 */
public class OpenDataBrowserAction implements IObjectActionDelegate
{
    private DataBrowserWidgedEditPart edit_part = null;
    private IWorkbenchPage page = null;

    /** Track the active workbench page */
    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart part)
    {
        page = part.getSite().getPage();
    }

    /** Track the Data Browser configuration filename */
    @Override
    public void selectionChanged(final IAction action, final ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
        {
            final Object o = ((IStructuredSelection)selection).getFirstElement();
            if (o instanceof DataBrowserWidgedEditPart)
            {
                edit_part = (DataBrowserWidgedEditPart)o;
                return;
            }
        }
        edit_part = null;
    }

    /** Open Data Browser */
    @Override
    public void run(final IAction action)
    {
        if (edit_part == null  ||  page == null)
            return;

        // TODO: In run mode, this works OK, but in edit mode, a relative path it not resolved
        final IPath filename = edit_part.getWidgetModel().getFilename();
        try
        {
            IFile input = ResourceUtil.getIFileFromIPath(filename);
            IDE.openEditor(page, input, DataBrowserEditor.ID, true);
        }
        catch (Exception ex)
        {
            new ErrorDetailDialog(page.getActivePart().getSite().getShell(),
                Messages.Error,
                NLS.bind(Messages.OpenDataBrowserErrorFmt, filename.toString()),
                NLS.bind(Messages.ErrorDetailFmt, filename.toString(), ex.getMessage())).open();
        }
    }
}
