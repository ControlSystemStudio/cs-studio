package org.epics.css.dal.tango;

import java.util.EventObject;

import fr.esrf.Tango.DevError;
import fr.esrf.TangoApi.DeviceAttribute;

/**
 * 
 * <code>CallbackEvent</code> is an event fired by the TangoMonitorRequestCallback
 * when a response from the control system is received. It provides information
 * about the obtained response. It is also fired when the heartbeat properties
 * are changed on the source.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class CallbackEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private TangoMonitorRequestCallback source;
	private String uniqueName;
	private DeviceAttribute[] attributes;
	private boolean error;
	private DevError[] errors;
	private long timerTrigger;
	private boolean heartbeat;
	
	/**
	 * Constructs a new CallbackEvent describing the callback properties.
	 * 
	 * @param source the source of the event
	 * @param timerTrigger the timer trigger set on the source
	 * @param heartbeat true if periodic, false if on change 
	 */
	public CallbackEvent(TangoMonitorRequestCallback source, long timerTrigger, boolean heartbeat) {
		super(source);
		this.source = source;
		this.timerTrigger = timerTrigger;
		this.heartbeat = heartbeat;
	}

	/**
	 * Constructs a new CallbackEvent describing the response
	 * 
	 * @param source the source of the event
	 * @param uniqueName the unique name of the property from where the response was received
	 * @param attributes the attributes read
	 * @param error true if error occured
	 * @param errors the array of errors
	 */
	public CallbackEvent(TangoMonitorRequestCallback source, String uniqueName, DeviceAttribute[] attributes, boolean error, DevError[] errors) {
		super(source);
		this.source = source;
		this.uniqueName = uniqueName;
		this.attributes = attributes;
		this.error = error;
		this.errors = errors;
	}
	
	/**
	 * Returns the source.
	 * 
	 * @return the source
	 */
	public TangoMonitorRequestCallback getCallback() {
		return source;
	}
	
	/**
	 * Returns the read values.
	 * 
	 * @return the values
	 */
	public DeviceAttribute[] getAttributes() {
		return attributes;
	}
	
	/**
	 * Returns the array of errors.
	 * 
	 * @return the errors
	 */
	public DevError[] getErrors() {
		return errors;
	}
	
	/**
	 * Returns the property name from where the response originates.
	 * 
	 * @return the property name
	 */
	public String getUniqueName() {
		return uniqueName;
	}
	
	/**
	 * Returns true if this is an error response.
	 * 
	 * @return true if error, false if ok
	 */
	public boolean isError() {
		return error;
	}
	
	/**
	 * Returns the timer trigger for the periodic connection.
	 * 
	 * @return the timer trigger
	 */
	public long getTimerTrigger() {
		return timerTrigger;
	}
	
	/**
	 * Returns true if the source is periodic or false if on change.
	 * 
	 * @return true if periodic, false if on change
	 */
	public boolean isHeartbeat() {
		return heartbeat;
	}
	
}
