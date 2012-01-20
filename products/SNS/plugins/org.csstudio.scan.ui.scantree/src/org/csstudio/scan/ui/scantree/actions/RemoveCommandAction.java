/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.actions;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanTreeGUI;
import org.csstudio.scan.ui.scantree.TreeManipulator;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** (Menu) action to add command to tree
 *  @author Kay Kasemir
 */
public class RemoveCommandAction extends Action
{
    final private ScanTreeGUI gui;

    public RemoveCommandAction(final ScanTreeGUI gui)
    {
        super(Messages.RemoveCommand,
              PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.gui = gui;
    }

    @Override
    public void run()
    {
        final List<ScanCommand> commands = gui.getCommands();
        final ScanCommand command = gui.getSelectedCommand();
        TreeManipulator.remove(commands, command);
        gui.refresh();
    }
}
