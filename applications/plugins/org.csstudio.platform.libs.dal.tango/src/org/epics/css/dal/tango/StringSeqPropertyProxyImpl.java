package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>StringSeqPropertyProxyImpl</code> is the property proxy that can
 * handle string array values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StringSeqPropertyProxyImpl extends PropertyProxyImpl<String[]> {

	/**
	 * Constructs a new string seq property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public StringSeqPropertyProxyImpl(String propertyName) {
		super(propertyName,String.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected String[] extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractStringArray();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(String[] value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value,value.length,0);
	}
}
