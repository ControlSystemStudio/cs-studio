/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.rcp;

import org.csstudio.autocomplete.ui.util.SSStyledText;
import org.csstudio.autocomplete.ui.util.SSTextLayout;
import org.csstudio.autocomplete.ui.util.UIHelper;

/**
 * Helper for accessing RCP UI.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class RCPUIHelper extends UIHelper {

	/** {@inheritDoc} */
	@Override
	public SSTextLayout newTextLayout() {
		return new RCPSSTextLayout();
	}

	/** {@inheritDoc} */
	@Override
	public SSStyledText newStyledText() {
		return new RCPSSStyledText();
	}

}
