package org.epics.css.dal.tango;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>DoubleSeqPropertyProxyImpl</code> is the property proxy that can
 * handle double array values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DoubleSeqPropertyProxyImpl extends PropertyProxyImpl<double[]> {

	/**
	 * Constructs a new double seq property proxy.
	 * 
	 * @param propertyName the name of the property
	 */
	public DoubleSeqPropertyProxyImpl(String propertyName) {
		super(propertyName,Double.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#extractValue(fr.esrf.TangoApi.DeviceAttribute)
	 */
	@Override
	protected double[] extractValue(DeviceAttribute da) throws DevFailed {
		return da.extractDoubleArray();
	}

	/*
	 * (non-Javadoc)
	 * @see org.ecpis.css.dal.tango.PropertyProxyImpl#valueToDeviceAttribute(java.lang.Object)
	 */
	@Override
	protected DeviceAttribute valueToDeviceAttribute(double[] value) {
		return new DeviceAttribute(getPropertyName().getPropertyName(),value,value.length,0);
	}
}
