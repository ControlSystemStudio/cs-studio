package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>StringPropertyProxyImpl</code> is the property proxy that can
 * handle string values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StringPropertyProxyImpl extends PropertyProxyImpl<String> {

	/**
	 * Constructs a new integer property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public StringPropertyProxyImpl(String propertyName) {
		super(propertyName,String.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected String extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(String value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value);
	}
}
