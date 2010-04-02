package org.csstudio.utility.adlparser.fileParser;

public class ADLResource {
	private String _name;
	private Object _value;
	
	public static final String X = "X";
	public static final String Y = "Y";
	public static final String WIDTH = "Width";
	public static final String HEIGHT = "Height";
	public static final String FOREGROUND_COLOR = "ForeGround Color";
	public static final String BACKGROUND_COLOR = "BackGround Color";
	public static final String LINE_WIDTH = "LineWidth";
	public static final String STYLE = "Style";
	public static final String FILL = "Fill";
	public static final String CHANNEL = "Channel";
	public static final String POSTFIX = "Postfix";
	public static final String CONNECTION_STATE = "Connection State";
	public static final String CHILDREN = "Children";
	public static final String POINTS = "Points";
	public static final String RD_LABEL = "Label";
	public static final String RD_NAME = "Name";
	public static final String RD_ARGS = "Args";
	public static final String VISIBILITY = "Visibility";
	public static final String COLOR_RULE = "Color Rule";
	
	public ADLResource (String name, Object value){
		this._name = name;
		this._value = value;
	}
	
	/**
	 * @param _name the _name to set
	 */
	public void setName(String _name) {
		this._name = _name;
	}

	/**
	 * @return the _name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param _value the _value to set
	 */
	public void setValue(Object _value) {
		this._value = _value;
	}

	/**
	 * @return the _value
	 */
	public Object getValue() {
		return _value;
	}
	
	
}
