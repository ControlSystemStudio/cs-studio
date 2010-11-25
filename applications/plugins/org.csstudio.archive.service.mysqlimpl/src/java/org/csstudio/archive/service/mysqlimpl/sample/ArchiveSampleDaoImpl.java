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
import org.csstudio.archive.service.mysqlimpl.severity.ArchiveSeverityDaoException;
import org.csstudio.archive.service.mysqlimpl.status.ArchiveStatusDaoException;
import org.csstudio.archive.service.sample.IArchiveSample;
import org.csstudio.archive.service.severity.ArchiveSeverityId;
import org.csstudio.archive.service.status.ArchiveStatusId;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;
import org.joda.time.Duration;
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
 * @param <T>
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
    public TimeInstant retrieveLatestSampleByChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveSampleDaoException {

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
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);
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
    public void createSamples(@Nonnull final Collection<IArchiveSample<?>> samples) throws ArchiveSampleDaoException {

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
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);

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
    private PreparedStatement composeStatements(@Nonnull final Collection<IArchiveSample<?>> samples) throws ArchiveSampleDaoException, ArchiveConnectionException, SQLException {

        final List<String> values = Lists.newArrayList();
        final List<String> valuesPerMinute = Lists.newArrayList();
        final List<String> valuesPerHour = Lists.newArrayList();

        try {
            for (final IArchiveSample<?> sample : samples) {

                final ArchiveChannelId channelId = sample.getChannelId();
                final EpicsAlarm alarm = sample.getAlarm(); // how to cope with alarms that don't have severities and status
                final TimeInstant timestamp = sample.getTimestamp();

                final ArchiveSeverityId sevId =
                    DAO_MGR.getSeverityDao().retrieveSeverityId(alarm.getSeverity());
                final ArchiveStatusId statusId = DAO_MGR.getStatusDao().retrieveStatusId(alarm.getStatus());

                if (sevId == null || statusId == null) {
                    LOG.warn("Ids could not be retrieved for severity "
                             + alarm.getSeverity().name() + " and/or status " + alarm.getStatus().name() +
                    ". Severity and/or status table corrupted?");
                    // FIXME (bknerr) : what to do with the sample? archive or not?
                } else {
                    // the VALUES (...) component for the standard sample table
                    values.add(createSampleValueStmtStr(channelId, sevId, statusId, sample.getValue(), timestamp));


                    final Map<ArchiveChannelId, SampleAggregator> minutesMap = _reducedDataMapForMinutes.get();

                    if (isWriteReducedDataDue(sample.getValue(), timestamp, Minutes.ONE.toStandardDuration(), minutesMap.get(channelId))) {
                        final String sampleMValue =
                            createReducedSampleValueStmtStr(channelId, alarm, sevId, sample.getValue(), timestamp, minutesMap);
                        valuesPerMinute.add(sampleMValue);

//                        final Double lastAvg = minutesMap.get(channelId).getAvg();
//
//                        final Map<ArchiveChannelId, SampleAggregator> hoursMap = _reducedDataMapForHours.get();

//                        if (isWriteReducedDataDue(lastAvg, timestamp, Hours.ONE.toStandardDuration(), hoursMap.get(channelId))) {
//                            final String sampleHValue =
//                                createReducedSampleValueStmtStr(channelId, alarm, sevId, sample.getValue(), timestamp, hoursMap);
//                            valuesPerHour.add(sampleHValue);
//                        }
//                        // aggregate for hours
//                        hoursMap.get(channelId).aggregateNewVal((Double) sample.getValue(), alarm);
                    }

                    // aggregate for minutes
                    if (sample.getValue() instanceof Double) {
                        minutesMap.get(channelId).aggregateNewVal(sample.getValue(), alarm);
                    } else if (sample.getValue() instanceof Integer) {
                        minutesMap.get(channelId).aggregateNewVal(Double.valueOf((Integer) sample.getValue()), alarm);
                    }
                }

            }
        } catch (final ArchiveStatusDaoException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);
        } catch (final ArchiveSeverityDaoException e) {
            throw new ArchiveSampleDaoException(RETRIEVAL_FAILED, e);
        }

        return joinStringsToStatementBatch(values, valuesPerMinute, valuesPerHour);
    }


    /**
     * @param <T>
     * @param channelId
     * @param object
     * @param timestamp
     * @param one
     * @return
     */
    private <T> boolean isWriteReducedDataDue(final T object,
                                         final TimeInstant timestamp,
                                         final Duration duration,
                                         final SampleAggregator agg) {

        if (!(object instanceof Double) && !(object instanceof Integer)) { // TODO (bknerr) : of course not only Double and not with instanceof
            return false;
        }

        if (agg == null) { // not yet present, write
            return true;
        }
        final TimeInstant lastWriteTime = agg.getLastWriteTime();
        final TimeInstant threshold = lastWriteTime.plus(duration.getMillis());
        if (timestamp.isAfter(threshold)) {
            return false; // not due, don't write
        }
        if (agg.getLastVal().compareTo((Double)object) == 0) {
            return false; // didn't change don't write
        }
        return true;
    }


    @CheckForNull
    private PreparedStatement joinStringsToStatementBatch(final List<String> values,
                                                          final List<String> valuesPerMinute,
                                                          final List<String> valuesPerHour)
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
     *
     * @param <T>
     * @param channelId
     * @param sevId
     * @param statusId
     * @param value
     * @param timestamp
     * @return
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
                         timestamp.getFractalSecondInNanos() +
                   ")";
        }





    /**
     * @param <T>
     * @param channelId
     * @param sevId
     * @param value
     * @param timestamp
     * @return
     */
    private <V, A> String createReducedSampleValueStmtStr(final ArchiveChannelId channelId,
                                                          final EpicsAlarm alarm,
                                                          final ArchiveSeverityId sevId,
                                                          final V value,
                                                          final TimeInstant timestamp,
                                                          final Map<ArchiveChannelId, SampleAggregator> map) {


        SampleAggregator storedStuff = map.get(channelId);
        if (storedStuff == null) {
            storedStuff = new SampleAggregator<V>(value, alarm, timestamp);
            map.put(channelId, storedStuff);
        }
        // write for all samples_x (channel_id, smpl_time, highest_sev_id, avg_val, min_val, max_val)
        final String valueStr =
            "(" + channelId.intValue() + ", '" +
                  SAMPLE_TIME_FMT.print(timestamp.getInstant()) + "', " +
                  sevId.intValue() + ", " +
                  storedStuff.getAvg() + " ," +
                  storedStuff.getMin() + " ," +
                  storedStuff.getMax() + ")";

        storedStuff.reset(timestamp);

        return valueStr;
    }

}
