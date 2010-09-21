package org.epics.css.dal.tango;

import java.util.EventListener;

/**
 * 
 * <code>CallbackListener</code> receives updates from the {@link TangoMonitorRequestCallback},
 * when a response is received or when heartbeat properties change.
 * 
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface CallbackListener extends EventListener {

	/**
	 * Is called when the callback receives a response from the control system.
	 * 
	 * @param e the event describing the response
	 */
	void responseReceived(CallbackEvent e);
	
	/**
	 * Is called when the heartbeat properties on the callback are changed.
	 * 
	 * @param e the event describing the properties
	 */
	void propertiesChanged(CallbackEvent e);
}
