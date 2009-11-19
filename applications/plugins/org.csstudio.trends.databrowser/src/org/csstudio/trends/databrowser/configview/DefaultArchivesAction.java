package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Make all PVs use the default archives.
 * 
 *  @author Kay Kasemir
 */
public class DefaultArchivesAction extends Action
{
    private ConfigView config;

	public DefaultArchivesAction(ConfigView config)
	{
        this.config = config;
		setText(Messages.DefaultArchives);
		setToolTipText(Messages.DefaultArchivesTT);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE));
	}

	@Override
	public void run()
	{
        IModelItem items[] = config.getSelectedModelEntries();
		if (items == null)
			return;
        for (IModelItem item : items)
        {
            if (! (item instanceof IPVModelItem))
                continue;
            IPVModelItem pv_item = (IPVModelItem) item;
            pv_item.useDefaultArchiveDataSources();
        }
	}
}
