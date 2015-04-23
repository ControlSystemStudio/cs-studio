/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.palette;

/**The Major categories of widgets on the palette.
 * @author Xihui Chen
 *
 */
public enum MajorCategories {
	
	GRAPHICS("Graphics"),
	
	MONITORS("Monitors"),
	
	CONTROLS("Controls"),
	
	OTHERS("Others");
	
	private String description;
	
	private MajorCategories(String description){
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
