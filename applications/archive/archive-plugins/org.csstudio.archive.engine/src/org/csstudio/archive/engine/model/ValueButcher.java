/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.archive.vtype.ArchiveVString;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;

/** Helper that does various unspeakable things to values.
 *  @author Kay Kasemir
 */
public class ValueButcher
{
    // These strings match the text representation of the
    // ChannelArchiver's informational severity codes
    // - if they existed in previous versions.

    /** Status string for 'disabled' state */
    private static final String DISABLED = "Archive_Disabled"; //$NON-NLS-1$

    /** Status string for 'disconnected' state */
    private static final String DISCONNECTED = "Disconnected"; //$NON-NLS-1$

    /** Status string for 'off' state */
    private static final String OFF = "Archive_Off"; //$NON-NLS-1$

    /** Status string for 'write error' state */
    private static final String WRITE_ERROR = "Write_Error"; //$NON-NLS-1$

    /** @return Info value to indicate disabled state */
    public static VType createDisabled()
    {
        return createInfoSample(DISABLED);
    }

    /** @return Info value to indicate disconnected state */
    public static VType createDisconnected()
    {
        return createInfoSample(DISCONNECTED);
    }

    /** @return Info value to indicate that archive was turned off */
    public static VType createOff()
    {
        return createInfoSample(OFF);
    }

    /** @return Info value to indicate write error */
    public static VType createWriteError()
    {
        return createInfoSample(WRITE_ERROR);
    }

    /** Create sample with status set to some info */
    private static VType createInfoSample(final String info)
    {
        return new ArchiveVString(Timestamp.now(), AlarmSeverity.INVALID, info, info);
    }
}
