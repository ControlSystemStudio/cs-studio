/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;


/**
 * Converter for double values.
 * 
 * @author Sven Wende
 * 
 */
class DoubleConverter implements IValueTypeConverter<Double> {
	/**
	 * {@inheritDoc}
	 */
	public Double convert(Object value) {
		Double result = 0.0;

		if (value != null) {
			if (value instanceof Double) {
				result = (Double) value;
			} else if (value instanceof Number) {
				Number n = (Number) value;
				result = n.doubleValue();
			} else {
				try {
					result = Double.valueOf(value.toString());
				} catch (NumberFormatException e) {
					result = 0.0;
				}
			}
		}

		assert result != null;
		return result;
	}
}