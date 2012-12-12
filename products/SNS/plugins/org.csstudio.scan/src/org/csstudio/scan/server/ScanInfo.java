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
public class ScanInfo extends Scan implements Serializable
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private ScanState state;
    final private String error;
    final private long runtime_ms;
    final private long performed_work_units;
    final private long total_work_units;
    final private long current_address;
    final private String current_commmand;

    /** Initialize
     *  @param scan {@link Scan}
     *  @param state Scan state
     */
    public ScanInfo(final Scan scan, final ScanState state)
    {
        this(scan, state, null, 0, 0, 0, 0, "");
    }

    /** Initialize
     *  @param scan Scan
     *  @param state Scan state
     *  @param error Error or <code>null</code>
     *  @param runtime_ms Runtime in millisecs
     *  @param performed_work_units Work units performed so far
     *  @param total_work_units Total number of work units
     *  @param current_commmand Description of current command
     */
    public ScanInfo(final Scan scan, final ScanState state,
            final String error, final long runtime_ms,
            final long performed_work_units, final long total_work_units,
            final long current_address, final String current_commmand)
    {
        super(scan);
        this.state = state;
        this.error = error;
        this.runtime_ms = runtime_ms;
        this.performed_work_units = performed_work_units;
        this.total_work_units = total_work_units;
        this.current_address = current_address;
        this.current_commmand = current_commmand;
    }

    /** @return State of the scan */
    public ScanState getState()
    {
        return state;
    }

    /** @return Error (if state indicates failure) or <code>null</code> */
    public String getError()
    {
        return error;
    }

    /** @return Run time of scan in milliseconds */
    public long getRuntimeMillisecs()
    {
        return runtime_ms;
    }

    /** @return Run time of scan as text */
    public String getRuntimeText()
    {
        if (runtime_ms < 1000)
            return runtime_ms + " ms";
        long seconds = runtime_ms / 1000;

        final long hours = seconds / 60 / 60;
        seconds -= hours * 60 * 60;

        final long minutes = seconds / 60;
        seconds -= minutes * 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
    
    /** @return (Estimated) finish time or <code>null</code> */
    public Date getFinishTime()
    {
        if (state.isDone())
            return new Date(getCreated().getTime() + runtime_ms);
        if (state.isActive())
        {
            // Better estimate based on total_work_units?
            if (performed_work_units <= 0)
                return getCreated();

            // Will need runtime_ms*total_work_units/performed_work_units
            final Date finish = new Date(getCreated().getTime() + runtime_ms*total_work_units/performed_work_units);
            return finish;
        }
        return null;
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

    /** Hash on most elements
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + state.hashCode();
        return result;
    }

    /** Compare all elements
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof ScanInfo))
            return false;
        final ScanInfo other = (ScanInfo) obj;
        return getId() == other.getId()  &&
               state == other.state  &&
               runtime_ms == other.runtime_ms &&
               performed_work_units == other.performed_work_units  &&
               total_work_units == other.total_work_units  &&
               current_commmand.equals(other.current_commmand) &&
               ((error == null  && other.error == null) ||
                (error != null  && error.equals(other.error))
               );
    }

    /** @return String representation for GUI */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Scan '").append(getName()).append("' [").append(getId()).append("]: ").append(state);
        if (error != null)
            buf.append(" (").append(error).append(")");
        if (state.isActive())
            buf.append(", ").append(getPercentage()).append("% done");
        return buf.toString();
    }
}
