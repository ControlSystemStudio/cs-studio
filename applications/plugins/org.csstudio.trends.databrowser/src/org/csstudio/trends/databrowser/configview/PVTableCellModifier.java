package org.csstudio.trends.databrowser.configview;

import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Item;

/** Table cell modifier for tables with TableHelper type data.
 *  @author Kay Kasemir
 */
public class PVTableCellModifier implements ICellModifier
{
    private final ConfigView view;
    
    PVTableCellModifier(ConfigView view)
    {
        this.view = view;
    }
    
	/** All columns of the model can change.
     *  The last row only allows name entry.
     */
	public boolean canModify(Object element, String col_title)
	{
        if (element == PVTableHelper.empty_row)
            return col_title.equals(PVTableHelper.Column.NAME.getTitle());
        return true;
    }

	/** @return Returns the original cell value. */
    public Object getValue(Object element, String col_title)
    {
        if (element == PVTableHelper.empty_row)
            return ""; //$NON-NLS-1$
        try
        {
            ModelItem entry = (ModelItem) element;
            PVTableHelper.Column col = PVTableHelper.findColumn(col_title);
            if (col == PVTableHelper.Column.COLOR)
                return entry.getColor().getRGB();
            else if (ConfigView.use_axis_combobox
                     && col == PVTableHelper.Column.AXIS)
            {   // If we edit Axis index in combo box.
                // Otherwise: fall through to String for text editor
                return new Integer(entry.getAxisIndex());
            }
            else if (col == PVTableHelper.Column.LOG_SCALE)
                return new Boolean(entry.getLogScale());
            else if (col == PVTableHelper.Column.AUTO_SCALE)
            	return new Boolean(entry.getAutoScale());
            else if (col == PVTableHelper.Column.TRACE_TYPE) 
            	return entry.getTraceType().ordinal();
            // Default: return item as String
            return PVTableHelper.getText(entry, col);
        }
        catch (Exception e)
        {
            Plugin.logException("PVTableCellModifier: " + e.getMessage(), e); //$NON-NLS-1$
        }
        return null;
    }

	/** Editor finished and tries to update element's property. */
	public void modify(Object element, String property, Object value)
    {
        try
        {   // Note that it is possible for an SWT Item to be passed
            // instead of the model element.
            if (element instanceof Item)
                element = ((Item) element).getData();
            // Was this the magic last row?
            if (element == PVTableHelper.empty_row)
            {
                view.addPV(value.toString());
                return;
            }
            // Edit existing item
            IModelItem entry = (IModelItem) element;
            PVTableHelper.Column id = PVTableHelper.findColumn(property);
            
            if (id == PVTableHelper.Column.NAME && value != null)
            {
                entry.changeName(value.toString());
            }
            else if (id == PVTableHelper.Column.COLOR && value != null)
            {
                RGB rgb = (RGB) value;
                entry.setColor(new Color(null, rgb));
            }
            else if (id == PVTableHelper.Column.MIN && value != null)
            {
                double new_limit = Double.valueOf(value.toString());
                entry.setAxisLow(new_limit);
            }
            else if (id == PVTableHelper.Column.MAX && value != null)
            {
                double new_limit = Double.valueOf(value.toString());
                entry.setAxisHigh(new_limit);
            }
            else if (id == PVTableHelper.Column.AXIS && value != null)
            {
                int new_axis = Integer.valueOf(value.toString());
                entry.setAxisIndex(new_axis);
            }
            else if (id == PVTableHelper.Column.LINE_WIDTH && value != null)
            {
                int new_width = Integer.valueOf(value.toString());
                entry.setLineWidth(new_width);
            }
            else if (id == PVTableHelper.Column.LOG_SCALE && value != null)
            {
                boolean use_log = ((Boolean)value).booleanValue();
                entry.setLogScale(use_log);
            }
            else if(id == PVTableHelper.Column.AUTO_SCALE && value != null) 
            {
            	boolean auto_scale = ((Boolean)value).booleanValue();
            	entry.setAutoScale(auto_scale);
            }
            else if(id == PVTableHelper.Column.TRACE_TYPE && value != null)
            {
                int ordinal = ((Integer) value).intValue();
            	entry.setTraceType(TraceType.fromOrdinal(ordinal));
            }
        }
        catch (Exception e)
        {
            Plugin.logException("Error", e); //$NON-NLS-1$
        }
    }
}
