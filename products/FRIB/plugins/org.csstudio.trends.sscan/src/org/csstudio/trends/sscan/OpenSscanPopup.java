/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.sscan.editor.SscanEditor;
import org.csstudio.trends.sscan.model.ChannelInfo;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.preferences.Preferences;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

/** Command handler for opening Data Browser on the current selection.
 *  Linked from popup menu that is sensitive to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class OpenSscanPopup extends AbstractHandler
{
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        // Create new editor
        final SscanEditor editor = SscanEditor.createInstance();
        if (editor == null)
            return null;
        final Model model = editor.getModel();

        // Add received items
        final IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        try
        {
            if (selection.getFirstElement() instanceof ChannelInfo)
            {   // Received items are from search dialog
                final Object channels[] = selection.toArray();
                for (Object channel : channels)
                {
                    final ChannelInfo info = (ChannelInfo) channel;
                    add(model, info.getProcessVariable());
                }
            }
            else
            {   // Add received PVs with default archive data sources
                final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
                for (ProcessVariable pv : pvs)
                    add(model, pv);
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

    /** Add item
     *  @param model Model to which to add the item
     *  @param pv PV to add
     *  @param archive Archive to use or <code>null</code>
     *  @throws Exception on error
     */
    private void add(final Model model, final ProcessVariable pv) throws Exception
    {
        final ModelItem item = new ModelItem(pv.getName());
        // Add item to new axes
        item.setAxes(model.addAxes());
        item.setSscan(model.addSscan(pv.getName()));
        model.addItem(item);
    }
}
