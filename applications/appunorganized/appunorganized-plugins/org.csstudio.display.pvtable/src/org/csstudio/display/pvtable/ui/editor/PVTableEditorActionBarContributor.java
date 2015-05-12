/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;

import org.csstudio.display.pvtable.ui.PVTableAction;
import org.csstudio.display.pvtable.ui.RestoreAction;
import org.csstudio.display.pvtable.ui.SnapshotAction;
import org.csstudio.display.pvtable.ui.ToleranceAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/** EditorActionBarContributor for the PV Table
 *  @author Kay Kasemir
 */
public class PVTableEditorActionBarContributor extends
        EditorActionBarContributor
{
    final private PVTableAction snap = new SnapshotAction(null);
    final private PVTableAction restore = new RestoreAction(null);
    final private PVTableAction tolerance = new ToleranceAction(null);

    /** Invoked once when the first PVTableEditor gets opened.
     *  Items stay in toolbar until the last PVTableEditor exits.
     */
    @Override
    public void contributeToToolBar(final IToolBarManager mgr)
    {
        mgr.add(snap);
        mgr.add(restore);
        mgr.add(tolerance);
    }

    /** @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart) */
    @Override
    public void setActiveEditor(final IEditorPart target)
    {
        final PVTableEditor editor = (PVTableEditor) target;
        snap.setViewer(editor.getTableViewer());
        restore.setViewer(editor.getTableViewer());
        tolerance.setViewer(editor.getTableViewer());
    }

    /** @see org.eclipse.ui.part.EditorActionBarContributor#dispose() */
    @Override
    public void dispose()
    {
        snap.setViewer(null);
        restore.setViewer(null);
        tolerance.setViewer(null);
        super.dispose();
    }
}
