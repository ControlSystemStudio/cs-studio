/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.util.logging.Logger;

/** Not really a plugin activator, for now just ID info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.alarm.beast.server";

    final public static Logger logger = Logger.getLogger(ID);

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }
}
