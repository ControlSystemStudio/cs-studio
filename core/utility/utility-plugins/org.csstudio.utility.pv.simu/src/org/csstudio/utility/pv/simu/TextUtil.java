/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/**The utility class help to parse text.
 * @author Xihui Chen
 *
 */
public class TextUtil {

	 /**Parse a string to get the IValue from it.
     * For example, an input of "0.12" will return a {@link IDoubleValue} with single double value;
     * an input of "1,2,3,4" will return a {@link IDoubleValue} with an array of double value;
     * an input of "hello" will return a {@link IStringValue}.
     * @param value_text the string to parse
     * @param metaData the meta data for the returned value. null to use default meta data.
     * @return the IValue
     */
    @SuppressWarnings("nls")
    public static IValue parseValueFromString(String value_text, IMetaData metaData){
    	IValue result;
    	INumericMetaData meta;
        // Is the value a number?
        final ISeverity OK = ValueFactory.createOKSeverity();
        final ITimestamp now = TimestampFactory.now();
        try
        {
            final double dbl = Double.parseDouble(value_text);
            if(metaData == null || !(metaData instanceof INumericMetaData))
            	meta = ValueFactory.createNumericMetaData(dbl-1, dbl+1, 0, 0, 0, 0, 1, "a.u.");
            else
            	meta = (INumericMetaData) metaData;
            result = ValueFactory.createDoubleValue(now,
                OK, OK.toString(), meta, Quality.Original,
                new double[] { dbl });
        }
        catch (NumberFormatException ex)
        {
        	try {
				String[] value_array = value_text.split(","); //$NON-NLS-1$
				if(value_array.length ==0)
					throw new NumberFormatException();
				double[] doubleArray = new double[value_array.length];
				int i=0;
				for(String s : value_array){
					doubleArray[i++] = Double.parseDouble(s);
				}
				if(metaData == null || !(metaData instanceof INumericMetaData))
		            meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 1, "a.u.");
	            else
	            	meta = (INumericMetaData) metaData;
				 result = ValueFactory.createDoubleValue(now,
				         OK, OK.toString(), meta, Quality.Original, doubleArray);
			} catch (NumberFormatException e) {
				// Cannot parse number, assume string data type
	            result = ValueFactory.createStringValue(now, OK, OK.toString(), Quality.Original, new String[] { value_text });
			}
        }
        return result;
    }
}
