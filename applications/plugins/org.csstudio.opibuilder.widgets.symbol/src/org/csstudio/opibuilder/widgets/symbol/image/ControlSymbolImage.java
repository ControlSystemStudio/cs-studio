/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.image;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public class ControlSymbolImage extends AbstractSymbolImage {

	public ControlSymbolImage(boolean runMode) {
		super(runMode);
	}
	
	public final static Color DISABLE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY);	
	
	/** The alpha (0 is transparency and 255 is opaque) for disabled paint */
	public static final int DISABLED_ALPHA = 100;
	
}
