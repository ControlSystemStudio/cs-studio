package org.csstudio.askap.utility.pv.ice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import Ice.Current;
import Ice.ObjectPrx;
import askap.interfaces.TimeTaggedTypedValueMap;
import askap.interfaces.TypedValue;
import askap.interfaces.TypedValueString;
import askap.interfaces.TypedValueType;
import askap.interfaces.datapublisher._ITimeTaggedTypedValueMapPublisherDisp;

public class Value implements PV {

	// Logger based on a name. Use current class name or plugin ID.
	private static final Logger logger = Logger
			.getLogger(Value.class.getName());

	private String pvName = "";
	private boolean isRunning = false;
	private IValue value = null;

	private List<PVListener> listeners = new ArrayList<PVListener>();
	
	private ObjectPrx subscriber = null;

	public Value(String name) {
		this.pvName = name;
		value = ValueFactory.createDoubleValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "", null, Quality.Original,
				new double[] { 1.0 });
	}

	@Override
	public String getName() {
		return IceStormPVFactory.PREFIX + PVFactory.SEPARATOR + pvName;
	}

	@Override
	public IValue getValue(double timeout_seconds) throws Exception {
		return value;
	}

	@Override
	public void addListener(PVListener listener) {
		listeners.add(listener);
		// If we already have a value, inform new listener right away
		if (value != null)
			listener.pvValueUpdate(this);
	}

	@Override
	public void removeListener(PVListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void start() throws Exception {
		logger.info(getName() + " start()");
		
		_ITimeTaggedTypedValueMapPublisherDisp callbackObj = new _ITimeTaggedTypedValueMapPublisherDisp() {
			
			@Override
			public void publish(TimeTaggedTypedValueMap value, Current arg1) {
				Map<String, TypedValue> valueMap = value.data;
				logger.log(Level.INFO, "Got value for " + pvName);
				for (Iterator<String> iter = valueMap.keySet().iterator(); iter.hasNext(); ) {
					String key = iter.next();
					TypedValue v = valueMap.get(key);
					setIValue(v);
				}
				logger.log(Level.INFO, "");
			}			
		};
		
		subscriber = IceManager.setupSubscriber(pvName, callbackObj);
		
		isRunning = true;
	}

	protected void setIValue(TypedValue typedValue) {
		if (typedValue.type.equals(TypedValueType.TypeString)) {
			value = ValueFactory.createStringValue(TimestampFactory.now(),
					ValueFactory.createOKSeverity(), "", Quality.Original,
					new String[] { ((TypedValueString) typedValue).value});
			
			for (Iterator<PVListener> iter = listeners.iterator(); iter.hasNext();) {
				PVListener listener = iter.next();
				listener.pvValueUpdate(this);
			}
		}
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public boolean isConnected() {
		return (subscriber==null);
	}

	@Override
	public boolean isWriteAllowed() {
		return false;
	}

	@Override
	public String getStateInfo() {
		return isRunning ? "running" : "stopped";
	}

	@Override
	public void stop() {
		logger.info(getName() + " stop()");
		try {
			if (subscriber!=null)
				IceManager.unsubscribe(pvName, subscriber);
			
			subscriber = null;
		} catch (Exception e) {
			logger.warning("Could not unsubscribe to " + pvName);
		}
		
		isRunning = false;
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public void setValue(Object new_value) throws Exception {
		// TODO Auto-generated method stub

	}
	

}
