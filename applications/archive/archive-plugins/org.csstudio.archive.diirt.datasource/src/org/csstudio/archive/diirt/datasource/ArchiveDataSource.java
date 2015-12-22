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
 * using the CS-Studio archive reader interface. The channel can be created as following:
 * <ul>
 * <li>archive://\<archive_source_name\>/pvName - loads the pvName from the archive source defined by the name; the
 * source needs to exist in the diirt configuration</li>
 * <li>archive://\<archive_source_url\>/pvName - loads the pvName from the archive source defined by the url; the source
 * does not need to be defined in the diirt configuration</li>
 * <li>archive://pvName - loads the pvName from all archive sources defined in diirt configuration</li>
 * </ul>
 * All of the above will create a channel, which can be written to. The data written to the channel are defined in
 * {@link DynamicArchiveChannelHandler}. If the times are static, one can also use the following patterns:
 * <ul>
 * <li>archive://\<archive_source\>/pvName[startTime;endTime;optimised] - startTime and endTime are obligatory
 * parameters and define the time window for which the data are loaded. The optimised parameter can be <code>true</code>
 * or <code>false</code> and defines whether data are fetched using the optimised retrieval or raw data are fetched.
 * </li>
 * <li>archive://\<archive_source\>/pvName?startTime=123&endTime=123&optimised=true - yields the same results as the
 * option above.</li>
 * <li>archive://\<archive_source\>/pvName[startTime] - fetches a single value at the given time</li>
 * <li>archive://\<archive_source\>/pvName?startTime=123 - fetches a single value at the given time 123</li>
 * <li>archive://\<archive_source\>/pvName?time=123 - fetches a single value at the given time 123</li>
 * </ul>
 * In all above cases time is given in a format <code>yyyyMMdd-HH:mm:ss.S</code> with the milliseconds being an optional
 * part. The time can also be provided as UTC time in milliseconds (e.g. {@link System#currentTimeMillis()}) or in diirt
 * format <code>UTCSeconds.Nanos</code>.
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
        int timeIdx = channelName.indexOf('[');
        if (timeIdx < 0) {
            // timestamp not provided in brackets
            timeIdx = channelName.indexOf('?');
            if (timeIdx < 0) {
                // timestamp not provided at all, it should be settable
                int sidx = channelName.lastIndexOf('/');
                String channel = channelName;
                if (sidx > 0) {
                    // source is provided
                    String source = channelName.substring(0, sidx);
                    channel = channelName.substring(sidx + 1);
                    ArchiveSource as = config.getSource(source);
                    if (as != null) {
                        return new DynamicArchiveChannelHandler(channelName, channel, config.getBinCount(), as);
                    } else if (source.contains("://")) {
                        // source is given as an url
                        as = new ArchiveSource(source, "1", source);
                        return new DynamicArchiveChannelHandler(channelName, channel, config.getBinCount(), as);
                    }
                }
                return new DynamicArchiveChannelHandler(channelName, channel, config.getBinCount(),
                    config.getSources());
            } else {
                String[] params = channelName.substring(timeIdx + 1).split("\\&");
                Timestamp start = null;
                Timestamp end = null;
                boolean optimised = true;
                for (String s : params) {
                    if (s.indexOf("startTime") > -1) {
                        start = parseTime(s.substring(s.indexOf('=') + 1));
                    } else if (s.indexOf("endTime") > -1) {
                        end = parseTime(s.substring(s.indexOf('=') + 1));
                    } else if (s.indexOf("stopTime") > -1) {
                        end = parseTime(s.substring(s.indexOf('=') + 1));
                    } else if (s.indexOf("optimised") > -1 || s.indexOf("optimized") > -1) {
                        optimised = Boolean.parseBoolean(s.substring(s.indexOf('=') + 1));
                    } else if (s.indexOf("time") > -1) {
                        start = parseTime(s.substring(s.indexOf('=') + 1));
                        end = start;
                    }
                }
                if (end == null) {
                    end = start;
                }
                return getChannelWithTime(channelName, timeIdx, optimised, start, end);
            }
        } else {
            // timestamp provided in brackets
            String time = channelName.substring(timeIdx + 1, channelName.indexOf(']', timeIdx));
            Timestamp start = null;
            Timestamp end = null;
            boolean optimised = true;
            if (time.indexOf(';') > 0) {
                // time range
                String[] times = time.split(";");
                start = parseTime(times[0]);
                end = times.length > 1 ? parseTime(times[1]) : start;
                if (times.length == 3) {
                    optimised = Boolean.parseBoolean(times[2]);
                }
            } else {
                // single timestamp
                start = parseTime(time);
            }
            return getChannelWithTime(channelName, timeIdx, optimised, start, end);
        }
    }

    private ChannelHandler getChannelWithTime(String channelName, int timeIdx, boolean optimised, Timestamp start,
        Timestamp end) {
        int sidx = channelName.lastIndexOf('/');
        String channel = channelName.substring(0, timeIdx);
        if (sidx > -1) {
            // source is provided
            String source = channelName.substring(0, sidx);
            channel = channelName.substring(sidx + 1, timeIdx);
            ArchiveSource as = config.getSource(source);
            if (as != null) {
                return new StaticArchiveChannelHandler(channelName, channel, start, end, config.getBinCount(),
                    optimised, as);
            } else if (source.contains("://")) {
                // source is given as an url
                as = new ArchiveSource(source, "1", source);
                return new StaticArchiveChannelHandler(channelName, channel, start, end, config.getBinCount(),
                    optimised, as);
            }
        }
        return new StaticArchiveChannelHandler(channelName, channel, start, end, config.getBinCount(), optimised,
            config.getSources());
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
