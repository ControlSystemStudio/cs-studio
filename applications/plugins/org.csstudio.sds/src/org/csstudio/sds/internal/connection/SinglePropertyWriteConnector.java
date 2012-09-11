package org.csstudio.sds.internal.connection;

import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.IPropertyChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class SinglePropertyWriteConnector implements IPropertyChangeListener {
	private IProcessVariableAddress pv;
	private SimpleDALBroker broker;
	private ValueType valueType;
    private static final Logger LOG = LoggerFactory.getLogger(SinglePropertyWriteConnector.class);

	public SinglePropertyWriteConnector(IProcessVariableAddress pv, ValueType valueType, SimpleDALBroker broker) {
		assert pv != null;
		assert valueType != null;
//		assert broker != null;
		this.pv = pv;
		this.valueType = valueType;
		this.broker = broker;
	}

	public void propertyManualValueChanged(String propertyId, Object manualValue) {
		// FIXME: 24.03.2010: swende: Was machen wir mit dem ValueType? Einbeziehen oder entfernen weil unwichtig?
		
		String cs = RemoteInfo.DAL_TYPE_PREFIX + pv.getControlSystem().getResponsibleDalPlugId();
		String property = pv.getProperty();
		RemoteInfo rinfo = new RemoteInfo(cs, property, null, null);

		try {
			broker.setValueAsync(new ConnectionParameters(rinfo), manualValue, new ResponseListener() {
				public void responseError(ResponseEvent event) {
					LOG.error("Could not set value for ["+pv.toString()+"].");
				}

				public void responseReceived(ResponseEvent event) {
					LOG.error("Value for ["+pv.toString()+"] was set.");
				}

			});
		} catch (Exception e) {
			// FIXME: 24.03.2010: swende: Igor bitten, diese Exception zu entfernen!
			e.printStackTrace();
		}
	}

	public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {

	}

	public void propertyValueChanged(Object oldValue, Object newValue) {

	}
}
