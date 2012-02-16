/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;

/** Handler for the Undo command
 *
 *  <p>Invoked via context menu or editor toolbar
 *  contributions from plugin.xml
 *
 *  <p>All scan editors share an operations history,
 *  but each editor adds operations for its own context.
 *  @author Kay Kasemir
 */
public class UndoHandler extends AbstractHandler implements IOperationHistoryListener
{
    public UndoHandler()
    {
        ScanEditor.getOperationHistory().addOperationHistoryListener(this);
        setBaseEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        ScanEditor.getOperationHistory().removeOperationHistoryListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        try
        {
            ScanEditor.getOperationHistory().undo(IOperationHistory.GLOBAL_UNDO_CONTEXT, null, null);
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex.getMessage(), ex);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void historyNotification(final OperationHistoryEvent event)
    {
        if (event.getEventType() == OperationHistoryEvent.OPERATION_ADDED)
            setBaseEnabled(true);
        else
            setBaseEnabled(event.getHistory().getUndoOperation(IOperationHistory.GLOBAL_UNDO_CONTEXT) != null);
    }
}
