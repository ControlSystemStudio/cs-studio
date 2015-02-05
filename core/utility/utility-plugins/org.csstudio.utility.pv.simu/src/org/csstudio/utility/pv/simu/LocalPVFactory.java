/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for 'local' PVs
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class LocalPVFactory implements IPVFactory
{
    private static final String QUOTE = "\""; //$NON-NLS-1$

	/** PV type prefix */
    public static final String PREFIX = "loc";

    /** All the 'local' PVs, mapped by name */
    private static Map<String, Value> values =
        new HashMap<String, Value>();

    /** @return Number of values */
    public static int getValueCount()
    {
        return values.size();
    }

    /** Create a 'local' PV.
     *  @param name Name of the PV, may also include initial value like "..(123)".
     * @throws Exception
     */
    @Override
    public PV createPV(final String name) throws Exception
    {
    	String namePart;
    	String value_text = null;
    	// Parse value, locate the "..(value)"
        final int value_start = name.indexOf('('); //$NON-NLS-1$
        if(value_start <0) //no initial value specified
        	namePart = name;
        else{	//it has initial value specified
        	namePart = name.substring(0, value_start);
        	 final int value_end = name.indexOf(')', value_start + 1); //$NON-NLS-1$
        	 if (value_end < 0)
        		 throw new Exception("Value in PV " + name +" not terminated by ')'");
        	 value_text = name.substring(value_start+1, value_end);
        	 if(value_text.startsWith(QUOTE) && value_text.endsWith(QUOTE))
        		 value_text=value_text.substring(1, value_text.length()-1);
        }


        Value value = values.get(namePart);
        if (value == null)
        {
            value = new Value(namePart);
            values.put(namePart, value);
            if(value_text != null)
            	value.setValue(TextUtil.parseValueFromString(value_text, null));
        }
        return new LocalPV(PREFIX, value);
    }
}
