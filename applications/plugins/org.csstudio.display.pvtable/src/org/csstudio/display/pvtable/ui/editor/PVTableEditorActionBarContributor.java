/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;


import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.display.pvtable.ui.ConfigAction;
import org.csstudio.display.pvtable.ui.RestoreAction;
import org.csstudio.display.pvtable.ui.SnapshotAction;
import org.csstudio.display.pvtable.ui.StartStopAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/** EditorActionBarContributor for the PV Table
 *  @author Kay Kasemir
 */
public class PVTableEditorActionBarContributor extends
        EditorActionBarContributor
{
    private ConfigAction config;
    private StartStopAction start_stop;
    private SnapshotAction snap;
    private RestoreAction restore;

    /** Invoked once when the first PVTableEditor gets opened.
     *  Items stay in toolbar until the last PVTableEditor exits.
     */
    @Override
    public void contributeToToolBar(IToolBarManager mgr)
    {
        config = new ConfigAction(null);
        start_stop = new StartStopAction(null);
        snap = new SnapshotAction(null);
        restore = new RestoreAction(null, null);
        mgr.add(config);
        mgr.add(start_stop);
        mgr.add(snap);
        mgr.add(restore);
    }

    /** @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart) */
    @Override
    public void setActiveEditor(IEditorPart target)
    {
        PVTableEditor editor = (PVTableEditor) target;
        PVListModel pv_list = editor.getModel();
        config.setPVListModel(pv_list);
        start_stop.setPVListModel(pv_list);
        snap.setPVListModel(pv_list);
        restore.setPVListModel(pv_list);
    }

    /** @see org.eclipse.ui.part.EditorActionBarContributor#dispose() */
    @Override
    public void dispose()
    {
        config.setPVListModel(null);
        start_stop.setPVListModel(null);
        snap.setPVListModel(null);
        restore.setPVListModel(null);
        super.dispose();
    }
}
