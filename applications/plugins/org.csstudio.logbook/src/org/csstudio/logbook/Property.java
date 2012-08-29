package org.csstudio.logbook;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An interface that represents properties attached to a LogEntry It has a name
 * and a group of attribute, value pairs.
 * 
 * @author shroffk
 * 
 */
public interface Property {

	/**
	 * The unique name to identify the property
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * A set of all the attributes defined for this property
	 * 
	 * @return
	 */
	public Collection<String> getAttributeNames();

	/**
	 * @return
	 */
	public Collection<String> getAttributeValues();

	/**
	 * Provides the value for the attribute _attributeName_
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getAttributeValue(String attributeName);

	/**
	 * @return
	 */
	public Set<Entry<String, String>> getAttributes();

}
