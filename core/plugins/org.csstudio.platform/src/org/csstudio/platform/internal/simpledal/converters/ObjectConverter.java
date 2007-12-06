/**
 * 
 */
package org.csstudio.platform.internal.simpledal.converters;


/**
 * Converter for Object values.
 * 
 * @author Sven Wende
 * 
 */
class ObjectConverter implements IValueTypeConverter<Object> {
	/**
	 * {@inheritDoc}
	 */
	public Object convert(Object value) {
		Object result = value!=null?value:new Object();
		assert result != null;
		return result;
	}
}