package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

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
    public synchronized void start() throws Exception
    {
        running = true;
        value.addListener(this);
        //give an initial value
        if(getValue() == null)
        	setValue(0);
        
    }

    /** {@inheritDoc} */
    public void stop()
    {
        value.removeListener(this);
        running = false;
    }
}
