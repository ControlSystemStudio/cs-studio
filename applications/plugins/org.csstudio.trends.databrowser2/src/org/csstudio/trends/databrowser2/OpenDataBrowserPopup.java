/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.trends.databrowser2.editor.DataBrowserEditor;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.preferences.Preferences;
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
public class OpenDataBrowserPopup extends AbstractHandler
{

    final double period = Preferences.getScanPeriod();
    
    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
    	//Get selection first because the ApplicationContext might change.
        final IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        // Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance();
        if (editor == null)
            return null;
        
        // Add received items
        final Model model = editor.getModel(); 
        
        try
        {
            if (selection.getFirstElement() instanceof ChannelInfo)
            {   // Received items are from search dialog
                final Object channels[] = selection.toArray();
                for (Object channel : channels)
                {
                    final ChannelInfo info = (ChannelInfo) channel;
                    add(model, info.getProcessVariable(), info.getArchiveDataSource());
                }
            }else {
                // Add received PVs with default archive data sources
                final List<TimestampedPV> timestampedPVs = Arrays
                        .asList(AdapterUtil.convert(selection,
                                TimestampedPV.class));
                if (!timestampedPVs.isEmpty()) {

                    // Add received items
                    List<Long> timeStamps = new ArrayList<Long>();

                    for (TimestampedPV timestampedPV : timestampedPVs) {
                        timeStamps.add(timestampedPV.getTime());
                        PVItem item = new PVItem(timestampedPV.getName().trim(), period);
                        item.setAxis(model.addAxis(item.getDisplayName()));
                        item.useDefaultArchiveDataSources();
                        model.addItem(item);
                    }

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Collections.min(timeStamps) - (30 * 60 * 1000));
                    String start_spec = AbsoluteTimeParser.format(c);
                    c.setTimeInMillis(Collections.max(timeStamps) + (30 * 60 * 1000));
                    String end_spec = AbsoluteTimeParser.format(c);
                    model.enableScrolling(false);
                    model.setTimerange(start_spec, end_spec);
                } else {
                    final ProcessVariable[] pvs = AdapterUtil.convert(
                            selection, ProcessVariable.class);
                    for (ProcessVariable pv : pvs)
                        add(model, pv, null);
                }
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
    private void add(final Model model, final ProcessVariable pv,
            final ArchiveDataSource archive) throws Exception
    {
        final PVItem item = new PVItem(pv.getName(), period);
        if (archive == null)
            item.useDefaultArchiveDataSources();
        else
            item.addArchiveDataSource(archive);
        // Add item to new axes
        item.setAxis(model.addAxis(item.getDisplayName()));
        model.addItem(item);
    }
}
