/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.actions;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

/** Handler to paste command from clibboard into tree
 *  @author Kay Kasemir
 */
public class PasteCommandHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ScanEditor editor = ScanEditor.getActiveEditor();
        if (editor == null)
            return null;

        // Get command from clipboard
        final Clipboard clip = new Clipboard(Display.getCurrent());
        final String text = (String) clip.getContents(TextTransfer.getInstance());
        clip.dispose();

        // Get command from XML
        // TODO  new ScanCommandFactory().
        MessageDialog.openInformation(editor.getSite().getShell(),
                "TODO", text);

        // Add command to scan
        final List<ScanCommand> commands = editor.getCommands();
        final ScanCommand location = editor.getSelectedCommand();

        // TODO Add command at location

        editor.refresh();

        return null;
    }
}
