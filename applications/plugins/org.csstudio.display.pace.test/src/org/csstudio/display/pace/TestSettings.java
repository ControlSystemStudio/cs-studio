/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;
import org.epics.pvmanager.loc.LocalDataSource;

/** Settings for unit tests
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestSettings
{
    /** EPICS CA address list */
    final private static String CA_ADDR_LIST = "127.0.0.1 160.91.228.17";

    /** Configuration file name used for testing.
     *  Tests are specific to this file,
     *  and some also expect to actually connect to
     *  the PVs in there.
     */
    final public static String TEST_CONFIG_FILE = "configFiles/rf_pwr_limits.pace";

    public static void setup()
    {
        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", CA_ADDR_LIST);
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", CA_ADDR_LIST);

        final CompositeDataSource sources = new CompositeDataSource();
        sources.putDataSource("loc", new LocalDataSource());
        sources.putDataSource("ca", new JCADataSource());
        sources.setDefaultDataSource("ca");
        PVManager.setDefaultDataSource(sources);
    }
}
