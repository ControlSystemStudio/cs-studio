/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

/** Operation for changing a property of a ScanCommand
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PropertyChangeOperation extends AbstractOperation
{
    final private ScanEditor editor;
    final private ScanCommand command;
    final private String property;
    final private Object old_value;
    final private Object new_value;

    /** Initialize
     *  @param editor Scan editor that owns the command
     *  @param command Command
     *  @param property Property that should be changed
     *  @param new_value New value of the property
     *  @throws Exception on error getting the old (current) value
     */
    public PropertyChangeOperation(final ScanEditor editor,
            final ScanCommand command,
            final String property,
            final Object new_value) throws Exception
    {
        super("property");
        this.editor = editor;
        this.command = command;
        this.property = property;
        this.old_value = command.getProperty(property);
        this.new_value = new_value;
    }

    public long getCommandAddress()
    {
        return command.getAddress();
    }

    public String getProperty()
    {
        return property;
    }

    public Object getValue()
    {
        return new_value;
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
        changeProperty(new_value);
        return Status.OK_STATUS;
    }

    /** {@inheritDoc} */
    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
        changeProperty(old_value);
        return Status.OK_STATUS;
    }

    /** Set property to desired value,
     *  updating scan server in background Job.
     *
     *  @param value Desired value
     *  @throws ExecutionException
     */
    private void changeProperty(final Object value) throws ExecutionException
    {
        final Job job = new Job(toString())
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                // Attempt update of 'live' editor,
                // sending change to the scan server
                try
                {
                    editor.changeLiveProperty(command, property, value);
                }
                catch (Exception ex)
                {
                	final String operation = PropertyChangeOperation.this.toString();
                    return new Status(IStatus.WARNING, Activator.PLUGIN_ID, operation, ex);
                }

                // Update local model in UI thread
                final Shell shell = editor.getSite().getShell();
                shell.getDisplay().asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            editor.getModel().changeProperty(command, property, value);
                        }
                        catch (Exception ex)
                        {
                            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
                        }
                    }
                });

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public String toString()
    {
        return "Change " + command.getCommandName() + " " + property
                + " from " + old_value + " to " + new_value;
    }
}
