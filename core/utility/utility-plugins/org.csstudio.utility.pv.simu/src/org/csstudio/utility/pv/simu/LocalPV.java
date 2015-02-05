/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/** Local PV.
 *  <p>
 *  Provides updates based on changes of the underlying Value,
 *  which can be set by writing to this PV.
 *  Can hold numeric (double) or String value.
 *
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class LocalPV extends BasicPV<Value>
{
    /** Initialize
     *  @param prefix PV type prefix
     *  @param value PV name
     */
    public LocalPV(final String prefix, final Value value)
    {
        super(prefix, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWriteAllowed()
    {
        return true;
    }

    /** Meta data */
    final private INumericMetaData meta = ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 3, "a.u.");

    /** {@inheritDoc} */
    @Override
    public void setValue(Object newValue) throws Exception
    {
		final ISeverity OK = ValueFactory.createOKSeverity();
	    final ITimestamp now = TimestampFactory.now();
		IValue result = null;
    	if(newValue instanceof Double[]){
    		double[] data = new double[((Double [])newValue).length];
    		int i=0;
    		for(Double d : (Double[])newValue)
    			data[i++] = d.doubleValue();
    		result = ValueFactory.createDoubleValue(now, OK, OK.toString(),
    				meta, Quality.Original, data);
    	}else if(newValue instanceof double[]){
    		result = ValueFactory.createDoubleValue(now, OK, OK.toString(),
    				meta, Quality.Original, (double[])newValue);
    	}else if(newValue instanceof Double){
    		result = ValueFactory.createDoubleValue(now, OK, OK.toString(),
    				meta, Quality.Original, new double[]{(Double)newValue});
    	}
    	if(result != null){
    		value.setValue(result);
    		return;
    	}
    	value.setValue(TextUtil.parseValueFromString(newValue.toString(), meta));
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void start() throws Exception
    {
        running = true;
        value.addListener(this);
        if (getValue() == null)
        {   // Give an initial 'Double' value, which will send initial update.
            // Note that just 0 would auto-box to Integer, which is then
            // handled as String. This way we assert that it's a number
            setValue(new Double(0.0));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        value.removeListener(this);
        running = false;
    }
}
