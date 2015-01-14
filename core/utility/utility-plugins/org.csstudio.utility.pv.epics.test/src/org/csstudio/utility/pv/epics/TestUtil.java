/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.utility.pv.PV;

/** Setup that works outside of Eclipse libs.epics plugin
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestUtil
{
    final private static int MAX_ARRAY = 50000;

    final private static String NETWORK = "127.0.0.1";

    public static Level log_level = Level.ALL;

    /** Perform setup that's usually done by the CSS plugin
     *  based on Eclipse preferences.
     */
    public static void setup()
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(log_level);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(log_level);

        PVContext.use_pure_java = true;
        PVContext.support_dbe_property = true;

        System.setProperty("gov.aps.jca.jni.ThreadSafeContext.event_dispatcher",
                           "gov.aps.jca.event.DirectEventDispatcher");
        //                 "gov.aps.jca.event.QueuedEventDispatcher");

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", NETWORK);
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", NETWORK);
        System.setProperty("gov.aps.jca.jni.JNIContext.auto_addr_list", "false");

        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", Integer.toString(MAX_ARRAY));
        System.setProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", Integer.toString(MAX_ARRAY));
    }

    /** @param name PV name
     *  @return PV
     */
    public static PV getPV(final String name)
    {
        setup();
        // Hard-coded to use EPICS_V3_PV, not using PV Factory
        return new EPICS_V3_PV(name);
    }


}
