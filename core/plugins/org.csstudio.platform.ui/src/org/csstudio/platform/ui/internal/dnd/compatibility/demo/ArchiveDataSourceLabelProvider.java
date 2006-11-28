package org.csstudio.platform.ui.internal.dnd.compatibility.demo;

import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** <code>LabelProvider</code> for <class>IArchiveDataSource</class> data. 
 *  @author Kay Kasemir
 */
public class ArchiveDataSourceLabelProvider extends LabelProvider implements
		ITableLabelProvider
{
	public String getColumnText(final Object obj, final int index)
	{
        IArchiveDataSource arch = (IArchiveDataSource) obj;
        switch (index)
        {
        case 0: return arch.getUrl();
        case 1: return Integer.toString(arch.getKey());
        case 2: return arch.getName();
        }
        return null;
	}

	public Image getColumnImage(final Object obj, final int index)
	{
        return null;
	}
}
