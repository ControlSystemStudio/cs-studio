/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.trends.databrowser.editor.DataBrowserEditor;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening Data Browser on the current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class OpenDataBrowserPopup extends AbstractHandler
{
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        // Retrieve the selection and the current page
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);

        // Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance();
        if (editor == null)
            return null;
        final Model model = editor.getModel();
        final double period = Preferences.getScanPeriod();
        try
        {
            // Add received PVs
            for (ProcessVariable pv : pvs)
            {
                final PVItem item = new PVItem(pv.getName(), period);
                if (pv instanceof IProcessVariableWithArchive)
                {   // Use received archive
                    final IArchiveDataSource archive =
                        ((IProcessVariableWithArchive) pv).getArchiveDataSource();
                    item.addArchiveDataSource(new ArchiveDataSource(archive));
                }
                else
                    item.useDefaultArchiveDataSources();
                // Add items to new axes
                item.setAxis(model.addAxis());
                model.addItem(item);
            }
        }
        catch (Exception ex)
        {
            MessageDialog.openError(editor.getSite().getShell(),
                    Messages.Error,
                    NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
        return null;
    }
}
