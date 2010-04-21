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
	public static final String LOPR_SRC = "LOPR Source";
	public static final String LOPR_DEFAULT = "LOPR Default";
	public static final String HOPR_SRC = "HOPR Source";
	public static final String HOPR_DEFAULT = "HOPR Default";
	public static final String PREC_SRC = "PREC Source";
	public static final String PREC_DEFAULT = "PREC Default";
	public static final String PLOT_TITLE = "title";
	public static final String PLOT_XLABEL = "x label";
	public static final String PLOT_YLABEL = "y label";
	public static final String PEN_COLOR = "color";
	public static final String ADL_OBJECT = "object";
	public static final String ADL_MONITOR = "monitor";
	public static final String ADL_CONTROL = "control";
	public static final String ADL_BASIC_ATTRIBUTE = "basic attribute";
	public static final String ADL_CHILDREN = "children";
	public static final String ADL_DYNAMIC_ATTRIBUTES = "dynamic attributes";
	public static final String ADL_LIMITS = "limits";
	public static final String ADL_MENU_ITEM = "menu item";
	public static final String ADL_PEN = "pen";
	public static final String ADL_PLOTCOM = "plotcom";
	public static final String ADL_PLOTDATA = "plot data";
	public static final String ADL_PLOT_TRACE = "trace";
	public static final String ADL_POINTS = "points";
	public static final String ADL_SENSITIVE = "sensitive";
	public static final String ADL_RELATED_DISPLAY_ITEM = "trace";
	
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
