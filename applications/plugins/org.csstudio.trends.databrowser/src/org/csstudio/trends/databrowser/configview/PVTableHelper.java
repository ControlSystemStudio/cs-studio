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
    	NAME(Messages.PV, 70, 90, false),
    
        /** Axis column identifier */
        AXIS(Messages.AxisIndex, 30, 5, true),
        
        /** Axis minimum */
        MIN(Messages.ValueRangeMin, 50, 20, false),
        
        /** Axis maximum */
        MAX(Messages.ValueRangeMax, 50, 20, false),
        
        /** Autoscale */
        AUTOSCALE(Messages.AutoScale, 35, 20, true),
        
        /** Color column identifier */
        COLOR(Messages.Color, 30, 5, false),
        
        /** Line width column identifier */
        LINEWIDTH(Messages.LineWidth, 30, 5, true),
        
        /** Axis type (linear, log) column identifier */
        AXISTYPE(Messages.AxisType, 45, 20, true),
        
        /** Trace display type */
        DISPLAYTYPE(Messages.DisplayType, 35, 20, true);
        
        private final String title;
        private final int size;
        private final int weight;
        private final boolean center;
        
        private Column(String title, int size, int weight, boolean center)
        {
            this.title = title;
            this.size = size;
            this.weight = weight;
            this.center = center;
        }
       
        /** @return Column title */
        public String getTitle()
        {   return title; }
        
        /** @return Minimum column size. */
        public int getSize()
        {   return size;  }

        /** @return Column weight. */
        public int getWeight()
        {   return weight; }

        /** @return <code>true</code> if column is center-aligned. */
        public boolean isCentered()
        {   return center; }

        /** @return Column for the given ordinal. */
        public static Column fromOrdinal(int ordinal)
        {   // This is expensive, but java.lang.Enum offers no easy way...
            for (Column id : Column.values())
                if (id.ordinal() == ordinal)
                    return id;
            throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
        }
    };

	/** Get ID for a property.
	 * 
	 * @param title One of the column titles.
	 * @return Returns the requested Column.
	 * @throws Exception on error.
	 */
	static public Column findColumn(String title) throws Exception
	{
		for (Column col : Column.values())
			if (col.getTitle().equals(title))
                return col;
		throw new Exception("Unknown column '" + title + "'");  //$NON-NLS-1$//$NON-NLS-2$
	}

	/** Get e.g. the "NAME" from a ChartItem.
	 * 
	 * @param qso
	 * @param col_title One of the properties[] strings.
	 * @return Returns the requested property.
	 * @throws Exception on error.
	 */
	static public Object getProperty(ModelItem entry, String col_title) throws Exception
	{
        Column id = findColumn(col_title);
	    return getText(entry, id);
	}

    /** Get a data piece of the entry.
     * @param entry The ChartItem.
     * @param item 0 for properties[0] etc.
     * @return Returns the String for the entry
     */
    static public String getText(ModelItem entry, int col_index)
    {
        return getText(entry, Column.fromOrdinal(col_index));
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
            case AXISTYPE:
                return entry.getLogScale() ?
                        Messages.LogAxisType : Messages.LinearAxisType;
            case DISPLAYTYPE:
            	return entry.getTraceType().toString();
            }
		}
		catch (Exception e)
		{
            Plugin.logException("Error", e); //$NON-NLS-1$
		}
		return null;
	}
}
