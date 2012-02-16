/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.TreeManipulator;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/** Handler to insert command into tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class InsertOperation extends AbstractOperation
{
    final private ScanEditor editor;
    final private List<ScanCommand> commands;
    final private ScanCommand location;
    final private ScanCommand command;
    final private boolean after;

    /** Initialize
     *  @param editor Editor that submitted this operation
     *  @param commands Scan commands
     *  @param location Where to add
     *  @param new_commands Command to add
     *
     */
    public InsertOperation(final ScanEditor editor, final List<ScanCommand> commands,
            final ScanCommand location,
            final ScanCommand command,
            final boolean after)
    {
        super("insert");
        this.editor = editor;
        this.commands = commands;
        this.location = location;
        this.command = command;
        this.after = after;
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
            TreeManipulator.insert(commands, location, command, after);
            editor.refresh();
        }
        catch (Exception ex)
        {
            throw new ExecutionException("'Insert' failed", ex);
        }

        return Status.OK_STATUS;
    }

    /** {@inheritDoc} */
    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
        try
        {
            TreeManipulator.remove(commands, command);
            editor.refresh();
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }

        return Status.OK_STATUS;
    }
}
