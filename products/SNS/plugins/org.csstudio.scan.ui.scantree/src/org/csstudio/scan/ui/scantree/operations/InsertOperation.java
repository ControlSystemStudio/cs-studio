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
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/** Operation that inserts command into tree
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class InsertOperation extends AbstractOperation
{
    final private ScanEditor editor;
    final private List<ScanCommand> commands;
    final private ScanCommand location;
    final private List<ScanCommand> new_commands;
    final private boolean after;

    /** Initialize
     *  @param editor Editor that submitted this operation
     *  @param location Where to add
     *  @param new_commands Commands to add
     */
    public InsertOperation(final ScanEditor editor,
            final ScanCommand location,
            final List<ScanCommand> new_commands,
            final boolean after)
    {
        this(editor, editor.getModel().getCommands(), location, new_commands, after);
    }

    /** Initialize
     *  @param editor Editor that submitted this operation
     *  @param location Where to add
     *  @param new_commands Commands to add
     */
    public InsertOperation(final ScanEditor editor,
            final List<ScanCommand> commands,
            final ScanCommand location,
            final List<ScanCommand> new_commands,
            final boolean after)
    {
        super("insert");
        this.editor = editor;
        this.commands = commands;
        this.location = location;
        this.new_commands = new_commands;
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
            ScanCommand target = location;
            if (location == null  && commands.size() > 0)
                target = commands.get(commands.size()-1);
            for (ScanCommand command : new_commands)
            {
                editor.getModel().insert(commands, target, command, after);
                target = command;
            }
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
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
            for (ScanCommand command : new_commands)
                editor.getModel().remove(command);
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }

        return Status.OK_STATUS;
    }
}
