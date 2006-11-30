package org.csstudio.trends.databrowser.configview;

import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** The JFace label provider for <class>IArchiveDataSource</class> data. 
 *  @author Kay Kasemir
 */
public class ArchiveDataSourceLabelProvider extends LabelProvider implements
		ITableLabelProvider
{
    public final static int ARCHIVE = 0;
    public final static int KEY = 1;
    public final static int URL = 2;

    /** Get text for the column. */
	public String getColumnText(Object obj, int index)
	{
        IArchiveDataSource archive = (IArchiveDataSource) obj;
        switch (index)
        {
        case ARCHIVE: return archive.getName();
        case KEY: return Integer.toString(archive.getKey());
        case URL: return archive.getUrl();
        }
        return "<Col " + index + "?>"; //$NON-NLS-1$ //$NON-NLS-2$
	}

    /** Get column image. */
	public Image getColumnImage(Object obj, int index)
	{
        return null;
	}
}
