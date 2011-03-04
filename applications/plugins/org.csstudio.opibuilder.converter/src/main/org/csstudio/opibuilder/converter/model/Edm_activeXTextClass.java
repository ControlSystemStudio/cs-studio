/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing activeXTextClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_activeXTextClass extends EdmWidget {

//	@EdmAttributeAn private int major;
//	@EdmAttributeAn private int minor;
//	@EdmAttributeAn private int release;

//	@EdmAttributeAn private int x;
//	@EdmAttributeAn private int y;
//	@EdmAttributeAn private int w;
//	@EdmAttributeAn private int h;

	@EdmAttributeAn private EdmFont font;

	@EdmAttributeAn private EdmColor fgColor;
	@EdmAttributeAn private EdmColor bgColor;

	@EdmAttributeAn private EdmMultilineText value;
	@EdmAttributeAn @EdmOptionalAn private boolean autoSize;

	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn @EdmOptionalAn private boolean border;
	@EdmAttributeAn @EdmOptionalAn private boolean useDisplayBg;
	@EdmAttributeAn @EdmOptionalAn private boolean fgAlarm;
	@EdmAttributeAn @EdmOptionalAn private boolean bgAlarm;
	@EdmAttributeAn @EdmOptionalAn private String alarmPv;
		
	@EdmAttributeAn @EdmOptionalAn private String visPv;
	@EdmAttributeAn @EdmOptionalAn private double visMax;
	@EdmAttributeAn @EdmOptionalAn private double visMin;
	@EdmAttributeAn @EdmOptionalAn private boolean visInvert;
	
	public Edm_activeXTextClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}


	/**
	 * @return the fgAlarm
	 */
	public final boolean isFgAlarm() {
		return fgAlarm;
	}


	/**
	 * @return the bgAlarm
	 */
	public final boolean isBgAlarm() {
		return bgAlarm;
	}


	/**
	 * @return the alarmPv
	 */
	public final String getAlarmPv() {
		return alarmPv;
	}


	public EdmFont getFont() {
		return font;
	}

	public EdmColor getFgColor() {
		return fgColor;
	}

	public EdmColor getBgColor() {
		return bgColor;
	}

	public EdmMultilineText getValue() {
		return value;
	}

	public boolean isAutoSize() {
		return autoSize;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public boolean isBorder() {
		return border;
	}

	public boolean isUseDisplayBg() {
		return useDisplayBg;
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
