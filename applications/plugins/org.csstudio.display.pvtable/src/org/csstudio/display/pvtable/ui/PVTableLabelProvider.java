package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVValue;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** The JFace label provider for the  <class>PVListModel</class> data. 
 *  @author Kay Kasemir
 */
public class PVTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider
{
    private PVListModel pv_list;
    private Color red;
    
    // For the checkbox images
    public static final String SELECTED = "checked";
    public static final String UNSELECTED  = "unchecked";
    private static ImageRegistry images = null;
    
    public PVTableLabelProvider(PVListModel pv_list)
    {
        this.pv_list = pv_list;
        // no need to dispose system color
        red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        // Images get disposed by registry.
        // Note that registry and its entries are only created once!
        if (images == null)
        {
            images = new ImageRegistry();
            images.put(SELECTED, Plugin
                    .getImageDescriptor("icons/" + SELECTED + ".gif"));
            images.put(UNSELECTED, Plugin
                    .getImageDescriptor("icons/" + UNSELECTED + ".gif"));
        }
    }
    
	public String getText(Object obj)
	{
		return getColumnText(obj, 0);
	}

	public Image getImage(Object obj)
	{
		return null;
	}

    /** Get text for all but the 'select' column. */
	public String getColumnText(Object obj, int index)
	{
        return PVTableHelper.getText((PVListEntry) obj, index);
	}

    /** Get column image (only for the 'select' column) */
	public Image getColumnImage(Object obj, int index)
	{
        if (index == PVTableHelper.SELECT)
        {
            PVListEntry entry = (PVListEntry) obj;
            if (entry.isSelected())
                return images.get(SELECTED);
            else
                return images.get(UNSELECTED);
        }
        return null;
	}

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getBackground(Object obj, int index)
    {
        return null;
    }

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    public Color getForeground(Object obj, int index)
    {
        if (shouldStandout(obj, index))
            return red;
        return null;
    }

    /** Determine if the table cell should shown in a special way. */
    private boolean shouldStandout(Object obj, int index)
    {
        PVListEntry entry = (PVListEntry) obj;
        double tolerance = pv_list.getTolerance();
        PV pv;
        
        switch (index)
        {
        case PVTableHelper.NAME:
            // Set PV name to red while disconnected
            if (! entry.getPV().isConnected())
                return true; 
            return false;
        case PVTableHelper.READBACK:
            // Set readback PV name to red while disconnected
            pv = entry.getReadbackPV();
            if (pv != null  && ! pv.isConnected())
                return true;
            return false;
        case PVTableHelper.VALUE:
            // Set value to red while it differs from the saved value
            pv = entry.getPV();
            if (pv == null  || ! pv.isConnected())
                return false;
            if (PVValue.match(pv.getValue(), entry.getSavedValue(), tolerance))
                return false;
            return true;
        case PVTableHelper.READBACK_VALUE:
            // Set saved readback value to red while it differs from the saved value
            pv = entry.getReadbackPV();
            if (pv == null  || ! pv.isConnected())
                return false;
            if (PVValue.match(pv.getValue(), entry.getSavedReadbackValue(), tolerance))
                return false;
            return true;
        }
        return false;
    }    
}
