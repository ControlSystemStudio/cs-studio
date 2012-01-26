/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.util.CommonException;

/**
 * JMS based implementation of the AlarmService.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmServiceJMSImpl implements IAlarmService {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmServiceJMSImpl.class);

    /**
     * Constructor.
     */
    public AlarmServiceJMSImpl() {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final IAlarmConnection newAlarmConnection() {
        return new AlarmConnectionJMSImpl();
    }

    @Override
    public final void retrieveInitialState(@Nonnull final List<IAlarmInitItem> initItems) {
        LOG.debug("retrieveInitialState for " + initItems.size() + " items");
        final long start = System.currentTimeMillis();

        // There may be several thousand pvs for which the initial state is requested at once.
        // Therefore the process of registering is performed in chunks and resources are freed after each chunk.

        final int pvChunkSize = getPvChunkSize();
        final int pvChunkWaitMsec = getPvChunkWaitMsec();

        // Queue is filled with announced pvs
        final BlockingQueue<String> announcedPVsQ = new ArrayBlockingQueue<String>(2 * pvChunkSize);

        // initItems are processed in chunks
        final ChunkableCollection<IAlarmInitItem> chunkableCollection = new ChunkableCollection<IAlarmInitItem>(initItems,
                                                                                                                pvChunkSize);
        for (final Collection<IAlarmInitItem> currentChunkOfPVs : chunkableCollection) {
            announcedPVsQ.clear();
            final SimpleDALBroker broker = newSimpleDALBroker();
            LOG.debug("retrieveInitialState about to register " + currentChunkOfPVs.size() + " pvs");
            registerChunkOfPVs(broker, currentChunkOfPVs, announcedPVsQ);
            LOG.debug("retrieveInitialState about to process " + currentChunkOfPVs.size() + " pvs");
            waitForProcessingOrTimeout(pvChunkWaitMsec, announcedPVsQ, currentChunkOfPVs);
            LOG.debug("retrieveInitialState about to free resources");
            freeResources(broker);
        }
        LOG.debug("retrieveInitialState finished after " + (System.currentTimeMillis() - start) + " msec");
    }

    // May be overridden in a test
    protected int getPvRegisterWaitMsec() {
        return AlarmPreference.ALARMSERVICE_PV_REGISTER_WAIT_MSEC.getValue();
    }

    // May be overridden in a test
    protected int getPvChunkWaitMsec() {
        return AlarmPreference.ALARMSERVICE_PV_CHUNK_WAIT_MSEC.getValue();
    }

    // May be overridden in a test
    protected int getPvChunkSize() {
        return AlarmPreference.ALARMSERVICE_PV_CHUNK_SIZE.getValue();
    }

    // May be overridden in a test
    @Nonnull
    protected SimpleDALBroker newSimpleDALBroker() {
        return SimpleDALBroker.newInstance(new CssApplicationContext("CSS"));
    }

    private void waitForProcessingOrTimeout(final int pvChunkWaitMsec,
                                            @Nonnull final BlockingQueue<String> announcedPVsQ,
                                            @Nonnull final Collection<IAlarmInitItem> currentChunkOfPVs) {
        final long startTime = System.currentTimeMillis();
        final Set<String> remainingPVs = new HashSet<String>(currentChunkOfPVs.size());
        for (final IAlarmInitItem initItem : currentChunkOfPVs) {
            remainingPVs.add(initItem.getPVName());
        }

        boolean proceed = true;
        while (proceed) {
            String announcedPV;
            try {
                announcedPV = announcedPVsQ.poll(pvChunkWaitMsec, TimeUnit.MILLISECONDS);
                if (announcedPV != null) {
                    remainingPVs.remove(announcedPV);
                } else {
                    LOG.debug("announcedPV was null");
                }
                proceed = announcedPV != null && !remainingPVs.isEmpty();
            } catch (final InterruptedException e) {
                LOG.debug("retrieveInitialState ended because of InterruptedException");
                proceed = false;
            }
        }
        logResult(startTime, currentChunkOfPVs.size(), remainingPVs.size());
    }

    private void logResult(final long startTime,
                           final int currentChunkOfPVsSize,
                           final int remainingPVsSize) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("processing time was "
                    + (System.currentTimeMillis() - startTime)
                    + ". "
                    + currentChunkOfPVsSize
                    + " PVs processed."
                    + (remainingPVsSize == 0 ? " All finished." : " Failure: " + remainingPVsSize
                            + " PVs unfinished."));
        }
    }

    @Override
    @Nonnull
    public final IAlarmResource createAlarmResource(@CheckForNull final List<String> topics,
                                                    @CheckForNull final String filepath) {
        return new AlarmResource(topics, filepath);
    }

    private void registerChunkOfPVs(@Nonnull final SimpleDALBroker broker,
                                    @Nonnull final Collection<IAlarmInitItem> initItems,
                                    @Nonnull final BlockingQueue<String> queue) {
        for (final IAlarmInitItem initItem : initItems) {
            try {
                broker.registerListener(newConnectionParameters(initItem.getPVName()),
                                        new ChannelListenerImpl(queue, initItem));
            } catch (final InstantiationException e) {
                LOG.error("Error registering pv '" + initItem.getPVName() + "'", e);
            } catch (final CommonException e) {
                LOG.error("Error registering pv '" + initItem.getPVName() + "'", e);
            }
        }
    }

    private void waitFixedTime(final int delayInMsec) {
        try {
            Thread.sleep(delayInMsec);
        } catch (final InterruptedException e) {
            LOG.warn("retrieveInitialState was interrupted ", e);
        }
    }

    private void freeResources(@Nonnull final SimpleDALBroker broker) {
        waitFixedTime(10);
        broker.releaseAll();
    }

    @Nonnull
    private ConnectionParameters newConnectionParameters(@Nonnull final String pvName) {
        // REVIEW (jpenning): hard coded type in connection parameter
        return new ConnectionParameters(newRemoteInfo(pvName), String.class);
    }

    @Nonnull
    private RemoteInfo newRemoteInfo(@Nonnull final String pvName) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, null, null);
    }

    /**
     * Listen to incoming events delivering channels. Map channels to alarm messages and in turn deliver alarm init items.
     */
    private static final class ChannelListenerImpl implements ChannelListener {
        private static final Logger LOG_INNER = LoggerFactory
                .getLogger(AlarmServiceJMSImpl.ChannelListenerImpl.class);

        private final BlockingQueue<String> _queue;
        private final IAlarmInitItem _initItem;

        public ChannelListenerImpl(@Nonnull final BlockingQueue<String> queue,
                                   @Nonnull final IAlarmInitItem initItem) {
            _queue = queue;
            _initItem = initItem;
        }

        @Override
        public void channelDataUpdate(@Nonnull final AnyDataChannel channel) {
            // Nothing to do
        }

        @Override
        public void channelStateUpdate(@Nonnull final AnyDataChannel channel) {
            final ConnectionState state = channel.getProperty().getConnectionState();
            if (state == ConnectionState.CONNECTION_FAILED) {
                processErroneousMessage(channel);
                _queue.offer(channel.getUniqueName());
            } else if (state == ConnectionState.OPERATIONAL) {
                processAlarmMessage(channel);
                _queue.offer(channel.getUniqueName());
            }
        }

        private void processErroneousMessage(@Nonnull final AnyDataChannel channel) {
            _initItem.notFound(channel.getUniqueName());
        }

        private void processAlarmMessage(@Nonnull final AnyDataChannel channel) {
            if (AlarmMessageDALImpl.canCreateAlarmMessageFrom(channel.getProperty(),
                                                              channel.getData())) {
                _initItem.init(AlarmMessageDALImpl.newAlarmMessage(channel.getProperty(),
                                                                   channel.getData()));
            } else {
                LOG_INNER.warn("Could not create alarm message for "
                        + channel.getProperty().getUniqueName());
                processErroneousMessage(channel);
            }
        }

    }

}
