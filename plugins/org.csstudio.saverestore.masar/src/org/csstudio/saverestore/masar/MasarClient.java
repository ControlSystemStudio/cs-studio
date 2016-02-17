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
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
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
        private final MasarClient client;
        private ConnectionState lastConnectionState = ConnectionState.NEVER_CONNECTED;

        public MasarChannelRequester(MasarClient client, CompletionNotifier notifier) {
            this.notifier = notifier;
            this.client = client;
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
                    notifier.synchronised();
                } else {
                    try {
                        client.connect();
                    } catch (MasarException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE, "Cannot reconnect to masar service", e);
                    }
                }
            }
            lastConnectionState = connectionState;
        }
    }

    private static class MasarChannelRPCRequester implements RPCRequester {
        private final CountDownLatch connectedSignaler = new CountDownLatch(1);
        private final Semaphore doneSemaphore = new Semaphore(0);
        private final String service;
        private volatile ChannelRPC channelRPC;
        private volatile PVStructure result;

        MasarChannelRPCRequester(String service) {
            this.service = service;
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
            return connectedSignaler.await(Activator.getInstance().getTimeout(), TimeUnit.SECONDS)
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
            if (doneSemaphore.tryAcquire(1, Activator.getInstance().getTimeout(), TimeUnit.SECONDS)) {
                return result;
            } else {
                throw new MasarException("Error sending request to masar service. Timeout occurred.");
            }
        }

        @Override
        public void destroy() {
            if (channelRPC != null) {
                channelRPC.destroy();
            }
        }

        @Override
        public boolean isConnected() {
            return channelRPC != null && channelRPC.getChannel().isConnected();
        }
    }

    private static RPCRequester createChannel(String service, MasarClient client, CompletionNotifier notifier)
        throws MasarException {
        if (service == null) {
            throw new MasarException("No service name provided.");
        }
        org.epics.pvaccess.ClientFactory.start();

        ChannelProvider channelProvider = ChannelProviderRegistryFactory.getChannelProviderRegistry()
            .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);

        MasarChannelRequester channelRequester = new MasarChannelRequester(client, notifier);
        Channel channel = channelProvider.createChannel(service, channelRequester, ChannelProvider.PRIORITY_DEFAULT);

        MasarChannelRPCRequester channelRPCRequester = new MasarChannelRPCRequester(service);
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
            return connect();
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
            connect();
        }
    }

    private boolean connect() throws MasarException {
        dispose();
        channelRPCRequester = createChannel(selectedService, this, connectionNotifier);
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
        RPCRequester channel = createChannel(newService, this, null);
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
     * Reads and returns the list of all base levels in the given service.
     *
     * @param service the service from which to retrieve base levels
     * @return the list of base levels
     * @throws MasarException in case of an error
     */
    public synchronized List<BaseLevel> getBaseLevels(Branch service) throws MasarException {
        setService(service);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BASE_LEVEL);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_BASE_LEVELS);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
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
            throw new MasarException("Error loading base levels.", e);
        }
    }

    /**
     * Returns the list of all available beamline sets in the current branch. The search is done by reading the data on
     * the file system, not by searching the git repository.
     *
     * @param baseLevel the base level for which the beamline sets are requested (optional, if base levels are not used)
     * @param branch the branch to switch to
     * @return the list of beamline sets
     * @throws MasarException in case of an error
     */
    public synchronized List<BeamlineSet> getBeamlineSets(Optional<BaseLevel> baseLevel, Branch branch)
        throws MasarException {
        setService(branch);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BEAMLINE_SET);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_BEAMLINE_SETS);
            request.getStringField(MasarConstants.F_SYSTEM).put(baseLevel.get().getStorageName());
            request.getStringField(MasarConstants.F_CONFIGNAME).put("*");
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }

            return MasarUtilities.createBeamlineSetsList(result, branch, baseLevel);
        } catch (InterruptedException e) {
            throw new MasarException("Error loading beamline sets.", e);
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
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            return MasarUtilities.createSnapshotsList(value, s -> new BeamlineSet(service, Optional.empty(),
                new String[] { "Beamline Set: " + s }, MasarDataProvider.ID));
        } catch (InterruptedException e) {
            throw new MasarException("Error loading snapshots.", e);
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
        setService(service);
        try {
            String index = String.valueOf(id);
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_BY_ID);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_FIND_SNAPSHOTS);
            request.getStringField(MasarConstants.F_EVENTID).put(index);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            List<Snapshot> list = MasarUtilities.createSnapshotsList(value, s -> new BeamlineSet(service,
                Optional.empty(), new String[] { "Beamline Set: " + s }, MasarDataProvider.ID));
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(list.get(0));
            }
        } catch (InterruptedException e) {
            throw new MasarException("Error loading snapshots data.", e);
        }
    }

    /**
     * Returns the list of all snapshots for the given beamline set.
     *
     * @param beamlineSet the beamline set for which the snapshots are requested
     * @param numberOfRevisions the maximum number of snapshot revisions to load
     * @param fromThisOneBack the revision at which to start and then going back
     * @return the list of all snapshot revisions for this beamline set
     * @throws MasarException in case of an error
     * @throws ParseException if parsing of date failed
     */
    public synchronized List<Snapshot> getSnapshots(BeamlineSet beamlineSet) throws MasarException, ParseException {
        setService(beamlineSet.getBranch());
        try {
            PVStructure request;
            String index = beamlineSet.getParameters().get(MasarConstants.P_CONFIG_INDEX);
            if (index == null) {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BASE_LEVEL);
            } else {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SNAPSHOT);
                request.getStringField(MasarConstants.F_CONFIGID).put(index);
            }
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SNAPSHOTS);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
            return MasarUtilities.createSnapshotsList(value, s -> beamlineSet);
        } catch (InterruptedException e) {
            throw new MasarException("Error loading snapshots.", e);
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
        setService(snapshot.getBeamlineSet().getBranch());
        try {
            String index = snapshot.getParameters().get(MasarConstants.P_EVENT_ID);
            if (index == null) {
                throw new MasarException("Unknown snapshot: " + snapshot);
            }
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_DATA);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SNAPSHOT_DATA);
            request.getStringField(MasarConstants.F_EVENTID).put(index);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            return MasarUtilities.resultToVSnapshot(result, snapshot, Timestamp.of(snapshot.getDate()));
        } catch (InterruptedException e) {
            throw new MasarException("Error loading snapshots data.", e);
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
        setService(snapshot.getBeamlineSet().getBranch());
        try {
            if (!snapshot.getSnapshot().isPresent()) {
                throw new MasarException("Snapshot " + snapshot + " cannot be saved by MASAR.");
            }
            String id = snapshot.getSnapshot().get().getParameters().get(MasarConstants.P_EVENT_ID);
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
            newSnap = new Snapshot(newSnap.getBeamlineSet(), date, comment, user, newSnap.getParameters());
            return new VSnapshot(newSnap, snapshot.getNames(), snapshot.getSelected(), snapshot.getValues(),
                snapshot.getReadbackNames(), snapshot.getReadbackValues(), snapshot.getDeltas(),
                snapshot.getTimestamp());
        } catch (InterruptedException e) {
            throw new MasarException("Error saving snapshots.", e);
        }
    }

    /**
     * Take a new snapshot for the given beamline set and return it.
     *
     * @param set the beamline set for which the snapshot will be taken
     * @return saved snapshot and change type describing what kind of updates were made to the repository
     * @throws MasarException in case of an error
     */
    public synchronized VSnapshot takeSnapshot(BeamlineSet set) throws MasarException {
        setService(set.getBranch());
        try {
            String name = set.getParameters().get(MasarConstants.P_CONFIG_NAME);
            if (name == null) {
                throw new MasarException("Unknown beamline set: " + set);
            }
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_TAKE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_TAKE_SNAPSHOT);
            request.getStringField(MasarConstants.F_CONFIGNAME).put(name);

            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
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
            parameters.put(MasarConstants.P_EVENT_ID, String.valueOf(id));
            Snapshot snapshot = new Snapshot(set, null, null, null, parameters);
            return MasarUtilities.resultToVSnapshot(result, snapshot, Timestamp.of(sec, nano));
        } catch (InterruptedException e) {
            throw new MasarException("Error taking a snapshot.", e);
        }
    }

    /**
     * Loads the beamline set data by trying to read the contents from one of the snapshot for this beamline set. If no
     * snapshot exists, one is taken and parsed. The snapshot that is taken is never saved.
     *
     * @param set the beamline set for which the content is being loaded
     * @return the beamline set data
     * @throws MasarException in case of an error
     * @throws ParseException if an existing snapshot was being parsed and failed to read the timestamp
     */
    public synchronized BeamlineSetData loadBeamlineSetData(BeamlineSet set) throws MasarException, ParseException {
        setService(set.getBranch());
        List<Snapshot> snapshots = getSnapshots(set);
        VSnapshot snapshot;
        if (snapshots.isEmpty()) {
            snapshot = takeSnapshot(set);
        } else {
            snapshot = loadSnapshotData(snapshots.get(0));
        }
        return new BeamlineSetData(set, snapshot.getNames(), null, null, null);
    }
}
