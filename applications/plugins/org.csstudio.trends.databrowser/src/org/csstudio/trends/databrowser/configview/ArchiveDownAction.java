package org.csstudio.trends.databrowser.configview;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/** Move selected archive info entry 'down' in the list.
 * 
 *  @author Kay Kasemir
 */
public class ArchiveDownAction extends Action
{
    private ConfigView config;

    private ImageRegistry images = null;
    private static final String DOWN = "down"; //$NON-NLS-1$
    
	public ArchiveDownAction(ConfigView config)
	{
        this.config = config;

        images = new ImageRegistry();
        try
        {
            images.put(DOWN, Plugin.getImageDescriptor("icons/down.gif")); //$NON-NLS-1$
        }
        catch (Exception e)
        {
            Plugin.logException("Missing image", e); //$NON-NLS-1$
        }
        setText(Messages.Down);
		setToolTipText(Messages.MoveArchsDown);
		setImageDescriptor(images.getDescriptor(DOWN));
		setEnabled(false);
		// Conditionally enable this action
		config.getArchiveTableViewer().addSelectionChangedListener(
		new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
                setEnabled(! event.getSelection().isEmpty());
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
        for (IModelItem item : items)
        {
            if (! (item instanceof IPVModelItem))
                continue;
            IPVModelItem pv_item = (IPVModelItem) item;
            // iterate in reverse order to avoid clobbering what you
            // just moved down with the next item...
            for (IArchiveDataSource archive : archives)
                pv_item.moveArchiveDataSourceDown(archive);
        }
	}
}
