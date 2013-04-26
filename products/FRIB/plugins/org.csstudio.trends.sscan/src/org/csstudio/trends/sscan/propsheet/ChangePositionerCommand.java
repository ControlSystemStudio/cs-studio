package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.model.Positioner;

public class ChangePositionerCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private Positioner old_positioner, new_positioner;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param axis New value
     */
    public ChangePositionerCommand(final OperationsManager operations_manager,
            final ModelItem item, final Positioner positioner)
    {
        this.item = item;
        this.old_positioner = item.getPositioner();
        this.new_positioner = positioner;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setPositioner(new_positioner);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setPositioner(old_positioner);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
