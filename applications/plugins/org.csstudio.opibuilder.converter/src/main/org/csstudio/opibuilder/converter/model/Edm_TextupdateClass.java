/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

/**
 * Specific class representing TextupdateClass widget.
 *
 * @author Matevz
 *
 */
public class Edm_TextupdateClass extends EdmWidget {


	@EdmAttributeAn private String controlPv;

	@EdmAttributeAn private EdmColor fgColor;
	@EdmAttributeAn private EdmColor bgColor;
	@EdmAttributeAn @EdmOptionalAn private boolean fill;
	
	@EdmAttributeAn @EdmOptionalAn private String mode;
	@EdmAttributeAn @EdmOptionalAn private int precision;
	
	@EdmAttributeAn private EdmFont font;
	@EdmAttributeAn @EdmOptionalAn private String fontAlign;

	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn @EdmOptionalAn private boolean lineAlarm;

	@EdmAttributeAn @EdmOptionalAn private boolean fgAlarm;
	
	public Edm_TextupdateClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

/**
	 * @return the mode
	 */
	public final String getMode() {
		return mode;
	}



	/**
	 * @return the precision
	 */
	public final int getPrecision() {
		return precision;
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

	public String getControlPv() {
		return controlPv;
	}

	public boolean isFill() {
		return fill;
	}

	public String getFontAlign() {
		return fontAlign;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public boolean isLineAlarm() {
		return lineAlarm;
	}

	public boolean isFgAlarm() {
		return fgAlarm;
	}
}
