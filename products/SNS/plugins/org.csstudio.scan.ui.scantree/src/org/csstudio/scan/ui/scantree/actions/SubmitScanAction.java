/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.actions;

import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanTreeGUI;
import org.csstudio.scan.ui.scantree.ScanTreeGUIListener;
import org.eclipse.jface.action.Action;

/** Action that submits a scan to the server
 *  @author Kay Kasemir
 */
public class SubmitScanAction extends Action
{
    final private ScanTreeGUIListener listener;
    final private ScanTreeGUI gui;
    
    public SubmitScanAction(final ScanTreeGUIListener listener,
            final ScanTreeGUI gui)
    {
        super(Messages.SubmitScan,
                Activator.getImageDescriptor("icons/run.png")); //$NON-NLS-1$
        this.listener = listener;
        this.gui = gui;
    }

    @Override
    public void run()
    {
        listener.submitScan(gui.getCommands());
    }
}
