package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVValue;


/** Helper for creating a table of PVListEntry rows.
 * 
 *  @author Kay Kasemir
 */
public class PVTableHelper
{
    /** The selection column identifier */
    final public static int SELECT = 0;
    /** The PV name column identifier */
	final public static int NAME = 1;
    /** The time column identifier */
	final public static int TIME = 2;
    /** The value column identifier */
    final public static int VALUE = 3;
    /** The saved value column identifier */
	final public static int SAVED_VALUE = 4;
    /** The readback PV name column identifier */
    final public static int READBACK = 5;
    /** The readback value column identifier */
    final public static int READBACK_VALUE = 6;
    /** The saved readback value column identifier */
    final public static int SAVED_READBACK = 7;
	
	/** Strings that one can use as column headers. */
	final public static String properties[] =
	{
		"Sel", "Name", "Time", "Value", "Saved", "Readback", "RB Value", "Saved RB"
	};

    final public static int sizes[] =
    {
        40, 50, 50, 50, 50, 50, 50, 50         
    };
    
    final public static int weights[] =
    {
        0, 50, 100, 80, 80, 100, 80, 80 
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
		throw new Exception("Unknown property '" + property + "'");
	}

	/** Get e.g. the "NAME" from a PVListEntry.
	 * 
	 * @param qso
	 * @param property One of the properties[] strings.
	 * @return Returns the requested property.
	 * @throws Exception on error.
	 */
	static public Object getProperty(PVListEntry entry, String property) throws Exception
	{
	    int id = getPropertyID(property);
        if (id == SELECT)
            return new Boolean(entry.isSelected());
        // else
	    return getText(entry, id);
	}

	/** Get a data piece of the entry.
	 * @param entry The PVListEntry.
	 * @param item 0 for properties[0] piece etc.
	 * @return Returns the String for the entry
	 */
	static public String getText(PVListEntry entry, int item)
	{
        PV pv;
		try
        {
			switch (item)
            {
            case NAME:
                return entry.getPV().getName();
            case TIME:
                ITimestamp time = entry.getPV().getTime();            	
                return (time == null) ? "" : time.toString();
            case VALUE:
                return PVValue.toString(entry.getPV().getValue());
            case SAVED_VALUE:
                return PVValue.toString(entry.getSavedValue());
            case READBACK:
                pv = entry.getReadbackPV();
                if (pv == null)
                    return "";
                return pv.getName();
            case READBACK_VALUE:
                pv = entry.getReadbackPV();
                if (pv == null)
                    return "";
                return PVValue.toString(pv.getValue());
            case SAVED_READBACK:
                return PVValue.toString(entry.getSavedReadbackValue());
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
