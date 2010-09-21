package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>LongPropertyProxyImpl</code> is the property proxy that can
 * handle long values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class LongPropertyProxyImpl extends PropertyProxyImpl<Long> {

	/**
	 * Constructs a new long property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public LongPropertyProxyImpl(String propertyName) {
		super(propertyName,Long.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected Long extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractLong64();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(Long value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value);
	}
}
