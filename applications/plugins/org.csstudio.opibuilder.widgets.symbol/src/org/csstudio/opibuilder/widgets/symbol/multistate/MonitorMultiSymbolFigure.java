/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.opibuilder.widgets.symbol.image.AbstractSymbolImage;
import org.csstudio.opibuilder.widgets.symbol.image.MonitorSymbolImage;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public class MonitorMultiSymbolFigure extends CommonMultiSymbolFigure {

	@Override
	protected AbstractSymbolImage createSymbolImage(boolean runMode) {
		MonitorSymbolImage msi = new MonitorSymbolImage(runMode);
		if (symbolProperties != null) {
			symbolProperties.fillSymbolImage(msi);
		}
		return msi;
	}
	
	public MonitorMultiSymbolFigure(boolean runMode) {
		super(runMode);
	}

}
