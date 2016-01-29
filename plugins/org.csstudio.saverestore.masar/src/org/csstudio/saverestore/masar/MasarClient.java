package org.csstudio.saverestore.masar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.csstudio.saverestore.ValueType;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.epics.pvaccess.ClientFactory;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelAccessFactory;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelRPC;
import org.epics.pvaccess.client.ChannelRPCRequester;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.util.logging.LoggingUtils;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.LongArrayData;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.StructureArrayData;

import gov.aps.jca.dbr.Severity;

/**
 * <code>MasarClient<code> provide access to the masar features required by the save and restore application.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class MasarClient {

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
    //used for transforming the date string from the MASAR format string to Date and vice versa
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String[] services;
    private String selectedService;
    private MasarChannelRPCRequester channelRPCRequester;

    // Output parameters IDs
    private static final String P_NAME = "config_name";
    private static final String P_INDEX = "config_idx";
    private static final String P_DESCRIPTION = "config_desc";
    private static final String P_DATE = "config_create_date";
    private static final String P_VERSION = "config_version";
    private static final String P_STATUS = "status";
    private static final String P_BASE_LEVEL_NAME = "system_val";
    private static final String P_EVENT_ID = "event_id";
    private static final String P_CONFIG_ID = "config_id";
    private static final String P_COMMENT = "comments";
    private static final String P_EVENT_TIME = "event_time";
    private static final String P_USER = "user_name";
    // Snapshot data output parameters
    private static final String P_PVNAME = "pv name";
    private static final String P_STRING_VALUE = "string value";
    private static final String P_DOUBLE_VALUE = "double value";
    private static final String P_LONG_VALUE = "long value";
    private static final String P_DBR_TYPE = "dbr type";
    private static final String P_IS_CONNECTED = "isConnected";
    private static final String P_SECONDS = "secondsPastEpoch";
    private static final String P_NANOS = "nanoSeconds";
    private static final String P_TIMESTAMP_TAG = "timeStampTag";
    private static final String P_ALARM_SEVERITY = "alarmSeverity";
    private static final String P_ALARM_STATUS = "alarmStatus";
    private static final String P_ALARM_MESSAGE = "alarmMessage";
    private static final String P_IS_ARRAY = "is_array";
    private static final String P_ARRAY_VALUE = "array_value";
    private static final String P_A_STRING = "stringVal";
    private static final String P_A_DOUBLE = "doubleVal";
    private static final String P_A_INT = "intVal";

    // The input parameter IDS
    private static final String F_FUNCTION = "function";
    private static final String F_SYSTEM = "system";
    private static final String F_CONFIGNAME = "configname";
    private static final String F_CONFIGID = "configid";
    private static final String F_EVENTID = "eventid";
    private static final String F_COMMENT = "comment";
    private static final String F_START = "start";
    private static final String F_END = "end";
    private static final String F_USER = "user";

    // Structure description for loading the base levels
    private final static Structure STRUCT_BASE_LEVEL = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION }, new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the list of beamline sets
    private final static Structure STRUCT_BEAMLINE_SET = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_SYSTEM, F_CONFIGNAME },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the list of snapshots
    private final static Structure STRUCT_SNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_CONFIGID },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the snapshot data
    private final static Structure STRUCT_SNAPSHOT_DATA = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_EVENTID },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    /**
     * Creates a structure description for performing the snapshot search.
     *
     * @param comment true if search will be performed on the comment
     * @param user true if search will be performed on the username
     * @param start true if lower time boundary will be specified in the search
     * @param end true if upper time boundary will be specified in the search
     * @return the structure for the given parameters
     */
    private static Structure createSearchStructure(boolean comment, boolean user, boolean start, boolean end) {
        List<String> names = new ArrayList<>(5);
        names.add(F_FUNCTION);
        if (comment)
            names.add(F_COMMENT);
        if (user)
            names.add(F_USER);
        if (start)
            names.add(F_START);
        if (end)
            names.add(F_END);
        Field[] fields = new Field[names.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
        }
        return FieldFactory.getFieldCreate().createStructure(names.toArray(new String[names.size()]), fields);
    }

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
        ClientFactory.start();
        ChannelProvider channelProvider = ChannelAccessFactory.getChannelAccess()
            .getProvider(org.epics.pvaccess.ClientFactory.PROVIDER_NAME);

        Channel channel = channelProvider.createChannel("masarService", new MasarChannelRequester(),
            ChannelProvider.PRIORITY_DEFAULT);

        MasarChannelRPCRequester channelRPCRequester = new MasarChannelRPCRequester(channel);
        channel.createChannelRPC(channelRPCRequester, null);
        if (channelRPCRequester.waitUntilConnected(3, TimeUnit.SECONDS)) {
            PVStructure request = PVDataFactory.getPVDataCreate()
                .createPVStructure(createSearchStructure(true, true, false, false));
            request.getStringField(F_FUNCTION).put("retrieveServiceEvents");
            request.getStringField(F_COMMENT).put("*Comple*");
            request.getStringField(F_USER).put("*");

            // PVStructure request = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT_DATA);
            // request.getStringField("function").put("retrieveSnapshot");
            // request.getStringField("eventid").put("8");

            PVStructure result = channelRPCRequester.request(request);

            System.out.println(result);
        } else {
            System.out.println("err");
        }
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
            request.getStringField(F_FUNCTION).put("retrieveServiceConfigProps");
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
            request.getStringField(F_FUNCTION).put("retrieveServiceConfigs");
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
            request.getStringField(F_FUNCTION).put("retrieveServiceEvents");
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
                request.getStringField(F_START).put(DATE_FORMAT.format(start.get()));
            }
            if (end.isPresent()) {
                request.getStringField(F_END).put(DATE_FORMAT.format(end.get()));
            }

            PVStructure result = channelRPCRequester.request(request);

            PVLongArray pvEvents = (PVLongArray) result.getScalarArrayField(P_EVENT_ID, ScalarType.pvLong);
            PVLongArray pvConfigs = (PVLongArray) result.getScalarArrayField(P_CONFIG_ID, ScalarType.pvLong);
            PVStringArray pvComments = (PVStringArray) result.getScalarArrayField(P_COMMENT, ScalarType.pvString);
            PVStringArray pvTimes = (PVStringArray) result.getScalarArrayField(P_EVENT_TIME, ScalarType.pvString);
            PVStringArray pvUsers = (PVStringArray) result.getScalarArrayField(P_USER, ScalarType.pvString);

            StringArrayData comments = new StringArrayData();
            pvComments.get(0, pvComments.getLength(), comments);
            StringArrayData times = new StringArrayData();
            pvTimes.get(0, pvTimes.getLength(), times);
            StringArrayData users = new StringArrayData();
            pvUsers.get(0, pvUsers.getLength(), users);
            LongArrayData events = new LongArrayData();
            pvEvents.get(0, pvEvents.getLength(), events);
            LongArrayData configs = new LongArrayData();
            pvConfigs.get(0, pvConfigs.getLength(), configs);

            List<Snapshot> snapshots = new ArrayList<>(events.data.length);
            for (int i = 0; i < events.data.length; i++) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(P_EVENT_ID, String.valueOf(events.data[i]));
                parameters.put(P_CONFIG_ID, String.valueOf(configs.data[i]));
                Date date = DATE_FORMAT.parse(times.data[i]);
                BeamlineSet bs = new BeamlineSet(service, Optional.empty(),
                    new String[] { "Beamline Set: " + parameters.get(P_CONFIG_ID) }, MasarDataProvider.ID);
                snapshots.add(new Snapshot(bs, date, comments.data[i].trim(), users.data[i].trim(), parameters));
            }
            return snapshots;
        } catch (InterruptedException e) {
            throw new MasarException("Error loading beamline snapshots.", e);
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
            request.getStringField(F_FUNCTION).put("retrieveServiceEvents");

            PVStructure result = channelRPCRequester.request(request);

            PVLongArray pvEvents = (PVLongArray) result.getScalarArrayField(P_EVENT_ID, ScalarType.pvLong);
            PVLongArray pvConfigs = (PVLongArray) result.getScalarArrayField(P_CONFIG_ID, ScalarType.pvLong);
            PVStringArray pvComments = (PVStringArray) result.getScalarArrayField(P_COMMENT, ScalarType.pvString);
            PVStringArray pvTimes = (PVStringArray) result.getScalarArrayField(P_EVENT_TIME, ScalarType.pvString);
            PVStringArray pvUsers = (PVStringArray) result.getScalarArrayField(P_USER, ScalarType.pvString);

            StringArrayData comments = new StringArrayData();
            pvComments.get(0, pvComments.getLength(), comments);
            StringArrayData times = new StringArrayData();
            pvTimes.get(0, pvTimes.getLength(), times);
            StringArrayData users = new StringArrayData();
            pvUsers.get(0, pvUsers.getLength(), users);
            LongArrayData events = new LongArrayData();
            pvEvents.get(0, pvEvents.getLength(), events);
            LongArrayData configs = new LongArrayData();
            pvConfigs.get(0, pvConfigs.getLength(), configs);

            List<Snapshot> snapshots = new ArrayList<>(events.data.length);
            for (int i = 0; i < events.data.length; i++) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(P_EVENT_ID, String.valueOf(events.data[i]));
                parameters.put(P_CONFIG_ID, String.valueOf(configs.data[i]));
                Date date = DATE_FORMAT.parse(times.data[i]);
                snapshots
                    .add(new Snapshot(beamlineSet, date, comments.data[i].trim(), users.data[i].trim(), parameters));
            }
            return snapshots;
        } catch (InterruptedException e) {
            throw new MasarException("Error loading beamline snapshots.", e);
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
            request.getStringField(F_FUNCTION).put("retrieveSnapshot");
            request.getStringField(F_EVENTID).put(index);

            PVStructure result = channelRPCRequester.request(request);
            PVStringArray pvPVName = (PVStringArray) result.getScalarArrayField(P_PVNAME, ScalarType.pvString);
            PVStringArray pvStringValue = (PVStringArray) result.getScalarArrayField(P_STRING_VALUE,
                ScalarType.pvString);
            PVDoubleArray pvDoubleValue = (PVDoubleArray) result.getScalarArrayField(P_DOUBLE_VALUE,
                ScalarType.pvDouble);
            PVStringArray pvAlarmMessage = (PVStringArray) result.getScalarArrayField(P_ALARM_MESSAGE,
                ScalarType.pvString);
            PVLongArray pvLongValue = (PVLongArray) result.getScalarArrayField(P_LONG_VALUE, ScalarType.pvLong);
            PVLongArray pvDBRType = (PVLongArray) result.getScalarArrayField(P_DBR_TYPE, ScalarType.pvLong);
            PVLongArray pvIsConnected = (PVLongArray) result.getScalarArrayField(P_IS_CONNECTED, ScalarType.pvLong);
            PVLongArray pvSeconds = (PVLongArray) result.getScalarArrayField(P_SECONDS, ScalarType.pvLong);
            PVLongArray pvNanos = (PVLongArray) result.getScalarArrayField(P_NANOS, ScalarType.pvLong);
            PVLongArray pvTimestampTag = (PVLongArray) result.getScalarArrayField(P_TIMESTAMP_TAG, ScalarType.pvLong);
            PVLongArray pvAlarmSeverity = (PVLongArray) result.getScalarArrayField(P_ALARM_SEVERITY, ScalarType.pvLong);
            PVLongArray pvAlarmStatus = (PVLongArray) result.getScalarArrayField(P_ALARM_STATUS, ScalarType.pvLong);
            PVLongArray pvIsArray = (PVLongArray) result.getScalarArrayField(P_IS_ARRAY, ScalarType.pvLong);
            PVStructureArray pvArrayData = result.getStructureArrayField(P_ARRAY_VALUE);

            StringArrayData pvName = new StringArrayData();
            pvPVName.get(0, pvPVName.getLength(), pvName);
            StringArrayData stringValue = new StringArrayData();
            pvStringValue.get(0, pvStringValue.getLength(), stringValue);
            StringArrayData alarmMessage = new StringArrayData();
            pvAlarmMessage.get(0, pvAlarmMessage.getLength(), alarmMessage);
            DoubleArrayData doubleValue = new DoubleArrayData();
            pvDoubleValue.get(0, pvDoubleValue.getLength(), doubleValue);
            LongArrayData longValue = new LongArrayData();
            pvLongValue.get(0, pvLongValue.getLength(), longValue);
            LongArrayData dbrType = new LongArrayData();
            pvDBRType.get(0, pvDBRType.getLength(), dbrType);
            LongArrayData isConnected = new LongArrayData();
            pvIsConnected.get(0, pvIsConnected.getLength(), isConnected);
            LongArrayData seconds = new LongArrayData();
            pvSeconds.get(0, pvSeconds.getLength(), seconds);
            LongArrayData nanos = new LongArrayData();
            pvNanos.get(0, pvNanos.getLength(), nanos);
            LongArrayData timestampTag = new LongArrayData();
            pvTimestampTag.get(0, pvTimestampTag.getLength(), timestampTag);
            LongArrayData alarmSeverity = new LongArrayData();
            pvAlarmSeverity.get(0, pvAlarmSeverity.getLength(), alarmSeverity);
            LongArrayData alarmStatus = new LongArrayData();
            pvAlarmStatus.get(0, pvAlarmStatus.getLength(), alarmStatus);
            LongArrayData isArray = new LongArrayData();
            pvIsArray.get(0, pvIsArray.getLength(), isArray);
            StructureArrayData arrayData = new StructureArrayData();
            pvArrayData.get(0, pvArrayData.getLength(), arrayData);

            int length = pvName.data.length;
            List<String> names = new ArrayList<>(length);
            List<VType> values = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                names.add(pvName.data[i]);
                Time time = ValueFactory.newTime(Timestamp.of(seconds.data[i], (int) nanos.data[i]));
                Alarm alarm = ValueFactory.newAlarm(fromEpics(Severity.forValue((int) alarmSeverity.data[i])),
                    gov.aps.jca.dbr.Status.forValue((int) alarmStatus.data[i]).getName());
                ValueType vt = toValueType((int) dbrType.data[i], isArray.data[i] != 0);
                values.add(vt.isArray() ? toValue(arrayData.data[i], vt, time, alarm)
                    : toValue(stringValue.data[i], doubleValue.data[i], longValue.data[i], vt, time, alarm));
            }
            return new VSnapshot(snapshot, names, values, Timestamp.of(snapshot.getDate()), null);
        } catch (InterruptedException e) {
            throw new MasarException("Error loading beamline snapshots.", e);
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
        // TODO updateSnapshotEvent
        return null;
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
        // TODO saveSnapshot
        return null;
    }

    private static AlarmSeverity fromEpics(Severity severity) {
        if (Severity.NO_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.NONE;
        } else if (Severity.MINOR_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.MINOR;
        } else if (Severity.MAJOR_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.MAJOR;
        } else if (Severity.INVALID_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.INVALID;
        } else {
            return AlarmSeverity.UNDEFINED;
        }
    }

    private static ValueType toValueType(int dbrType, boolean isArray) {
        int baseType = dbrType % 7;
        switch (baseType) {
            case 0:
                return isArray ? ValueType.STRING_ARRAY : ValueType.STRING;
            case 1:
                return isArray ? ValueType.INT_ARRAY : ValueType.INT;
            case 2:
                return isArray ? ValueType.FLOAT_ARRAY : ValueType.FLOAT;
            case 3:
                return isArray ? ValueType.ENUM_ARRAY : ValueType.ENUM;
            case 4:
                return isArray ? ValueType.STRING_ARRAY : ValueType.STRING;
            case 5:
                return isArray ? ValueType.LONG_ARRAY : ValueType.LONG;
            case 6:
                return isArray ? ValueType.DOUBLE_ARRAY : ValueType.DOUBLE;
            default:
                return isArray ? ValueType.NUMBER_ARRAY : ValueType.NUMBER;
        }
    }

    private static VType toValue(PVStructure val, ValueType type, Time time, Alarm alarm) {
        if (!type.isArray()) {
            throw new IllegalArgumentException("The value type should be an array type, but it was not: " + type);
        }
        PVDoubleArray pvDoubleValue = (PVDoubleArray) val.getScalarArrayField(P_A_DOUBLE, ScalarType.pvDouble);
        PVStringArray pvStringValue = (PVStringArray) val.getScalarArrayField(P_A_STRING, ScalarType.pvString);
        PVIntArray pvIntValue = (PVIntArray) val.getScalarArrayField(P_A_INT, ScalarType.pvInt);

        StringArrayData sval = new StringArrayData();
        pvStringValue.get(0, pvStringValue.getLength(), sval);
        DoubleArrayData dval = new DoubleArrayData();
        pvDoubleValue.get(0, pvDoubleValue.getLength(), dval);
        IntArrayData ival = new IntArrayData();
        pvIntValue.get(0, pvIntValue.getLength(), ival);

        Display display = ValueFactory.displayNone();
        switch (type) {
            case INT_ARRAY:
                return ValueFactory.newVIntArray(new ArrayInt(ival.data), alarm, time, display);
            case LONG_ARRAY:
                long[] lvals = new long[ival.data.length];
                for (int i = 0; i < lvals.length; i++) {
                    lvals[i] = ival.data[i];
                }
                return ValueFactory.newVLongArray(new ArrayLong(lvals), alarm, time, display);
            case ENUM_ARRAY:
                List<String> labels = new ArrayList<>();
                int[] values = new int[sval.data.length];
                for (int i = 0; i < sval.data.length; i++) {
                    int idx = labels.indexOf(sval.data[i]);
                    if (idx < 0) {
                        idx = labels.size();
                        labels.add(sval.data[i]);
                    }
                    values[i] = idx;
                }
                return ValueFactory.newVEnumArray(new ArrayInt(values), labels, alarm, time);
            case DOUBLE_ARRAY:
                return ValueFactory.newVDoubleArray(new ArrayDouble(dval.data), alarm, time, display);
            case FLOAT_ARRAY:
                float[] fvals = new float[dval.data.length];
                for (int i = 0; i < fvals.length; i++) {
                    fvals[i] = (float) dval.data[i];
                }
                return ValueFactory.newVFloatArray(new ArrayFloat(fvals), alarm, time, display);
            case NUMBER_ARRAY:
                try {
                    double[] dvals = new double[sval.data.length];
                    for (int i = 0; i < dvals.length; i++) {
                        dvals[i] = Double.parseDouble(sval.data[i]);
                    }
                    return ValueFactory.newVDoubleArray(new ArrayDouble(dvals), alarm, time, display);
                } catch (NumberFormatException e) {
                    // fall through to string
                }
            case STRING_ARRAY:
            default:
                return ValueFactory.newVStringArray(Arrays.asList(sval.data), alarm, time);
        }
    }

    private static VType toValue(String sval, double dval, long lval, ValueType type, Time time, Alarm alarm) {
        if (type.isArray()) {
            throw new IllegalArgumentException("The value type should not be an array type, but it was: " + type);
        }
        Display display = ValueFactory.displayNone();
        switch (type) {
            case INT:
                return ValueFactory.newVInt(Integer.valueOf((int) lval), alarm, time, display);
            case LONG:
                return ValueFactory.newVLong(lval, alarm, time, display);
            case ENUM:
                return ValueFactory.newVEnum(0, Arrays.asList(sval), alarm, time);
            case DOUBLE:
                return ValueFactory.newVDouble(dval, alarm, time, display);
            case FLOAT:
                return ValueFactory.newVFloat((float) dval, alarm, time, display);
            case NUMBER:
                try {
                    return ValueFactory.newVDouble(Double.parseDouble(sval), alarm, time, display);
                } catch (NumberFormatException e) {
                    // fall through to string
                }
            case STRING:
            default:
                return ValueFactory.newVString(sval, alarm, time);
        }
    }
}
