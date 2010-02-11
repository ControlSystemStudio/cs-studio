package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.model.RequestType;

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
    public void redo()
    {
        item.setRequestType(new_request_type);
    }

    /** {@inheritDoc} */
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
