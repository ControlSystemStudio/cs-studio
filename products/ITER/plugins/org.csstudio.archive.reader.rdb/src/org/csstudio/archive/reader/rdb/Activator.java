/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.archive.reader.rdb";
    
    private static Activator instance;

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }
}
