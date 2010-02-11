package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;

/** Undo-able command to change axis configuration
 *  @author Kay Kasemir
 */
public class ChangeAxisConfigCommand implements IUndoableCommand
{
    final private AxisConfig axis;
    final private AxisConfig old_config;
    private AxisConfig new_config;

    /** Register the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param axis Axis configuration to undo/redo
     */
    public ChangeAxisConfigCommand(final OperationsManager operations_manager,
            final AxisConfig axis)
    {
        this.axis = axis;
        this.old_config = axis.copy();
        operations_manager.addCommand(this);
    }

    /** Must be called after the original configuration was changed */
    public void rememberNewConfig()
    {
        this.new_config = axis.copy();
    }
    
    /** {@inheritDoc} */
    public void redo()
    {
        apply(new_config);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        apply(old_config);
    }
    
    /** Apply whatever's different in the given configuration to the axis
     *  @param config
     */
    private void apply(final AxisConfig config)
    {
        if (! axis.getName().equals(config.getName()))
            axis.setName(config.getName());
        if (axis.getMin() != config.getMin()  ||
            axis.getMax() != config.getMax())
            axis.setRange(config.getMin(), config.getMax());
        if (! axis.getColor().equals(config.getColor()))
            axis.setColor(config.getColor());
        if (! axis.isLogScale() == config.isLogScale())
            axis.setLogScale(config.isLogScale());
        if (! axis.isAutoScale() == config.isAutoScale())
            axis.setAutoScale(config.isAutoScale());
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
