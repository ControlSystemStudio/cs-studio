/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Undo-able command to change item's Live Sample Buffer Size
 *  @author Kay Kasemir
 */
public class ChangeLiveCapacityCommand implements IUndoableCommand
{
    final private Shell shell;
    final private PVItem item;
    final private int old_size, new_size;

    /** Register and perform the command
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_size New value
     */
    public ChangeLiveCapacityCommand(final Shell shell,
            final OperationsManager operations_manager,
            final PVItem item, final int new_size)
    {
        this.shell = shell;
        this.item = item;
        this.old_size = item.getLiveCapacity();
        this.new_size = new_size;

        // Exit before registering for undo because there's nothing to undo
        if (!apply(new_size))
            return;
        operations_manager.addCommand(this);
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        apply(new_size);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        apply(old_size);
    }

    /** Change item's data buffer
     *  @param size Desired size
     *  @return <code>true</code> on success
     */
    private boolean apply(final int size)
    {
        try
        {
            item.setLiveCapacity(size);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                NLS.bind(Messages.ChangeLiveCapacityCommandErrorFmt,
                        new Object[] { item.getName(), size, ex.getMessage()}));
            return false;
        }
        return true;
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.LiveSampleBufferSize;
    }
}
