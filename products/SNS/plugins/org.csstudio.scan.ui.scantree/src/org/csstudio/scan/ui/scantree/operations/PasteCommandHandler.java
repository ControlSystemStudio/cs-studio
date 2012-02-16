/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.TreeManipulator;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

/** Handler to paste command from clipboard into tree
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
        try
        {
            final List<ScanCommand> received_commands;
            final ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes());
            final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
            received_commands = reader.readXMLStream(stream);
            stream.close();

            // Add command to scan, either at selected command or at end
            final List<ScanCommand> commands = editor.getCommands();
            final ScanCommand location = editor.getSelectedCommand();
            TreeManipulator.insertAfter(commands, location, received_commands);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(editor.getSite().getShell(),
                Messages.Error,
                NLS.bind(Messages.XMLCommandErrorFmt, text),
                ex);
            return null;
        }


        editor.refresh();

        return null;
    }
}
