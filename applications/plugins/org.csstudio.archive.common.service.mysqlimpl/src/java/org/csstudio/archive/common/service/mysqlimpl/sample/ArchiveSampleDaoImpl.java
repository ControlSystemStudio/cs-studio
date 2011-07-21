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
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
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

    /**
     * Minute type sample.
     *
     * @author bknerr
     * @since 21.07.2011
     */
    class MinuteReducedDataSample extends AbstractReducedDataSample {
        /**
         * Constructor.
         */
        public MinuteReducedDataSample(@Nonnull final ArchiveChannelId id,
                                       @Nonnull final TimeInstant timestamp,
                                       @Nonnull final Double avg,
                                       @Nonnull final Double min,
                                       @Nonnull final Double max) {
            super(id, timestamp, avg, min, max);
        }
    }
    /**
     * Hour type sample.
     *
     * @author bknerr
     * @since 21.07.2011
     */
    class HourReducedDataSample extends AbstractReducedDataSample {
        /**
         * Constructor.
         */
        public HourReducedDataSample(@Nonnull final ArchiveChannelId id,
                                     @Nonnull final TimeInstant timestamp,
                                     @Nonnull final Double avg,
                                     @Nonnull final Double min,
                                     @Nonnull final Double max) {
            super(id, timestamp, avg, min, max);
        }
    }

    private static final String ARCH_TABLE_PLACEHOLDER = "<arch.table>";

    private static final String RETRIEVAL_FAILED = "Sample retrieval from archive failed.";

    private static final String SELECT_RAW_PREFIX = "SELECT sample_time, nanosecs, value ";

    private final String _dbName = getDatabaseName();

    private final String _selectSamplesStmt =
        SELECT_RAW_PREFIX +
        "FROM " + _dbName + "." + ARCH_TABLE_PLACEHOLDER + " WHERE channel_id=? " +
        "AND sample_time BETWEEN ? AND ?";
    private final String _selectOptSamplesStmt =
        "SELECT sample_time, avg_val, min_val, max_val " +
        "FROM " + _dbName + "."+ ARCH_TABLE_PLACEHOLDER + " WHERE channel_id=? " +
        "AND sample_time BETWEEN ? AND ?";
    private final String _selectLatestSampleBeforeTimeStmt =
        SELECT_RAW_PREFIX +
        "FROM " + _dbName + ".sample WHERE channel_id=? " +
        "AND sample_time<? ORDER BY sample_time DESC LIMIT 1";


    private final Map<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForMinutes =
        Maps.newConcurrentMap();
    private final Map<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForHours =
        Maps.newConcurrentMap();


    /**
     * Constructor.
     */
    @Inject
    public ArchiveSampleDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                @Nonnull final PersistEngineDataManager persister) throws ArchiveDaoException {
        super(handler, persister);

        getEngineMgr().registerBatchQueueHandler(new ArchiveSampleBatchQueueHandler(getDatabaseName()));

        getEngineMgr().registerBatchQueueHandler(new MinuteReducedDataSampleBatchQueueHandler(getDatabaseName()));

        getEngineMgr().registerBatchQueueHandler(new HourReducedDataSampleBatchQueueHandler(getDatabaseName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V, T extends ISystemVariable<V>>
    void createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException {

        getEngineMgr().submitToBatch(samples);

        try {
            List<? extends AbstractReducedDataSample> minuteSamples;
                minuteSamples = generatePerMinuteSamples(samples);
            getEngineMgr().submitToBatch(minuteSamples);

            final List<? extends AbstractReducedDataSample> hourSamples =
                generatePerHourSamples(minuteSamples);
            getEngineMgr().submitToBatch(hourSamples);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Type support for sample type could not be found.", e);
        }
    }

    @Nonnull
    private List<? extends AbstractReducedDataSample>
        generatePerHourSamples(@Nonnull final Collection<? extends AbstractReducedDataSample> samples) {

        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<HourReducedDataSample> hourSamples = Lists.newLinkedList();

        for (final AbstractReducedDataSample sample : samples) {

            final Double newValue = sample.getAvg();
            final Double minValue = sample.getMin();
            final Double maxValue = sample.getMax();
            final TimeInstant time = sample.getTimestamp();

            final ArchiveChannelId channelId = sample.getChannelId();
            SampleMinMaxAggregator agg = _reducedDataMapForHours.get(channelId);

            agg = aggregateSample(newValue, minValue, maxValue, time, channelId, agg, _reducedDataMapForHours);

            if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Hours.ONE.toStandardDuration())) {
                hourSamples.add(new HourReducedDataSample(channelId,
                                                          time,
                                                          agg.getAvg(),
                                                          agg.getMin(),
                                                          agg.getMax()));
            }


        }
        return hourSamples;
    }

    @Nonnull
    private <V, T extends ISystemVariable<V>>
    List<? extends AbstractReducedDataSample> generatePerMinuteSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples)
                                                                       throws TypeSupportException {
        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<MinuteReducedDataSample> minuteSamples = Lists.newLinkedList();

        for (final IArchiveSample<V, T> sample : samples) {
            final T sysVar = sample.getSystemVariable();
            final V data = sysVar.getData();

            if (ArchiveTypeConversionSupport.isDataTypeOptimizable(data.getClass())) {

                final Double newValue = createDoubleFromValueOrNull(sysVar);
                if (newValue == null) {
                    continue;
                }
                final Double minValue = newValue;
                final Double maxValue = newValue;
                final TimeInstant time = sample.getSystemVariable().getTimestamp();

                final ArchiveChannelId channelId = sample.getChannelId();
                SampleMinMaxAggregator agg = _reducedDataMapForMinutes.get(channelId);

                agg = aggregateSample(newValue, minValue, maxValue, time, channelId, agg, _reducedDataMapForMinutes);

                if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Minutes.ONE.toStandardDuration())) {
                    minuteSamples.add(new MinuteReducedDataSample(channelId,
                                                                  time,
                                                                  agg.getAvg(),
                                                                  agg.getMin(),
                                                                  agg.getMax()));
                }
            }
        }
        return minuteSamples;

    }

    @Nonnull
    private SampleMinMaxAggregator aggregateSample(@Nonnull final Double newValue,
                                                   @Nonnull final Double minValue,
                                                   @Nonnull final Double maxValue,
                                                   @Nonnull final TimeInstant time,
                                                   @Nonnull final ArchiveChannelId channelId,
                                                   @Nonnull final SampleMinMaxAggregator agg,
                                                   @Nonnull final Map<ArchiveChannelId, SampleMinMaxAggregator> aggMap) {
        if (agg != null) {
            agg.aggregate(newValue, minValue, maxValue, time);
            return agg;
        }
        final SampleMinMaxAggregator aggregator = new SampleMinMaxAggregator(newValue, time);
        aggMap.put(channelId, aggregator);

        return aggregator;
    }


    @CheckForNull
    private <T extends ISystemVariable<?>>
    Double createDoubleFromValueOrNull(@Nonnull final T sysVar) {
        Double newValue = null;
        try {
            newValue = BaseTypeConversionSupport.toDouble(sysVar.getData());
        } catch (final TypeSupportException e) {
            return null; // not convertible. Type support missing.
        }
        if (newValue.equals(Double.NaN)) {
            return null; // not convertible, no data reduction possible
        }
        return newValue;
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
    public <V, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                     @Nonnull final IArchiveChannel channel,
                                                     @Nonnull final TimeInstant s,
                                                     @Nonnull final TimeInstant e) throws ArchiveDaoException {

        PreparedStatement stmt = null;
        try {
            DesyArchiveRequestType reqType = determineRequestType(type, channel.getDataType(), s, e);

            ResultSet result;
            do {
                stmt = createReadSamplesStatement(channel, s, e, reqType);
                result = stmt.executeQuery();

                if (!result.next()) { // no result, try the next lower order request type
                    reqType = reqType.getNextLowerOrderRequestType();
                } else {
                    return createRetrievedSamplesContainer(channel, reqType, result);
                }
            } while (reqType != null); // no other request type of lower order

        } catch (final Exception ex) {
            handleExceptions(RETRIEVAL_FAILED, ex);
        } finally {
            closeStatement(stmt, "Closing of statement failed.");
        }
        return Collections.emptyList();
    }

    @Nonnull
    private <V, T extends ISystemVariable<V>>
    Collection<IArchiveSample<V, T>> createRetrievedSamplesContainer(@Nonnull final IArchiveChannel channel,
                                                                     @Nonnull final DesyArchiveRequestType reqType,
                                                                     @CheckForNull final ResultSet result)
                                                                     throws SQLException,
                                                                            ArchiveDaoException,
                                                                            TypeSupportException {
        final List<IArchiveSample<V, T>> samples = Lists.newArrayList();
        while (result != null && result.next()) {
            final IArchiveSample<V, T> sample =
                createSampleFromQueryResult(reqType, channel, result);
            samples.add(sample);
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
        PreparedStatement stmt;
        stmt = dispatchRequestTypeToStatement(reqType);
        stmt.setInt(1, channel.getId().intValue());
        stmt.setTimestamp(2, new Timestamp(s.getMillis()));
        stmt.setTimestamp(3, new Timestamp(e.getMillis() + 1)); // + 1 for all with nanosecs > 1
        return stmt;
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
    private <V, T extends ISystemVariable<V>>
    IArchiveMinMaxSample<V, T> createSampleFromQueryResult(@Nonnull final DesyArchiveRequestType type,
                                                           @Nonnull final IArchiveChannel channel,
                                                           @Nonnull final ResultSet result) throws SQLException,
                                                                                                   ArchiveDaoException,
                                                                                                   TypeSupportException {
        final String dataType = channel.getDataType();
        final Timestamp timestamp = result.getTimestamp("sample_time");

        long nanosecs = 0L;
        V value = null;
        V min = null;
        V max = null;

        switch (type) {
            case RAW : {
                // (..., nanosecs, value)
                nanosecs = result.getLong("nanosecs");
                value = ArchiveTypeConversionSupport.fromArchiveString(dataType, result.getString("value"));
                break;
            }
            case AVG_PER_MINUTE :
            case AVG_PER_HOUR : {
                // (..., avg_val, min_val, max_val)
                value = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("avg_val"));
                min = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("min_val"));
                max = ArchiveTypeConversionSupport.fromDouble(dataType, result.getDouble("max_val"));
                break;
            }
            default:
                throw new ArchiveDaoException("Archive request type unknown. Sample could not be created from query", null);
        }
        final TimeInstant timeInstant = TimeInstantBuilder.fromMillis(timestamp.getTime()).plusNanosPerSecond(nanosecs);
        final IArchiveControlSystem cs = channel.getControlSystem();
        final ISystemVariable<V> sysVar = SystemVariableSupport.create(channel.getName(),
                                                                       value,
                                                                       ControlSystem.valueOf(cs.getName(), cs.getType()),
                                                                       timeInstant);
        final ArchiveMinMaxSample<V, T> sample =
            new ArchiveMinMaxSample<V, T>(channel.getId(), (T) sysVar, null, min, max);
        return sample;
    }


    @Nonnull
    private DesyArchiveRequestType determineRequestType(@CheckForNull final DesyArchiveRequestType type,
                                                        @Nonnull final String dataType,
                                                        @Nonnull final TimeInstant s,
                                                        @Nonnull final TimeInstant e) throws TypeSupportException {

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

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V, T extends ISystemVariable<V>>
    IArchiveSample<V, T> retrieveLatestSampleBeforeTime(@Nonnull final IArchiveChannel channel,
                                                        @Nonnull final TimeInstant time) throws ArchiveDaoException {
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectLatestSampleBeforeTimeStmt);
            stmt.setInt(1, channel.getId().intValue());
            stmt.setTimestamp(2, new Timestamp(time.getMillis()));
            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return createSampleFromQueryResult(DesyArchiveRequestType.RAW, channel, result);
            }
        } catch(final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeStatement(stmt, "Closing of statement failed.");
        }
        return null;
    }
}
