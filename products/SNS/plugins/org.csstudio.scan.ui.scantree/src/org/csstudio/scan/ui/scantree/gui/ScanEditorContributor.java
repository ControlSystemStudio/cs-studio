/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.lang.ref.WeakReference;

import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.properties.ScanCommandPVAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/** Add actions to toolbar while ScanEditor is active
 *
 *  <p>Also tracks the currently active editor
 *  for global actions, handlers, ...
 *
 *  @author Kay Kasemir
 */
public class ScanEditorContributor extends EditorActionBarContributor
{
    /** Weak reference to the last active scan editor
     *
     *  <p>Getting the current editor via
     *  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
     *  does not work because activating a context menu
     *  on a ScanEditor may invoke an object contribution,
     *  and when that then tries to retrieve the current selection
     *  via the {@link ScanCommandPVAdapterFactory},
     *  the scan editor was no longer the active editor.
     *
     *  <p>Tracking the last active scan editor via
     *  setActiveEditor() seems to work better.
     *
     *  <p>Since we never clear the reference, we use a weak
     *  reference.
     */
    private static WeakReference<ScanEditor> editor = null;

    /** @return Currently active scan editor or <code>null</code> */
    public static ScanEditor getCurrentScanEditor()
    {
        if (editor != null)
            return editor.get();
        return null;
    }

    /** Static setter to please FindBugs */
    private static void setEditor(final ScanEditor editor)
    {
        if (editor == null)
            ScanEditorContributor.editor = null;
        else
            ScanEditorContributor.editor = new WeakReference<ScanEditor>(editor);
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveEditor(final IEditorPart editor)
    {
        if (editor instanceof ScanEditor)
            setEditor((ScanEditor)editor);
        else
            setEditor(null);

        // With global Actions, this required code like
        // getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), my_copy_actionl);
        // Now using menu command (from o.c.ui.menu.app) and handlers (from this plugin)
    }
}
