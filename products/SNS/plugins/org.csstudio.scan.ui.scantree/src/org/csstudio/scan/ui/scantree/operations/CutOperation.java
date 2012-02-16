/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.TreeManipulator;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory;

/** Handler to remove selected command from tree,
 *  putting it onto clipboard
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CutOperation extends AbstractOperation
{
    final private ScanEditor editor;
    final private List<ScanCommand> commands;
    final private ScanCommand command;
    private TreeManipulator.RemovalInfo removal = null;

    /** Initialize
     *  @param editor Editor that submitted this operation
     *  @param commands Scan commands
     *  @param command Command to remove
     */
    public CutOperation(final ScanEditor editor, final List<ScanCommand> commands,
            final ScanCommand command)
    {
        super(ActionFactory.CUT.getId());
        this.editor = editor;
        this.commands = commands;
        this.command = command;
    }

    /** {@inheritDoc} */
    @Override
    public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
        return redo(monitor, info);
    }

    /** {@inheritDoc} */
    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
                    throws ExecutionException
    {
        try
        {
            // Remove command from scan
            removal = TreeManipulator.remove(commands, command);
            editor.refresh();

            // Format as XML
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            XMLCommandWriter.write(buf, Arrays.asList(command));
            buf.close();

            // Put onto clipboard
            final Clipboard clip = new Clipboard(Display.getCurrent());
            clip.setContents(new Object[] { buf.toString() },
                    new Transfer[] { TextTransfer.getInstance() });
            clip.dispose();
        }
        catch (Exception ex)
        {
            throw new ExecutionException("'Cut' failed", ex);
        }

        return Status.OK_STATUS;
    }

    /** {@inheritDoc} */
    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
        if (removal == null)
            throw new ExecutionException("Noting to undo for 'cut'");
        try
        {
            removal.undo(commands);
            editor.refresh();
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }

        return Status.OK_STATUS;
    }
}
