/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import java.util.logging.Logger;

/** Not really a plugin activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.alarm.beast.ui.areapanel";
    
    /** @return Logger for the plugin ID */
    public static Logger getLogger()
    {
    	return Logger.getLogger(ID);
    }
}
