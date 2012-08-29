package org.csstudio.logbook;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author shroffk
 *
 */
public interface Property {
	
	/**
	 * @return
	 */
	public String getName();
	
	/**
	 * @return
	 */
	public Collection<String> getAttributeNames();
	
	/**
	 * @return
	 */
	public Collection<String> getAttributeValues();
	
	/**
	 * @param attributeName
	 * @return
	 */
	public String getAttributeValue(String attributeName);
	
	/**
	 * @return
	 */
	public Set<Entry<String, String>> getAttributes();

}
