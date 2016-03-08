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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.DataSource;
import org.diirt.util.time.Timestamp;

/**
 *
 * <code>ArchiveDataSource</code> provides DIIRT archive channels. Those channels fetch the data from the archive server
 * using the CS-Studio archive reader interface. The channel name is provided as an URL with parameters. The possible
 * parameters are:
 * <ul>
 * <li>url - the URL to the archive server to use; the value can be given as a name of the archive server configured in
 * DIIRT, or as a URL to the server (does not need to be configured in DIIRT). If the URL contains a <code>&#38</code>
 * character wrap the value into quotes <code>&#34</code></li>
 * <li>url[] - same as above</li>
 * <li>startTime - the start time of the time window for which to fetch the data</li>
 * <li>endTime - the end time of the time window for which to fetch the data</li>
 * <li>time - the time for which to load a single value</li>
 * <li>optimised - a flag indicating if optimised algorithm should be used (true) or raw values should be fetched
 * (false)</li>
 * <li>optimized - same as above</li>
 * <li>points - number of points to load in case of optimised algorithm</li>
 * <li>binCount - same as above</li>
 * </ul>
 * All parameters are optional. Examples:
 * <ul>
 * <li>archive://pvName - loads the pvName from the archive sources configured in the DIIRT configuration files</li>
 * <li>archive://pvName?url=&#60archive_source_name&#62 - loads the data for pvName from the archive source defined by
 * the <code>archive_source_name</code>; the source must be defined in the DIIRT configuration, otherwise it defaults to
 * the previous option</li>
 * <li>archive://pvName?url=&#60archive_source_url&#62 - loads the pvName from the archive source defined by the
 * <code>archive_source_url</code>; the source does not need to be defined in the DIIRT configuration</li>
 * <li>archive://pvName?url[]=&#60archive_source1&#62&#38url[]=&#60archve_source2&#62... - loads the pvName from the
 * archive sources defined by the <code>archive_source&#42</code>; if the sources are given as names they have to be in
 * DIIRT configuration, if given as URLs they do not need to be defined in the DIIRT configuration</li>
 * </ul>
 * All of the above will create a channel, which can be written to. The data written to the channel are defined in
 * {@link DynamicArchiveChannelHandler}. The created channel will be multiplexed, so if it is requested by another
 * client and that client changes the parameters via write method, all clients will receive new values. If the retrieval
 * window is static or sharing of channels between clients is undesired, one can also use the following patterns:
 * <ul>
 * <li>archive://pvName?startTime=123&#38endTime=223&#38optimised=true - loads the data for the given pv for the time
 * window specified by the startTime and endTime parameters.</li>
 * <li>archive://pvName?time=123 - loads a single value pv at the given time</li>
 * <li>archive://pvName?time=123&#38url=&#60archive_source&#62 - loads a single value pv at the given time using only
 * the provided archive source.</li>
 * </ul>
 * In all above cases time is given in a format <code>yyyyMMdd-HH:mm:ss.S</code> with the milliseconds being an optional
 * part. The time can also be provided as UTC time in milliseconds (e.g. {@link System#currentTimeMillis()}) or in DIIRT
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
        String fullChannelName = channelName;
        String strippedChannelName = channelName;
        int idx = channelName.indexOf('?');
        int binCount = config.getBinCount();
        boolean optimised = true;
        if (idx < 0) {
            // no parameter
            return new DynamicArchiveChannelHandler(fullChannelName, strippedChannelName, binCount, optimised,
                config.getSources());
        } else {
            // parameters provided, find which ones
            strippedChannelName = fullChannelName.substring(0, idx);
            String parameters = fullChannelName.substring(idx + 1);
            List<ArchiveSource> sources = new ArrayList<>();
            Timestamp startTime = null;
            Timestamp endTime = null;
            String key, value;
            while (!parameters.isEmpty()) {
                idx = parameters.indexOf('"');
                if (idx > -1) {
                    // quoted parameters require special deal, because they might contain & characters
                    int idx2 = parameters.indexOf('"', idx + 1);
                    String param = parameters.substring(0, idx2);
                    if (idx2 + 2 < parameters.length()) {
                        parameters = parameters.substring(idx2 + 2);
                    } else {
                        parameters = "";
                    }
                    idx = param.indexOf('=');
                    key = param.substring(0, idx).toLowerCase(Locale.UK).trim();
                    value = param.substring(idx + 2).trim();
                } else {
                    idx = parameters.indexOf('&');
                    String param = null;
                    if (idx < 0) {
                        // last parameter
                        param = parameters;
                        parameters = "";
                    } else {
                        param = parameters.substring(0, idx);
                        parameters = parameters.substring(idx + 1);
                    }
                    idx = param.indexOf('=');
                    key = param.substring(0, idx).toLowerCase(Locale.UK).trim();
                    value = param.substring(idx + 1).trim();
                }
                if ("optimised".equals(key) || "optimized".equals(key)) {
                    optimised = Boolean.parseBoolean(value);
                } else if ("time".equals(key)) {
                    startTime = parseTime(value);
                    endTime = startTime;
                } else if ("starttime".equals(key)) {
                    startTime = parseTime(value);
                } else if ("stoptime".equals(key) || "endtime".equals(key)) {
                    endTime = parseTime(value);
                } else if ("points".equals(key) || "bincount".equals(key)) {
                    binCount = Integer.parseInt(value);
                } else if ("url".equals(key) || "url[]".equals(key)) {
                    ArchiveSource as = config.getSource(value);
                    if (as == null && value.contains("://")) {
                        as = new ArchiveSource(value, null, value);
                    }
                    if (as != null) {
                        sources.add(as);
                    }
                }
            }
            ArchiveSource[] as = sources.isEmpty() ? config.getSources()
                : sources.toArray(new ArchiveSource[sources.size()]);
            if (startTime == null && endTime == null) {
                // no time provided, it is a dynamic channel
                return new DynamicArchiveChannelHandler(fullChannelName, strippedChannelName, binCount, optimised, as);
            } else if (startTime == null) {
                // if start or end time is missing, it is a single value call
                startTime = endTime;
            } else if (endTime == null) {
                endTime = startTime;
            }
            return new StaticArchiveChannelHandler(fullChannelName, strippedChannelName, startTime, endTime, binCount,
                optimised, as);

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
                //maybe it is a human readable timestamp with milliseconds
                return Timestamp.of(new SimpleDateFormat(TIME_FORMAT_MILLIS).parse(time));
            }
            //no dot can mean it is a human readable timestamp without milliseconds
            return Timestamp.of(new SimpleDateFormat(TIME_FORMAT).parse(time));

        } catch (ParseException e) {
            String message = "Invalid time format: " + time + ". Use " + TIME_FORMAT + " or " + TIME_FORMAT_MILLIS
                + ".";
            LOGGER.log(Level.FINEST, message, e);
            throw new IllegalArgumentException(message, e);
        }
    }
}
