package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;

/** Action to delete all unused value axes from model
 *  @author Kay Kasemir
 */
public class RemoveUnusedAxesAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;

    public RemoveUnusedAxesAction(final OperationsManager operations_manager,
            final Model model)
    {
        super(Messages.RemoveEmptyAxes,
              Activator.getDefault().getImageDescriptor("icons/remove_unused.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.model = model;
    }

    @Override
    public void run()
    {
        AxisConfig axis = model.getEmptyAxis();
        while (axis != null)
        {
            new DeleteAxisCommand(operations_manager, model, axis);
            axis = model.getEmptyAxis();
        }
    }
}
