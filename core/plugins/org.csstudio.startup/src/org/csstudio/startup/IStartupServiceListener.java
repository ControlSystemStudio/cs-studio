package org.csstudio.startup;


/**
 * Classes must implement this interface if they want
 * to register with the extension point to be executed
 * when the application starts (in headless more, or
 * in GUI mode).
 * 
 * @author avodovnik
 *
 */
public interface IStartupServiceListener {
	/**
	 * This method starts the service.
	 *
	 */
	public void run();
}
