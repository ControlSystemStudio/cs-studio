package org.csstudio.archive.diirt.datasource;

import java.util.Date;

import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Time;

/**
 *
 * <code>DynamicArchiveChannelHandler</code> is a channel handler that is bound to a specific pv. The time window for
 * the channel can be changed through the write method.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DynamicArchiveChannelHandler extends AbstractChannelHandler {

    private Timestamp startTime;
    private Timestamp endTime;
    private boolean optimised = true;

    /**
     * Constructs a new dynamic archive channel handler.
     *
     * @param fullChannelName the full unique channel name
     * @param strippedChannelName stripped channel name (pv name that archiver understands)
     * @param binCount number of bins for optimised data retrieval
     * @param sources the sources to use
     */
    protected DynamicArchiveChannelHandler(String fullChannelName, String strippedChannelName, int binCount,
        ArchiveSource... sources) {
        super(fullChannelName, strippedChannelName, binCount, sources);
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
        //values can be written as
        //time
        //startTime, endTime
        //startTime, endTime, optimised
        //Time, startTime, endTime can be either Long, Date, Timestamp or Time
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
                startTime = Timestamp.of(new Date((Long)newValue));
                endTime = startTime;
            } else if (newValue instanceof Long[]) {
                startTime = Timestamp.of(new Date(((Long[])newValue)[0]));
                endTime = Timestamp.of(new Date(((Long[])newValue)[1]));
            } else if (newValue instanceof Date) {
                startTime = Timestamp.of((Date)newValue);
                endTime = startTime;
            } else if (newValue instanceof Date[]) {
                startTime = Timestamp.of(((Date[])newValue)[0]);
                endTime = Timestamp.of(((Date[])newValue)[1]);
            } else if (newValue instanceof Timestamp) {
                startTime = (Timestamp) newValue;
                endTime = startTime;
            } else if (newValue instanceof Timestamp[]) {
                Timestamp[] t = (Timestamp[]) newValue;
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
                if (v[0] instanceof Timestamp && v[1] instanceof Timestamp && v[2] instanceof Boolean) {
                    startTime = (Timestamp) v[0];
                    endTime = (Timestamp) v[1];
                    optimised = (Boolean) v[2];
                } else if (v[0] instanceof Long && v[2] instanceof Long && v[2] instanceof Boolean) {
                    startTime = Timestamp.of(new Date((Long) v[0]));
                    endTime = Timestamp.of(new Date((Long) v[1]));
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
