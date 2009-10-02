package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>LongSeqPropertyProxyImpl</code> is the property proxy that can
 * handle long array values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class LongSeqPropertyProxyImpl extends PropertyProxyImpl<long[]> {

	/**
	 * Constructs a new long seq property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public LongSeqPropertyProxyImpl(String propertyName) {
		super(propertyName,Long.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected long[] extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractLong64Array();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(long[] value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value,value.length,0);
	}
}
