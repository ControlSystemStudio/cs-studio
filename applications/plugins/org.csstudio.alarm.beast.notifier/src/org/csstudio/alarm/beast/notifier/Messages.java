/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import org.eclipse.osgi.util.NLS;

/**
 * Access to externalized strings.
 * @author Fred Arnaud (Sopra Group)
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.notifier.messages"; //$NON-NLS-1$

	public static String Priority_IMPORTANT;
	public static String Priority_MAJOR;
	public static String Priority_MINOR;
	public static String Priority_OK;
	
	public static String Status_OK;
	public static String Status_CANCELED;
	public static String Status_FAILED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Prevent instantiation
	}
}