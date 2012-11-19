package org.csstudio.opibuilder.widgets.symbol.bool;

/**
 * Monitor model for a Boolean Symbol Image widget.
 * 
 * @author SOPRA Group
 * 
 */
public class MonitorBoolSymbolModel extends CommonBoolSymbolModel {

	/**
	 * Type ID for Boolean Symbol Image Monitor widget
	 */
	private static final String ID = "org.csstudio.opibuilder.widgets.symbol.bool.BoolMonitorWidget";

	/**
	 * Initialize the properties when the widget is first created.
	 */
	public MonitorBoolSymbolModel() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}


}
