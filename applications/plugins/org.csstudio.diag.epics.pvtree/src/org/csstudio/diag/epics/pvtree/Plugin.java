/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin class for EPICS PV Tree.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractUIPlugin
{
    /** The plug-in ID defined in MANIFEST.MF */
    public static final String ID = "org.csstudio.diag.epics.pvtree"; //$NON-NLS-1$

    /** The shared instance */
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }
    
    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }
}
