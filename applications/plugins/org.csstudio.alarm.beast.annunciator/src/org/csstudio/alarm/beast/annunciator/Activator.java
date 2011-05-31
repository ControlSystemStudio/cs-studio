/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator;

import java.util.logging.Logger;

/** Not really an activator, just holds ID
 *  @author Kay Kasemir
 */
public class Activator
{
	/** The plug-in ID defined in MANIFEST.MF */
	public static final String PLUGIN_ID = "org.csstudio.alarm.beast.annunciator"; //$NON-NLS-1$

	/** @return Logger for plugin ID */
	public static Logger getLogger()
	{
	    return Logger.getLogger(PLUGIN_ID);
	}
}
