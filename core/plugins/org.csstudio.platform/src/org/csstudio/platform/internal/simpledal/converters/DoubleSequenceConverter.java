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
class DoubleSequenceConverter implements IValueTypeConverter<double[]> {
	private DoubleConverter simpleConverter = new DoubleConverter();

	/**
	 * {@inheritDoc}
	 */
	public double[] convert(Object value) {
		double[] result = new double[0];
		if (value != null) {
			if (value instanceof double[]) {
				result = (double[]) value;
			} else if (value instanceof Collection) {
				Object[] values = ((Collection) value).toArray();
				result = new double[values.length];

				for (int i = 0; i < values.length; i++) {
					result[i] = simpleConverter.convert(values[i]);
				}
			} else {
				result = new double[1];
				result[0] = simpleConverter.convert(value);
			}
		}
		assert result != null;
		return result;
	}
}