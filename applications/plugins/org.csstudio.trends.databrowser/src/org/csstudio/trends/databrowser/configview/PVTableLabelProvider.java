package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.util.swt.CheckBoxImages;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/** The JFace label provider for the Model data. 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider
{
    // For the checkbox images
    final private CheckBoxImages images;
    
    /** Constructor */
    public PVTableLabelProvider(final Control control)
    {
        images = CheckBoxImages.getInstance(control);
    }
    
    /** Get text for all but the 'select' column,
     *  where some placeholder is returned.
     */
	public String getColumnText(Object obj, int index)
	{
        if (obj == PVTableHelper.empty_row)
        {
            if (index == PVTableHelper.Column.NAME.ordinal())
                return PVTableHelper.empty_row;
            return ""; //$NON-NLS-1$
        }
        return PVTableHelper.getText((IModelItem) obj, index);
	}

    /** {@inheritDoc} */
	public Image getColumnImage(Object obj, int index)
	{
        if (obj != PVTableHelper.empty_row)
        {
            IModelItem entry = (IModelItem) obj;
            if (index == PVTableHelper.Column.VISIBLE.ordinal())
                return images.getImage(entry.isVisible());
                            
            if (index == PVTableHelper.Column.AUTO_SCALE.ordinal())
                return images.getImage(entry.getAutoScale());
        }
        return null;
	}

    /** Change the background of the 'color' cells to the item's color.
     *  @see org.eclipse.jface.viewers.ITableColorProvider
     */
    public Color getBackground(Object obj, int index)
    {
        if (index == PVTableHelper.Column.COLOR.ordinal()  &&
            obj != PVTableHelper.empty_row)
        {
            IModelItem entry = (IModelItem) obj;
            return entry.getColor();
        }
        return null;
    }

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getForeground(Object obj, int index)
    {
        return null;
    }
}
