/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.archive.diirt.datasource;

import java.time.Instant;
import java.util.Date;

import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.vtype.Time;

/**
 *
 * <code>DynamicArchiveChannelHandler</code> is a channel handler that is bound to a specific pv. The time window for
 * the channel can be changed via the write method. The write method accepts different variations of parameters:
 * <ul>
 * <li>Boolean - sets the value of the optimised data retrieval</li>
 * <li>boolean[1] - sets the value of the optimised data retrieval</li>
 * <li>Long or {@link Date} or {@link Timestamp} or {@link Time} - fetches a single archive value at the given time in
 * UTC</li>
 * <li>long[2] or Long[2] or Date[2] or Timestamp[2] or Time[2] - fetches the archived values between the first and
 * second timestamp given as array elements</li>
 * <li>Object[3] - fetches the archive values between param[0] and param[1] using optimised or raw retrieval as
 * specified by param[2]. The first two parameters can be Date, Timestamp, Time or Long, the third parameter has to be
 * Boolean.</li>
 * </ul>
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DynamicArchiveChannelHandler extends AbstractChannelHandler {

    private Instant startTime;
    private Instant endTime;
    private boolean optimised = true;

    /**
     * Constructs a new dynamic archive channel handler.
     *
     * @param fullChannelName the full unique channel name
     * @param strippedChannelName stripped channel name (pv name that archiver understands)
     * @param binCount number of bins for optimised data retrieval
     * @param optimised true if optimised algorithm should be used to fetch data or false for raw data
     * @param sources the sources to use
     */
    protected DynamicArchiveChannelHandler(String fullChannelName, String strippedChannelName, int binCount,
        boolean optimised, ArchiveSource... sources) {
        super(fullChannelName, strippedChannelName, binCount, sources);
        this.optimised = optimised;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.MultiplexedChannelHandler#connect()
     */
    @Override
    protected void connect() {
        processConnection(Boolean.TRUE);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.MultiplexedChannelHandler#write(java.lang.Object,
     * org.diirt.datasource.ChannelWriteCallback)
     */
    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        // values can be written as
        // time
        // startTime, endTime
        // startTime, endTime, optimised
        // Time, startTime, endTime can be either Long, Date, Timestamp or Time
        try {
            boolean fetchData = startTime != null && endTime != null;
            if (newValue instanceof Boolean) {
                boolean opt = (Boolean) newValue;
                if (optimised == opt) {
                    fetchData = false;
                }
                optimised = opt;
            } else if (newValue instanceof boolean[]) {
                boolean opt = ((boolean[]) newValue)[0];
                if (optimised == opt) {
                    fetchData = false;
                }
                optimised = opt;
            } else if (newValue instanceof Long) {
                startTime = Instant.ofEpochMilli((Long) newValue);
                endTime = startTime;
            } else if (newValue instanceof long[]) {
                startTime = Instant.ofEpochMilli(((long[]) newValue)[0]);
                endTime = Instant.ofEpochMilli(((long[]) newValue)[1]);
            } else if (newValue instanceof Long[]) {
                startTime = Instant.ofEpochMilli(((Long[]) newValue)[0]);
                endTime = Instant.ofEpochMilli(((Long[]) newValue)[1]);
            } else if (newValue instanceof Date) {
                startTime = ((Date) newValue).toInstant();
                endTime = startTime;
            } else if (newValue instanceof Date[]) {
                startTime = ((Date[]) newValue)[0].toInstant();
                endTime = ((Date[]) newValue)[1].toInstant();
            } else if (newValue instanceof Instant) {
                startTime = (Instant) newValue;
                endTime = startTime;
            } else if (newValue instanceof Instant[]) {
            	Instant[] t = (Instant[]) newValue;
                if (t.length == 0) {
                    throw new IllegalArgumentException("Write value not provided.");
                } else if (t.length == 1) {
                    startTime = t[0];
                    endTime = t[0];
                } else if (t.length > 1) {
                    startTime = t[0];
                    endTime = t[1];
                }
            } else if (newValue instanceof Time) {
                startTime = ((Time) newValue).getTimestamp();
                endTime = startTime;
            } else if (newValue instanceof Time[]) {
                Time[] t = (Time[]) newValue;
                if (t.length == 0) {
                    throw new IllegalArgumentException("Write value not provided.");
                } else if (t.length == 1) {
                    startTime = t[0].getTimestamp();
                    endTime = t[0].getTimestamp();
                } else if (t.length > 1) {
                    startTime = t[0].getTimestamp();
                    endTime = t[1].getTimestamp();
                }
            } else if (newValue instanceof Object[] && ((Object[]) newValue).length == 3) {
                Object[] v = (Object[]) newValue;
                if (v[0] instanceof Instant && v[1] instanceof Instant && v[2] instanceof Boolean) {
                    startTime = (Instant) v[0];
                    endTime = (Instant) v[1];
                    optimised = (Boolean) v[2];
                } else if (v[0] instanceof Long && v[1] instanceof Long && v[2] instanceof Boolean) {
                    startTime = Instant.ofEpochMilli((Long) v[0]);
                    endTime = Instant.ofEpochMilli((Long) v[1]);
                    optimised = (Boolean) v[2];
                } else if (v[0] instanceof Date && v[1] instanceof Date && v[2] instanceof Boolean) {
                    startTime = ((Date) v[0]).toInstant();
                    endTime = ((Date) v[1]).toInstant();
                    optimised = (Boolean) v[2];
                } else if (v[0] instanceof Time && v[1] instanceof Time && v[2] instanceof Boolean) {
                    startTime = ((Time) v[0]).getTimestamp();
                    endTime = ((Time) v[1]).getTimestamp();
                    optimised = (Boolean) v[2];
                } else {
                    throw new IllegalArgumentException("Write value '" + newValue
                        + "' is not supported. Should be {Timestamp startTime, Timestamp endTime, boolean optimised}.");
                }
            } else {
                throw new IllegalArgumentException("Write value '" + newValue + "' is not supported.");
            }
            if (fetchData) {
                fetchData(startTime, endTime, optimised);
            }
            callback.channelWritten(null);
        } catch (Exception e) {
            callback.channelWritten(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.MultiplexedChannelHandler#isWriteConnected(java.lang.Object)
     */
    @Override
    protected boolean isWriteConnected(Boolean payload) {
        return true;
    }
}
