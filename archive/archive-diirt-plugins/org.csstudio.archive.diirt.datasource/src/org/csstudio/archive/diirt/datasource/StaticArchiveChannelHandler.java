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

import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.util.time.Timestamp;

/**
 *
 * <code>StaticArchiveChannelHandler</code> represents a channel bound to a specific pv for a specific time range or
 * timestamp. Once created the time cannot be changed.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StaticArchiveChannelHandler extends AbstractChannelHandler {

    private final Timestamp startTime;
    private final Timestamp endTime;
    private final boolean optimised;

    /**
     * Constructs a new static channel handler which provides a single value fetched from the archive/.
     *
     * @param fullChannelName the full unique name of the channel
     * @param strippedName stripped channel name (the pv name that archiver understands)
     * @param timestamp the timestamp for which the value will be fetched
     * @param binCount number of bins for optimised data retrieval
     * @param optimised true to fetch data using optimised algorithm or false for raw values
     * @param sources the sources to use
     */
    public StaticArchiveChannelHandler(String fullChannelName, String strippedName, Timestamp timestamp, int binCount,
        boolean optimised, ArchiveSource... sources) {
        this(fullChannelName, strippedName, timestamp, timestamp, binCount, optimised, sources);

    }

    /**
     * Constructs a static archive channel handler which provides data for a fixed time range.
     *
     * @param fullChannelName the full unique channel name
     * @param strippedName stripped channel name (the pv name that archiver understands)
     * @param startTime the start time of the time window for which to fetch the data
     * @param endTime the end time of the window for which to fetch the data
     * @param binCount number of bins for optimised data retrieval
     * @param optimised true to fetch data using optimised algorithm or false for raw values
     * @param sources the archive sources to use
     */
    public StaticArchiveChannelHandler(String fullChannelName, String strippedName, Timestamp startTime,
        Timestamp endTime, int binCount, boolean optimised, ArchiveSource... sources) {
        super(fullChannelName, strippedName, binCount, sources);
        this.startTime = startTime;
        this.endTime = endTime == null || endTime.equals(startTime) ? startTime : endTime;
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
        try {
            fetchData(startTime, endTime, optimised);
        } catch (Exception e) {
            //FIXME ideally this should be some common diirt exception type
            throw new UnsupportedOperationException("Cannot load archive data for channel " + getChannelName(), e);
            //LOGGER.log(Level.WARNING, "Cannot load archive data for channel " + getChannelName(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.MultiplexedChannelHandler#write(java.lang.Object,
     * org.diirt.datasource.ChannelWriteCallback)
     */
    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        throw new UnsupportedOperationException("Archive channel '" + getChannelName() + "' is read only.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.MultiplexedChannelHandler#isWriteConnected(java.lang.Object)
     */
    @Override
    protected boolean isWriteConnected(Boolean payload) {
        return false;
    }
}
