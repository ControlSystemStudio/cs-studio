package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.model.ModelItem;

/** Helper for creating a table of PV rows from the model.
 * 
 *  @author Kay Kasemir
 */
public class PVTableHelper
{
    /** Name column identifier */
	final public static int NAME = 0;
    /** Axis minimum */
    final public static int MIN = 1;
    /** Axis maximum */
    final public static int MAX = 2;
    /** Axis column identifier */
    final public static int AXIS = 3;
    /** Color column identifier */
    final public static int COLOR = 4;
    /** Line width column identifier */
    final public static int LINEWIDTH = 5;
    /** Axis type column identifier */
    final public static int TYPE = 6;
	
	/** Strings used for column headers. */
	final public static String properties[] =
	{
		Messages.PV, Messages.ValueRangeMin, Messages.ValueRangeMax,
        Messages.AxisIndex, Messages.Color, Messages.LineWidth,
        Messages.AxisType
	};

    final public static int sizes[] =
    {
        80, 50, 50, 35, 35, 35, 35       
    };
    
    final public static int weights[] =
    {
        100, 10, 10, 5, 5, 5, 5
    };

	/** Get ID for a property.
	 * 
	 * @param property One of the properties[] strings.
	 * @return Returns the requested property ID, e.g. NAME.
	 * @throws Exception on error.
	 */
	static public int getPropertyID(String property) throws Exception
	{
		for (int id=0; id<properties.length; ++id)
			if (properties[id].equals(property))
				return id;
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
	    int id = getPropertyID(property);
	    return getText(entry, id);
	}

	/** Get a data piece of the entry.
	 * @param entry The ChartItem.
	 * @param item 0 for properties[0] piece etc.
	 * @return Returns the String for the entry
	 */
	static public String getText(ModelItem entry, int item)
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
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
