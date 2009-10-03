package org.epics.css.dal.tango;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.epics.css.dal.RemoteException;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttrReadEvent;
import fr.esrf.TangoApi.CallBack;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.events.EventData;
import fr.esrf.TangoDs.TangoConst;

/**
 * 
 * <code>TangoMonitorRequestCallback</code> is a callback class that 
 * receives notifications from the tango control system and forwards
 * them to the registered callback listeners. This class is a sort 
 * of adapter class for {@link MonitorProxyImpl}, because tango
 * allows only one monitor per property.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
class TangoMonitorRequestCallback extends CallBack {

	private static final long serialVersionUID = 8001560457662183225L;
	
	private List<CallbackListener> listeners = Collections.synchronizedList(new ArrayList<CallbackListener>());
	
	private final PropertyProxyImpl<?> source;
	
	private boolean destroyed = false;
	
	private int eventID = -1;
	private long timerTrigger = MonitorProxyImpl.DEFAULT_TIMER_TRIGGER;
	private boolean heartbeat = true;
	
	/**
	 * Constructs a new TangoMonitorRequestCallback.
	 * 
	 * @param uniqueName the unique property name that this callback listens to 
	 */
	TangoMonitorRequestCallback(PropertyProxyImpl<?> source) {
		super();
		this.source = source;
	}
	
	/**
	 * Initializes this callback.
	 * 
	 * @param timerTrigger the timer trigger for periodic polling
	 * @param heartbeat true if heartbeat monitor is requested or false if on change is requested
	 * 
	 * @throws RemoteException if initialization of monitor failed
	 */
	synchronized void initialize(long timerTrigger, boolean heartbeat) throws RemoteException {
		this.timerTrigger = timerTrigger;
		this.heartbeat = heartbeat;
		firePropertiesChanged();
		try {
			if (eventID >= 0) {
				source.getDeviceProxy().unsubscribe_event(eventID);
			}
			int event = 0;
			source.getDeviceProxy().poll_attribute(source.getPropertyName().getPropertyName(),(int)timerTrigger);
			if (heartbeat) {
				event = TangoConst.PERIODIC_EVENT;
			} else {
				event = TangoConst.CHANGE_EVENT;
			}
			eventID = source.getDeviceProxy().subscribe_event(source.getPropertyName().getPropertyName(),event,this,new String[0],false);
		} catch (DevFailed e) {
			throw new RemoteException(this, "Cannot create monitor for '" + source.getPropertyName().getPropertyName() +"' on device '" + source.getDeviceProxy().get_name() +"'.",e);
		}
	}
	
	/**
	 * Adds a calback listener, which will receive updates from this callback
	 * when a response is received.
	 * 
	 * @param listener the listener to be added
	 */
	void addCallbackListener(CallbackListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a callback listener from this callback.
	 * 
	 * @param listener the listener to be removed
	 */
	void removeCallbackListener(CallbackListener listener) {
		listeners.remove(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#attr_read(fr.esrf.TangoApi.AttrReadEvent)
	 */
	@Override
	public void attr_read(AttrReadEvent evt) {
		CallbackEvent e = new CallbackEvent(this, source.getUniqueName(), evt.argout, evt.err, evt.errors);
		fireResponseReceived(e);
	}
		
	/*
	 * (non-Javadoc)
	 * @see fr.esrf.TangoApi.CallBack#push_event(fr.esrf.TangoApi.events.EventData)
	 */
	@Override
	public void push_event(EventData evt) {
		CallbackEvent e = new CallbackEvent(this, source.getUniqueName(), new DeviceAttribute[]{evt.attr_value}, evt.err, evt.errors);
		fireResponseReceived(e);
	}
	
	/**
	 * Notifies all registered callback listeners.
	 * 
	 * @param event the event to be fired.
	 */
	private void fireResponseReceived(CallbackEvent event) {
		if (destroyed) return;
		CallbackListener[] list = listeners.toArray(new CallbackListener[listeners.size()]);
		for (CallbackListener l : list) {
			l.responseReceived(event);
		}
	}
	
	/**
	 * Notifies all registered callback listeners.
	 * 
	 * @param event the event to be fired.
	 */
	private void firePropertiesChanged() {
		if (destroyed) return;
		CallbackEvent e = new CallbackEvent(this, timerTrigger, heartbeat);
		CallbackListener[] list = listeners.toArray(new CallbackListener[listeners.size()]);
		for (CallbackListener l : list) {
			l.propertiesChanged(e);
		}
	}
	
	/**
	 * Destroys this callback. No events are fired after the callback is destroyed.
	 */
	void destroy() {
		try {
			if (eventID >= 0) {
				source.getDeviceProxy().unsubscribe_event(eventID);
			}
		} catch (DevFailed e) {
			CallbackEvent event = new CallbackEvent(this,source.getUniqueName(),null,true,new DevError[]{new DevError(null,null,e.getMessage(),null)});
			fireResponseReceived(event);
		} finally {
			listeners.clear();
			destroyed = true;
		}
	}
}
