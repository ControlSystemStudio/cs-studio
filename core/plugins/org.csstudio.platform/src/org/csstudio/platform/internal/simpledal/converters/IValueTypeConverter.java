/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;

/**
 * Value type converter.
 * 
 * @author swende
 * 
 * @param <E>
 */
interface IValueTypeConverter<E> {
	/**
	 * Converts the specified value. A fall back value is returned in case a
	 * null or incompatible value or a incompatible value is provided. The fall
	 * back value is NOT null.
	 * 
	 * @param value
	 *            the value which should be converted
	 * 
	 * @return a value of the right type, NEVER null
	 */
	E convert(Object value);
}