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
package org.csstudio.saverestore.masar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelProviderRegistryFactory;
import org.epics.pvaccess.client.ChannelRPC;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.util.logging.LoggingUtils;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.Status.StatusType;
import org.epics.pvdata.pv.StringArrayData;

/**
 * <code>MasarClient<code> provide access to the masar features required by the save and restore application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class MasarClient {

    private static class MasarChannelRequester implements ChannelRequester {

        private final CompletionNotifier notifier;
        private ConnectionState lastConnectionState = ConnectionState.NEVER_CONNECTED;

        public MasarChannelRequester(CompletionNotifier notifier, boolean neverConnected) {
            this.notifier = notifier;
            if (neverConnected) {
                lastConnectionState = ConnectionState.NEVER_CONNECTED;
            } else {
                lastConnectionState = ConnectionState.CONNECTED;
            }
        }

        @Override
        public String getRequesterName() {
            return getClass().getName();
        }

        @Override
        public void message(String message, MessageType messageType) {
            SaveRestoreService.LOGGER.log(LoggingUtils.toLevel(messageType), message);
        }

        @Override
        public void channelCreated(Status status, Channel channel) {
            SaveRestoreService.LOGGER.log(Level.INFO, "Channel {0} created with status {1}.",
                new Object[] { channel.getChannelName(), status });
        }

        @Override
        public void channelStateChange(Channel channel, ConnectionState connectionState) {
            SaveRestoreService.LOGGER.log(Level.INFO, "State of channel {0} channel to {1}.",
                new Object[] { channel.getChannelName(), connectionState });
            if (connectionState == ConnectionState.CONNECTED) {
                if (lastConnectionState == ConnectionState.NEVER_CONNECTED && notifier != null) {
                    //this is to bring up a service which was disconnected at start up time, but became online later
                    notifier.synchronised();
                }
                //if not working, reconnect will happen at the next request
//                else if (lastConnectionState != ConnectionState.CONNECTED) {
//                    try {
//                        client.connect(true);
//                    } catch (MasarException e) {
//                        SaveRestoreService.LOGGER.log(Level.SEVERE, "Cannot reconnect to masar service", e);
//                    }
//                }
            }
            lastConnectionState = connectionState;
        }
    }

    private static class MasarChannelRPCRequester implements RPCRequester {
        private final CountDownLatch connectedSignaler = new CountDownLatch(1);
        private final Semaphore doneSemaphore = new Semaphore(0);
        private final String service;
        private final Channel channel;
        private volatile ChannelRPC channelRPC;
        private volatile PVStructure result;

        MasarChannelRPCRequester(String service, Channel channel) {
            this.service = service;
            this.channel = channel;
        }

        @Override
        public String getRequesterName() {
            return getClass().getName();
        }

        @Override
        public void message(String message, MessageType messageType) {
            SaveRestoreService.LOGGER.log(LoggingUtils.toLevel(messageType), message);
        }

        @Override
        public void channelRPCConnect(Status status, ChannelRPC channelRPC) {
            SaveRestoreService.LOGGER.log(Level.INFO, "ChannelRPC for {0} connected with status {1}.",
                new Object[] { channelRPC.getChannel().getChannelName(), status });
            boolean reconnect = this.channelRPC != null;
            this.channelRPC = channelRPC;
            connectedSignaler.countDown();
            // in case of reconnect, issued request was lost
            if (reconnect) {
                this.result = null;
                doneSemaphore.release();
            }
        }

        @Override
        public boolean waitUntilConnected() throws InterruptedException {
            return connectedSignaler.await(Activator.getInstance().getConnectionTimeout(), TimeUnit.SECONDS)
                && channelRPC != null;
        }

        @Override
        public void requestDone(Status status, ChannelRPC channelRPC, PVStructure result) {
            if (status.getType() != StatusType.OK) {
                SaveRestoreService.LOGGER.log(Level.WARNING, "RequestDone for {0} called with status {1}.",
                    new Object[] { channelRPC.getChannel().getChannelName(), status });
            }
            this.result = result;
            doneSemaphore.release();
        }

        @Override
        public PVStructure request(PVStructure requestData) throws InterruptedException, MasarException {
            ChannelRPC rpc = channelRPC;
            if (rpc == null) {
                throw new MasarException("Cannot connect to the MASAR service " + service + ".");
            }
            this.result = null;
            rpc.request(requestData);
            if (Activator.getInstance().getTimeout() > 0) {
                if (!doneSemaphore.tryAcquire(1, Activator.getInstance().getTimeout(), TimeUnit.SECONDS)) {
                    throw new MasarException("Timeout sending request to " + service + ".");
                }
            } else {
                doneSemaphore.acquire(1);
            }
            return result;
        }

        @Override
        public void destroy() {
            if (channelRPC != null) {
                channelRPC.destroy();
            }
            if (channel != null) {
                channel.destroy();
            }
        }

        @Override
        public boolean isConnected() {
            return channelRPC != null && channelRPC.getChannel().isConnected();
        }
    }

    private static RPCRequester createChannel(String service, CompletionNotifier notifier,
        boolean neverConnected) throws MasarException {
        if (service == null) {
            throw new MasarException("No service name provided.");
        }
        org.epics.pvaccess.ClientFactory.start();

        ChannelProvider channelProvider = ChannelProviderRegistryFactory.getChannelProviderRegistry()
            .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);

        MasarChannelRequester channelRequester = new MasarChannelRequester(notifier, neverConnected);
        Channel channel = channelProvider.createChannel(service, channelRequester, ChannelProvider.PRIORITY_DEFAULT);

        MasarChannelRPCRequester channelRPCRequester = new MasarChannelRPCRequester(service, channel);
        channel.createChannelRPC(channelRPCRequester, null);
        return channelRPCRequester;
    }

    private String[] services;
    private String selectedService;
    private RPCRequester channelRPCRequester;
    private CompletionNotifier connectionNotifier;

    /**
     * Creates a new client, but does not initialise it. {@link #initialise(String[])} has to be called before anything
     * can be done with this client.
     */
    public MasarClient() {
        // default constructor to allow extensions

    }

    /**
     * Construct a new client and initialise it using the provided parameters.
     *
     * @param services the list of available masar services
     * @param notifier the notifier which is notified when masar service comes online
     * @throws MasarException in case of an error
     */
    public MasarClient(String[] services, CompletionNotifier notifier) throws MasarException {
        initialise(services, notifier);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    /**
     * Dispose of all resources allocated by this client (e.g. close the channels).
     */
    public void dispose() {
        try {
            if (channelRPCRequester != null) {
                channelRPCRequester.destroy();
                channelRPCRequester = null;
            }
        } catch (Exception e) {
            SaveRestoreService.LOGGER.log(Level.SEVERE, "MASAR Client cleanup error.", e);
        }
    }

    /**
     * Initialises this MASAR client by setting up the available services and connecting to either the selected service
     * or the first available service in the list.
     *
     * @param services the list of available services
     * @param notifier which is notified when the service is connected
     * @return true if successfully initialised or false otherwise
     * @throws MasarException in case of an error
     */
    public synchronized boolean initialise(String[] services, CompletionNotifier notifier) throws MasarException {
        this.services = new String[services.length];
        this.connectionNotifier = notifier;
        System.arraycopy(services, 0, this.services, 0, services.length);
        dispose();
        if (this.services.length > 0) {
            findService: {
                if (this.selectedService != null && !this.selectedService.isEmpty()) {
                    for (String s : this.services) {
                        if (this.selectedService.equals(s)) {
                            break findService;
                        }
                    }
                }
                this.selectedService = this.services[0];
            }
            return connect(true);
        }
        return false;
    }

    /**
     * Switch the working service to the given service. If the current service is already the requested service, nothing
     * happens.
     *
     * @param branch the service to switch to
     * @throws MasarException if there was an exception selecting the service (e.g. service unavailable)
     */
    public synchronized void setService(Branch service) throws MasarException {
        if (!service.getShortName().equals(selectedService) && (selectedService == null || !service.isDefault())) {
            selectedService = service.getShortName();
            connect(true);
        }
    }

    private boolean connect() throws MasarException {
        return connect(false);
    }

    private boolean connect(boolean neverConnected) throws MasarException {
        dispose();
        channelRPCRequester = createChannel(selectedService, connectionNotifier, neverConnected);
        try {
            return channelRPCRequester.waitUntilConnected();
        } catch (InterruptedException e) {
            throw new MasarException("Could not connecto to masar service", e);
        }
    }

    /**
     * Tries to connect to the given service. If successful the new service is selected and the Branch describing the
     * service is returned. The new service is also added to the list of available services.
     *
     * @param newBranch the name of the new service
     * @return branch describing this service if the service exists
     * @throws MasarException in case of unreachable service
     */
    public synchronized Branch createService(String newService) throws MasarException {
        if (Arrays.asList(services).contains(newService)) {
            throw new MasarException("Service '" + newService + "' already exists.");
        }
        RPCRequester channel = createChannel(newService, null, true);
        boolean connected = false;
        try {
            connected = channel.waitUntilConnected();
        } catch (InterruptedException e) {
            // ignore
        }
        if (connected) {
            dispose();
            this.channelRPCRequester = channel;
            this.selectedService = newService;
            List<String> newServices = new ArrayList<>(this.services.length + 1);
            newServices.add(newService);
            for (String s : services) {
                newServices.add(s);
            }
            this.services = newServices.toArray(new String[newServices.size()]);
            Activator.getInstance().setServices(services);
            return new Branch(newService, newService);
        }
        throw new MasarException("Service '" + newService + "' is unreachable.");
    }

    /**
     * Returns the list of all services (branches).
     *
     * @return the list of services (branches)
     */
    public synchronized List<Branch> getServices() {
        List<Branch> branches = new ArrayList<>(Arrays.asList(services)).stream().map(s -> new Branch(s, s))
            .collect(Collectors.toList());
        Collections.sort(branches);
        return branches;
    }

    /**
     * Reads and returns the list of all system configs in the given service.
     *
     * @param service the service from which to retrieve base levels
     * @return the list of system configurations levels
     * @throws MasarException in case of an error
     */
    public synchronized List<BaseLevel> getSystemConfigs(Branch service) throws MasarException {
        return getSystemConfigs(service, true);
    }

    /**
     * Reads and returns the list of all system configs (base levels) in the given service.
     *
     * @param service the service from which to retrieve the system configs
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the list of system configs
     * @throws MasarException in case of an error
     */
    private List<BaseLevel> getSystemConfigs(Branch service, boolean retryOnError) throws MasarException {
        setService(service);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BASE_LEVEL);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_BASE_LEVELS);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return getSystemConfigs(service, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            PVStringArray array = (PVStringArray) value.getScalarArrayField(MasarConstants.P_BASE_LEVEL_NAME,
                ScalarType.pvString);
            StringArrayData data = new StringArrayData();
            int l = array.get(0, array.getLength(), data);
            Set<BaseLevel> bls = new HashSet<>(l);
            for (String s : data.data) {
                bls.add(new BaseLevel(service, s, s));
            }
            List<BaseLevel> list = new ArrayList<>(bls);
            Collections.sort(list);
            list.add(0, new BaseLevel(service, "all", "all"));
            return list;
        } catch (InterruptedException e) {
            throw new MasarException("Loading system configurations aborted.", e);
        }
    }

    /**
     * Returns the list of all available save sets in the current service.
     *
     * @param baseLevel the base level for which the save sets are requested (optional, if base levels are not used)
     * @param service the service to switch to
     * @return the list of save sets
     * @throws MasarException in case of an error
     */
    public synchronized List<SaveSet> getSaveSets(Optional<BaseLevel> baseLevel, Branch service)
        throws MasarException {
        return getSaveSets(baseLevel, service, true);
    }

    /**
     * Returns the list of all available save sets in the current branch.
     *
     * @param baseLevel the base level for which the save sets are requested (optional, if base levels are not used)
     * @param service the service to switch to
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the list of save sets
     * @throws MasarException in case of an error
     */
    private List<SaveSet> getSaveSets(Optional<BaseLevel> baseLevel, Branch service, boolean retryOnError)
        throws MasarException {
        setService(service);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SAVE_SET);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SAVE_SETS);
            request.getStringField(MasarConstants.F_SYSTEM).put(baseLevel.get().getStorageName());
            request.getStringField(MasarConstants.F_CONFIGNAME).put("*");
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return getSaveSets(baseLevel, service, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }

            return MasarUtilities.createSaveSetsList(result, service, baseLevel);
        } catch (InterruptedException e) {
            throw new MasarException("Loading save sets aborted.", e);
        }
    }

    /**
     * Search for snapshots that match the given criteria. Snapshot is accepted if the search is performed by user or by
     * comment and expression is found in either the snapshot comment or creator. The snapshot also has to be created
     * after start and before end if those two parameters are provided.
     *
     * @param service the service on which to search
     * @param expression the expression to search for
     * @param byUser true if the username should match the expression
     * @param byComment true if the comment should match the expression
     * @param start the start date of the time range to search
     * @param end the end date of the time range to search
     * @return list of snapshots that match criteria
     * @throws MasarException in case of an error
     * @throws ParseException in case that the returned timestamp could not be parsed
     */
    public synchronized List<Snapshot> findSnapshots(Branch service, String expression, boolean byUser,
        boolean byComment, Optional<Date> start, Optional<Date> end) throws MasarException, ParseException {
        return findSnapshots(service, expression, byUser, byComment, start, end, true);
    }

    /**
     * Search for snapshots that match the given criteria. Snapshot is accepted if the search is performed by user or by
     * comment and expression is found in either the snapshot comment or creator. The snapshot also has to be created
     * after start and before end if those two parameters are provided.
     *
     * @param service the service on which to search
     * @param expression the expression to search for
     * @param byUser true if the username should match the expression
     * @param byComment true if the comment should match the expression
     * @param start the start date of the time range to search
     * @param end the end date of the time range to search
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return list of snapshots that match criteria
     * @throws MasarException in case of an error
     * @throws ParseException in case that the returned timestamp could not be parsed
     */
    private List<Snapshot> findSnapshots(Branch service, String expression, boolean byUser, boolean byComment,
        Optional<Date> start, Optional<Date> end, boolean retryOnError) throws MasarException, ParseException {
        setService(service);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(
                MasarConstants.createSearchStructure(true, true, start.isPresent(), end.isPresent()));
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_FIND_SNAPSHOTS);
            request.getStringField(MasarConstants.F_COMMENT).put("*");
            request.getStringField(MasarConstants.F_USER).put("*");
            String newExpression = new StringBuilder(expression.length() + 2).append('*').append(expression).append('*')
                .toString();
            if (byComment) {
                request.getStringField(MasarConstants.F_COMMENT).put(newExpression);
            }
            if (byUser) {
                request.getStringField(MasarConstants.F_USER).put(newExpression);
            }
            if (start.isPresent()) {
                request.getStringField(MasarConstants.F_START)
                    .put(MasarConstants.DATE_FORMAT.get().format(start.get()));
            }
            if (end.isPresent()) {
                request.getStringField(MasarConstants.F_END).put(MasarConstants.DATE_FORMAT.get().format(end.get()));
            }
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return findSnapshots(service, expression, byUser, byComment, start, end, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            return MasarUtilities.createSnapshotsList(value, s -> new SaveSet(service, Optional.empty(),
                new String[] { "Save Set: " + s }, MasarDataProvider.ID));
        } catch (InterruptedException e) {
            throw new MasarException("Searching snapshots aborted.", e);
        }
    }

    /**
     * Finds the snapshot that has the given id. If the snapshot was not found an empty optional is returned.
     *
     * @param service the service on which to search for the snapshot
     * @param id the snapshot id
     * @return the snapshot if found
     * @throws ParseException if the snapshot date could not be parsed
     * @throws MasarException in case of an error
     */
    public synchronized Optional<Snapshot> findSnapshotById(Branch service, int id)
        throws MasarException, ParseException {
        return findSnapshotById(service, id, true);
    }

    /**
     * Finds the snapshot that has the given id. If the snapshot was not found an empty optional is returned.
     *
     * @param service the service on which to search for the snapshot
     * @param id the snapshot id
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the snapshot if found
     * @throws ParseException if the snapshot date could not be parsed
     * @throws MasarException in case of an error
     */
    private Optional<Snapshot> findSnapshotById(Branch service, int id, boolean retryOnError)
        throws MasarException, ParseException {
        setService(service);
        try {
            String index = String.valueOf(id);
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_BY_ID);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_FIND_SNAPSHOTS);
            request.getStringField(MasarConstants.F_EVENTID).put(index);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return findSnapshotById(service, id, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            List<Snapshot> list = MasarUtilities.createSnapshotsList(value, s -> new SaveSet(service,
                Optional.empty(), new String[] { "Save Set: " + s }, MasarDataProvider.ID));
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(list.get(0));
            }
        } catch (InterruptedException e) {
            throw new MasarException("Searching snapshots aborted.", e);
        }
    }

    /**
     * Returns the list of all snapshots for the given save set.
     *
     * @param saveSet the save set for which the snapshots are requested
     * @return the list of all snapshot revisions for this save set
     * @throws MasarException in case of an error
     * @throws ParseException if parsing of date failed
     */
    public synchronized List<Snapshot> getSnapshots(SaveSet saveSet) throws MasarException, ParseException {
        return getSnapshots(saveSet, true);
    }

    /**
     * Returns the list of all snapshots for the given save set.
     *
     * @param saveSet the save set for which the snapshots are requested
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the list of all snapshot revisions for this save set
     * @throws MasarException in case of an error
     * @throws ParseException if parsing of date failed
     */
    private List<Snapshot> getSnapshots(SaveSet saveSet, boolean retryOnError)
        throws MasarException, ParseException {
        setService(saveSet.getBranch());
        try {
            PVStructure request;
            String index = saveSet.getParameters().get(MasarConstants.P_CONFIG_INDEX);
            if (index == null) {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BASE_LEVEL);
            } else {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SNAPSHOT);
                request.getStringField(MasarConstants.F_CONFIGID).put(index);
            }
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SNAPSHOTS);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return getSnapshots(saveSet, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            return MasarUtilities.createSnapshotsList(value, s -> saveSet);
        } catch (InterruptedException e) {
            throw new MasarException("Loading snapshots aborted.", e);
        }
    }

    /**
     * Loads the data from the snapshot revision.
     *
     * @param snapshot the snapshot descriptor to read
     * @return the content of the snapshot
     * @throws MasarException in case of an error
     */
    public synchronized VSnapshot loadSnapshotData(Snapshot snapshot) throws MasarException {
        return loadSnapshotData(snapshot, true);
    }

    /**
     * Loads the data from the snapshot revision.
     *
     * @param snapshot the snapshot descriptor to read
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the content of the snapshot
     * @throws MasarException in case of an error
     */
    private VSnapshot loadSnapshotData(Snapshot snapshot, boolean retryOnError) throws MasarException {
        setService(snapshot.getSaveSet().getBranch());
        try {
            String index = snapshot.getParameters().get(MasarConstants.PARAM_SNAPSHOT_ID);
            if (index == null) {
                index = snapshot.getParameters().get(MasarConstants.P_EVENT_ID);
            }
            if (index == null) {
                throw new MasarException("Unknown snapshot: " + snapshot);
            }
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_DATA);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SNAPSHOT_DATA);
            request.getStringField(MasarConstants.F_EVENTID).put(index);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return loadSnapshotData(snapshot, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            return MasarUtilities.resultToVSnapshot(result, snapshot, Timestamp.of(snapshot.getDate()));
        } catch (InterruptedException e) {
            throw new MasarException("Loading snapshots data aborted.", e);
        }
    }

    /**
     * Signal to the service that this snapshot should be stored permanently.
     *
     * @param snapshot the snapshot data
     * @param comment the comment for the commit
     * @return saved snapshot
     * @throws MasarException in case of an error
     */
    public synchronized VSnapshot saveSnapshot(VSnapshot snapshot, String comment) throws MasarException {
        return saveSnapshot(snapshot, comment, true);
    }

    /**
     * Signal to the service that this snapshot should be stored permanently.
     *
     * @param snapshot the snapshot data
     * @param comment the comment for the commit
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return saved snapshot
     * @throws MasarException in case of an error
     */
    private VSnapshot saveSnapshot(VSnapshot snapshot, String comment, boolean retryOnError) throws MasarException {
        setService(snapshot.getSaveSet().getBranch());
        try {
            if (!snapshot.getSnapshot().isPresent()) {
                throw new MasarException("Snapshot " + snapshot + " cannot be saved by MASAR.");
            }
            String id = snapshot.getSnapshot().get().getParameters().get(MasarConstants.PARAM_SNAPSHOT_ID);
            if (id == null) {
                id = snapshot.getSnapshot().get().getParameters().get(MasarConstants.P_EVENT_ID);
            }
            if (id == null) {
                throw new MasarException("Snapshot " + snapshot + " is not a valid MASAR snapshot.");
            }
            String user = MasarUtilities.getUser();
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_SAVE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_SAVE_SNAPSHOT);
            request.getStringField(MasarConstants.F_EVENTID).put(id);
            request.getStringField(MasarConstants.F_USER).put(user);
            request.getStringField(MasarConstants.F_DESCRIPTION).put(comment);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return saveSnapshot(snapshot, comment, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVBoolean status = (PVBoolean) result.getBooleanField(MasarConstants.P_STRUCTURE_VALUE);
            if (!status.get()) {
                // masar returns status=false, error description is given in the alarm message
                PVStructure alarm = result.getStructureField(MasarConstants.P_ALARM);
                String message = alarm.getStringField(MasarConstants.P_MESSAGE).get();
                throw new MasarResponseException(message);
            }
            Snapshot newSnap = snapshot.getSnapshot().get();
            Date date = newSnap.getDate();
            if (date == null) {
                date = snapshot.getTimestamp().toDate();
            }
            newSnap = new Snapshot(newSnap.getSaveSet(), date, comment, user, newSnap.getParameters(),
                newSnap.getPublicParameters());
            return new VSnapshot(newSnap, snapshot.getNames(), snapshot.getSelected(), snapshot.getValues(),
                snapshot.getReadbackNames(), snapshot.getReadbackValues(), snapshot.getDeltas(),
                snapshot.getTimestamp());
        } catch (InterruptedException e) {
            throw new MasarException("Saving snapshot aborted.", e);
        }
    }

    /**
     * Take a new snapshot for the given save set and return it.
     *
     * @param set the save set for which the snapshot will be taken
     * @return saved snapshot and change type describing what kind of updates were made to the repository
     * @throws MasarException in case of an error
     */
    public synchronized VSnapshot takeSnapshot(SaveSet set) throws MasarException {
        return takeSnapshot(set, true);
    }

    /**
     * Take a new snapshot for the given save set and return it.
     *
     * @param set the save set for which the snapshot will be taken
     * @param retryOnError if true and there is a communication error the channel will be reconnected and request sent
     *            again
     * @return saved snapshot and change type describing what kind of updates were made to the repository
     * @throws MasarException in case of an error
     */
    private VSnapshot takeSnapshot(SaveSet set, boolean retryOnError) throws MasarException {
        setService(set.getBranch());
        try {
            String name = set.getParameters().get(MasarConstants.P_CONFIG_NAME);
            if (name == null) {
                throw new MasarException("Unknown save set: " + set);
            }
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_TAKE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_TAKE_SNAPSHOT);
            // request.getStringField(MasarConstants.F_SERVICENAME).put(set.getBranch().getShortName());
            request.getStringField(MasarConstants.F_CONFIGNAME).put(name);

            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return takeSnapshot(set, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            if (result.getScalarArrayField(MasarConstants.P_SNAPSHOT_IS_CONNECTED, ScalarType.pvBoolean) == null) {
                // if there was an error masar does not return anything but the alarm and timestamp,
                // error description is given in the alarm message
                PVStructure alarm = result.getStructureField(MasarConstants.P_ALARM);
                String message = alarm.getStringField(MasarConstants.P_MESSAGE).get();
                throw new MasarResponseException(message);
            }
            PVStructure timestamp = result.getStructureField(MasarConstants.P_TIMESTAMP);
            long sec = timestamp.getLongField(MasarConstants.P_SECONDS).get();
            int nano = timestamp.getIntField(MasarConstants.P_NANOS).get();
            int id = timestamp.getIntField(MasarConstants.P_USER_TAG).get();
            Map<String, String> parameters = new HashMap<>();
            parameters.put(MasarConstants.PARAM_SNAPSHOT_ID, String.valueOf(id));
            Snapshot snapshot = new Snapshot(set, null, null, null, parameters,
                Arrays.asList(MasarConstants.PARAM_SNAPSHOT_ID));
            return MasarUtilities.resultToVSnapshot(result, snapshot, Timestamp.of(sec, nano));
        } catch (InterruptedException e) {
            throw new MasarException("Taking snapshot aborted.", e);
        }
    }

    /**
     * Loads the save set data by trying to read the contents from one of the snapshot for this save set. If no
     * snapshot exists, one is taken and parsed. The snapshot that is taken is never saved.
     *
     * @param set the save set for which the content is being loaded
     * @return the save set data
     * @throws MasarException in case of an error
     * @throws ParseException if an existing snapshot was being parsed and failed to read the timestamp
     */
    public synchronized SaveSetData loadSaveSetData(SaveSet set) throws MasarException, ParseException {
        setService(set.getBranch());
        List<Snapshot> snapshots = getSnapshots(set);
        VSnapshot snapshot;
        if (snapshots.isEmpty()) {
            snapshot = takeSnapshot(set);
        } else {
            snapshot = loadSnapshotData(snapshots.get(0));
        }
        return new SaveSetData(set, snapshot.getNames(), null, null, null);
    }
}
