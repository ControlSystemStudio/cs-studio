/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.util;

import org.csstudio.autocomplete.ui.AutoCompleteUIPlugin;

/**
 * Helper for accessing UI.
 * 
 * <p>
 * This implementation provides the common support. Derived classes can add
 * support that is specific to RCP or RAP.
 * 
 * <p>
 * Client code should obtain a {@link UIHelper} via the
 * {@link AutoCompleteUIPlugin}
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
@SuppressWarnings("nls")
public class UIHelper {

	public SSTextLayout newTextLayout() {
		return new SSTextLayout();
	}

	public SSStyledText newStyledText() {
		return new SSStyledText();
	}

}
