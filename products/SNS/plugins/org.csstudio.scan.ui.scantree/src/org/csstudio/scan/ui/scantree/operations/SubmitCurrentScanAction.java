/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.ui.ScanUIActivator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/** Action that submits scan from the current editor to the server
 *  @author Kay Kasemir
 */
public class SubmitCurrentScanAction extends Action
{
    public SubmitCurrentScanAction()
    {
        super(Messages.SubmitScan,
                ScanUIActivator.getImageDescriptor("icons/run.png")); //$NON-NLS-1$
    }

    @Override
    public void run()
    {
        final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (! (editor instanceof ScanEditor))
            return;
        final ScanEditor scan_edit = (ScanEditor) editor;
        scan_edit.submitCurrentScan();
    }
}
