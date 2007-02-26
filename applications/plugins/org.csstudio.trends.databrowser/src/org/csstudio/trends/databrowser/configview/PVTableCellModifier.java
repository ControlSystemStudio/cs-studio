package org.csstudio.trends.databrowser.configview;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
	public boolean canModify(Object element, String property)
	{
        if (element == PVTableHelper.empty_row)
            return property.equals(
                            PVTableHelper.properties[PVTableHelper.NAME]);
        return true;
    }

	/** @return Returns the original cell value. */
    public Object getValue(Object element, String property)
    {
        if (element == PVTableHelper.empty_row)
            return ""; //$NON-NLS-1$
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
            else if (id == PVTableHelper.TYPE) {
                return new Boolean(entry.getLogScale());
            }
            else if (id == PVTableHelper.DATATYPE) 
            {
            	try {
            		// Let's update the columns datatype options.
            		IArchiveDataSource archives[] = entry.getArchiveDataSources();
                	((ComboBoxCellEditor)view.getPVTableViewer().getCellEditors()[PVTableHelper.DATATYPE]).setItems(ArchiveCache.getInstance().getServer(archives[0].getUrl()).getRequestTypes());
            	}
                catch(Exception e) 
                {
                	// If we catch an exception than we allowe only one option.
                	((ComboBoxCellEditor)view.getPVTableViewer().getCellEditors()[PVTableHelper.DATATYPE]).setItems(new String[] {"N/A"});
                }
                return new Integer(entry.getDataType());
            }
            else if(id == PVTableHelper.DISPLAYTYPE) 
            {
            	return (int)entry.getDisplayType().convert();
            }
            // Default: return item as String
            return PVTableHelper.getText(entry, id);
        }
        catch (Exception e)
        {
            Plugin.logException("Error", e); //$NON-NLS-1$
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
            int id = PVTableHelper.getPropertyID(property);
            
            if (id == PVTableHelper.NAME && value != null)
            {
                entry.changeName(value.toString());
            }
            else if (id == PVTableHelper.COLOR && value != null)
            {
                RGB rgb = (RGB) value;
                entry.setColor(new Color(null, rgb));
            }
            else if (id == PVTableHelper.MIN && value != null)
            {
                double new_limit = Double.valueOf(value.toString());
                entry.setAxisLow(new_limit);
            }
            else if (id == PVTableHelper.MAX && value != null)
            {
                double new_limit = Double.valueOf(value.toString());
                entry.setAxisHigh(new_limit);
            }
            else if (id == PVTableHelper.AXIS && value != null)
            {
                int new_axis = Integer.valueOf(value.toString());
                entry.setAxisIndex(new_axis);
            }
            else if (id == PVTableHelper.LINEWIDTH && value != null)
            {
                int new_width = Integer.valueOf(value.toString());
                entry.setLineWidth(new_width);
            }
            else if (id == PVTableHelper.TYPE && value != null)
            {
                boolean use_log = ((Boolean)value).booleanValue();
                entry.setLogScale(use_log);
            }
            else if (id == PVTableHelper.DATATYPE && value != null) 
            {
            	int new_data_type = Integer.valueOf(value.toString());
            	entry.setDataType(new_data_type);
            }
            else if(id == PVTableHelper.BINS && value != null)
            {
            	int new_bins = Integer.valueOf(value.toString());
                entry.setBins(new_bins);
            }
            else if(id == PVTableHelper.DISPLAYTYPE && value != null) {
            	//IModelItem.DisplayType new_display_type = IModelItem.DisplayType.valueOf(value.toString());
            	int new_display_type = Integer.valueOf(value.toString());
            	entry.setDisplayType(IModelItem.DisplayType.fromInteger(new_display_type));
            }
        }
        catch (Exception e)
        {
            Plugin.logException("Error", e); //$NON-NLS-1$
        }
    }
}
