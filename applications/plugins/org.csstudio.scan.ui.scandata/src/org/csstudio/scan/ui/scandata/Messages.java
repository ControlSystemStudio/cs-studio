/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.eclipse.osgi.util.NLS;

/** Externalized strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.csstudio.scan.ui.scandata.messages"; //$NON-NLS-1$

	public static String DataFileExtension;

	public static String ExportDataToFile;

	public static String ExportFileFmt;

	public static String NoSampleTT;

	public static String ScanEditorTTFmt;

	public static String Timestamp;

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
