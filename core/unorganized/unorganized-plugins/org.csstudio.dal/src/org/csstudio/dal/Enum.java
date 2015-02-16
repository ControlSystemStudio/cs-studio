/**
 * 
 */
package org.csstudio.dal;

/**
 * Enum is object representing single value from list of enumerated values.
 * 
 * @author ikriznar
 *
 */
public interface Enum {
	
	/**
	 * Index within enumerated list of values
	 * @return index
	 */
	public int index();
	/**
	 * Value associated with this enumerated object. 
	 * @return value
	 */
	public Object value();
	/**
	 * Description of this enumerated object. 
	 * @return description
	 */
	public String description();

}
