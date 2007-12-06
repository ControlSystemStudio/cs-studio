/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;

import java.util.Collection;


/**
 * Converter for double sequence values.
 * 
 * @author Sven Wende
 * 
 */
class ObjectSequenceConverter implements IValueTypeConverter<Object[]> {
	/**
	 * {@inheritDoc}
	 */
	public Object[] convert(Object value) {
		Object[] result = new Object[0];
		if (value != null) {
			if (value instanceof Object[]) {
				result = (Object[]) value;
			} else if (value instanceof Collection) {
				result = ((Collection) value).toArray();
			} else {
				result = new Object[1];
				result[0] = value;
			}
		}
		assert result != null;
		return result;
	}
}