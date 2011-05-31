/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.utility.pv.PV;

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
		Messages.TableCol_Sel, Messages.TableCol_Name, Messages.TableCol_Time, Messages.TableCol_Value, Messages.TableCol_SavedValue, Messages.TableCol_ReadbackPV, Messages.TableCol_ReadbackValue, Messages.TableCol_SavedReadbackValue
	};

    final public static int sizes[] =
    {
        40, 50, 50, 50, 50, 50, 50, 50
    };

    final public static int weights[] =
    {
        0, 100, 100, 80, 80, 100, 80, 80
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

    /** @return String for PV's Value, handle all the <code>null</code>s. */
    private static String getPVValueString(PV pv)
    {
        if (pv == null)
            return ""; //$NON-NLS-1$
        IValue value = pv.getValue();
        if (value == null)
            return ""; //$NON-NLS-1$
    	return value.format();
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
                return entry.getName();
            case TIME:
            {
                IValue value = entry.getPV().getValue();
                if (value == null)
                    return ""; //$NON-NLS-1$
                ITimestamp time = value.getTime();
                return (time == null) ? "" : time.toString(); //$NON-NLS-1$
            }
            case VALUE:
                return getPVValueString(entry.getPV());
            case SAVED_VALUE:
                return entry.getSavedValue().toString();
            case READBACK:
                pv = entry.getReadbackPV();
                if (pv == null)
                    return ""; //$NON-NLS-1$
                return pv.getName();
            case READBACK_VALUE:
                return getPVValueString(entry.getReadbackPV());
            case SAVED_READBACK:
                return entry.getSavedReadbackValue().toString();
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
