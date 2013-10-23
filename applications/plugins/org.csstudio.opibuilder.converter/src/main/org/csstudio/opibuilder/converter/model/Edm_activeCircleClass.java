/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing activeRectangleClass widget.
 *
 * @author 
 *
 */
public class Edm_activeCircleClass extends EdmWidget {

	
	@EdmAttributeAn private EdmColor lineColor;
	
	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn @EdmOptionalAn private EdmLineStyle lineStyle;	
	@EdmAttributeAn @EdmOptionalAn private EdmColor fillColor;
	@EdmAttributeAn @EdmOptionalAn private boolean fill;
	@EdmAttributeAn @EdmOptionalAn private boolean invisible;
	@EdmAttributeAn @EdmOptionalAn private boolean lineAlarm;
	@EdmAttributeAn @EdmOptionalAn private boolean fillAlarm;
	@EdmAttributeAn @EdmOptionalAn private String alarmPv;
	@EdmAttributeAn @EdmOptionalAn private String visPv;
	@EdmAttributeAn @EdmOptionalAn private double visMax;
	@EdmAttributeAn @EdmOptionalAn private double visMin;
	@EdmAttributeAn @EdmOptionalAn private boolean visInvert;

	public Edm_activeCircleClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	

	

	/**
	 * @return the lineAlarm
	 */
	public final boolean isLineAlarm() {
		return lineAlarm;
	}





	/**
	 * @return the fillAlarm
	 */
	public final boolean isFillAlarm() {
		return fillAlarm;
	}





	/**
	 * @return the alarmPv
	 */
	public final String getAlarmPv() {
		return alarmPv;
	}





	public EdmColor getLineColor() {
		return lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public EdmLineStyle getLineStyle() {
		return lineStyle;
	}

	public EdmColor getFillColor() {
		return fillColor;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public boolean isFill(){
		return fill;
	}
	
	
	public String getVisPv() {
		return visPv;
	}

	public double getVisMax() {
		return visMax;
	}

	public double getVisMin() {
		return visMin;
	}

	public boolean isVisInvert() {
		return visInvert;
	}
}
