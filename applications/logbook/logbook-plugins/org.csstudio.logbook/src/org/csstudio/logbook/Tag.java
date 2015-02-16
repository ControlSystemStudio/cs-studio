package org.csstudio.logbook;

/**
 * A Tag consists of an unique name and state
 * 
 * @author shroffk
 *
 */
public interface Tag {

	/**
	 * @return String - tag name
	 */
	public String getName();
	
	/**
	 * @return String - tag state
	 */
	public String getState();
}
