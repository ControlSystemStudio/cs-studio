package org.csstudio.trends.databrowser.configview;


import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
        // context menu with 'delete' on OS X via conrol-click.
        // With a 3-button mouse, the editor never gets invoked
        // on the 'right' button.
        //
        // Anyway: Close any editor before proceeding,
        // since that seems to help in all cases.
        config.getPVTableViewer().cancelEditing();
		for (int i = 0; i < items.length; i++)
            model.remove(items[i].getName());
	}
}
