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
package org.csstudio.archive.common.service.mysqlimpl;

import java.util.Collection;
import java.util.EnumSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineConfigService;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.archivermgmt.IArchiverMgmtEntry;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archive.common.service.requesttypes.RequestTypeParameterException;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.common.service.samplemode.IArchiveSampleMode;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.epics.types.EpicsCssValueTypeSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.ITimedCssAlarmValueType;
import org.csstudio.domain.desy.types.ITimedCssValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;


/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * Uses DAO design pattern with DaoManager to handle several connections in a pool (later) and
 * facilite CRUD command infrastructure for proper multiple command transactions.
 *
 * @author bknerr
 * @since 01.11.2010
 */
public enum MySQLArchiveServiceImpl implements IArchiveEngineConfigService,
                                               IArchiveWriterService,
                                               IArchiveReaderService {

    INSTANCE;

    /**
     * Constructor.
     */
    private MySQLArchiveServiceImpl() {
        ArchiveTypeConversionSupport.install();
        EpicsCssValueTypeSupport.install();
    }

    /**
     * Static converter function.
     *
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class ArchiveSampleToIValueFunction implements
            Function<IArchiveMinMaxSample<Object, ITimedCssAlarmValueType<Object>>, IValue> {
        /**
         * Constructor.
         */
        public ArchiveSampleToIValueFunction() {
            // Empty
        }

        @Override
        @CheckForNull
        public IValue apply(@Nonnull final IArchiveMinMaxSample<Object, ITimedCssAlarmValueType<Object>> from) {
            try {
                // TODO (bknerr) : support lookup for every single value... check performance
                final Object min = from.getMinimum();
                final Object max = from.getMaximum();
                if (min != null && max != null) {
                    return EpicsCssValueTypeSupport.toIMinMaxDoubleValue(from.getData(), min, max);
                }
                return EpicsCssValueTypeSupport.toIValue(from.getData());
            } catch (final TypeSupportException e) {
                return null;
            }
        }
    }
    private static final ArchiveSampleToIValueFunction ARCH_SAMPLE_2_IVALUE_FUNC =
        new ArchiveSampleToIValueFunction();


    static final Logger LOG = CentralLogger.getInstance().getLogger(MySQLArchiveServiceImpl.class);

    private static ArchiveDaoManager DAO_MGR = ArchiveDaoManager.INSTANCE;


    /**
     * {@inheritDoc}
     */
    @Override
    public
    <V, T extends ITimedCssValueType<V> & IHasAlarm>
    boolean writeSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveServiceException {
        try {
            DAO_MGR.getSampleDao().createSamples(samples);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of samples failed.", e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public void writeMetaData(@Nonnull final String channelName,
                              @Nonnull final IValue sample) {
        // this meta data concept does not exist in this impl
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChannelId(@Nonnull final String name) throws ArchiveServiceException {

        IArchiveChannel channel;
        try {
            channel = DAO_MGR.getChannelDao().retrieveChannelByName(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel Id information for " + name +
                                              " could not be retrieved.", e);
        }
        if (channel == null) {
            throw new ArchiveServiceException("Channel Id information for " + name +
                                              " is null.", null);
        }
        return channel.getId().intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngine findEngine(@Nonnull final String name) throws ArchiveServiceException {
        try {
            return DAO_MGR.getEngineDao().retrieveEngineByName(name);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Engine information for " + name +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> getGroupsForEngine(@Nonnull final ArchiveEngineId id) throws ArchiveServiceException {
        try {
            return DAO_MGR.getChannelGroupDao().retrieveGroupsByEngineId(id);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Groups for engine " + id.asString() +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IArchiveChannel> getChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId) throws ArchiveServiceException {
        try {
            return DAO_MGR.getChannelDao().retrieveChannelsByGroupId(groupId);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channels for group " + groupId.asString() +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveSampleMode getSampleModeById(@Nonnull final ArchiveSampleModeId sampleModeId) throws ArchiveServiceException {
        try {
            return DAO_MGR.getSampleModeDao().retrieveSampleModeById(sampleModeId);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Sample modes for " + sampleModeId.asString() +
                                              " could not be retrieved.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void submitSample(final int channelId, @Nonnull final IValue value) throws ArchiveServiceException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flush() throws ArchiveServiceException {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMonitorModeInformation(@Nonnull final Collection<IArchiverMgmtEntry> monitorStates) throws ArchiveServiceException {
        try {
            DAO_MGR.getArchiverMgmtDao().createMgmtEntries(monitorStates);
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Creation of archiver management entry failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMonitorModeInformation(@Nonnull final IArchiverMgmtEntry entry) throws ArchiveServiceException {
        try {
            DAO_MGR.getArchiverMgmtDao().createMgmtEntry(entry);
      } catch (final ArchiveDaoException e) {
          throw new ArchiveServiceException("Creation of archiver management entry failed.", e);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Iterable<IValue> readSamples(@Nonnull final String channelName,
                                        @Nonnull final ITimestamp start,
                                        @Nonnull final ITimestamp end) throws ArchiveServiceException {
        return readSamples(channelName, start, end, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Iterable<IValue> readSamples(@Nonnull final String channelName,
                                        @Nonnull final ITimestamp start,
                                        @Nonnull final ITimestamp end,
                                        @Nullable final IArchiveRequestType type) throws ArchiveServiceException {

        try {
            final DesyArchiveRequestType desyType = validateRequestType(type);

            final TimeInstant s = BaseTypeConversionSupport.toTimeInstant(start);
            final TimeInstant e = BaseTypeConversionSupport.toTimeInstant(end);

            final IArchiveChannel channel = DAO_MGR.getChannelDao().retrieveChannelByName(channelName);
            if (channel == null) {
                throw new ArchiveDaoException("Information for channel " + channelName + " could not be retrieved.", null);
            }

            final Iterable<IArchiveMinMaxSample<Object, ITimedCssAlarmValueType<Object>>> samples =
                DAO_MGR.getSampleDao().retrieveSamples(desyType, channel, s, e);

            final Iterable<IValue> iValues =
                Iterables.filter(Iterables.transform(samples, ARCH_SAMPLE_2_IVALUE_FUNC),
                                 Predicates.<IValue>notNull());
            return iValues;

        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Sample retrieval failed.", ade);
        } catch (final RequestTypeParameterException re) {
            throw new ArchiveServiceException("Sample retrieval failed.", re);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestType> getRequestTypes() {
        return ImmutableSet.<IArchiveRequestType>builder().addAll(EnumSet.allOf(DesyArchiveRequestType.class)).build();
    }

    @CheckForNull
    private DesyArchiveRequestType validateRequestType(@CheckForNull final IArchiveRequestType type) throws RequestTypeParameterException {
        try {
            return DesyArchiveRequestType.class.cast(type);
        } catch(final ClassCastException cce) {
            throw new RequestTypeParameterException("Request type is not the correct type instance!" +
                                                    " Use one the type instances returned by the service interface or null", cce);
        }
    }
}
