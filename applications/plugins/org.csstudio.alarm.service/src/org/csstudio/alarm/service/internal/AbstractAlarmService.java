/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.service.internal;

import java.io.FileNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.dal.CssApplicationContext;
import org.csstudio.domain.common.collection.ChunkableCollection;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.remote.jms.command.RemoteCommandException;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
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
 * Implementation for Dal- and Jms-based services.
 * 
 * @author jpenning
 * @since 19.01.2012
 */
public abstract class AbstractAlarmService implements IAlarmService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAlarmService.class);
    
    private final List<IAlarmService.IListener> _listeners = new ArrayList<IAlarmService.IListener>();
    
    private ContentModel<LdapEpicsAlarmcfgConfiguration> _contentModel = null;
    private AtomicBoolean _requestReloadOfContentModel = new AtomicBoolean(true);
    
    public AbstractAlarmService() {
        if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue() || AlarmPreference.ALARMSERVICE_RUNS_AS_SERVER.getValue()) {
            registerCallbackAtRemoteCommandService();
        }
    }

    private void registerCallbackAtRemoteCommandService() {
        IRemoteCommandService service = ServiceLocator.getService(IRemoteCommandService.class);
        IRemoteCommandService.IListener listener = new IRemoteCommandService.IListener() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void receiveCommand(@Nonnull final String command) {
                for (IAlarmService.IListener updateListener : _listeners) {
                    if (IRemoteCommandService.ReloadFromLdapCommand.equals(command)) {
                        invalidateConfigurationCache();
                        updateListener.configurationUpdated();
                    } else if (IRemoteCommandService.Dal2JmsReloadedCommand.equals(command)) {
                        updateListener.alarmServerReloaded();
                    } else if (IRemoteCommandService.Dal2JmsStartedCommand.equals(command)) {
                        updateListener.alarmServerStarted();
                    } else if (IRemoteCommandService.Dal2JmsWillStopCommand.equals(command)) {
                        updateListener.alarmServerWillStop();
                    }
                }
            }
        };
        try {
            service.register(AlarmPreference.getClientGroup(), listener);
        } catch (RemoteCommandException e) {
            LOG.error("Could not register alarm service at remote command service. Remote update will not work.", e);
        }
    }
    
    @Override
    @Nonnull
    public synchronized ContentModel<LdapEpicsAlarmcfgConfiguration> getConfiguration() throws AlarmServiceException {
        LOG.trace("getConfiguration entered");
        if (_requestReloadOfContentModel.getAndSet(false)) {
            LOG.trace("getConfiguration will retrieve configuration");
            _contentModel = retrieveConfiguration();
            LOG.trace("getConfiguration finished retrieval of configuration");
        }
        return _contentModel;
    }
    
    @Override
    public void invalidateConfigurationCache() {
        _requestReloadOfContentModel.set(true);
    }

    @Nonnull
    private ContentModel<LdapEpicsAlarmcfgConfiguration> retrieveConfiguration() throws AlarmServiceException {
        final IAlarmConfigurationService configService = ServiceLocator
                .getService(IAlarmConfigurationService.class);
        
        if (configService == null) {
            String message = "Retrieval of alarm tree configuration failed. Alarm configuration service not available.";
            LOG.error(message);
            throw new AlarmServiceException(message);
        }
        
        ContentModel<LdapEpicsAlarmcfgConfiguration> model = null;
        
        try {
            if (AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue()) {
                LOG.trace("retrieve configuration from ldap");
                model = configService.retrieveInitialContentModel(AlarmPreference
                        .getFacilityNames());
            } else {
                LOG.trace("retrieve configuration from file");
                model = configService.retrieveInitialContentModelFromFile(AlarmPreference
                        .getConfigFilename());
            }
        } catch (FileNotFoundException e) {
            String message = "Opening File!\n" + "Could not properly open the input file stream: "
                    + e.getMessage();
            LOG.error(message, e);
            throw new AlarmServiceException(message, e);
        } catch (CreateContentModelException e) {
            String message = "Building content model!\n"
                    + "Could not properly build the content model from LDAP or XML: "
                    + e.getMessage();
            LOG.error(message, e);
            throw new AlarmServiceException(message, e);
        } catch (LdapServiceException e) {
            String message = "Accessing LDAP!\n" + "Internal service error: " + e.getMessage();
            LOG.error(message, e);
            throw new AlarmServiceException(message, e);
        }
        
        return model;
    }
    
    @Override
    @Nonnull
    public Set<String> getPvNames() throws AlarmServiceException {
        return getConfiguration().getSimpleNames(LdapEpicsAlarmcfgConfiguration.RECORD);
    }
    
    @Override
    public void register(@Nonnull final IListener listener) {
        _listeners.add(listener);
    }
    
    @Override
    public void deregister(@Nonnull final IListener listener) {
        _listeners.remove(listener);
    }
    
    @Override
    public final void retrieveInitialState(@Nonnull final List<IAlarmInitItem> initItems) throws AlarmServiceException {
        LOG.debug("retrieveInitialState for " + initItems.size() + " items");
        long start = System.currentTimeMillis();
        
        // There may be several thousand pvs for which the initial state is requested at once.
        // Therefore the process of registering is performed in chunks and resources are freed after each chunk.
        
        int pvChunkSize = getPvChunkSize();
        int pvChunkWaitMsec = getPvChunkWaitMsec();
        
        // Queue is filled with announced pvs
        final BlockingQueue<String> announcedPVsQ = new ArrayBlockingQueue<String>(2 * pvChunkSize);
        
        // initItems are processed in chunks
        final ChunkableCollection<IAlarmInitItem> chunkableCollection = new ChunkableCollection<IAlarmInitItem>(initItems,
                                                                                                                pvChunkSize);
        for (Collection<IAlarmInitItem> currentChunkOfPVs : chunkableCollection) {
            announcedPVsQ.clear();
            SimpleDALBroker broker = newSimpleDALBroker();
            LOG.debug("retrieveInitialState about to register " + currentChunkOfPVs.size() + " pvs");
            registerChunkOfPVs(broker, currentChunkOfPVs, announcedPVsQ);
            LOG.debug("retrieveInitialState about to process " + currentChunkOfPVs.size() + " pvs");
            waitForProcessingOrTimeout(pvChunkWaitMsec, announcedPVsQ, currentChunkOfPVs);
            LOG.debug("retrieveInitialState about to free resources");
            freeResources(broker);
        }
        LOG.debug("retrieveInitialState finished after " + (System.currentTimeMillis() - start)
                + " msec");
        
        if (AlarmPreference.ALARMSERVICE_LISTENS_TO_ALARMSERVER.getValue()) {
            retrieveAcknowledgeState(initItems);
        }
    }
    
    private void retrieveAcknowledgeState(@Nonnull final List<IAlarmInitItem> initItems) throws AlarmServiceException {
        IRemoteAcknowledgeService acknowledgeService = ServiceLocator
                .getService(IRemoteAcknowledgeService.class);
        if (acknowledgeService != null) {
            
            try {
                Collection<String> acknowledgedPvs = acknowledgeService.getAcknowledgedPvs();
                for (IAlarmInitItem item : initItems) {
                    if (acknowledgedPvs.contains(item.getPVName())) {
                        item.acknowledge();
                    }
                }
            } catch (RemoteException e) {
                LOG.error("Cannot get acknowledged PVs from server", e);
                // Tunneling of remote error
                throw new AlarmServiceException("No connection to acknowledge server", e);
            }
        } else {
            LOG.error("Cannot lookup acknowledge server");
            // Tunneling of remote error
            throw new AlarmServiceException("No connection to acknowledge server");
        }
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
    protected int getPvRegisterWaitMsec() {
        return AlarmPreference.ALARMSERVICE_PV_REGISTER_WAIT_MSEC.getValue();
    }
    
    // May be overridden in a test
    @Nonnull
    protected SimpleDALBroker newSimpleDALBroker() {
        return SimpleDALBroker.newInstance(new CssApplicationContext("CSS"));
    }
    
    private void registerChunkOfPVs(@Nonnull final SimpleDALBroker broker,
                                    @Nonnull final Collection<IAlarmInitItem> initItems,
                                    @Nonnull final BlockingQueue<String> queue) {
        for (final IAlarmInitItem initItem : initItems) {
            try {
                broker.registerListener(newConnectionParameters(initItem.getPVName()),
                                        new ChannelListenerImpl(queue, initItem));
            } catch (InstantiationException e) {
                LOG.error("Error registering pv '" + initItem.getPVName() + "'", e);
            } catch (CommonException e) {
                LOG.error("Error registering pv '" + initItem.getPVName() + "'", e);
            }
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
    
    private void waitFixedTime(final int delayInMsec) {
        try {
            Thread.sleep(delayInMsec);
        } catch (final InterruptedException e) {
            LOG.warn("retrieveInitialState was interrupted ", e);
        }
    }
    
    private void waitForProcessingOrTimeout(int pvChunkWaitMsec,
                                            @Nonnull final BlockingQueue<String> announcedPVsQ,
                                            @Nonnull final Collection<IAlarmInitItem> currentChunkOfPVs) {
        final long startTime = System.currentTimeMillis();
        final Set<String> remainingPVs = new HashSet<String>(currentChunkOfPVs.size());
        for (IAlarmInitItem initItem : currentChunkOfPVs) {
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
                proceed = (announcedPV != null) && !remainingPVs.isEmpty();
            } catch (InterruptedException e) {
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
    
    @Nonnull
    private RemoteInfo newRemoteInfo(@Nonnull final String pvName) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, null, null);
    }
    
    /**
     * Listen to incoming events delivering channels. Map channels to alarm messages and in turn deliver alarm init items.
     */
    private static final class ChannelListenerImpl implements ChannelListener {
        private static final Logger LOG_INNER = LoggerFactory.getLogger(ChannelListenerImpl.class);

        private final BlockingQueue<String> _queue;
        private final IAlarmInitItem _initItem;
        
        public ChannelListenerImpl(@Nonnull final BlockingQueue<String> queue,
                                   @Nonnull final IAlarmInitItem initItem) {
            _queue = queue;
            _initItem = initItem;
        }
        
        @Override
        public void channelDataUpdate(@Nonnull final AnyDataChannel channel) {
            try {
                LOG_INNER.trace("channelDataUpdate for " + channel.getUniqueName() + ": " + channel.getStateInfo());
            } catch (Exception e) {
                LOG_INNER.trace("channelDataUpdate: Access to channel failed");
            }
            // Nothing to do
        }
        
        @Override
        public void channelStateUpdate(@Nonnull final AnyDataChannel channel) {
            try {
                LOG_INNER.trace("channelStateUpdate for " + channel.getUniqueName() + ": " + channel.getStateInfo());
            } catch (Exception e) {
                LOG_INNER.trace("channelStateUpdate: Access to channel failed");
            }
            ConnectionState state = channel.getProperty().getConnectionState();
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
