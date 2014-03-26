/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.util;

import org.csstudio.opibuilder.widgets.symbol.multistate.CommonMultiSymbolModel;

/**
 * Label position values for {@link CommonMultiSymbolModel}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public enum SymbolLabelPosition {

	DEFAULT("Default"),				
	TOP("Top"),	
	LEFT("Left"),
	CENTER("Center"),
	RIGHT("Right"),
	BOTTOM("Bottom"),
	TOP_LEFT("Top Left"),
	TOP_RIGHT("Top Right"),	
	BOTTOM_LEFT("Bottom Left"),
	BOTTOM_RIGHT("Bottom Right");
	
	public static String[] stringValues() {
		String[] result = new String[values().length];
		int i = 0;
		for (SymbolLabelPosition h : values()) {
			result[i++] = h.toString();
		}
		return result;
	}

	String descripion;

	SymbolLabelPosition(String description) {
		this.descripion = description;
	}

	@Override
	public String toString() {
		return descripion;
	}
}
