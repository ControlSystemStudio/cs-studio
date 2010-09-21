package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>DoublePropertyProxyImpl</code> is the property proxy that can
 * handle double values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DoublePropertyProxyImpl extends PropertyProxyImpl<Double> {

	/**
	 * Constructs a new double property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public DoublePropertyProxyImpl(String propertyName) {
		super(propertyName,Double.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected Double extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractDouble();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(Double value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value);
	}
}