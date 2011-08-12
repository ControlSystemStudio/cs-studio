/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.propsheet;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.model.RequestType;
import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;

/** Undo-able command to change a PV item's request type
 *  @author Kay Kasemir
 */
public class ChangeRequestTypeCommand implements IUndoableCommand
{
    final private PVItem item;
    final private RequestType old_request_type, new_request_type;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_trace_type New value
     */
    public ChangeRequestTypeCommand(final OperationsManager operations_manager,
            final PVItem item, final RequestType new_request_type)
    {
        this.item = item;
        this.old_request_type = item.getRequestType();
        this.new_request_type = new_request_type;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setRequestType(new_request_type);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setRequestType(old_request_type);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.RequestType;
    }
}
