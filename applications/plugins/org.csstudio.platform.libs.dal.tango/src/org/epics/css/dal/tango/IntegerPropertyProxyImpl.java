package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>IntegerPropertyProxyImpl</code> is the property proxy that can
 * handle long values as integers
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class IntegerPropertyProxyImpl extends PropertyProxyImpl<Long> {

	/**
	 * Constructs a new integer property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public IntegerPropertyProxyImpl(String propertyName) {
		super(propertyName,Long.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected Long extractValue(DeviceAttribute da) throws DevFailed {
		return new Long(da.extractLong());
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(Long value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value.intValue());
	}
}
