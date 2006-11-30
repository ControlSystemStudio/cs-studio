package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.utility.pv.PVValue;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Item;

/** Table cell modifier for tables with TableHelper type data.
 *  @author Kay Kasemir
 */
public class PVTableCellModifier implements ICellModifier
{
	/** All columns can change. */
	public boolean canModify(Object element, String property)
	{	return true; }

	/** @return Returns the original cell value. */
    public Object getValue(Object element, String property)
    {
        try
        {
            ModelItem entry = (ModelItem) element;
            int id = PVTableHelper.getPropertyID(property);
            if (id == PVTableHelper.COLOR)
                return entry.getColor().getRGB();
            else if (ConfigView.use_axis_combobox
                     && id == PVTableHelper.AXIS)
            {   // If we edit Axis index in combo box.
                // Otherwise: fall through to String for text editor
                return new Integer(entry.getAxisIndex());
            }
            else if (id == PVTableHelper.TYPE)
                return new Boolean(entry.getLogScale());
            // Default: return item as String
            return PVTableHelper.getText(entry, id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

	/** Editor finished and tries to update element's property. */
	public void modify(Object element, String property, Object value)
    {
        try
        {   // Note that it is possible for an SWT Item to be passed
            // instead of the model element.
            IModelItem entry;
            if (element instanceof Item)
                entry = (IModelItem) ((Item) element).getData();
            else
                entry = (IModelItem) element;
            int id = PVTableHelper.getPropertyID(property);
            
            if (id == PVTableHelper.NAME && value != null)
            {
                entry.changeName(PVValue.toString(value));
            }
            else if (id == PVTableHelper.COLOR && value != null)
            {
                RGB rgb = (RGB) value;
                entry.setColor(new Color(null, rgb));
            }
            else if (id == PVTableHelper.MIN && value != null)
            {
                double new_limit = PVValue.toDouble(value);
                entry.setAxisLow(new_limit);
            }
            else if (id == PVTableHelper.MAX && value != null)
            {
                double new_limit = PVValue.toDouble(value);
                entry.setAxisHigh(new_limit);
            }
            else if (id == PVTableHelper.AXIS && value != null)
            {
                int new_axis = PVValue.toInt(value);
                entry.setAxisIndex(new_axis);
            }
            else if (id == PVTableHelper.LINEWIDTH && value != null)
            {
                int new_width = PVValue.toInt(value);
                entry.setLineWidth(new_width);
            }
            else if (id == PVTableHelper.TYPE && value != null)
            {
                boolean use_log = ((Boolean)value).booleanValue();
                entry.setLogScale(use_log);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
