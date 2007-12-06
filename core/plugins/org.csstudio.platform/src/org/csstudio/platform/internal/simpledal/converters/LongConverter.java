/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;


/**
 * Converter for long values.
 * 
 * @author Sven Wende
 * 
 */
class LongConverter implements IValueTypeConverter<Long> {
	/**
	 * {@inheritDoc}
	 */
	public Long convert(Object value) {
		Long result = 0l;

		if (value != null) {
			if (value instanceof Long) {
				result = (Long) value;
			} else if (value instanceof Number) {
				Number n = (Number) value;
				result = n.longValue();
			} else {
				try {
					result = Long.valueOf(value.toString());
				} catch (NumberFormatException e) {
					result = 0l;
				}
			}
		}
		assert result != null;
		return result;
	}
}