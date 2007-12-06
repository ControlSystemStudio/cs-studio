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
class LongSequenceConverter implements IValueTypeConverter<long[]> {
	private LongConverter simpleConverter = new LongConverter();

	/**
	 * {@inheritDoc}
	 */
	public long[] convert(Object value) {
		long[] result = new long[0];

		if (value != null) {
			if (value instanceof long[]) {
				result = (long[]) value;
			} else if (value instanceof Collection) {
				Object[] values = ((Collection) value).toArray();
				result = new long[values.length];

				for (int i = 0; i < values.length; i++) {
					result[i] = simpleConverter.convert(values[i]);
				}
			} else {
				result = new long[1];
				result[0] = simpleConverter.convert(value);
			}
		}
		assert result != null;
		return result;
	}
}