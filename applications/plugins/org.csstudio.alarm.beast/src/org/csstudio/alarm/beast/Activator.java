/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.logging.Logger;

/** Plug-in activator
 *  @author Kay Kasemir
 */
public class Activator
{
	/** Plug-in ID, defined in MANUIFEST.MF */
	public static final String ID = "org.csstudio.alarm.beast"; //$NON-NLS-1$

	/** @return Logger for the plugin ID */
	public static Logger getLogger()
	{
	    return Logger.getLogger(ID);
	}
}
