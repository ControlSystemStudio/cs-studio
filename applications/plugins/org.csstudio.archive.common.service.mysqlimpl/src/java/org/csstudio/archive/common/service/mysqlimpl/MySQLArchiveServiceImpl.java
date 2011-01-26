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
import org.csstudio.archive.common.service.IArchiveRequestType;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.mysqlimpl.adapter.ArchiveEngineAdapter;
import org.csstudio.archive.common.service.mysqlimpl.adapter.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample2;
import org.csstudio.archive.common.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.common.service.samplemode.IArchiveSampleMode;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsCssValueTypeSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.ICssValueType;
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
            Function<IArchiveMinMaxSample<Object, ICssAlarmValueType<Object>, EpicsAlarm>, IValue> {
        /**
         * Constructor.
         */
        public ArchiveSampleToIValueFunction() {
            // Empty
        }

        @Override
         public IValue apply(final IArchiveMinMaxSample<Object, ICssAlarmValueType<Object>, EpicsAlarm> from) {
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

//    /**
//     * Converter function with email error.
//     *
//     * @author bknerr
//     * @since 20.12.2010
//     */
//    private static final class IValueWithId2ICssAlarmValueFunction implements
//            Function<IValueWithChannelId, IArchiveSample<ICssAlarmValueType<Object>, EpicsAlarm>> {
//        /**
//         * Constructor.
//         */
//        public IValueWithId2ICssAlarmValueFunction() {
//            // EMPTY
//        }
//
//        @SuppressWarnings("synthetic-access")
//        @Override
//        @CheckForNull
//        public IArchiveSample<ICssAlarmValueType<Object>, EpicsAlarm> apply(@Nonnull final IValueWithChannelId valWithId) {
//            try {
//                return ADAPT_MGR.adapt(valWithId);
//
//            } catch (final TypeSupportException e) {
//                final String msg = "Value for channel " + valWithId.getChannelId() + " could not be adapted. Sample not written!";
//                LOG.error(msg, e);
//                try {
//                    final EMailSender mailer =new EMailSender("smtp.desy.de",
//                                                              "archive.service@dontreply",
//                                                              "bastian.knerr@desy.de",
//                                                              msg);
//                    mailer.addText(e.getMessage() + "\n" + e.getCause());
//                    mailer.close();
//                } catch (final IOException ioe) {
//                    LOG.error("Closing of mailer for error message failed.", ioe);
//                }
//                return null;
//            }
//        }
//    }
//    private static final IValueWithId2ICssAlarmValueFunction IVALUE_2_CSS_VALUE_FUNC =
//        new IValueWithId2ICssAlarmValueFunction();

    static final Logger LOG = CentralLogger.getInstance().getLogger(MySQLArchiveServiceImpl.class);

    private static ArchiveDaoManager DAO_MGR = ArchiveDaoManager.INSTANCE;
    private static ArchiveEngineAdapter ADAPT_MGR = ArchiveEngineAdapter.INSTANCE;



    /**
     * {@inheritDoc}
     */
    @Override
    public
    <V, T extends ICssValueType<V> & IHasAlarm>
    boolean writeSamples(@Nonnull final Collection<IArchiveSample2<V, T>> samples) throws ArchiveServiceException {

        // FIXME (bknerr) : Get rid of this IValueWithChannelId class..., get rid of the mailer when tests exist
       //                   And apparently the type leads to Object instead of generic type...damn
        try {
//            final Collection<IArchiveSample<ICssAlarmValueType<Object>, EpicsAlarm>> sampleBeans =
//                Collections2.filter(Collections2.transform(samples, IVALUE_2_CSS_VALUE_FUNC),
//                                    Predicates.<IArchiveSample<ICssAlarmValueType<Object>, EpicsAlarm>>notNull());

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
    public void writeMetaData(@Nonnull final String channelName, final IValue sample) {
        // FIXME (bknerr) : check the conception of meta data coming out of caj,jca or whatever

        // Metadata are partly themselves record fields (for numerics), which might be again
        // configured and registered channels, hence having channel configurations that then have
        // again meta data and so on.
        //
        // Question:
        // could we treat all channels = 'record fields' exactly the same in the archiver, just as samples in the sample table
        // and let the archive reading clients handle the relations between the record fields (whether they
        // belong to the 'same' record or influence each other in any way is only of interest for the archive reading
        // tool not for the archive).
        // How about that:
        // Consider making the channel id in the rdb split into two columns, record and field.
        // Hence, asking about a channel's VAL samples, e.g. <record>.<field>=kryoBox.VAL can easily be
        // modified by the client to ask additionally, if channelType of kryoBox.VAL is numeric, get the samples for
        // channel kryoBox.deadband, kryoBox.HIHI and kryoBox.LOLO or how these are called. That can
        // be called meta data or whatever. But the archivereader wouldn't notice any difference, and just
        // deliver type safe sample collections.
        //
        // Sidenote; it is envisioned to have several control systems. Hence record and field might not
        // be appropriate. Generify this idea.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ITimestamp getLatestTimestampForChannel(@Nonnull final String name) throws ArchiveServiceException {

        IArchiveChannel channel = null;
        try {
            channel = DAO_MGR.getChannelDao().retrieveChannelByName(name);
            if (channel != null) {
                return ADAPT_MGR.adapt(channel.getLatestTimestamp());
            }
            return null;
        } catch (final ArchiveDaoException e) {
            throw new ArchiveServiceException("Channel information could not be retrieved.", e);
        }
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
    public Collection<IArchiveChannelGroup> getGroupsForEngine(final ArchiveEngineId id) throws ArchiveServiceException {
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
    public Collection<IArchiveChannel> getChannelsByGroupId(final ArchiveChannelGroupId groupId) throws ArchiveServiceException {
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
    public IArchiveSampleMode getSampleModeById(final ArchiveSampleModeId sampleModeId) throws ArchiveServiceException {
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
    public void submitSample(final int channelId, final IValue value) throws ArchiveServiceException {
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
    @Nonnull
    public Iterable<IValue> readSamples(final String channelName,
                                        final ITimestamp start,
                                        final ITimestamp end) throws ArchiveServiceException {
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

        final TimeInstant s = BaseTypeConversionSupport.toTimeInstant(start);
        final TimeInstant e = BaseTypeConversionSupport.toTimeInstant(end);

        try {
            final IArchiveChannel channel = DAO_MGR.getChannelDao().retrieveChannelByName(channelName);
            if (channel == null) {
                throw new ArchiveDaoException("Information for channel " + channelName + " could not be retrieved.", null);
            }

            final Iterable<IArchiveMinMaxSample<Object, ICssAlarmValueType<Object>, EpicsAlarm>> samples =
                DAO_MGR.getSampleDao().retrieveSamples(type, channel, s, e);

            final Iterable<IValue> iValues =
                Iterables.filter(Iterables.transform(samples, ARCH_SAMPLE_2_IVALUE_FUNC),
                                 Predicates.<IValue>notNull());
            return iValues;

        } catch (final IllegalArgumentException iae) {
            throw new ArchiveServiceException("Sample retrieval failed. Unsupported archive request type " + type.getTypeIdentifier(), iae);
        } catch (final ArchiveDaoException ade) {
            throw new ArchiveServiceException("Sample retrieval failed.", ade);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ImmutableSet<IArchiveRequestType> getRequestTypes() {
        return ImmutableSet.<IArchiveRequestType>builder().addAll(EnumSet.allOf(ArchiveRequestType.class)).build();
    }

}
