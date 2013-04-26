/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.model.RemovalInfo;
import org.csstudio.scan.ui.scantree.model.ScanTreeModel;
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

/** Operation that removes commands from tree, putting them onto clipboard
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CutOperation extends AbstractOperation
{
    final private ScanTreeModel model;
    final private List<ScanCommand> to_remove;
    private List<RemovalInfo> removals = null;

    /** Initialize
     *  @param model Model where insertions should be performed
     *  @param to_remove Commands to remove
     */
    public CutOperation(final ScanTreeModel model, final List<ScanCommand> to_remove)
    {
        super(ActionFactory.CUT.getId());
        this.model = model;
        this.to_remove = to_remove;
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
            // Remove commands from scan, going in reverse:
            // The list may contain a loop and items in that loop.
            // When first removing the loop, the items in the loop
            // can no longer be removed...
            // Going in reverse avoids that problem.
            //
            // Similarly, removed items are remembered in reverse
            // so that the undo can simply undo each removed item.
            removals = new ArrayList<RemovalInfo>();
            for (int i=to_remove.size()-1;  i>=0;  --i)
            {
                final ScanCommand command = to_remove.get(i);
                removals.add(0, model.remove(command));
            }

            // Format as XML
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            XMLCommandWriter.write(buf, to_remove);
            buf.close();

            // Put onto clipboard
            final Clipboard clip = new Clipboard(Display.getCurrent());
            clip.setContents(new Object[] { buf.toString() },
                    new Transfer[] { TextTransfer.getInstance() });
            clip.dispose();
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
        if (removals == null)
            throw new ExecutionException("Noting to undo for 'cut'");
        try
        {
            for (RemovalInfo removal : removals)
                removal.undo();
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }

        return Status.OK_STATUS;
    }
}
