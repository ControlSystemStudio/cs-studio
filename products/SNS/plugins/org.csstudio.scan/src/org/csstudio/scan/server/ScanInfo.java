/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server;

import java.io.Serializable;
import java.util.Date;

/** Information about a Scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfo implements Serializable
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private long id;
    final private String name;
    final private Date created;
    final private ScanState state;
    final private String error;
    final private long performed_work_units;
    final private long total_work_units;
    final private long current_address;
    final private String current_commmand;

    /** Initialize
     *  @param id
     *  @param name
     *  @param created
     *  @param state
     *  @param error
     *  @param performed_work_units
     *  @param total_work_units
     *  @param current_commmand
     */
    public ScanInfo(final long id, final String name, final Date created, final ScanState state,
            final String error,
            final long performed_work_units, final long total_work_units,
            final long current_address, final String current_commmand)
    {
        this.id = id;
        this.name = name;
        this.created = created;
        this.state = state;
        this.error = error;
        this.performed_work_units = performed_work_units;
        this.total_work_units = total_work_units;
        this.current_address = current_address;
        this.current_commmand = current_commmand;
    }

    /** @return Unique scan identifier (within JVM of the scan engine) */
    public long getId()
    {
        return id;
    }

    /** @return Name of the scan */
    public String getName()
    {
        return name;
    }

    /** @return Time when scan was created on server */
    public Date getCreated()
    {
        return created;
    }

    /** @return State of the scan */
    public ScanState getState()
    {
        return state;
    }

    /** @return <code>true</code> if state is Aborted, Failed or Finished */
    public boolean isDone()
    {
        switch (state)
        {
        case Aborted:
        case Failed:
        case Finished:
            return true;
        default:
            return false;
        }
    }

    /** @return Error (if state indicates failure) or <code>null</code> */
    public String getError()
    {
        return error;
    }

    /** @return Number of work units that have been performed */
    public long getPerformedWorkUnits()
    {
        return performed_work_units;
    }

    /** @return Total number of work units */
    public long getTotalWorkUnits()
    {
        return total_work_units;
    }

    /** @return Percent of work done, 0...100 */
    public int getPercentage()
    {
        if (total_work_units <= 0)
            return 0;
        return (int) (performed_work_units * 100 / total_work_units);
    }

    /** @return Address of currently executing command or -1 */
    public long getCurrentAddress()
    {
        return current_address;
    }

    /** @return Currently executing command or empty string */
    public String getCurrentCommand()
    {
        return current_commmand;
    }

    /** Hash on all elements
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + name.hashCode();
        result = prime * result + state.hashCode();
        result = prime * result + (int) (performed_work_units ^ (performed_work_units >>> 32));
        result = prime * result + (int) (total_work_units ^ (total_work_units >>> 32));
        result = prime * result + current_commmand.hashCode();
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        return result;
    }

    /** Compare nearly all elements
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof ScanInfo))
            return false;
        final ScanInfo other = (ScanInfo) obj;
        final boolean most_match =
               id == other.id  &&
               state == other.state  &&
               performed_work_units == other.performed_work_units  &&
               total_work_units == other.total_work_units  &&
               name.equals(other.name)  &&
               current_commmand.equals(other.current_commmand);
        if (! most_match)
            return false;
        // Comparing the error is difficult because the same error
        // (class, message, stack trace) will arrive as different instances
        // resulting from the RMI serialization.
        // We don't recursively compare each Exception element, just their class.
        return (error == null  &&  other.error == null) ||
               (error != null  &&  other.error != null  &&
                error.getClass() == other.error.getClass());
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Scan '").append(name).append("' [").append(id).append("]: ").append(state);
        if (error != null)
            buf.append(" (").append(error).append(")");
        buf.append(", ").append(getPercentage()).append("% done");
        if (current_address >= 0)
            buf.append(" @ ").append(current_address).append(", ").append(current_commmand);
        return buf.toString();
    }
}
