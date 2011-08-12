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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Archive sample dao implementation.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public class ArchiveSampleDaoImpl extends AbstractArchiveDao implements IArchiveSampleDao {

    public static final String TAB_SAMPLE = "sample";
    public static final String TAB_SAMPLE_M = "sample_m";
    public static final String TAB_SAMPLE_H = "sample_h";
    public static final String TAB_SAMPLE_BLOB = "sample_blob";

    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CHANNEL_ID = "channel_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_AVG = "avg_val";
    public static final String COLUMN_MIN = "min_val";
    public static final String COLUMN_MAX = "max_val";

    private static final String ARCH_TABLE_PLACEHOLDER = "<arch.table>";
    private static final String RETRIEVAL_FAILED = "Sample retrieval from archive failed.";

    private static final String SELECT_RAW_PREFIX =
        "SELECT " + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_VALUE) + " ";
    private final String _selectRawSamplesStmt =
        SELECT_RAW_PREFIX +
        "FROM " + getDatabaseName() + "." + ARCH_TABLE_PLACEHOLDER + " WHERE " + COLUMN_CHANNEL_ID + "=? " +
        "AND " + COLUMN_TIME + " BETWEEN ? AND ?";
    private final String _selectOptSamplesStmt =
        "SELECT " + Joiner.on(",").join(COLUMN_CHANNEL_ID, COLUMN_TIME, COLUMN_AVG, COLUMN_MIN, COLUMN_MAX) + " " +
        "FROM " + getDatabaseName() + "."+ ARCH_TABLE_PLACEHOLDER + " WHERE " + COLUMN_CHANNEL_ID + "=? " +
        "AND " + COLUMN_TIME + " BETWEEN ? AND ?";
    private final String _selectLatestSampleBeforeTimeStmt =
        SELECT_RAW_PREFIX +
        "FROM " + getDatabaseName() + "." + TAB_SAMPLE + " WHERE " + COLUMN_CHANNEL_ID + "=? " +
        "AND " + COLUMN_TIME + "<? ORDER BY " + COLUMN_TIME + " DESC LIMIT 1";

    private final Map<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForMinutes =
        Maps.newConcurrentMap();
    private final Map<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForHours =
        Maps.newConcurrentMap();

    private final IArchiveChannelDao _channelDao;

    /**
     * Constructor.
     */
    @Inject
    public ArchiveSampleDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                @Nonnull final PersistEngineDataManager persister,
                                @Nonnull final IArchiveChannelDao channelDao) {
        super(handler, persister);
        _channelDao = channelDao;

        ArchiveTypeConversionSupport.install();
        EpicsSystemVariableSupport.install();

        BatchQueueHandlerSupport.installHandlerIfNotExists(new ArchiveSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new CollectionDataSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new MinuteReducedDataSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new HourReducedDataSampleBatchQueueHandler(getDatabaseName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Serializable, T extends ISystemVariable<V>>
    void createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException {

        try {
            getEngineMgr().submitToBatch(samples);

            final List<? extends AbstractReducedDataSample> minuteSamples;
                minuteSamples = generatePerMinuteSamples(samples, _reducedDataMapForMinutes);
            if (minuteSamples.isEmpty()) {
                return;
            }
            getEngineMgr().submitToBatch(minuteSamples);

            final List<? extends AbstractReducedDataSample> hourSamples =
                generatePerHourSamples(minuteSamples, _reducedDataMapForHours);
            if (hourSamples.isEmpty()) {
                return;
            }
            getEngineMgr().submitToBatch(hourSamples);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Type support for sample type could not be found.", e);
        }
    }

    @Nonnull
    private List<? extends AbstractReducedDataSample>
    generatePerHourSamples(@Nonnull final Collection<? extends AbstractReducedDataSample> samples,
                           @Nonnull final Map<ArchiveChannelId, SampleMinMaxAggregator> aggregatorMap) throws ArchiveDaoException {

        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<HourReducedDataSample> hourSamples = Lists.newLinkedList();

        for (final AbstractReducedDataSample sample : samples) {

            final ArchiveChannelId channelId = sample.getChannelId();
            final Double newValue = sample.getAvg();
            final Double minValue = sample.getMin();
            final Double maxValue = sample.getMax();
            final TimeInstant time = sample.getTimestamp();

            final SampleMinMaxAggregator agg = retrieveAndInitializeAggregator(channelId,
                                                                               aggregatorMap,
                                                                               newValue,
                                                                               minValue,
                                                                               maxValue,
                                                                               time);

            processHourSampleOnTimeCondition(hourSamples, channelId, newValue, time, agg);
        }
        return hourSamples;
    }

    private void processHourSampleOnTimeCondition(@Nonnull final List<HourReducedDataSample> hourSamples,
                                                  @Nonnull final ArchiveChannelId channelId,
                                                  @Nonnull final Double newValue,
                                                  @Nonnull final TimeInstant time,
                                                  @Nonnull final SampleMinMaxAggregator agg) {
        if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Hours.ONE.toStandardDuration())) {
            final Double avg = agg.getAvg();
            final Double min = agg.getMin();
            final Double max = agg.getMax();
            if (avg != null && min != null && max != null) {
                hourSamples.add(new HourReducedDataSample(channelId, time, avg, min, max));
            }
            agg.reset();
        }
    }

    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>>
    List<? extends AbstractReducedDataSample> generatePerMinuteSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples,
                                                                       @Nonnull final Map<ArchiveChannelId, SampleMinMaxAggregator> aggregatorMap)
                                                                       throws TypeSupportException, ArchiveDaoException {
        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<MinuteReducedDataSample> minuteSamples = Lists.newLinkedList();

        for (final IArchiveSample<V, T> sample : samples) {
            final T sysVar = sample.getSystemVariable();
            final V data = sysVar.getData();

            if (ArchiveTypeConversionSupport.isDataTypeOptimizable(data.getClass())) {
                final Double newValue =
                    BaseTypeConversionSupport.createDoubleFromValueOrNull(sysVar);
                if (newValue == null) {
                    continue;
                }
                final ArchiveChannelId channelId = sample.getChannelId();
                final Double minValue = newValue;
                final Double maxValue = newValue;
                final TimeInstant time = sample.getSystemVariable().getTimestamp();

                final SampleMinMaxAggregator agg = retrieveAndInitializeAggregator(channelId,
                                                                                   aggregatorMap,
                                                                                   newValue,
                                                                                   minValue,
                                                                                   maxValue,
                                                                                   time);
                processMinuteSampleOnTimeCondition(minuteSamples, newValue, channelId, time, agg);
            }
        }
        return minuteSamples;

    }

    private void processMinuteSampleOnTimeCondition(@Nonnull final List<MinuteReducedDataSample> minuteSamples,
                                                    @Nonnull final Double newValue,
                                                    @Nonnull final ArchiveChannelId channelId,
                                                    @Nonnull final TimeInstant time,
                                                    @Nonnull final SampleMinMaxAggregator agg) {
        if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Minutes.ONE.toStandardDuration())) {
            final Double avg = agg.getAvg();
            final Double min = agg.getMin();
            final Double max = agg.getMax();
            if (avg != null && min != null && max != null) {
                minuteSamples.add(new MinuteReducedDataSample(channelId, time, avg, min, max));
            }
            agg.reset();
        }
    }

    @Nonnull
    private SampleMinMaxAggregator retrieveAndInitializeAggregator(@Nonnull final ArchiveChannelId channelId,
                                                                   @Nonnull final Map<ArchiveChannelId, SampleMinMaxAggregator> aggMap,
                                                                   @Nonnull final Double value,
                                                                   @Nonnull final Double min,
                                                                   @Nonnull final Double max,
                                                                   @Nonnull final TimeInstant time) throws ArchiveDaoException {
        SampleMinMaxAggregator aggregator = aggMap.get(channelId);
        if (aggregator == null) {
            aggregator = new SampleMinMaxAggregator();
            initAggregatorToLastKnownSample(channelId, time, aggregator);
            aggMap.put(channelId, aggregator);
        }
        aggregator.aggregate(value, min, max, time);
        return aggregator;
    }

    private void initAggregatorToLastKnownSample(@Nonnull final ArchiveChannelId channelId,
                                                 @Nonnull final TimeInstant time,
                                                 @Nonnull final SampleMinMaxAggregator aggregator) throws ArchiveDaoException {
        final IArchiveChannel channel = _channelDao.retrieveChannelById(channelId);
        if (channel == null) {
            throw new ArchiveDaoException("Init sample aggregator failed. Channel with id " + channelId.intValue() +
                                          " does not exist.", null);
        }
        final IArchiveSample<Serializable, ISystemVariable<Serializable>> sample =
            retrieveLatestSampleBeforeTime(channel, time);
        if (sample != null) {
            final Double lastWrittenValue =
                BaseTypeConversionSupport.createDoubleFromValueOrNull(sample.getSystemVariable());
            if (lastWrittenValue != null) {
                final TimeInstant lastWriteTime = sample.getSystemVariable().getTimestamp();
                aggregator.aggregate(lastWrittenValue, lastWriteTime);
            }
        }
    }

    private boolean isReducedDataWriteDueAndHasChanged(@Nonnull final Double newVal,
                                                       @Nonnull final SampleMinMaxAggregator agg,
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                     @Nonnull final ArchiveChannelId channelId,
                                                     @Nonnull final TimeInstant start,
                                                     @Nonnull final TimeInstant end) throws ArchiveDaoException {
        final IArchiveChannel channel = _channelDao.retrieveChannelById(channelId);
        if (channel != null) {
            return retrieveSamples(type, channel, start, end);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                     @Nonnull final IArchiveChannel channel,
                                                     @Nonnull final TimeInstant start,
                                                     @Nonnull final TimeInstant end) throws ArchiveDaoException {

        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            DesyArchiveRequestType reqType = type != null ? // if null = determine automatically
                                             type :
                                             SampleRequestTypeUtil.determineRequestType(channel.getDataType(), start, end);
            do {
                stmt = createReadSamplesStatement(channel, start, end, reqType);
                result = stmt.executeQuery();
                if (result.next()) {
                    return createRetrievedSamplesContainer(channel, reqType, result);
                } else if (type == null) { // type == null means use automatic lookup
                    reqType = reqType.getNextLowerOrderRequestType();
                }
            } while (type == null && reqType != null); // no other request type of lower order

        } catch (final Exception ex) {
            handleExceptions(RETRIEVAL_FAILED, ex);
        } finally {
            closeStatement(result, stmt, "Closing of statement failed.");
        }
        return Collections.emptyList();
    }

    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> createRetrievedSamplesContainer(@Nonnull final IArchiveChannel channel,
                                                                     @Nonnull final DesyArchiveRequestType reqType,
                                                                     @CheckForNull final ResultSet result)
                                                                     throws SQLException,
                                                                            ArchiveDaoException,
                                                                            TypeSupportException {
        final List<IArchiveSample<V, T>> samples = Lists.newArrayList();
        while (result != null && !result.isAfterLast()) {
            final IArchiveSample<V, T> sample =
                createSampleFromQueryResult(reqType, channel, result);
            samples.add(sample);
            result.next();
        }
        return samples;
    }

    @Nonnull
    private PreparedStatement createReadSamplesStatement(@Nonnull final IArchiveChannel channel,
                                                         @Nonnull final TimeInstant s,
                                                         @Nonnull final TimeInstant e,
                                                         @Nonnull final DesyArchiveRequestType reqType)
                                                         throws SQLException,
                                                                ArchiveConnectionException {
        final PreparedStatement stmt = dispatchRequestTypeToStatement(reqType);
        stmt.setInt(1, channel.getId().intValue());
        stmt.setLong(2, s.getNanos());
        stmt.setLong(3, e.getNanos());
        return stmt;
    }

    @Nonnull
    private PreparedStatement dispatchRequestTypeToStatement(@Nonnull final DesyArchiveRequestType type)
                                                             throws SQLException,
                                                                    ArchiveConnectionException {

        PreparedStatement stmt = null;
        switch (type) {
            case RAW :
                stmt = getConnection().prepareStatement(_selectRawSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE));
                break;
            case RAW_MULTI_SCALAR :
                stmt = getConnection().prepareStatement(_selectRawSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_BLOB));
                break;
            case AVG_PER_MINUTE :
                stmt = getConnection().prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_M));
                break;
            case AVG_PER_HOUR :
                stmt = getConnection().prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_H));
                break;
            default :
        }
        return stmt;
    }


    @SuppressWarnings("unchecked")
    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveMinMaxSample<V, T> createSampleFromQueryResult(@Nonnull final DesyArchiveRequestType type,
                                                           @Nonnull final IArchiveChannel channel,
                                                           @Nonnull final ResultSet result) throws SQLException,
                                                                                                   ArchiveDaoException,
                                                                                                   TypeSupportException {
        final String dataType = channel.getDataType();
        V value = null;
        V min = null;
        V max = null;
        switch (type) {
            case RAW : {
                // (..., value)
                value = ArchiveTypeConversionSupport.fromArchiveString(dataType, result.getString(COLUMN_VALUE));
                break;
            }
            case RAW_MULTI_SCALAR : {
                value = ArchiveTypeConversionSupport.fromByteArray(result.getBytes(COLUMN_VALUE));
                break;
            }
            case AVG_PER_MINUTE :
            case AVG_PER_HOUR : {
                // (..., avg_val, min_val, max_val)
                value = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble(COLUMN_AVG));
                min = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble(COLUMN_MIN));
                max = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble(COLUMN_MAX));
                break;
            }
            default:
                throw new ArchiveDaoException("Archive request type unknown. Sample could not be created from query", null);
        }
        final long time = result.getLong(COLUMN_TIME);

        final TimeInstant timeInstant = TimeInstantBuilder.fromNanos(time);
        final IArchiveControlSystem cs = channel.getControlSystem();
        final ISystemVariable<V> sysVar = SystemVariableSupport.create(channel.getName(),
                                                                       value,
                                                                       ControlSystem.valueOf(cs.getName(), cs.getType()),
                                                                       timeInstant);
        final ArchiveMinMaxSample<V, T> sample =
            new ArchiveMinMaxSample<V, T>(channel.getId(), (T) sysVar, null, min, max);
        return sample;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V extends Serializable, T extends ISystemVariable<V>>
    IArchiveSample<V, T> retrieveLatestSampleBeforeTime(@Nonnull final IArchiveChannel channel,
                                                        @Nonnull final TimeInstant time) throws ArchiveDaoException {
        PreparedStatement stmt = null;
        ResultSet result  = null;
        try {
            stmt = getConnection().prepareStatement(_selectLatestSampleBeforeTimeStmt);
            stmt.setInt(1, channel.getId().intValue());
            stmt.setLong(2, time.getNanos());
            result = stmt.executeQuery();
            if (result.next()) {
                return createSampleFromQueryResult(DesyArchiveRequestType.RAW, channel, result);
            }
        } catch(final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeStatement(result, stmt, "Closing of statement failed.");
        }
        return null;
    }
}
