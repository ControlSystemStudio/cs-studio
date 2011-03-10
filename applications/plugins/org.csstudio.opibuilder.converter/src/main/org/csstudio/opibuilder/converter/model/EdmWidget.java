/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;


/**
 * Base class for all specific EdmWidget classes.
 *
 * @author Matevz
 *
 */
public class EdmWidget extends EdmEntity {

	@EdmAttributeAn private int x;
	@EdmAttributeAn private int y;
	@EdmAttributeAn private int w;
	@EdmAttributeAn private int h;
	@EdmAttributeAn private int major;
	@EdmAttributeAn private int minor;
	@EdmAttributeAn private int release;
	/**
	 * Constructs EdmWidget from general EdmEntity.
	 *
	 * @param genericEntity
	 * @throws EdmException
	 */
	public EdmWidget(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}
	
	
	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getRelease() {
		return release;
	}
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}
}
