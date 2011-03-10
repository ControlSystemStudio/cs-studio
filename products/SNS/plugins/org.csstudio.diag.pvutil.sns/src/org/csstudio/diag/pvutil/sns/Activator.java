/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.sns;

import java.util.logging.Logger;

/** Plugin info, not quite an Activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator
{
    /** Plugin ID registered in MANIFEST.MF */
    final public static String ID = "org.csstudio.diag.pvutil.sns";

    final private static Logger logger = Logger.getLogger(ID);

    /** @return Logger for plugin ID */
    final public static Logger getLogger()
    {
        return logger;
    }
}
