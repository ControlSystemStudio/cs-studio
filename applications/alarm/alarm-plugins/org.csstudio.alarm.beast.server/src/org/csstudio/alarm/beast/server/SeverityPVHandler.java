/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.SeverityLevel;

/** Handle the update of "severity PVs"
 *
 *  <p>Maintains the PV connections,
 *  updates the PVs in a background thread.
 *
 *  @author Kay Kasemir
 */
public class SeverityPVHandler
{

    public static void update(final String severity_pv_name, final SeverityLevel severity)
    {
        System.out.println("Should update PV '" + severity_pv_name + "' to " + severity.name());
        // TODO Write to queue, handle that in other thread
    }

    public static void stop()
    {
        // TODO Stop all PVs
    }
}
