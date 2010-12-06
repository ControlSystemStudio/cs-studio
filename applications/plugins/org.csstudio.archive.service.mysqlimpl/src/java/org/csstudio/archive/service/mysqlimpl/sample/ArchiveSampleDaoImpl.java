/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.service.mysqlimpl.sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.mysqlimpl.MySQLArchiveServicePreference;
import org.csstudio.archive.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.archive.service.severity.ArchiveSeverityId;
import org.csstudio.archive.service.status.ArchiveStatusId;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.ConversionTypeSupportException;
import org.csstudio.domain.desy.types.ICssValueType;
import org.csstudio.domain.desy.types.TypeSupport;
import org.csstudio.platform.logging.CentralLogger;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Archive sample dao implementation.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public class ArchiveSampleDaoImpl extends AbstractArchiveDao implements IArchiveSampleDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveSampleDaoImpl.class);

    private static final String RETRIEVAL_FAILED = "Channel configuration retrieval from archive failed.";

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectLastSmplTimeByChannelIdStmt =
        "SELECT MAX(smpl_time) FROM archive.sample WHERE channel_id=?";
    private final String _insertSamplesStmt =
        "INSERT INTO archive.sample (channel_id, smpl_time, severity_id, status_id, float_val, nanosecs) VALUES ";
    private final String _insertSamplesPerMinuteStmt =
        "INSERT INTO archive.sample_m (channel_id, smpl_time, highest_sev_id, avg_val, min_val, max_val) VALUES ";
    private final String _insertSamplesPerHourStmt =
        "INSERT INTO archive.sample_h (channel_id, smpl_time, highest_sev_id, avg_val, min_val, max_val) VALUES ";


    // TODO (bknerr) : move this to a place where we collect the DESY archive standard time stamp format
    private static final DateTimeFormatter SAMPLE_TIME_FMT = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * the reduced data, I'd love to use gabriele's aggregators, but they are his alarms, and times.
     */
    private final ThreadLocal<Map<ArchiveChannelId, SampleAggregator>> _reducedDataMapForMinutes =
        new ThreadLocal<Map<ArchiveChannelId, SampleAggregator>>();
    private final ThreadLocal<Map<ArchiveChannelId, SampleAggregator>> _reducedDataMapForHours =
        new ThreadLocal<Map<ArchiveChannelId, SampleAggregator>>();

    /**
     * Constructor.
     */
    public ArchiveSampleDaoImpl() {
        final Map<ArchiveChannelId, SampleAggregator> minutesMap = Maps.newHashMap();
        _reducedDataMapForMinutes.set(minutesMap);
        final Map<ArchiveChannelId, SampleAggregator> hoursMap = Maps.newHashMap();
        _reducedDataMapForHours.set(hoursMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public TimeInstant retrieveLatestSampleByChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException {

        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectLastSmplTimeByChannelIdStmt);
            stmt.setInt(1, id.intValue());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {

                final Timestamp ltstSmplTime = result.getTimestamp(1);
                return TimeInstantBuilder.buildFromMillis(ltstSmplTime.getTime());
            }

        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectLastSmplTimeByChannelIdStmt + " failed.");
                }
            }
        }
        return null;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public <V, T extends ICssValueType<V> & IHasAlarm>
        void createSamples(@Nonnull final Collection<IArchiveSample<V, T, EpicsAlarm>> samples) throws ArchiveDaoException {

        // Build complete and reduced set statements
        PreparedStatement stmt = null;

        // TODO (bknerr) : LOG warning if single statement size is too large.
        // and when com.mysql.jdbc.PacketTooBigException:
        // Packet for query is too large (45804672 > 1048576).
        // You can change this value on the server by setting the max_allowed_packet' variable.
        try {
            stmt = composeStatements(samples); // batches three statements for different tables: samples, samples_m, samples_h

            stmt.setQueryTimeout(MySQLArchiveServicePreference.SQL_TIMEOUT.getValue());
            stmt.executeBatch();

            getConnection().commit();

        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);

        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectLastSmplTimeByChannelIdStmt + "... failed.");
                }
            }
        }
    }

    @CheckForNull
    private <V, T extends ICssValueType<V> & IHasAlarm>
        PreparedStatement composeStatements(@Nonnull final Collection<IArchiveSample<V, T, EpicsAlarm>> samples) throws ArchiveDaoException, ArchiveConnectionException, SQLException {

        final List<String> values = Lists.newArrayList();
        final List<String> valuesPerMinute = Lists.newArrayList();
        final List<String> valuesPerHour = Lists.newArrayList();

        for (final IArchiveSample<V, T, EpicsAlarm> sample : samples) {

            final ArchiveChannelId channelId = sample.getChannelId();
            final EpicsAlarm alarm = sample.getAlarm(); // how to cope with alarms that don't have severities and status?
            final TimeInstant timestamp = sample.getTimestamp();

            final ArchiveSeverityId sevId =
                DAO_MGR.getSeverityDao().retrieveSeverityId(alarm.getSeverity());
            final ArchiveStatusId statusId = DAO_MGR.getStatusDao().retrieveStatusId(alarm.getStatus());

            if (sevId == null || statusId == null) {
                LOG.warn("Ids could not be retrieved for severity " +
                         alarm.getSeverity().name() + " and/or status " + alarm.getStatus().name() +
                ". Severity and/or status table corrupted?");
                // FIXME (bknerr) : what to do with the sample? archive/log/backup?
            } else {
                // the VALUES (...) component for the standard sample table
                final T data = sample.getData();
                values.add(createSampleValueStmtStr(channelId, sevId, statusId, data, timestamp));

                writeReducedData(channelId,
                                 data,
                                 alarm,
                                 timestamp,
                                 valuesPerMinute,
                                 valuesPerHour);
            }
        }
        return joinStringsToStatementBatch(values, valuesPerMinute, valuesPerHour);
    }


    private <T extends ICssValueType<?>>
        void writeReducedData(@Nonnull final ArchiveChannelId channelId,
                              @Nonnull final T data,
                              @Nonnull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp,
                              @Nonnull final List<String> valuesPerMinute,
                              @Nonnull final List<String> valuesPerHour) throws ArchiveDaoException {

        final Double newValue = isDataConvertibleToDouble(data);
        if (newValue == null) {
            return; // not convertible, no data reduction possible
        }

        final String minuteValue = checkAndCreateValueStatement(_reducedDataMapForMinutes.get(),
                                                                channelId,
                                                                newValue,
                                                                alarm,
                                                                timestamp,
                                                                Minutes.ONE.toStandardDuration());

        if (minuteValue != null) {
            valuesPerMinute.add(minuteValue); // add to write VALUES() list for minutes
            final SampleAggregator minuteAgg = _reducedDataMapForMinutes.get().get(channelId);

            final String hoursValue = checkAndCreateValueStatement(_reducedDataMapForHours.get(),
                                                                   channelId,
                                                                   minuteAgg.getAvg(),
                                                                   minuteAgg.getHighestAlarm(),
                                                                   minuteAgg.getLastWriteTime(),
                                                                   Hours.ONE.toStandardDuration());
            minuteAgg.reset(timestamp); // and reset this aggregator

            if (hoursValue != null) {
                valuesPerHour.add(hoursValue); // add to the write VALUES() list for hours
                final SampleAggregator hoursAgg = _reducedDataMapForHours.get().get(channelId);

                // for days would be here...

                hoursAgg.reset(timestamp); // and reset this aggregator
            }
        }

    }

    @CheckForNull
    private <T extends ICssValueType<?>>
        String checkAndCreateValueStatement(final Map<ArchiveChannelId, SampleAggregator> map,
                                            final ArchiveChannelId channelId,
                                            final Double newValue, // redundant
                                            final EpicsAlarm alarm,
                                            final TimeInstant timestamp,
                                            final Duration interval) throws ArchiveDaoException {

        SampleAggregator agg =  map.get(channelId);
        if (agg == null) {
            // not yet an aggregator present, seems to be the first sample, aggregate and return
            agg = new SampleAggregator(newValue, alarm, timestamp);
            map.put(channelId, agg);
            return null;
        }

        // aggregate
        agg.aggregateNewVal(newValue, alarm);

        // aggregator did already exist, check whether the next write is due and whether the data changed
        if (!isReducedDataWriteDueAndHasChanged(newValue, timestamp, interval, agg)) {
            return null;
        }

        // write the reduced data for minutes
        return createReducedSampleValueString(channelId, timestamp, agg);
    }


    private <T extends ICssValueType<?>> Double isDataConvertibleToDouble(@Nonnull final T data) {
        try {
            return TypeSupport.toDouble(data);
        } catch (final ConversionTypeSupportException e) {
            return null; // is not convertible. Type support missing.
        }
    }

    private boolean isReducedDataWriteDueAndHasChanged(@Nonnull final Double val,
                                                       @Nonnull final TimeInstant timestamp,
                                                       @Nonnull final Duration duration,
                                                       @Nonnull final SampleAggregator agg) {

        final TimeInstant lastWriteTime = agg.getLastWriteTime();
        final TimeInstant dueTime = lastWriteTime.plusMillis(duration.getMillis());
        if (timestamp.isBefore(dueTime)) {
            return false; // not yet due, don't write
        }

        if (agg.getLastWrittenValue().compareTo(val) == 0) {
            return false;
        }
        return true;
    }


    @CheckForNull
    private PreparedStatement joinStringsToStatementBatch(@Nonnull final List<String> values,
                                                          @Nonnull final List<String> valuesPerMinute,
                                                          @Nonnull final List<String> valuesPerHour)
        throws SQLException, ArchiveConnectionException {

        PreparedStatement stmt = null;
        if (!values.isEmpty()) {
            stmt = getConnection().prepareStatement(Joiner.on(" ").join(_insertSamplesStmt, Joiner.on(", ").join(values)));
        }
        if (!valuesPerMinute.isEmpty()) {
            final String stmtStr = Joiner.on(" ").join(_insertSamplesPerMinuteStmt, Joiner.on(", ").join(valuesPerMinute));
            if (stmt == null) {
                stmt = getConnection().prepareStatement(stmtStr);
            } else {
                stmt.addBatch(stmtStr);
            }
        }
        if (!valuesPerHour.isEmpty()) {
            final String stmtStr = Joiner.on(" ").join(_insertSamplesPerHourStmt, Joiner.on(", ").join(valuesPerHour));
            if (stmt == null) {
                stmt = getConnection().prepareStatement(stmtStr);
            } else {
                stmt.addBatch(stmtStr);
            }
        }
        return stmt;
    }

    /**
     * The simple VALUES component for table sample:
     * "(channel_id, smpl_time, severity_id, status_id, float_val, nanosecs),"
     */
    private <T> String createSampleValueStmtStr(final ArchiveChannelId channelId,
                                                final ArchiveSeverityId sevId,
                                                final ArchiveStatusId statusId,
                                                final T value,
                                                final TimeInstant timestamp) {
            return "(" + channelId.intValue() + ", '" +
                         timestamp.formatted(SAMPLE_TIME_FMT) + "', " +
                         sevId.intValue() + ", " +
                         statusId.intValue() + ", '" +
                         value + "' ," + // toString() is called - should be overridden in any type BaseValueType
                         timestamp.getFractalMillisInNanos() +
                   ")";
        }


    /**
     * The averaged VALUES component for table sample_*:
     * "(channel_id, smpl_time, highest_sev_id, avg_val, min_val, max_val),"
     * @throws ArchiveSeverityDaoException
     */
    private String createReducedSampleValueString(@Nonnull final ArchiveChannelId channelId,
                                                  @Nonnull final TimeInstant timestamp,
                                                  @Nonnull final SampleAggregator agg) throws ArchiveDaoException {
        // write for all samples_x (channel_id, smpl_time, highest_sev_id, avg_val, min_val, max_val)
        final String valueStr =
            "(" + channelId.intValue() + ", '" +
                  SAMPLE_TIME_FMT.print(timestamp.getInstant()) + "', " +
                  DAO_MGR.getSeverityDao().retrieveSeverityId(agg.getHighestAlarm().getSeverity()) + ", " +
                  agg.getAvg() + " ," +
                  agg.getMin() + " ," +
                  agg.getMax() + ")";

        agg.reset(timestamp);

        return valueStr;
    }

}
