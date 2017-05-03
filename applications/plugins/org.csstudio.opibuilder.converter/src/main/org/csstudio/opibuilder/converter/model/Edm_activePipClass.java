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
 * @author Matevz
 *
 */
public class Edm_activePipClass extends EdmWidget {

	@EdmAttributeAn @EdmOptionalAn private String displaySource;
	@EdmAttributeAn @EdmOptionalAn private String file;
	@EdmAttributeAn @EdmOptionalAn private EdmColor fgColor;
	@EdmAttributeAn @EdmOptionalAn private EdmColor bgColor;

	public Edm_activePipClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}


	/**
	 * @return the alarmPv
	 */
	public final String getDisplaySource() {
		return displaySource;
	}

	public final String getFile() {
		return file;
	}



	public EdmColor getFgColor() {
		return fgColor;
	}
	public EdmColor getBgColor() {
		return bgColor;
	}
}
