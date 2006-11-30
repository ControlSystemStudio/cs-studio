package org.csstudio.trends.databrowser.configview;


import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Remove selected archive info entries from the model.
 * 
 *  @author Kay Kasemir
 */
public class DeleteArchiveAction extends Action
{
    private ConfigView config;

	public DeleteArchiveAction(ConfigView config)
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
		config.getArchiveTableViewer().addSelectionChangedListener(
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
        IArchiveDataSource archives[] = config.getSelectedArchiveEntries();
		if (items == null  ||  archives == null)
			return;
        Model model = config.getModel();
        if (model == null)
            return;
        for (IModelItem item : items)
            for (IArchiveDataSource archive : archives)
            {
                //System.out.println("remove from " + item.getName()
                //                + " : " + archive.getName());
                item.removeArchiveDataSource(archive);
            }
	}
}
