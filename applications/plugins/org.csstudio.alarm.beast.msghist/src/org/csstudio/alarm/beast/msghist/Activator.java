/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin Activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    final public static String ID = "org.csstudio.alarm.beast.msghist";

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }
}
