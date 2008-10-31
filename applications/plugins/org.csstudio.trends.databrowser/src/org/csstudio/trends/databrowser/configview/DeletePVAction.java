package org.csstudio.trends.databrowser.configview;


import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Remove selected PVs in table from the model.
 * 
 *  @author Kay Kasemir
 */
public class DeletePVAction extends Action
{
    private ConfigView config;

	public DeletePVAction(ConfigView config)
	{
        this.config = config;
		setText(Messages.Delete);
		setToolTipText(Messages.DeleteSelectedEntry);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
		// Conditionally enable this action
		config.getPVTableViewer().addSelectionChangedListener(
		new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean anything = !event.getSelection().isEmpty();
				setEnabled(anything);
			}
		});
	}

	@Override
	public void run()
	{
        IModelItem items[] = config.getSelectedModelEntries();
        if (items == null)
            return;
        Model model = config.getModel();
        if (model == null)
            return;
        // The act of invoking this action via a context menu
        // may also start a cell editor on the PV name.
        // The 'refresh' caused by the item removal
        // will cause that editor to think that somebody just entered
        // a name.
        // The result: It re-enters the PV Name that we just deleted!
        // At least that's what I saw happening when invoking the
        // context menu with 'delete' on OS X via control-click.
        // With a 3-button mouse, the editor never gets invoked
        // on the 'right' button.
        //
        // Anyway: Close any editor before proceeding,
        // since that seems to help in all cases.
        config.getPVTableViewer().cancelEditing();
        
        // Another problem arose in Eclipse 3.4 where the table refresh
        // that was triggered by the model change ran into
        // "Ignored reentrant call" errors.
        // Unclear why exactly that happened, but the JFace code
        // called the PVTableLazyContentProvider recursively because
        // the currently selected elements (the one we're deleting!)
        // had changed in the model.
        // Clearing the selection (since we're deleting those elements anyway)
        // seems to fix that issue.
        config.getPVTableViewer().setSelection(null);
		for (int i = 0; i < items.length; i++)
		{
            final String name = items[i].getName();
            final String formula = model.isUsedInFormula(name);
            if (formula != null)
            {
                MessageDialog.openWarning(config.getSite().getShell(),
                    Messages.RemoveTitle,
                    NLS.bind(Messages.RemoveFormulaInputError, name, formula));
                return;
            }
            model.remove(name);
		}
	}
}
