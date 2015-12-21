package org.csstudio.archive.diirt.datasource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.DataSource;
import org.diirt.util.time.Timestamp;

/**
 *
 * <code>ArchiveDataSource</code> provides diirt archive channels. Those channels fetch the data from the archive server
 * using the CS-Studio archive reader interface.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ArchiveDataSource extends DataSource {

    private static final Logger LOGGER = Logger.getLogger(ArchiveDataSource.class.getName());
    private final ArchiveDataSourceConfiguration config;

    // two possible formats to specify the start and end time for the data retrieval
    private static final String TIME_FORMAT_MILLIS = "yyyyMMdd-HH:mm:ss.S";
    private static final String TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    /**
     * Construct a new data source.
     *
     * @param config the configuration for the datasource (provides the archive sources)
     */
    public ArchiveDataSource(ArchiveDataSourceConfiguration config) {
        super(false);
        this.config = config;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSource#createChannel(java.lang.String)
     */
    @Override
    protected ChannelHandler createChannel(String channelName) {
        //channel can be created as:
        //archive://pvName
        //archive://pvName[time]
        //archive://pvName[startTime;endTime]
        //archive://pvName[startTime;endTime;optimised], where optimised is true or false
        //archive://<archiver>/pvName...
        int timeIdx = channelName.indexOf('[');
        if (timeIdx < 0) {
            // timestamp not provided, it should be settable
            int sidx = channelName.indexOf('<');
            if (sidx > -1) {
                int sidx2 = channelName.indexOf('>');
                if (sidx2 > sidx) {
                    String source = channelName.substring(sidx + 1, sidx2);
                    ArchiveSource as = config.getSource(source);
                    if (as != null) {
                        return new DynamicArchiveChannelHandler(channelName, channelName.substring(sidx2+2),
                            config.getBinCount(), as);
                    }
                }
            }
            return new DynamicArchiveChannelHandler(channelName, channelName, config.getBinCount(),
                config.getSources());
        } else {
            String time = channelName.substring(timeIdx + 1, channelName.indexOf(']', timeIdx));
            Timestamp start = null;
            Timestamp end = null;
            boolean optimised = true;
            if (time.indexOf(';') > 0) {
                // time range
                String[] times = time.split(";");
                start = parseTime(times[0]);
                end = parseTime(times[1]);
                if (times.length == 3) {
                    optimised = Boolean.parseBoolean(times[2]);
                }
            } else {
                // single timestamp
                start = parseTime(time);
            }
            int sidx = channelName.indexOf('<');
            if (sidx > -1) {
                int sidx2 = channelName.indexOf('>');
                if (sidx2 > sidx) {
                    String source = channelName.substring(sidx + 1, sidx2);
                    ArchiveSource as = config.getSource(source);
                    if (as != null) {
                        return new StaticArchiveChannelHandler(channelName, channelName.substring(sidx2+2, timeIdx),
                            start, end, config.getBinCount(), optimised, as);
                    }
                }
            }
            return new StaticArchiveChannelHandler(channelName, channelName.substring(0, timeIdx), start, end,
                config.getBinCount(), optimised, config.getSources());
        }
    }

    /**
     * Check the format of the time and parse it. The time can be given in UTC format as a long number, it can be given
     * as a {@link Timestamp#toString()}, which is seconds.nanoseconds or it can be given as a date in format
     * yyyyMMdd-HH:mm:ss(.S). The date format can include the milliseconds or not.
     *
     * @param time the string to parse into time
     * @return the timestamp
     * @throws IllegalArgumentException if the time cannot be parsed (wrong format)
     */
    private static Timestamp parseTime(String time) throws IllegalArgumentException {
        try {
            try {
                // check if it is a utc number in milliseconds
                long timestamp = Long.parseLong(time);
                return Timestamp.of(new Date(timestamp));
            } catch (NumberFormatException e) {
                // ignore
            }
            int idx = time.indexOf('.');
            if (idx > 0) {
                try {
                    // perhaps it is Timestamp.toString
                    long seconds = Long.parseLong(time.substring(0, idx));
                    int nano = Integer.parseInt(time.substring(idx + 1));
                    return Timestamp.of(seconds, nano);
                } catch (NumberFormatException e) {
                    // ignore
                }
                return Timestamp.of(new SimpleDateFormat(TIME_FORMAT_MILLIS).parse(time));
            }
            return Timestamp.of(new SimpleDateFormat(TIME_FORMAT).parse(time));

        } catch (ParseException e) {
            String message = "Invalid time format: " + time + ". Use " + TIME_FORMAT + " or " + TIME_FORMAT_MILLIS
                + ".";
            LOGGER.log(Level.FINEST, message, e);
            throw new IllegalArgumentException(message, e);
        }
    }
}
