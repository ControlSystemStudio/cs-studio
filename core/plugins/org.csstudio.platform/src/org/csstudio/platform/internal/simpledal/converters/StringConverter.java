/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;


/**
 * Converter for String values.
 * 
 * @author Sven Wende
 * 
 */
class StringConverter implements IValueTypeConverter<String> {
	/**
	 * {@inheritDoc}
	 */
	public String convert(Object value) {
		String result = value!=null?value.toString():"";
		assert result != null;
		return result;
	}
}