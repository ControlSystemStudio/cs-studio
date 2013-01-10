/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.eclipse.osgi.util.NLS;

/** Localized Texts
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.ui.menu.pvscript.messages"; //$NON-NLS-1$
	public static String Error;
	public static String PrefEdit_Command;
	public static String PrefEdit_Description;
	public static String PrefEdit_IndividualScripts;
	public static String PrefEdit_Scripts;
	public static String PreferenceErrorFmt;
	public static String PreferencePageMessage;
	public static String ScriptExecutionErrorFmt;
	public static String ScriptInfoDlgTitle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
		// Prevent instantiation
	}
}
