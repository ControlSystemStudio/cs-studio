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
class StringSequenceConverter implements IValueTypeConverter<String[]> {
	/**
	 * {@inheritDoc}
	 */
	public String[] convert(Object value) {
		String[] result = new String[0];
		if (value != null) {
			if (value instanceof String[]) {
				result = (String[]) value;
			} else if (value instanceof Collection) {
				Object[] values = ((Collection) value).toArray();
				result = new String[values.length];

				for (int i = 0; i < values.length; i++) {
					result[i] = values[i].toString();
				}
			} else {
				result = new String[1];
				result[0] = value.toString();
			}
		}
		assert result != null;
		return result;
	}
}