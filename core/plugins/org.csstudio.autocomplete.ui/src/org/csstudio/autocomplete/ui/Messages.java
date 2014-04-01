/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Eclipse string externalization.
 * 
 * @author Fred Arnaud (Sopra Group)
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.csstudio.autocomplete.ui.messages"; //$NON-NLS-1$

	public static String PrefPage_Title;
	public static String PrefPage_HistorySize;
	public static String PrefPage_ClearHistory;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instantiation
	}
}
