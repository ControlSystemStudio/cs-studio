package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.AxisConfig;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Action to delete value axes from model
 *  @author Kay Kasemir
 */
public class DeleteAxesAction extends Action
{
    final private OperationsManager operations_manager;
    final private Model model;
    final private TableViewer axes_table;

    public DeleteAxesAction(final OperationsManager operations_manager,
            final TableViewer axes_table, final Model model)
    {
        super(Messages.DeleteAxis,
                PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.operations_manager = operations_manager;
        this.axes_table = axes_table;
        this.model = model;
    }

    @Override
    public void run()
    {
        // Get selected axis items
        final Object sel[] = 
            ((IStructuredSelection)axes_table.getSelection()).toArray();
        if (sel.length <= 0)
            return;
        final AxisConfig axes[] = new AxisConfig[sel.length];
        for (int i=0; i<axes.length; ++i)
        {
            axes[i] = (AxisConfig)sel[i];
            // Check if axis is used by any model items
            for (int j=0; j<model.getItemCount(); ++j)
            {
                if (model.getItem(j).getAxis() == axes[i])
                {
                    MessageDialog.openWarning(axes_table.getTable().getShell(),
                            Messages.DeleteAxis,
                            NLS.bind(Messages.DeleteAxisWarningFmt,
                                    axes[i].getName(), model.getItem(j).getName()));
                    return;
                }
            }
        }
        new DeleteAxesCommand(operations_manager, model, axes);
    }
}
