package org.csstudio.platform;

/**
 * Interface for the IO name service.
 * 
 * @author Sven Wende
 */
// FIXME: swende: Provide a real implementation
public interface IoNameService {
	/**
	 * Returns the IO name for the specified key.
	 * 
	 * @param key
	 *            the key (mandatory)
	 * @param field
	 *            a field name (optional)
	 * @return the IO name for the specified key or null if no IO name exists
	 *         for that key
	 */
	String getIoName(String key, String field);
}
