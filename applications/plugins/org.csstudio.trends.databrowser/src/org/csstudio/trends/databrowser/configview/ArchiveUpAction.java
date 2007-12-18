package org.csstudio.trends.databrowser.configview;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/** Move selected archive info entry 'up' in the list.
 * 
 *  @author Kay Kasemir
 */
public class ArchiveUpAction extends Action
{
    private ConfigView config;

    private ImageRegistry images = null;
    private static final String UP = "up"; //$NON-NLS-1$
    
	public ArchiveUpAction(ConfigView config)
	{
        this.config = config;

        images = new ImageRegistry();
        try
        {
            images.put(UP, Plugin.getImageDescriptor("icons/up.gif")); //$NON-NLS-1$
        }
        catch (Exception e)
        {
            Plugin.getLogger().error("Missing image", e); //$NON-NLS-1$
        }
        setText(Messages.Up);
		setToolTipText(Messages.MoveArchsUp);
		setImageDescriptor(images.getDescriptor(UP));
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
            // The selection appears in reverse order?!
            // In any case, iterating this way looks better.
            // With entries A, B, selecting both,
            // we'll now get B, A.
            for (int i=archives.length-1; i>=0; --i)
                pv_item.moveArchiveDataSourceUp(archives[i]);
        }
	}
}
