package org.csstudio.utility.pv.simu;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ValueFactory;

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
