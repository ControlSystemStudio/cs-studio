/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email;

import java.util.logging.Logger;

/** Not really Plugin Activator, just ID
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
    /**
     * The id of this Java plug-in (value <code>{@value}</code> as defined in MANIFEST.MF.
     */
    final public static String ID = "org.csstudio.email";
    
    final private static Logger logger = Logger.getLogger(ID);

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return logger;
	}
}
