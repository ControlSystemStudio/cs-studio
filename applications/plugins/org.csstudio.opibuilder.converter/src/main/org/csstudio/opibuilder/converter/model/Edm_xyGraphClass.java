/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

/**
 * @author Lei Hu, Xihui Chen 
 *
 */
public class Edm_xyGraphClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String graphTitle;
	@EdmAttributeAn @EdmOptionalAn private String xLabel;
	@EdmAttributeAn @EdmOptionalAn private String yLabel;
	@EdmAttributeAn @EdmOptionalAn private String y2Label;
	

	@EdmAttributeAn @EdmOptionalAn private EdmColor gridColor;
	
	@EdmAttributeAn @EdmOptionalAn private boolean border;
	@EdmAttributeAn @EdmOptionalAn private boolean plotAreaBorder;
	//------------
	@EdmAttributeAn @EdmOptionalAn private boolean autoScaleBothDirections;
	@EdmAttributeAn @EdmOptionalAn private double autoScaleThreshPct;
	@EdmAttributeAn @EdmOptionalAn private String plotMode;
	@EdmAttributeAn @EdmOptionalAn private int nPts;
	@EdmAttributeAn @EdmOptionalAn private int updateTimerMs; //Update Delay
	@EdmAttributeAn @EdmOptionalAn private String triggerPv;
	@EdmAttributeAn @EdmOptionalAn private String resetPv;
	@EdmAttributeAn @EdmOptionalAn private String resetMode;
	
	@EdmAttributeAn @EdmOptionalAn private double xMin;
	@EdmAttributeAn @EdmOptionalAn private double yMin;
	@EdmAttributeAn @EdmOptionalAn private double y2Min;
	
	@EdmAttributeAn @EdmOptionalAn private double xMax;
	@EdmAttributeAn @EdmOptionalAn private double yMax;
	@EdmAttributeAn @EdmOptionalAn private double y2Max;
	
	@EdmAttributeAn @EdmOptionalAn private boolean xShowMajorGrid;
	@EdmAttributeAn @EdmOptionalAn private boolean yShowMajorGrid;
	@EdmAttributeAn @EdmOptionalAn private boolean y2ShowMajorGrid;
	
	@EdmAttributeAn @EdmOptionalAn private String xAxisSrc; //AutoScale, fromUser
	@EdmAttributeAn @EdmOptionalAn private String yAxisSrc; 
	@EdmAttributeAn @EdmOptionalAn private String y2AxisSrc; 
	
	@EdmAttributeAn @EdmOptionalAn private EdmMultiColors plotColor;
	
	
	//------------
	@EdmAttributeAn @EdmOptionalAn private boolean showXAxis;
	@EdmAttributeAn @EdmOptionalAn private boolean showYAxis;
	@EdmAttributeAn @EdmOptionalAn private boolean showY2Axis;
	
	@EdmAttributeAn @EdmOptionalAn private String xAxisStyle;
	@EdmAttributeAn @EdmOptionalAn private String xAxisTimeFormat;
	@EdmAttributeAn @EdmOptionalAn private String yAxisStyle;
	@EdmAttributeAn @EdmOptionalAn private String y2AxisStyle;
	
	
	// trace properties
	@EdmAttributeAn @EdmOptionalAn private int numTraces;
	@EdmAttributeAn @EdmOptionalAn private EdmMultilineText xPv;
	@EdmAttributeAn @EdmOptionalAn private EdmMultilineText yPv;
	
	
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
	
	public String getY2AxisStyle() {
		return y2AxisStyle;
	}
	
	public final EdmMultiColors getPlotColor() {
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
