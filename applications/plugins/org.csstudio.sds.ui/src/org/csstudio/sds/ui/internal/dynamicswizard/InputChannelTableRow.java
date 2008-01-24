/**
 * 
 */
package org.csstudio.sds.ui.internal.dynamicswizard;

public class InputChannelTableRow {
	private ParameterType _parameterType;
	private String _description;
	private String _channel;
	private Object _defaultValue = null;
	private Class _valueType;

	public InputChannelTableRow(ParameterType parameterType, String description, String channel, Class valueType) {
		_parameterType = parameterType;
		_description = description;
		_channel = channel;
		_valueType = valueType;
	}
	
	public InputChannelTableRow(ParameterType parameterType, String description, String channel) {
		this(parameterType, description, channel, Object.class);
	}

	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String description) {
		_description = description;
	}

	public String getChannel() {
		return _channel;
	}
	
	public void setChannel(String channel) {
		_channel = channel;
	}
	
	public Object getDefaultValue() {
		return _defaultValue;
	}
	
	public String getDefaultValueAsString() {
		if (_defaultValue==null) {
			return "";
		}
		return _defaultValue.toString();
	}
	
	public void setValueType(Class clazz) {
		_valueType = clazz;
	}
	
	public Class getValueType() {
		return _valueType;
	}
	
	public void setDefaultValue(Object defaultvalue) {
		_defaultValue = defaultvalue;
	}

	public ParameterType getParameterType() {
		return _parameterType;
	}
	
	
}