package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;

/** Undo-able command to delete value axes from Msodel
 *  @author Kay Kasemir
 */
public class DeleteAxesCommand implements IUndoableCommand
{
    /** Model to which axis is added */
    final private Model model;
    
    /** The axis that was added */
    final private AxisConfig axes[];

    public DeleteAxesCommand(final OperationsManager operationsManager, 
            final Model model, final AxisConfig axes[])
    {
        this.model = model;
        this.axes = axes;
        operationsManager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    public void redo()
    {
        for (AxisConfig axis : axes)
            model.removeAxis(axis);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        for (AxisConfig axis : axes)
            model.addAxis(axis);
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.DeleteAxis;
    }
}
