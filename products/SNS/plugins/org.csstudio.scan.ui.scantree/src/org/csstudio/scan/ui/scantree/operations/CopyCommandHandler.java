/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.ScanEditorContributor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/** Handler to copy selected commands onto clipboard
 *  @author Kay Kasemir
 */
public class CopyCommandHandler extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        final ScanEditor editor = ScanEditorContributor.getCurrentScanEditor();
        if (editor == null)
            return null;

        final List<ScanCommand> selection = editor.getSelectedCommands();

        try
        {
            // Format as XML
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            XMLCommandWriter.write(buf, selection);
            buf.close();

            // Put command onto clipboard
            final Clipboard clip = new Clipboard(Display.getCurrent());
            clip.setContents(new Object[] { buf.toString() }, new Transfer[] { TextTransfer.getInstance() });
            clip.dispose();
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }

        return null;
    }
}
