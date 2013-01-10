/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.osgi.util.NLS;

/** Externalized texts
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.navigator.applaunch.messages"; //$NON-NLS-1$
	public static String CommandLbl;
	public static String CommandTT;
	public static String ConfigFileErrorFmt;
	public static String ConfigureDescr;
	public static String CustomIconLbl;
	public static String CustomIconTT;
	public static String Error;
	public static String FileWizardDescr;
	public static String IconLbl;
	public static String LaunchConfigTitle;
	public static String LaunchConfigUpdateErrorFmt;
    public static String LaunchErrorApp;
	public static String LaunchErrorProgram;
	public static String LaunchErrorCmd;
	public static String NoProgramFoundError;


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
