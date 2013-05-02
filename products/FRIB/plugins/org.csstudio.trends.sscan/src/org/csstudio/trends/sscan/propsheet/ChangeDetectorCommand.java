package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.Detector;
import org.csstudio.trends.sscan.model.ModelItem;

public class ChangeDetectorCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private Detector old_detector, new_detector;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param axis New value
     */
    public ChangeDetectorCommand(final OperationsManager operations_manager,
            final ModelItem item, final Detector detector)
    {
        this.item = item;
        this.old_detector = item.getDetector();
        this.new_detector = detector;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setDetector(new_detector);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setDetector(old_detector);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
