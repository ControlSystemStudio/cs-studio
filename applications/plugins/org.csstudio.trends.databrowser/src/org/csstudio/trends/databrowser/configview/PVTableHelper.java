package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.ModelItem;

/** Helper for creating a table of PV rows from the model.
 * 
 *  @author Kay Kasemir
 */
public class PVTableHelper
{
    /** Placeholder, magic entry for the last row in the PV table. */
    public static final String empty_row = Messages.EmptyPVRowMarker;
    
    enum Column
    {
        /** Name column identifier */
    	NAME,
    
        /** Axis column identifier */
        AXIS,
        
        /** Axis minimum */
        MIN,
        
        /** Axis maximum */
        MAX,
        
        /** Autoscale */
        AUTOSCALE,
        
        /** Color column identifier */
        COLOR,
        
        /** Line width column identifier */
        LINEWIDTH,
        
        /** Axis type column identifier */
        TYPE,
        
        /** Trace display type */
        DISPLAYTYPE;
        
        /** @return Column for the given ordinal. */
        public static Column fromOrdinal(int ordinal)
        {   // This is expensive, but java.lang.Enum offers no easy way...
            for (Column id : Column.values())
                if (id.ordinal() == ordinal)
                    return id;
            throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
        }
    };
    
	/** Strings used for column headers. */
	final public static String properties[] =
	{
		Messages.PV,
        Messages.AxisIndex,
        Messages.ValueRangeMin,
        Messages.ValueRangeMax,
        Messages.AutoScale,
        Messages.Color,
        Messages.LineWidth,
        Messages.AxisType,
        Messages.DisplayType,
	};

    final public static int sizes[]   = {  70, 30, 50, 50, 40, 35, 35, 45, 35 };
    final public static int weights[] = { 100, 10, 10, 10,  5,  5,  5, 20,  5 };

	/** Get ID for a property.
	 * 
	 * @param property One of the properties[] strings.
	 * @return Returns the requested property ID, e.g. NAME.
	 * @throws Exception on error.
	 */
	static public Column getPropertyID(String property) throws Exception
	{
		for (int id=0; id<properties.length; ++id)
			if (properties[id].equals(property))
                return Column.fromOrdinal(id);
		throw new Exception("Unknown property '" + property + "'");  //$NON-NLS-1$//$NON-NLS-2$
	}

	/** Get e.g. the "NAME" from a ChartItem.
	 * 
	 * @param qso
	 * @param property One of the properties[] strings.
	 * @return Returns the requested property.
	 * @throws Exception on error.
	 */
	static public Object getProperty(ModelItem entry, String property) throws Exception
	{
        Column id = getPropertyID(property);
	    return getText(entry, id);
	}

    /** Get a data piece of the entry.
     * @param entry The ChartItem.
     * @param item 0 for properties[0] etc.
     * @return Returns the String for the entry
     */
    static public String getText(ModelItem entry, int index)
    {
        return getText(entry, Column.fromOrdinal(index));
    }

	/** Get a data piece of the entry.
	 * @param entry The ChartItem.
	 * @param item The Column of interest.
	 * @return Returns the String for the entry
	 */
	static public String getText(ModelItem entry, Column item)
	{
		try
        {
            switch (item)
            {
            case NAME:
                return entry.getName();
            case MIN:
                return Double.toString(entry.getAxisLow());
            case MAX:
                return Double.toString(entry.getAxisHigh());
            case AXIS:
                return Integer.toString(entry.getAxisIndex());
            case LINEWIDTH:
                return Integer.toString(entry.getLineWidth());
            case TYPE:
                return entry.getLogScale() ?
                        Messages.LogAxisType : Messages.LinearAxisType;
            case DISPLAYTYPE:
            	return entry.getTraceType().toString();
            case AUTOSCALE:
            	return Boolean.toString(entry.getIsTraceAutoScalable());
            }
		}
		catch (Exception e)
		{
            Plugin.logException("Error", e); //$NON-NLS-1$
		}
		return null;
	}
}
