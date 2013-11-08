/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.gui.CommandListView;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/** (Menu) action to add command to tree
 *
 *  <p>Opens CommandListView and displays guidance.
 *  @author Kay Kasemir
 */
public class AddCommandAction extends OpenViewAction
{
    public AddCommandAction()
    {
        super(CommandListView.ID, Messages.AddCommand, Activator.getImageDescriptor("icons/add.gif")); //$NON-NLS-1$
    }

    @Override
    public void run()
    {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        MessageDialog.openInformation(shell, Messages.AddCommandTitle,
            Messages.AddCommandMessage);
        super.run();
    }
}
