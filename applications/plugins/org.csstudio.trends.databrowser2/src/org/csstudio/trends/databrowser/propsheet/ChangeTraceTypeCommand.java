package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.TraceType;

/** Undo-able command to change item's trace type
 *  @author Kay Kasemir
 */
public class ChangeTraceTypeCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private TraceType old_trace_type, new_trace_type;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_trace_type New value
     */
    public ChangeTraceTypeCommand(final OperationsManager operations_manager,
            final ModelItem item, final TraceType new_trace_type)
    {
        this.item = item;
        this.old_trace_type = item.getTraceType();
        this.new_trace_type = new_trace_type;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    public void redo()
    {
        item.setTraceType(new_trace_type);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        item.setTraceType(old_trace_type);
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.TraceType;
    }
}
