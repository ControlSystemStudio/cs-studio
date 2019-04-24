/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Activator extends AbstractUIPlugin
{
    public static final String ID = "org.csstudio.logging.es"; //$NON-NLS-1$

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return Logger.getLogger(ID);
    }
}
