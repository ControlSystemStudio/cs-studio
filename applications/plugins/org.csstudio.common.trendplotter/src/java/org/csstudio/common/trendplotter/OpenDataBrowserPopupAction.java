/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter;


//import org.csstudio.platform.model.IArchiveDataSource;
//import org.csstudio.platform.model.IProcessVariable;
//import org.csstudio.platform.model.IProcessVariableWithArchive;
//import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.common.trendplotter.editor.DataBrowserEditor;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

import com.sun.xml.internal.bind.v2.TODO;

/** Object contribution registered in plugin.xml for context menus with 
 *  IProcessVariable in the current selection.
 *  @author Kay Kasemir
 */
//TODO jhatje: implement new datatype
public class OpenDataBrowserPopupAction// extends ProcessVariablePopupAction
{
//    /** {@inheritDoc} */
//    @Override
//    public void handlePVs(final IProcessVariable pvNames[])
//    {
//        // Create new editor
//        final DataBrowserEditor editor = DataBrowserEditor.createInstance();
//        if (editor == null)
//            return;
//        final Model model = editor.getModel();
//        final double period = Preferences.getScanPeriod();
//        try
//        {
//            // Add received PVs
//            for (IProcessVariable pv : pvNames)
//            {
//                final PVItem item = new PVItem(pv.getName(), period);
//                if (pv instanceof IProcessVariableWithArchive)
////TODO (jhatje): Check datatype in archive channel view 
//                {   // Use received archive
//                    final IArchiveDataSource archive =
//                        ((IProcessVariableWithArchive) pv).getArchiveDataSource();
//                    add(model, pv, null);
//                }
//                else
//                	add(model, pv, null);
//            }
//        }
//        catch (Exception ex)
//        {
//            MessageDialog.openError(editor.getSite().getShell(),
//                    Messages.Error,
//                    NLS.bind(Messages.ErrorFmt, ex.getMessage()));
//        }
//    }
//    
//    /** Add item
//     *  @param model Model to which to add the item
//     *  @param pv PV to add
//     *  @param archive Archive to use or <code>null</code>
//     *  @throws Exception on error
//     */
//    private void add(final Model model, final IProcessVariable pv,
//            final ArchiveDataSource archive) throws Exception
//    {
//        final double period = Preferences.getScanPeriod();
//        final PVItem item = new PVItem(pv.getName(), period);
//        if (archive == null)
//            item.useDefaultArchiveDataSources();
//        else
//            item.addArchiveDataSource(archive);
//        // Add item to new axes
//        item.setAxis(model.addAxis());
//        model.addItem(item);
//    }
}
