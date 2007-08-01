package org.csstudio.trends.databrowser.configview;

import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem.RequestType;
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
    
    /** @return <code>true</code> if this object's column can be edited */
	public boolean canModify(Object element, String col_title)
	{
	    // The last row only allows name entry.
        if (element == PVTableHelper.empty_row)
            return col_title.equals(PVTableHelper.Column.NAME.getTitle());
        // The request type only applies to PVs.
        if (col_title.equals(PVTableHelper.Column.REQUEST_TYPE.getTitle()))
            return element instanceof IPVModelItem;
        // Otherwise, all columns of the model can change.
        return true;
    }

	/** @return Returns the original cell value. */
    public Object getValue(Object element, String col_title)
    {
        if (element == PVTableHelper.empty_row)
            return ""; //$NON-NLS-1$
        try
        {
            IModelItem entry = (IModelItem) element;
            PVTableHelper.Column col = PVTableHelper.findColumn(col_title);
            switch (col)
            {
            case VISIBLE:
                return new Boolean(entry.isVisible());
            case NAME:
                break; // use string
            case AXIS:
                // If we edit Axis index in combo box.
                if (ConfigView.use_axis_combobox)
                    return new Integer(entry.getAxisIndex());
                // Otherwise: fall through to String for text editor
                break; // use string
            case MIN:
                break; // use string
            case MAX:
                break; // use string
            case AUTO_SCALE:
                return new Boolean(entry.getAutoScale());
            case COLOR:
                return entry.getColor().getRGB();
            case LINE_WIDTH:
                break; // use string
            case LOG_SCALE:
                return new Boolean(entry.getLogScale());
            case REQUEST_TYPE:
                
            case TRACE_TYPE:
                return entry.getTraceType().ordinal();
            }
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
        if (value == null)
            return;
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
            PVTableHelper.Column col = PVTableHelper.findColumn(property);
            
            switch (col)
            {
            case VISIBLE:
                boolean visible = ((Boolean)value).booleanValue();
                entry.setVisible(visible);
                return;
            case NAME:
                entry.changeName(value.toString());
                return;
            case AXIS:
                int new_axis = Integer.valueOf(value.toString());
                entry.setAxisIndex(new_axis);
                return;
            case MIN:
                double new_min = Double.valueOf(value.toString());
                entry.setAxisLow(new_min);
                return;
            case MAX:
                double new_max = Double.valueOf(value.toString());
                entry.setAxisHigh(new_max);
                return;
            case AUTO_SCALE:
                boolean auto_scale = ((Boolean)value).booleanValue();
                entry.setAutoScale(auto_scale);
                return;
            case COLOR:
                RGB rgb = (RGB) value;
                entry.setColor(new Color(null, rgb));
                return;
            case LINE_WIDTH:
                int new_width = Integer.valueOf(value.toString());
                entry.setLineWidth(new_width);
                return;
            case LOG_SCALE:
                boolean use_log = ((Boolean)value).booleanValue();
                entry.setLogScale(use_log);
                return;
            case REQUEST_TYPE:
                if (entry instanceof IPVModelItem)
                {
                    IPVModelItem pv = (IPVModelItem) entry;
                    int ordinal = ((Integer) value).intValue();
                    final RequestType request_type = IPVModelItem.RequestType.fromOrdinal(ordinal);
                     pv.setRequestType(request_type);
                }
                return;
            case TRACE_TYPE:
                {
                    int ordinal = ((Integer) value).intValue();
                    entry.setTraceType(TraceType.fromOrdinal(ordinal));
                    return;
                }
            }
        }
        catch (Exception e)
        {
            Plugin.logException("Error", e); //$NON-NLS-1$
        }
    }
}
