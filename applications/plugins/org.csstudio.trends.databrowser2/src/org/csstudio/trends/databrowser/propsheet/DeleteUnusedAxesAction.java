package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Action to delete all unused value axes from model
 *  @author Kay Kasemir
 */
public class DeleteUnusedAxesAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;

    public DeleteUnusedAxesAction(final OperationsManager operations_manager,
            final Model model)
    {
        super(Messages.DeleteEmptyAxes,
                PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
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
