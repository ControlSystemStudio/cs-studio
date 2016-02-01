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

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.epics.pvaccess.ClientFactory;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelAccessFactory;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelRPC;
import org.epics.pvaccess.client.ChannelRPCRequester;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.util.logging.LoggingUtils;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.BooleanArrayData;
import org.epics.pvdata.pv.LongArrayData;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.StringArrayData;

/**
 * <code>MasarClient<code> provide access to the masar features required by the save and restore application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class MasarClient implements MasarConstants {

    private static class MasarChannelRequester implements ChannelRequester {

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
            SaveRestoreService.LOGGER
                .info("Channel '" + channel.getChannelName() + "' created with status: " + status + ".");
        }

        @Override
        public void channelStateChange(Channel channel, ConnectionState connectionState) {
            SaveRestoreService.LOGGER.info("Channel '" + channel.getChannelName() + "' " + connectionState + ".");
        }
    }

    private static class MasarChannelRPCRequester implements ChannelRPCRequester {
        private final Channel channel;
        private final CountDownLatch connectedSignaler = new CountDownLatch(1);
        private final Semaphore doneSemaphore = new Semaphore(0);

        private volatile ChannelRPC channelRPC = null;
        private volatile PVStructure result = null;

        MasarChannelRPCRequester(Channel channel) {
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
            SaveRestoreService.LOGGER
                .info("ChannelRPC for '" + channel.getChannelName() + "' connected with status: " + status + ".");
            boolean reconnect = (this.channelRPC != null);
            this.channelRPC = channelRPC;
            connectedSignaler.countDown();
            // in case of reconnect, issued request was lost
            if (reconnect) {
                this.result = null;
                doneSemaphore.release();
            }
        }

        @Override
        public void requestDone(Status status, PVStructure result) {
            SaveRestoreService.LOGGER
                .info("requestDone for '" + channel.getChannelName() + "' called with status: " + status + ".");

            this.result = result;
            doneSemaphore.release();
        }

        boolean waitUntilConnected(long timeout, TimeUnit unit) throws InterruptedException {
            return connectedSignaler.await(timeout, unit) && channelRPC != null;
        }

        PVStructure request(PVStructure pvArgument) throws InterruptedException {
            ChannelRPC rpc = channelRPC;
            if (rpc == null)
                throw new IllegalStateException("ChannelRPC never connected.");

            rpc.request(pvArgument, false);
            // use tryAcquire if you need timeout support
            doneSemaphore.acquire(1);
            return result;
        }

        void destroy() {
            channel.destroy();
            if (channelRPC != null) {
                channelRPC.destroy();
            }
        }
    }

    private String[] services;
    private String selectedService;
    private MasarChannelRPCRequester channelRPCRequester;

    /**
     * Creates a new client, but does not initialise it. {@link #initialise(String[])} has to be called before anything
     * can be done with this client.
     */
    public MasarClient() {
    }

    /**
     * Construct a new client and initialise it using the provided parameters.
     *
     * @param services the list of available masar services
     * @throws MasarException in case of an error
     */
    public MasarClient(String[] services) throws MasarException {
        initialise(services);
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
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Git cleanup error", e);
        }
    }

    /**
     * Initialises this masar client by setting up the available services and connecting to either the selected
     * service or the first available service.
     *
     * @param services the list of available services
     * @return true if successfully initialised or false otherwise
     * @throws MasarException in case of an error
     */
    public synchronized boolean initialise(String[] services) throws MasarException {
        this.services = new String[services.length];
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

    public static void main(String[] args) throws Exception {
        System.out.println(gov.aps.jca.dbr.Status.DISABLE_ALARM);
        System.out.println(System.getProperty("user"));
        System.out.println(System.getProperty("user.name"));
        ClientFactory.start();
        ChannelProvider channelProvider = ChannelAccessFactory.getChannelAccess()
            .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);

        Channel channel = channelProvider.createChannel("masarService", new MasarChannelRequester(),
            ChannelProvider.PRIORITY_DEFAULT);

        MasarChannelRPCRequester channelRPCRequester = new MasarChannelRPCRequester(channel);
        channel.createChannelRPC(channelRPCRequester, null);
        if (channelRPCRequester.waitUntilConnected(3, TimeUnit.SECONDS)) {
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_TAKE);
            request.getStringField(F_FUNCTION).put("saveSnapshot");
            request.getStringField(F_CONFIGNAME).put("BL_BM_01");

            // PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_SAVE);
            // request.getStringField(F_FUNCTION).put("updateSnapshotEvent");
            // request.getStringField(F_EVENTID).put("24");
            // request.getStringField(F_USER).put(getUser());
            // request.getStringField(F_DESCRIPTION).put("some dummy comment");

            // request.getStringField("servicename").put("masarService");
            PVStructure result = channelRPCRequester.request(request);
            // PVStructure struct = result.getStructureField("timeStamp");
            // System.out.println(struct.getLongField("secondsPastEpoch"));
            // System.out.println(struct.getIntField("nanoSeconds"));
            // System.out.println(struct.getIntField("userTag"));
            // PVBooleanArray status = (PVBooleanArray)result.getScalarArrayField("status", ScalarType.pvBoolean);
            // PVBoolean status = result.getBooleanField("status");
            // System.out.println(status);
            System.out.println(result);
        } else {
            System.out.println("err");
        }
        ClientFactory.stop();
    }

    /**
     * Switch the working service to the given service. If the current service is already the requested service, nothing
     * happens.
     *
     * @param branch the service to switch to
     * @throws MasarException if there was an exception selecting the service (e.g. service unavailable)
     */
    public synchronized void setService(Branch service) throws MasarException {
        if (!service.getShortName().equals(selectedService)) {
            if (selectedService == null || !service.isDefault()) {
                selectedService = service.getShortName();
                connect();
            }
        }
    }

    private boolean connect() throws MasarException {
        dispose();
        if (this.selectedService == null) {
            throw new MasarException("No service selected.");
        }
        ClientFactory.start();
        ChannelProvider channelProvider = ChannelAccessFactory.getChannelAccess()
            .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);

        Channel channel = channelProvider.createChannel(selectedService, new MasarChannelRequester(),
            ChannelProvider.PRIORITY_DEFAULT);

        channelRPCRequester = new MasarChannelRPCRequester(channel);
        channel.createChannelRPC(channelRPCRequester, null);
        try {
            return channelRPCRequester.waitUntilConnected(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new MasarException("Could not connecto to masar service", e);
        }
    }

    /**
     * Returns the list of all branches (services).
     *
     * @return the list of branches (services)
     */
    public synchronized List<Branch> getBranches() {
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
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_BASE_LEVEL);
            request.getStringField(F_FUNCTION).put(FC_LOAD_BASE_LEVELS);
            PVStructure result = channelRPCRequester.request(request);
            PVStringArray array = (PVStringArray) result.getScalarArrayField(P_BASE_LEVEL_NAME, ScalarType.pvString);
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
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_BEAMLINE_SET);
            request.getStringField(F_FUNCTION).put(FC_LOAD_BEAMLINE_SETS);
            request.getStringField(F_SYSTEM).put(baseLevel.get().getStorageName());
            request.getStringField(F_CONFIGNAME).put("*");

            PVStructure result = channelRPCRequester.request(request);
            PVLongArray pvIndices = (PVLongArray) result.getScalarArrayField(P_INDEX, ScalarType.pvLong);
            PVStringArray pvNames = (PVStringArray) result.getScalarArrayField(P_NAME, ScalarType.pvString);
            PVStringArray pvDesciptions = (PVStringArray) result.getScalarArrayField(P_DESCRIPTION,
                ScalarType.pvString);
            PVStringArray pvDates = (PVStringArray) result.getScalarArrayField(P_DATE, ScalarType.pvString);
            PVStringArray pvVersions = (PVStringArray) result.getScalarArrayField(P_VERSION, ScalarType.pvString);
            PVStringArray pvStatuses = (PVStringArray) result.getScalarArrayField(P_STATUS, ScalarType.pvString);

            StringArrayData names = new StringArrayData();
            pvNames.get(0, pvNames.getLength(), names);
            StringArrayData descriptions = new StringArrayData();
            pvDesciptions.get(0, pvDesciptions.getLength(), descriptions);
            StringArrayData dates = new StringArrayData();
            pvDates.get(0, pvDates.getLength(), dates);
            StringArrayData versions = new StringArrayData();
            pvVersions.get(0, pvVersions.getLength(), versions);
            StringArrayData statuses = new StringArrayData();
            pvStatuses.get(0, pvStatuses.getLength(), statuses);
            LongArrayData indices = new LongArrayData();
            pvIndices.get(0, pvIndices.getLength(), indices);

            List<BeamlineSet> beamlines = new ArrayList<>(names.data.length);
            for (int i = 0; i < names.data.length; i++) {
                Map<String, String> parameters = new HashMap<>(6);
                parameters.put(P_NAME, names.data[i]);
                parameters.put(P_INDEX, String.valueOf(indices.data[i]));
                parameters.put(P_DESCRIPTION, descriptions.data[i]);
                parameters.put(P_DATE, dates.data[i]);
                parameters.put(P_VERSION, versions.data[i]);
                parameters.put(P_STATUS, statuses.data[i]);
                beamlines.add(new BeamlineSet(branch, baseLevel, new String[] { names.data[i] }, MasarDataProvider.ID,
                    parameters));
            }
            return beamlines;
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
     * @throws ParseException in case that the time
     */
    public synchronized List<Snapshot> findSnapshots(Branch service, String expression, boolean byUser,
        boolean byComment, Optional<Date> start, Optional<Date> end) throws MasarException, ParseException {
        setService(service);
        try {
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(createSearchStructure(true, true, start.isPresent(), end.isPresent()));
            request.getStringField(F_FUNCTION).put(FC_FIND_SNAPSHOTS);
            request.getStringField(F_COMMENT).put("*");
            request.getStringField(F_USER).put("*");
            String newExpression = "*" + expression + "*";
            if (byComment) {
                request.getStringField(F_COMMENT).put(newExpression);
            }
            if (byUser) {
                request.getStringField(F_USER).put(newExpression);
            }
            if (start.isPresent()) {
                request.getStringField(F_START).put(DATE_FORMAT.get().format(start.get()));
            }
            if (end.isPresent()) {
                request.getStringField(F_END).put(DATE_FORMAT.get().format(end.get()));
            }
            PVStructure result = channelRPCRequester.request(request);
            return MasarUtilities.parseSnapshots(result, s -> new BeamlineSet(service, Optional.empty(),
                new String[] { "Beamline Set: " + s }, MasarDataProvider.ID));
        } catch (InterruptedException e) {
            throw new MasarException("Error loading snapshots.", e);
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
            String index = beamlineSet.getParameters().get(P_INDEX);
            if (index != null) {
                request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT);
                request.getStringField(F_CONFIGID).put(index);
            } else {
                request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_BASE_LEVEL);
            }
            request.getStringField(F_FUNCTION).put(FC_LOAD_SNAPSHOTS);
            PVStructure result = channelRPCRequester.request(request);
            return MasarUtilities.parseSnapshots(result, s -> beamlineSet);
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
            String index = snapshot.getParameters().get(P_EVENT_ID);
            if (index == null) {
                throw new MasarException("Unknown snapshot: " + snapshot);
            }
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_DATA);
            request.getStringField(F_FUNCTION).put(FC_LOAD_SNAPSHOT_DATA);
            request.getStringField(F_EVENTID).put(index);
            PVStructure result = channelRPCRequester.request(request);
            return MasarUtilities.resultToSnapshot(result, snapshot, Timestamp.of(snapshot.getDate()), false);
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
            String id = snapshot.getSnapshot().get().getParameters().get(P_EVENT_ID);
            if (id == null) {
                throw new MasarException("Snapshot " + snapshot + " is not a valid MASAR snapshot.");
            }
            String user = MasarUtilities.getUser();
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_SAVE);
            request.getStringField(F_FUNCTION).put(FC_SAVE_SNAPSHOT);
            request.getStringField(F_EVENTID).put(id);
            request.getStringField(F_USER).put(user);
            request.getStringField(F_DESCRIPTION).put(comment);
            PVStructure result = channelRPCRequester.request(request);
            PVBooleanArray status = (PVBooleanArray) result.getScalarArrayField(P_STATUS, ScalarType.pvBoolean);
            BooleanArrayData st = new BooleanArrayData();
            status.get(0, status.getLength(), st);
            if (!st.data[0]) {
                // masar returns status=false, error description is given in the alarm message
                PVStructure alarm = result.getStructureField(P_ALARM);
                String message = alarm.getStringField(P_MESSAGE).get();
                throw new MasarException(message);
            }
            Snapshot newSnap = snapshot.getSnapshot().get();
            newSnap = new Snapshot(newSnap.getBeamlineSet(), newSnap.getDate(), comment, user, newSnap.getParameters());
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
            String name = set.getParameters().get(P_NAME);
            if (name == null) {
                throw new MasarException("Unknown beamline set: " + set);
            }
            PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_TAKE);
            request.getStringField(F_FUNCTION).put(FC_TAKE_SNAPSHOT);
            request.getStringField(F_CONFIGNAME).put(name);

            PVStructure result = channelRPCRequester.request(request);
            PVBooleanArray status = (PVBooleanArray) result.getScalarArrayField(P_STATUS, ScalarType.pvBoolean);
            if (status != null) {
                BooleanArrayData st = new BooleanArrayData();
                status.get(0, status.getLength(), st);
                if (!st.data[0]) {
                    // masar returns status=false, error description is given in the alarm message
                    PVStructure alarm = result.getStructureField(P_ALARM);
                    String message = alarm.getStringField(P_MESSAGE).get();
                    throw new MasarException(message);
                }
            }
            PVStructure timestamp = result.getStructureField(P_TIMESTAMP);
            long sec = timestamp.getLongField(P_SECONDS).get();
            int nano = timestamp.getIntField(P_NANOS).get();
            int id = timestamp.getIntField(P_USER_TAG).get();
            Map<String, String> parameters = new HashMap<>();
            parameters.put(P_EVENT_ID, String.valueOf(id));
            Snapshot snapshot = new Snapshot(set, null, null, null, parameters);
            return MasarUtilities.resultToSnapshot(result, snapshot, Timestamp.of(sec, nano), true);
        } catch (InterruptedException e) {
            throw new MasarException("Error taking a snapshot.", e);
        }
    }
}
