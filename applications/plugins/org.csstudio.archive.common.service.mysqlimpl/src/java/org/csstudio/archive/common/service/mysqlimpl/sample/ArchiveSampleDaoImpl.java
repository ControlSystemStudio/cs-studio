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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;
import org.joda.time.Duration;
import org.joda.time.Minutes;

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


    private static final String ARCH_TABLE_PLACEHOLDER = "<arch.table>";

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveSampleDaoImpl.class);

    private static final String RETRIEVAL_FAILED = "Sample retrieval from archive failed.";

    private final String _dbName = getDaoMgr().getDatabaseName();

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    private final String _insertSamplesStmt =
        "INSERT INTO " + _dbName + ".sample (channel_id, sample_time, nanosecs, value) VALUES ";
    private final String _insertSamplesPerMinuteStmt =
        "INSERT INTO " + _dbName + ".sample_m (channel_id, sample_time, avg_val, min_val, max_val) VALUES ";
    private final String _insertSamplesPerHourStmt =
        "INSERT INTO " + _dbName + ".sample_h (channel_id, sample_time, avg_val, min_val, max_val) VALUES ";

    private final String _selectSamplesStmt =
        "SELECT sample_time, nanosecs, value " +
        "FROM " + _dbName + "." + ARCH_TABLE_PLACEHOLDER + " WHERE channel_id=? " +
        "AND sample_time BETWEEN ? AND ?";
    private final String _selectOptSamplesStmt =
        "SELECT sample_time, avg_val, min_val, max_val " +
        "FROM " + _dbName + "."+ ARCH_TABLE_PLACEHOLDER + " WHERE channel_id=? " +
        "AND sample_time BETWEEN ? AND ?";

    /**
     * the reduced data, I'd love to use gabriele's aggregators, but there are his alarms, and times.
     */
    private final ThreadLocal<Map<ArchiveChannelId, SampleAggregator>> _reducedDataMapForMinutes =
        new ThreadLocal<Map<ArchiveChannelId, SampleAggregator>>();
    private final ThreadLocal<Map<ArchiveChannelId, SampleAggregator>> _reducedDataMapForHours =
        new ThreadLocal<Map<ArchiveChannelId, SampleAggregator>>();


    /**
     * Constructor.
     */
    public ArchiveSampleDaoImpl(@Nonnull final ArchiveDaoManager mgr) {
        super(mgr);
        final Map<ArchiveChannelId, SampleAggregator> minutesMap = Maps.newHashMap();
        _reducedDataMapForMinutes.set(minutesMap);
        final Map<ArchiveChannelId, SampleAggregator> hoursMap = Maps.newHashMap();
        _reducedDataMapForHours.set(hoursMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V, T extends IAlarmSystemVariable<V>>
    void createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException {

        try {
            final List<String> stmts = composeStatements(samples);
            if (stmts != null && !stmts.isEmpty()) {
                getDaoMgr().submitStatementsToBatch(stmts);
            }
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        }
    }

    @CheckForNull
    private <V, T extends IAlarmSystemVariable<V>>
        List<String> composeStatements(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException, ArchiveConnectionException, SQLException, TypeSupportException {

        final List<String> values = Lists.newArrayList();
        final List<String> valuesPerMinute = Lists.newArrayList();
        final List<String> valuesPerHour = Lists.newArrayList();

        for (final IArchiveSample<V, T> sample : samples) {

            final ArchiveChannelId channelId = sample.getChannelId();
            final T sysVar = sample.getSystemVariable();
            //final EpicsAlarm alarm = (EpicsAlarm) sysVar.getAlarm(); // FIXME (bknerr) : how to cope with alarms that don't have severities and status?
            final TimeInstant timestamp = sysVar.getTimestamp();

//            final ArchiveSeverityId sevId =
//                getDaoMgr().getSeverityDao().retrieveSeverityId(alarm.getSeverity());
//            final ArchiveStatusId statusId = getDaoMgr().getStatusDao().retrieveStatusId(alarm.getStatus());

//            if (sevId == null || statusId == null) {
//                LOG.warn("Ids could not be retrieved for severity " +
//                         alarm.getSeverity().name() + " and/or status " + alarm.getStatus().name() +
//                ". Severity and/or status table corrupted?");
//                // FIXME (bknerr) : what to do with the sample? archive/log/backup?
//            } else {
                // the VALUES (...) component for the standard sample table
                values.add(createSampleValueStmtStr(channelId,
//                                                    sevId,
//                                                    statusId,
                                                    sysVar,
                                                    timestamp));

                if (ArchiveTypeConversionSupport.isDataTypeOptimizable(sysVar.getData().getValueData().getClass())) {
                    writeReducedData(channelId,
                                     sysVar,
//                                     alarm,
                                     timestamp,
                                     valuesPerMinute,
                                     valuesPerHour);
                }

//            }
        }
        return joinStringsToStatementBatch(values, valuesPerMinute, valuesPerHour);
    }


    private <T extends IAlarmSystemVariable<?>>
        void writeReducedData(@Nonnull final ArchiveChannelId channelId,
                              @Nonnull final T data,
//                              @CheckForNull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp,
                              @Nonnull final List<String> valuesPerMinute,
                              @Nonnull final List<String> valuesPerHour) throws ArchiveDaoException {

        final Double newValue = isDataConvertibleToDouble(data.getData().getValueData());
        if (newValue == null || newValue.equals(Double.NaN)) {
            return; // not convertible, no data reduction possible
        }

        final String minuteValueStr = aggregateAndComposeValueString(_reducedDataMapForMinutes.get(),
                                                                     channelId,
                                                                     newValue,
//                                                                     alarm,
                                                                     newValue,
                                                                     newValue,
                                                                     timestamp,
                                                                     Minutes.ONE.toStandardDuration());
        if (minuteValueStr == null) {
            return;
        }
        valuesPerMinute.add(minuteValueStr); // add to write VALUES() list for minutes

        final SampleAggregator minuteAgg = _reducedDataMapForMinutes.get().get(channelId);


        final String hourValueStr = aggregateAndComposeValueString(_reducedDataMapForHours.get(),
                                                                   channelId,
                                                                   minuteAgg.getAvg(),
//                                                                   minuteAgg.getHighestAlarm(),
                                                                   minuteAgg.getMin(),
                                                                   minuteAgg.getMax(),
                                                                   timestamp,
                                                                   Minutes.THREE.toStandardDuration());
        minuteAgg.reset();
        if (hourValueStr == null) {
            return;
        }
        valuesPerHour.add(hourValueStr);


        final SampleAggregator hoursAgg = _reducedDataMapForHours.get().get(channelId);
        // for days would be here...
        hoursAgg.reset(); // and reset this aggregator
    }

    // CHECKSTYLE OFF: ParameterNumber
    @CheckForNull
    private String aggregateAndComposeValueString(@Nonnull final Map<ArchiveChannelId, SampleAggregator> map,
                                                  @Nonnull final ArchiveChannelId channelId,
                                                  @Nonnull final Double newValue,
//                                                  @CheckForNull final EpicsAlarm highestAlarm,
                                                  @Nonnull final Double min,
                                                  @Nonnull final Double max,
                                                  @Nonnull final TimeInstant timestamp,
                                                  @Nonnull final Duration interval) throws ArchiveDaoException {
        // CHECKSTYLE ON: ParameterNumber

        SampleAggregator agg =  map.get(channelId);
        if (agg == null) {
            agg = new SampleAggregator(newValue, /*highestAlarm,*/ timestamp);
            map.put(channelId, agg);
        } else {
            agg.aggregateNewVal(newValue, /*highestAlarm,*/ min, max, timestamp);
        }
        if (!isReducedDataWriteDueAndHasChanged(newValue, agg, timestamp, interval)) {
            return null;
        }
        return createReducedSampleValueString(channelId,
                                              timestamp,
//                                              agg.getHighestAlarm(),
                                              agg.getAvg(),
                                              agg.getMin(),
                                              agg.getMax());
    }

    @CheckForNull
    private Double isDataConvertibleToDouble(@Nonnull final Object data) {
        try {
            return ArchiveTypeConversionSupport.toDouble(data);
        } catch (final TypeSupportException e) {
            return null; // is not convertible. Type support missing.
        }
    }

    private boolean isReducedDataWriteDueAndHasChanged(@Nonnull final Double newVal,
                                                       @Nonnull final SampleAggregator agg,
                                                       @Nonnull final TimeInstant timestamp,
                                                       @Nonnull final Duration duration) {

        final TimeInstant lastWriteTime = agg.getResetTimestamp();
        if (lastWriteTime == null) {
            return true;
        }
        final TimeInstant dueTime = lastWriteTime.plusMillis(duration.getMillis());
        if (timestamp.isBefore(dueTime)) {
            return false; // not yet due, don't write
        }

        final Double lastWrittenValue = agg.getAverageBeforeReset();
        if (lastWrittenValue != null && lastWrittenValue.compareTo(newVal) == 0) {
            return false; // hasn't changed much TODO (bknerr) : consider a sort of 'deadband' here, too
        }
        return true;
    }


    @CheckForNull
    private List<String> joinStringsToStatementBatch(@Nonnull final List<String> values,
                                                  @Nonnull final List<String> valuesPerMinute,
                                                  @Nonnull final List<String> valuesPerHour)
        throws SQLException, ArchiveConnectionException {
        final List<String> statements = Lists.newLinkedList();
        if (!values.isEmpty()) {
            statements.add(Joiner.on(" ").join(_insertSamplesStmt, Joiner.on(", ").join(values)));
        }
        if (!valuesPerMinute.isEmpty()) {
            final String stmtStr = Joiner.on(" ").join(_insertSamplesPerMinuteStmt, Joiner.on(", ").join(valuesPerMinute));
            statements.add(stmtStr);
        }
        if (!valuesPerHour.isEmpty()) {
            final String stmtStr = Joiner.on(" ").join(_insertSamplesPerHourStmt, Joiner.on(", ").join(valuesPerHour));
            statements.add(stmtStr);
        }
        return statements;
    }

    /**
     * The simple VALUES component for table sample:
     * "(channel_id, smpl_time, /// severity_id, status_id, // str_val, nanosecs),"
     */
    @Nonnull
    private <T extends IAlarmSystemVariable<?>>
        String createSampleValueStmtStr(@Nonnull final ArchiveChannelId channelId,
//                                        @Nonnull final ArchiveSeverityId sevId,
//                                        @Nonnull final ArchiveStatusId statusId,
                                        @Nonnull final T value,
                                        @Nonnull final TimeInstant timestamp) {
            try {
                return "(" + Joiner.on(", ").join(channelId.intValue(),
                                                  "'" + timestamp.formatted() + "'",
                                                  timestamp.getFractalSecondsInNanos(),
//                                                  sevId.intValue(),
//                                                  statusId.intValue(),
                                                  "'" + ArchiveTypeConversionSupport.toArchiveString(value.getData().getValueData()) + "'") +
                       ")";
            } catch (final TypeSupportException e) {
                LOG.warn("No type support for archive string representation.", e);
                return "";
            }
        }

    /**
     * The averaged VALUES component for table sample_*:
     * "(channel_id, sample_time, highest_severity_id, avg_val, min_val, max_val)"
     * @throws ArchiveSeverityDaoException
     */
    @Nonnull
    private String createReducedSampleValueString(@Nonnull final ArchiveChannelId channelId,
                                                  @Nonnull final TimeInstant timestamp,
//                                                  @Nonnull final EpicsAlarm highestAlarm,
                                                  @CheckForNull final Double avg,
                                                  @CheckForNull final Double min,
                                                  @CheckForNull final Double max) throws ArchiveDaoException {
        // write for all samples_x (channel_id, sample_time, highest_severity_id, avg_val, min_val, max_val)
//        final ArchiveSeverityId sevId = null;
//        if (highestAlarm != null) {
//            sevId = getDaoMgr().getSeverityDao().retrieveSeverityId(highestAlarm.getSeverity());
//        }
//        final int sevIdInt = sevId == null ? ArchiveSeverityId.NONE.intValue() : sevId.intValue();
        if (avg == null) {
            throw new ArchiveDaoException("Average value must not be null on write reduced samples.", null);
        }

        final String valueStr =
            "(" +
            Joiner.on(",").join(channelId.intValue(),
                                "'" + timestamp.formatted() + "'",
//                                sevIdInt,
                                avg,
                                min == null ? avg : min,
                                max == null ? avg : max) +
            ")";
        return valueStr;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V, T extends IAlarmSystemVariable<V>>
    Iterable<IArchiveMinMaxSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                         @Nonnull final IArchiveChannel channel,
                                                         @Nonnull final TimeInstant s,
                                                         @Nonnull final TimeInstant e) throws ArchiveDaoException {

        PreparedStatement stmt = null;
        try {
            final DesyArchiveRequestType reqType = determineRequestType(type, channel.getDataType(), s, e);

            stmt = dispatchRequestTypeToStatement(reqType);
            stmt.setInt(1, channel.getId().intValue());
            stmt.setTimestamp(2, new Timestamp(s.getMillis()));
            stmt.setTimestamp(3, new Timestamp(e.getMillis() + 1000));

            final ResultSet result = stmt.executeQuery();

            final List<IArchiveMinMaxSample<V, T>> iterable = Lists.newArrayList();

            while (result.next()) {
                final IArchiveMinMaxSample<V, T> sample =
                    createSampleFromQueryResult(reqType, channel, result);
                iterable.add(sample);
            }
            return iterable;

        } catch (final ArchiveConnectionException ace) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, ace);
        } catch (final SQLException se) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, se);
        } catch (final TypeSupportException tse) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, tse);
        } finally {
            closeStatement(stmt, "Closing of statement failed.");
        }
    }

    @Nonnull
    private PreparedStatement dispatchRequestTypeToStatement(@Nonnull final DesyArchiveRequestType type)
        throws SQLException, ArchiveConnectionException {

        PreparedStatement stmt = null;
        switch (type) {
            case RAW :
                stmt = getConnection().prepareStatement(_selectSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, "sample"));
                break;
            case AVG_PER_MINUTE :
                stmt = getConnection().prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, "sample_m"));
                break;
            case AVG_PER_HOUR :
                stmt = getConnection().prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, "sample_h"));
                break;
            default :
        }
        return stmt;
    }


    @SuppressWarnings("unchecked")
    @Nonnull
    private <V, T extends IAlarmSystemVariable<V>>
    IArchiveMinMaxSample<V, T> createSampleFromQueryResult(@Nonnull final DesyArchiveRequestType type,
                                                           @Nonnull final IArchiveChannel channel,
                                                           @Nonnull final ResultSet result) throws SQLException,
                                                                                                   ArchiveDaoException,
                                                                                                   TypeSupportException {
        final String dataType = channel.getDataType();
        final Timestamp timestamp = result.getTimestamp(1);
//        final ArchiveSeverityId sevId = new ArchiveSeverityId(result.getInt(2));
//        final IArchiveSeverity sev = getDaoMgr().getSeverityDao().retrieveSeverityById(sevId);

        long nanosecs = 0L;
        V value = null;
        V min = null;
        V max = null;
        //final IArchiveStatus st = null;

        switch (type) {
            case RAW : {
                // (..., nanosecs, status_id, value)
                nanosecs = result.getLong("nanosecs");
                //final ArchiveStatusId statusId = new ArchiveStatusId(result.getInt("status_id"));
                value = ArchiveTypeConversionSupport.fromArchiveString(dataType, result.getString("value"));
                //st = getDaoMgr().getStatusDao().retrieveStatusById(statusId);
                break;
            }
            case AVG_PER_MINUTE :
            case AVG_PER_HOUR : {
                // (..., avg_val, min_val, max_val)
                value = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("avg_val"));
                min = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("min_val"));
                max = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("max_val"));
                //st = new ArchiveStatus(ArchiveStatusId.NONE, "UNKNOWN");
                break;
            }
            default:
                break;
        }
//        if (sev == null || st == null) {
//            throw new ArchiveDaoException("Severity or status could not be retrieved for sample.", null);
//        }
        // TODO (bknerr) : Epics specific, refactor this into a generic alarm for all control system types
//        final EpicsAlarm alarm = new EpicsAlarm(EpicsAlarmSeverity.parseSeverity(sev.getName()),
//                                                EpicsAlarmStatus.parseStatus(st.getName()));
        final TimeInstant timeInstant = TimeInstantBuilder.buildFromMillis(timestamp.getTime()).plusNanosPerSecond(nanosecs);
        final IArchiveControlSystem cs = channel.getControlSystem();
        final ISystemVariable<V> sysVar = SystemVariableSupport.create(channel.getName(),
                                                                       value,
                                                                       new ControlSystem(cs.getName(), cs.getType()),
                                                                       timeInstant);

        final ArchiveMinMaxSample<V, T> sample =
            new ArchiveMinMaxSample<V, T>(channel.getId(), (T) sysVar, null, min, max);
        return sample;
    }


    @Nonnull
    private DesyArchiveRequestType determineRequestType(@CheckForNull final DesyArchiveRequestType type,
                                                        @Nonnull final String dataType,
                                                        @Nonnull final TimeInstant s,
                                                        @Nonnull final TimeInstant e) throws ArchiveDaoException, TypeSupportException {

        if (DesyArchiveRequestType.RAW.equals(type) || !ArchiveTypeConversionSupport.isDataTypeOptimizable(dataType)) {
            return DesyArchiveRequestType.RAW;
        } else if (type != null) {
            return type;
        } else {
            DesyArchiveRequestType reqType;
            final Duration d = new Duration(s.getInstant(), e.getInstant());
            if (d.isLongerThan(Duration.standardDays(45))) {
                reqType = DesyArchiveRequestType.AVG_PER_HOUR;
            } else if (d.isLongerThan(Duration.standardDays(1))) {
                reqType = DesyArchiveRequestType.AVG_PER_MINUTE;
            } else {
                reqType = DesyArchiveRequestType.RAW;
            }
            return reqType;
        }
    }
}
