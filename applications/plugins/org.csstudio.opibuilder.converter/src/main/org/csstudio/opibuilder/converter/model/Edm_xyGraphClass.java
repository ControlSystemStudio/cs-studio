package org.csstudio.opibuilder.converter.model;

public class Edm_xyGraphClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String graphTitle;
	@EdmAttributeAn @EdmOptionalAn private String xLabel;
	@EdmAttributeAn @EdmOptionalAn private String yLabel;
	@EdmAttributeAn @EdmOptionalAn private String y2Label;
	
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor gridColor;
	
	@EdmAttributeAn @EdmOptionalAn private boolean border;
	@EdmAttributeAn @EdmOptionalAn private boolean plotAreaBorder;
	@EdmAttributeAn @EdmOptionalAn private EdmMultilineText plotColor;
	
	//axis ×ª»»
	@EdmAttributeAn @EdmOptionalAn private boolean showXAxis;
	@EdmAttributeAn @EdmOptionalAn private boolean showYAxis;
	@EdmAttributeAn @EdmOptionalAn private boolean showY2Axis;
	
	@EdmAttributeAn @EdmOptionalAn private String xAxisStyle;
	@EdmAttributeAn @EdmOptionalAn private String xAxisTimeFormat;
	@EdmAttributeAn @EdmOptionalAn private String yAxisStyle;
	@EdmAttributeAn @EdmOptionalAn private String yAxisTimeFormat;
	// trace properties
	@EdmAttributeAn @EdmOptionalAn private int numTraces;
	@EdmAttributeAn @EdmOptionalAn private EdmMultilineText xPv;
	@EdmAttributeAn @EdmOptionalAn private EdmMultilineText yPv;
	@EdmAttributeAn @EdmOptionalAn private String triggerPv;
	public Edm_xyGraphClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public final String getGraphTitle() {
		return graphTitle;
	}
	public final String getXLabel() {
		return xLabel;
	}
	public final String getYLabel() {
		return yLabel;
	}
	public final String getY2Label() {
		return y2Label;
	}
	
	public EdmColor getFgColor() {
		return fgColor;
	}
	public EdmColor getBgColor() {
		return bgColor;
	}
	public EdmColor getGridColor() {
		return gridColor;
	}
	
	public boolean isBorder(){
		return border;
	}
	public boolean isPlotAreaBorder(){
		return plotAreaBorder;
	}
	//axis 
	public boolean isShowXAxis(){
		return showXAxis;
	}
	public boolean isShowYAxis(){
		return showYAxis;
	}
	public boolean isShowY2Axis(){
		return showY2Axis;
	}
	
	public final String getXAxisStyle() {
		return xAxisStyle;
	}
	public final String getXAxisTimeFormat() {
		return xAxisTimeFormat;
	}
	public final String getYAxisStyle() {
		return yAxisStyle;
	}
	public final String getYAxisTimeFormat() {
		return yAxisTimeFormat;
	}
	
	
	public final EdmMultilineText getPlotColor() {
		return plotColor;
	}	
	public final EdmMultilineText getXPv() {
		return xPv;
	}	
	public final EdmMultilineText getYPv() {
		return yPv;
	}
	public int getNumTraces() {
		return numTraces;
	}
	
	public final String getTriggerPv() {
		return triggerPv;
	}
}
