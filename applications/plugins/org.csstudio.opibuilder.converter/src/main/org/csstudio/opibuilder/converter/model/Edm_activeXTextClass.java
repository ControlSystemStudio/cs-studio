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



	@EdmAttributeAn private EdmMultilineText value;
	@EdmAttributeAn @EdmOptionalAn private boolean autoSize;

	@EdmAttributeAn @EdmOptionalAn private int lineWidth;
	@EdmAttributeAn @EdmOptionalAn private boolean border;
	@EdmAttributeAn @EdmOptionalAn private boolean useDisplayBg;
	@EdmAttributeAn @EdmOptionalAn private String alarmPv;
		
	public Edm_activeXTextClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}


	/**
	 * @return the alarmPv
	 */
	public final String getAlarmPv() {
		return alarmPv;
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

}
