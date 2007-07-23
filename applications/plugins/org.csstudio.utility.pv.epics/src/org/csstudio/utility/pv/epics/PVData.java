package org.csstudio.utility.pv.epics;

import gov.aps.jca.Monitor;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;

/** Data of one PV.
 *  Accessed by CA callbacks as well as user threads,
 *  so this class keeps it synchronized.
 *  @author Kay Kasemir
 */
public class PVData
{
    /** isConnected?
     *  <code>true</code> if we are currently connected
     *  (based on the most recent connection callback).
     *  <p>
     *  EPICS_V3_PV also runs notifyAll() on the PV data
     *  whenever the connected flag changes.
     */
    boolean connected = false;

    /** Either <code>null</code>, or the subscription identifier. */
    Monitor subscription = null;
    
    /** Meta data obtained during connection cycle. */
    IMetaData meta = null;

    /** Most recent 'live' value. */
    IValue value = null;
}
