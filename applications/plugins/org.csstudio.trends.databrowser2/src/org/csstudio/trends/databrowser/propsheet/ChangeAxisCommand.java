package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.ModelItem;

/** Undo-able command to change item's axis
 *  @author Kay Kasemir
 */
public class ChangeAxisCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private AxisConfig old_axis, new_axis;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param axis New value
     */
    public ChangeAxisCommand(final OperationsManager operations_manager,
            final ModelItem item, final AxisConfig axis)
    {
        this.item = item;
        this.old_axis = item.getAxis();
        this.new_axis = axis;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    public void redo()
    {
        item.setAxis(new_axis);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        item.setAxis(old_axis);
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
