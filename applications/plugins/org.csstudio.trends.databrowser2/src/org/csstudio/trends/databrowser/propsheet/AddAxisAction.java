package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;

/** Action to add new value axis to model
 *  @author Kay Kasemir
 */
public class AddAxisAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;

    public AddAxisAction(final OperationsManager operations_manager,
            final Model model)
    {
        super(Messages.AddAxis,
                Activator.getDefault().getImageDescriptor("icons/add.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.model = model;
    }

    @Override
    public void run()
    {
        new AddAxisCommand(operations_manager, model);
    }
}
