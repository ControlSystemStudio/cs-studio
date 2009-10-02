package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>IntegerSeqPropertyProxyImpl</code> is the property proxy that can
 * handle integer array values as long arrays.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class IntegerSeqPropertyProxyImpl extends PropertyProxyImpl<long[]> {

	/**
	 * Constructs a new integer seq property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public IntegerSeqPropertyProxyImpl(String propertyName) {
		super(propertyName,Long.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected long[] extractValue(DeviceAttribute da) throws DevFailed {
		int[] vals = da.extractLongArray();
		long[] retVal = new long[vals.length];
		System.arraycopy(vals,0,retVal,0,vals.length);
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(long[] value) {
		int[] vals = new int[value.length];
		System.arraycopy(value,0,vals,0,vals.length);
		return new DeviceAttribute(getPropertyName().getPropertyName(),vals,vals.length,0);
	}
}
