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

import static org.csstudio.saverestore.masar.MasarConstants.FC_LOAD_SNAPSHOT_DATA;
import static org.csstudio.saverestore.masar.MasarConstants.F_DESCRIPTION;
import static org.csstudio.saverestore.masar.MasarConstants.F_EVENTID;
import static org.csstudio.saverestore.masar.MasarConstants.F_FUNCTION;
import static org.csstudio.saverestore.masar.MasarConstants.F_NAME;
import static org.csstudio.saverestore.masar.MasarConstants.F_USER;
import static org.csstudio.saverestore.masar.MasarConstants.F_VALUE;
import static org.csstudio.saverestore.masar.MasarConstants.STRUCT_SNAPSHOT_DATA;

import java.text.ParseException;
import java.time.Instant;
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
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelProviderRegistryFactory;
import org.epics.pvaccess.client.ChannelRPC;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.util.logging.LoggingUtils;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.PVUnionArray;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.Status.StatusType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.Union;
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
                    // this is to bring up a service which was disconnected at start up time, but became online later
                    notifier.synchronised();
                }
                // if not working, reconnect will happen at the next request
                // else if (lastConnectionState != ConnectionState.CONNECTED) {
                // try {
                // client.connect(true);
                // } catch (MasarException e) {
                // SaveRestoreService.LOGGER.log(Level.SEVERE, "Cannot reconnect to masar service", e);
                // }
                // }
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
                throw new MasarException(String.format("Cannot connect to the MASAR service '%s'.", service));
            }
            this.result = null;
            rpc.request(requestData);
            if (Activator.getInstance().getTimeout() > 0) {
                if (!doneSemaphore.tryAcquire(1, Activator.getInstance().getTimeout(), TimeUnit.SECONDS)) {
                    throw new MasarException(String.format("Timeout sending request to '%s'.", service));
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

    private static RPCRequester createChannel(String service, CompletionNotifier notifier, boolean neverConnected)
        throws MasarException {
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
            throw new MasarException(String.format("Service '%s' already exists.", newService));
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
        throw new MasarException(String.format("Service '%s' is unreachable.", newService));
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
     * Creates a new SaveSet configuration in masar.
     *
     * @param set the new SaveSet configuration to be created
     * @param comment a comment on the SaveSet configuration
     * @return the successfully created SaveSetData
     * @throws MasarException
     */
    public synchronized SaveSetData createSaveSets(SaveSetData set, String comment) throws MasarException {
        return createSaveSets(set, comment, true);
    }

    private SaveSetData createSaveSets(SaveSetData set, String comment, boolean retryOnError) throws MasarException{
        try {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_REQUEST);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_SAVE_SAVE_SETS);

            PVStringArray names = (PVStringArray) request.getScalarArrayField(F_NAME, ScalarType.pvString);
            names.put(0, 4, new String[] { MasarConstants.F_CONFIGNAME, MasarConstants.F_OLDCONFIGID,
                    MasarConstants.F_DESCRIPTION, MasarConstants.F_CONFIG }, 0);

            PVUnion u1 = PVDataFactory.getPVDataCreate()
                    .createPVUnion(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]));
            Scalar s1 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
            PVString a1 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s1);
            a1.put(set.getDescriptor().getName());
            u1.set(a1);

            PVUnion u2 = PVDataFactory.getPVDataCreate()
                    .createPVUnion(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]));
            Scalar s2 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
            PVString a2 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s2);
            List<SaveSet> existing = getSaveSets(set.getDescriptor().getBaseLevel(), set.getDescriptor().getBranch(),
                    set.getDescriptor().getName(), false);
            if(existing == null || existing.isEmpty()){
                a2.put("0");
            } else {
                a2.put(existing.get(0).getParameters().get(MasarConstants.P_CONFIG_INDEX));
            }
            u2.set(a2);

            PVUnion u3 = PVDataFactory.getPVDataCreate()
                    .createPVUnion(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]));
            Scalar s3 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
            PVString a3 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s3);
            a3.put(set.getDescription());
            u3.set(a3);

            Union uu4 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
            PVUnion u4 = PVDataFactory.getPVDataCreate().createPVUnion(uu4);

            Structure val = FieldFactory.getFieldCreate().createStructure(new String[] { MasarConstants.F_LABELS, F_VALUE},
                    new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                            FieldFactory.getFieldCreate().createStructure(
                                    new String[] {
                                            MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
                                            MasarConstants.P_SNAPSHOT_READONLY,
                                            MasarConstants.P_SNAPSHOT_GROUP_NAME,
                                            MasarConstants.P_SNAPSHOT_TAG },
                                    new Field[] {
                                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString) }) });

            PVStructure valStruct = PVDataFactory.getPVDataCreate().createPVStructure(val);
            PVStringArray labels = (PVStringArray) valStruct.getScalarArrayField(MasarConstants.F_LABELS, ScalarType.pvString);
            labels.put(0, 4, new String[] {
                    MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
                    MasarConstants.P_SNAPSHOT_READONLY,
                    MasarConstants.P_SNAPSHOT_GROUP_NAME,
                    MasarConstants.P_SNAPSHOT_TAG }, 0);
            PVStructure config = valStruct.getStructureField(F_VALUE);

            int entryListLength = set.getEntries().size();
            // TODO (shroffk) the individual stream operations should be merged into one loop through
            String[] pvNames = set.getEntries().stream().map(SaveSetEntry::getPVName).collect(Collectors.toList())
                    .toArray(new String[set.getEntries().size()]);
            PVStringArray channelName = (PVStringArray) config.getScalarArrayField(MasarConstants.P_SNAPSHOT_CHANNEL_NAME, ScalarType.pvString);
            channelName.put(0, entryListLength, pvNames, 0);

            PVStringArray readonly = (PVStringArray) config.getScalarArrayField(MasarConstants.P_SNAPSHOT_READONLY, ScalarType.pvString);
            readonly.put(0, entryListLength, set.getEntries().stream().map(e -> {
                if(e.isReadOnly()) {
                    return "1";
                } else {
                    return "0";
                }
            } ).collect(Collectors.toList())
                    .toArray(new String[set.getEntries().size()]), 0);

            PVStringArray groupName = (PVStringArray) config.getScalarArrayField(MasarConstants.P_SNAPSHOT_GROUP_NAME, ScalarType.pvString);
            groupName.put(0, entryListLength, set.getEntries().stream().map(e -> {
                return "".equals(e.getReadback())? "" : "RB:" + e.getReadback() + ";";
            }).collect(Collectors.toList()).toArray(new String[entryListLength]), 0);
            
            PVStringArray tags = (PVStringArray) config.getScalarArrayField(MasarConstants.P_SNAPSHOT_TAG, ScalarType.pvString);
            tags.put(0, entryListLength, set.getEntries().stream().map(e -> {
                return "".equals(e.getDelta()) ? "" : "DELTA:" + e.getDelta() + ";";
            }).collect(Collectors.toList()).toArray(new String[entryListLength]), 0);

            u4.set(valStruct);

            /**
             * TODO (shroffk) replace the above table creation code with the following from the NTType utility library
             *
            NTTable ntTable = NTTable.createBuilder()
                    .addColumn("channelName", ScalarType.pvString)
                    .addColumn("readonly", ScalarType.pvString)
                    .addColumn("groupName", ScalarType.pvString)
                    .addColumn("tags", ScalarType.pvString).create();
            ((PVStringArray) ntTable.getColumn("channelName")).put(0, 3, new String[] { "XF:31IDA-OP{Tbl-Ax:X1}Mtr", "XF:31IDA-OP{Tbl-Ax:X2}Mtr",
            "XF:31IDA-OP{Tbl-Ax:X3}Mtr" }, 0);
            ((PVStringArray) ntTable.getColumn("readonly")).put(0, 3, new String[] { "", "", "" }, 0);
            ((PVStringArray) ntTable.getColumn("groupName")).put(0, 3, new String[] { "G1", "G1", "G1" }, 0);
            ((PVStringArray) ntTable.getColumn("tags")).put(0, 3, new String[] { "T1", "T1", "t2" }, 0);
            u4.set(ntTable.getPVStructure());
            **/
            ((PVUnionArray) request.getUnionArrayField(F_VALUE)).put(0, 4, new PVUnion[] { u1, u2, u3, u4 }, 0);

            PVStructure result = channelRPCRequester.request(request);
            List<SaveSet> parsed = MasarUtilities.createSaveSetsList(result, set.getDescriptor().getBranch(),
                    set.getDescriptor().getBaseLevel());
            if (parsed.size() == 1) {
                SaveSet createdSaveSet = parsed.get(0);
                String id = createdSaveSet.getParameters().get(MasarConstants.P_CONFIG_INDEX);
                List<SaveSetEntry> entries = getSaveSetData(createdSaveSet.getBaseLevel(), createdSaveSet.getBranch(), id, true);
                return new SaveSetData(createdSaveSet, entries, createdSaveSet.getParameters().get(MasarConstants.P_CONFIG_DESCRIPTION));
            }
        } catch (Exception e) {
            throw new MasarException("Creating new snapshots config failed: ", e);
        }
        return null;
    }

    /**
     * Returns the list of all available save sets in the current service.
     *
     * @param baseLevel the base level for which the save sets are requested (optional, if base levels are not used)
     * @param service the service to switch to
     * @return the list of save sets
     * @throws MasarException in case of an error
     */
    public synchronized List<SaveSet> getSaveSets(Optional<BaseLevel> baseLevel, Branch service) throws MasarException {
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
     * Returns the list of all available save sets in the current branch which matches the configName.
     *
     * TODO currently this method is private but there may be a case to make it public
     *
     * @param baseLevel the base level for which the save sets are requested (optional, if base levels are not used)
     * @param service the service to switch to
     * @param confingName the name of the config to be retrieved
     * @param retryOnError if true and there is an error in communication the channel will be reconnected and the
     *            request sent again
     * @return the list of save sets
     * @throws MasarException in case of an error
     */
    private List<SaveSet> getSaveSets(Optional<BaseLevel> baseLevel, Branch service, String configName,
            boolean retryOnError) throws MasarException {
        setService(service);
        try {
            final Structure STRUCT_REQUEST_NAME_VALUE = FieldFactory.getFieldCreate().createStructure(
                    new String[] { F_FUNCTION, F_NAME, F_VALUE },
                    new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString) });
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_REQUEST_NAME_VALUE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SAVE_SETS);
            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME,
                    ScalarType.pvString);
            names.put(0, 2, new String[] { MasarConstants.F_SYSTEM, MasarConstants.F_CONFIGNAME }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE,
                    ScalarType.pvString);
            values.put(0, 2, new String[] { baseLevel.get().getStorageName(), configName }, 0);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return getSaveSets(baseLevel, service, configName, false);
                }
                throw new MasarException(
                        channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }

            return MasarUtilities.createSaveSetsList(result, service, baseLevel);
        } catch (InterruptedException e) {
            throw new MasarException("Loading save sets aborted.", e);
        }
    }

    private List<SaveSetEntry> getSaveSetData(Optional<BaseLevel> baseLevel, Branch service, String configId,
            boolean retryOnError) throws MasarException {
        setService(service);
        try {
            final Structure STRUCT_REQUEST_NAME_VALUE = FieldFactory.getFieldCreate().createStructure(
                    new String[] { F_FUNCTION, F_NAME, F_VALUE},
                    new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
                            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString) });
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_REQUEST_NAME_VALUE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_LOAD_SAVE_SET_DATA);
            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME,
                    ScalarType.pvString);
            names.put(0, 1, new String[] { MasarConstants.F_CONFIGID }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE,
                    ScalarType.pvString);
            values.put(0, 1, new String[] { configId }, 0);
            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return getSaveSetData(baseLevel, service, configId, false);
                }
                throw new MasarException(
                        channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }

            return MasarUtilities.createSaveSetEntryList(result);
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
//            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(
//                MasarConstants.createSearchStructure(true, true, start.isPresent(), end.isPresent()));
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SIMPLE_REQUEST);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_FIND_SNAPSHOTS);
            List<String> nameParameters = new ArrayList<String>();
            List<String> valueParameters = new ArrayList<String>();

            String newExpression = new StringBuilder(expression.length() + 2).append('*').append(expression).append('*')
                .toString();
            if (byComment) {
                nameParameters.add(MasarConstants.F_COMMENT);
                valueParameters.add(newExpression);
                nameParameters.add(MasarConstants.F_USER);
                valueParameters.add("*");
            }
            if (byUser) {
                nameParameters.add(MasarConstants.F_COMMENT);
                valueParameters.add("*");
                nameParameters.add(MasarConstants.F_USER);
                valueParameters.add(newExpression);
            }
            if (start.isPresent()) {
                nameParameters.add(MasarConstants.F_START);
                valueParameters.add(MasarConstants.DATE_FORMAT.get().format(start.get()));
            }
            if (end.isPresent()) {
                nameParameters.add(MasarConstants.F_END);
                valueParameters.add(MasarConstants.DATE_FORMAT.get().format(end.get()));
            }

            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME,
                    ScalarType.pvString);
            names.put(0, nameParameters.size(), nameParameters.toArray(new String[nameParameters.size()]), 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE,
                    ScalarType.pvString);
            values.put(0, valueParameters.size(), valueParameters.toArray(new String[valueParameters.size()]), 0);

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
            return MasarUtilities.createSnapshotsList(value,
                s -> new SaveSet(service, Optional.empty(), new String[] { "Save Set: " + s }, MasarDataProvider.ID));
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
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SIMPLE_REQUEST);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_FIND_SNAPSHOTS);
            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME,
                    ScalarType.pvString);
            names.put(0, 1, new String[] { MasarConstants.F_EVENTID }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE,
                    ScalarType.pvString);
            values.put(0, 1, new String[] { index }, 0);
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
            List<Snapshot> list = MasarUtilities.createSnapshotsList(value,
                s -> new SaveSet(service, Optional.empty(), new String[] { "Save Set: " + s }, MasarDataProvider.ID));
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
    private List<Snapshot> getSnapshots(SaveSet saveSet, boolean retryOnError) throws MasarException, ParseException {
        setService(saveSet.getBranch());
        try {
            PVStructure request;
            String index = saveSet.getParameters().get(MasarConstants.P_CONFIG_INDEX);
            if (index == null) {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_BASE_LEVEL);
            } else {
                request = PVDataFactory.getPVDataCreate().createPVStructure(MasarConstants.STRUCT_SIMPLE_REQUEST);
                PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME,
                        ScalarType.pvString);
                names.put(0, 1, new String[] { MasarConstants.F_CONFIGID }, 0);
                PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE,
                        ScalarType.pvString);
                values.put(0, 1, new String[] { index }, 0);
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
                throw new MasarException(String.format("Unknown snapshot: %s.", String.valueOf(snapshot)));
            }
            //TODO fix backward compatability issues.
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(STRUCT_SNAPSHOT_DATA);
            request.getStringField(F_FUNCTION).put(FC_LOAD_SNAPSHOT_DATA);
            PVStringArray names = (PVStringArray) request.getScalarArrayField(F_NAME, ScalarType.pvString);
            names.put(0, 1, new String[] { F_EVENTID }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(F_VALUE, ScalarType.pvString);
            values.put(0, 1, new String[] { index }, 0);

            PVStructure result = channelRPCRequester.request(request);
            if (result == null) {
                if (retryOnError && channelRPCRequester.isConnected()) {
                    connect();
                    return loadSnapshotData(snapshot, false);
                }
                throw new MasarException(
                    channelRPCRequester.isConnected() ? "Unknown error." : "Masar service not available.");
            }
            return MasarUtilities.resultToVSnapshot(result, snapshot, snapshot.getDate());
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
                throw new MasarException(
                    String.format("Snapshot '%s' cannot be saved by MASAR.", String.valueOf(snapshot)));
            }
            String id = snapshot.getSnapshot().get().getParameters().get(MasarConstants.PARAM_SNAPSHOT_ID);
            if (id == null) {
                id = snapshot.getSnapshot().get().getParameters().get(MasarConstants.P_EVENT_ID);
            }
            if (id == null) {
                throw new MasarException(
                    String.format("Snapshot '%s' is not a valid MASAR snapshot.", String.valueOf(snapshot)));
            }
            String user = MasarUtilities.getUser();
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(MasarConstants.STRUCT_SNAPSHOT_SAVE);
            request.getStringField(MasarConstants.F_FUNCTION).put(MasarConstants.FC_SAVE_SNAPSHOT);
            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME, ScalarType.pvString);
            names.put(0, 3, new String[] { F_EVENTID, F_USER, F_DESCRIPTION }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE, ScalarType.pvString);
            values.put(0, 3, new String[] { id, user, comment }, 0);
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
            Instant date = newSnap.getDate();
            if (date == null) {
                date = snapshot.getTimestamp();
            }
            newSnap = new Snapshot(newSnap.getSaveSet(), date, comment, user, newSnap.getParameters(),
                newSnap.getPublicParameters());
            return new VSnapshot(newSnap, snapshot.getEntries(), snapshot.getTimestamp());
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
            PVStringArray names = (PVStringArray) request.getScalarArrayField(MasarConstants.F_NAME, ScalarType.pvString);
            names.put(0, 1, new String[] { MasarConstants.F_CONFIGNAME }, 0);
            PVStringArray values = (PVStringArray) request.getScalarArrayField(MasarConstants.F_VALUE, ScalarType.pvString);
            values.put(0, 1, new String[] { name }, 0);
//            request.getStringField(MasarConstants.F_CONFIGNAME).put(name);

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
            return MasarUtilities.resultToVSnapshot(result, snapshot, Instant.ofEpochSecond(sec, nano));
        } catch (InterruptedException e) {
            throw new MasarException("Taking snapshot aborted.", e);
        }
    }

    /**
     * Loads the save set data by trying to read the contents from one of the snapshot for this save set. If no snapshot
     * exists, one is taken and parsed. The snapshot that is taken is never saved.
     *
     * @param set the save set for which the content is being loaded
     * @return the save set data
     * @throws MasarException in case of an error
     * @throws ParseException if an existing snapshot was being parsed and failed to read the timestamp
     */
    public synchronized SaveSetData loadSaveSetData(SaveSet set) throws MasarException, ParseException {
        setService(set.getBranch());
        String description = set.getParameters().get(MasarConstants.P_CONFIG_DESCRIPTION);
        List<Snapshot> snapshots = getSnapshots(set);
        VSnapshot snapshot;
        if (snapshots.isEmpty()) {
            snapshot = takeSnapshot(set);
        } else {
            snapshot = loadSnapshotData(snapshots.get(0));
        }
        List<SaveSetEntry> entries = snapshot.getEntries().stream()
                .map(e -> new SaveSetEntry(e.getPVName(), e.getReadbackName(), e.getDelta(), e.isReadOnly()))
                .collect(Collectors.toList());
        return new SaveSetData(set, entries, description);
    }

}
