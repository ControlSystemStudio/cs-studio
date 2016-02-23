package org.csstudio.saverestore.masar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VLongArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.PVUnionArray;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Union;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * <code>MasarClientTest</code> tests the methods of the {@link MasarClient}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MasarClientTest {

    private MasarClient client;
    private Branch service = new Branch();
    private long time = System.currentTimeMillis();
    // seconds resolution is needed
    private Date date1 = new Date((time / 1000) * 1000 - 10000);
    private Date date2 = new Date((time / 1000) * 1000 - 25000);
    private Date date3 = new Date((time / 1000) * 1000 - 30000);

    @Before
    public void setUp() throws Exception {
        RPCRequester requester = mock(RPCRequester.class);
        when(requester.waitUntilConnected()).thenReturn(true);
        when(requester.isConnected()).thenReturn(true);
        when(requester.request(any(PVStructure.class))).thenAnswer(new Answer<PVStructure>() {
            @Override
            public PVStructure answer(InvocationOnMock invocation) throws Throwable {
                PVStructure in = (PVStructure) invocation.getArguments()[0];
                PVStructure structure = null;
                if (MasarConstants.FC_LOAD_BASE_LEVELS.equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_CONFIGS);
                    PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
                    PVStringArray field = (PVStringArray) struct.getScalarArrayField(MasarConstants.P_BASE_LEVEL_NAME,
                        ScalarType.pvString);
                    field.put(0, 3, new String[] { "config1", "config2", "config3" }, 0);
                } else if (MasarConstants.FC_LOAD_SAVE_SETS
                    .equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
                    structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SAVE_SET);
                    PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
                    ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_INDEX, ScalarType.pvLong)).put(0,
                        3, new long[] { 1, 2, 3 }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_NAME, ScalarType.pvString))
                        .put(0, 3, new String[] { "name1", "name2", "name3" }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_DESCRIPTION,
                        ScalarType.pvString)).put(0, 3, new String[] { "desc1", "desc2", "desc3" }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_DATE, ScalarType.pvString)).put(
                        0, 3, new String[] { format.format(date1), format.format(date2), format.format(date3) }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_VERSION, ScalarType.pvString))
                        .put(0, 3, new String[] { "ver1", "ver2", "ver3" }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_STATUS, ScalarType.pvString))
                        .put(0, 3, new String[] { "sts1", "sts2", "sts3" }, 0);
                } else if (MasarConstants.FC_LOAD_SNAPSHOTS
                    .equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    PVString field = in.getStringField(MasarConstants.F_EVENTID);
                    if (field != null && "5".equals(field.get())) {
                        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
                        structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SNAPSHOT);
                        PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_EVENT_ID, ScalarType.pvLong)).put(0,
                            1, new long[] { 42 }, 0);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_ID, ScalarType.pvLong)).put(0,
                            1, new long[] { 12345 }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_COMMENT, ScalarType.pvString))
                            .put(0, 1, new String[] { "taz-mania rules" }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_EVENT_TIME, ScalarType.pvString))
                            .put(0, 1, new String[] { format.format(date1) }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_USER, ScalarType.pvString)).put(0,
                            1, new String[] { "taz-mania" }, 0);
                    } else if (in.getStringField(MasarConstants.F_CONFIGID) == null) {
                        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
                        structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SNAPSHOT);
                        PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_EVENT_ID, ScalarType.pvLong)).put(0,
                            3, new long[] { 1, 2, 3 }, 0);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_ID, ScalarType.pvLong)).put(0,
                            3, new long[] { 1, 1, 1 }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_COMMENT, ScalarType.pvString))
                            .put(0, 3, new String[] { "comment1", "comment2", "comment3" }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_EVENT_TIME, ScalarType.pvString))
                            .put(0, 3,
                                new String[] { format.format(date1), format.format(date2), format.format(date3) }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_USER, ScalarType.pvString)).put(0,
                            3, new String[] { "bugsbunny", "elmerfudd", "daffyduck" }, 0);
                    } else if (in.getStringField(MasarConstants.F_EVENTID) == null) {
                        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
                        structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SNAPSHOT);
                        PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_EVENT_ID, ScalarType.pvLong)).put(0,
                            1, new long[] { 42 }, 0);
                        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_ID, ScalarType.pvLong)).put(0,
                            1, new long[] { 12345 }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_COMMENT, ScalarType.pvString))
                            .put(0, 1, new String[] { "taz-mania rules" }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_EVENT_TIME, ScalarType.pvString))
                            .put(0, 1, new String[] { format.format(date1) }, 0);
                        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_USER, ScalarType.pvString)).put(0,
                            1, new String[] { "taz-mania" }, 0);
                    }
                } else if (MasarConstants.FC_LOAD_SNAPSHOT_DATA
                    .equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    PVStructure struct = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_VSNAPSHOT);
                    ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_SECONDS, ScalarType.pvLong))
                        .put(0, 3, new long[] { 1, 2, 3 }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_MESSAGE,
                        ScalarType.pvString)).put(0, 3, new String[] { "ok1", "ok2", "ok3" }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
                        ScalarType.pvString)).put(0, 3, new String[] { "channel1", "channel2", "channel3" }, 0);
                    ((PVBooleanArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_IS_CONNECTED,
                        ScalarType.pvBoolean)).put(0, 3, new boolean[] { true, true, false }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_DBR_TYPE, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_NANOS, ScalarType.pvInt)).put(0,
                        3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_USER_TAG, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_SEVERITY,
                        ScalarType.pvInt)).put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_STATUS, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    Union uu1 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u1 = PVDataFactory.getPVDataCreate().createPVUnion(uu1);
                    ScalarArray s1 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvDouble);
                    PVDoubleArray a1 = (PVDoubleArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s1);
                    a1.put(0, 3, new double[] { 1, 2, 3 }, 0);
                    u1.set(a1);
                    Union uu2 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u2 = PVDataFactory.getPVDataCreate().createPVUnion(uu2);
                    ScalarArray s2 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong);
                    PVLongArray a2 = (PVLongArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s2);
                    a2.put(0, 3, new long[] { 1, 2, 3 }, 0);
                    u2.set(a2);
                    Union uu3 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u3 = PVDataFactory.getPVDataCreate().createPVUnion(uu3);
                    Scalar s3 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
                    PVString a3 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s3);
                    a3.put("disabled");
                    u3.set(a3);
                    ((PVUnionArray) struct.getUnionArrayField(MasarConstants.P_STRUCTURE_VALUE)).put(0, 3,
                        new PVUnion[] { u1, u2, u3 }, 0);
                    structure = struct;
                } else if (MasarConstants.FC_SAVE_SNAPSHOT.equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    if ("42".equals(in.getStringField(MasarConstants.F_EVENTID).get())) {
                        if (!"Sylvester".equals(in.getStringField(MasarConstants.F_USER).get())) {
                            fail("Incorrect username sent to service");
                        } else if (!"saprans succotash".equals(in.getStringField(MasarConstants.F_DESCRIPTION).get())) {
                            fail("Incorrect comment sent to service");
                        }
                        structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SAVE_SNAPSHOT);
                        PVBoolean status = (PVBoolean) structure.getBooleanField(MasarConstants.P_STRUCTURE_VALUE);
                        status.put(true);
                    } else if ("45".equals(in.getStringField(MasarConstants.F_EVENTID).get())) {
                        structure = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_SAVE_SNAPSHOT);
                        PVBoolean status = (PVBoolean) structure.getBooleanField(MasarConstants.P_STRUCTURE_VALUE);
                        status.put(false);
                        PVStructure struct = structure.getStructureField(MasarConstants.P_ALARM);
                        struct.getStringField(MasarConstants.P_MESSAGE)
                            .put("Aw, the poor puddy tat! He fall down and go... BOOM!");
                    } else {
                        fail("Iconrrect event id sent to service");
                    }
                } else if (MasarConstants.FC_TAKE_SNAPSHOT.equals(in.getStringField(MasarConstants.F_FUNCTION).get())) {
                    PVStructure struct = PVDataFactory.getPVDataCreate().createPVStructure(Utilities.STRUCT_TAKE_SNAPSHOT);
                    ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_SECONDS, ScalarType.pvLong))
                        .put(0, 3, new long[] { 1, 2, 3 }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_MESSAGE,
                        ScalarType.pvString)).put(0, 3, new String[] { "ok1", "ok2", "ok3" }, 0);
                    ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
                        ScalarType.pvString)).put(0, 3, new String[] { "channel1", "channel2", "channel3" }, 0);
                    ((PVBooleanArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_IS_CONNECTED,
                        ScalarType.pvBoolean)).put(0, 3, new boolean[] { true, true, false }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_DBR_TYPE, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_NANOS, ScalarType.pvInt)).put(0,
                        3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_USER_TAG, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_SEVERITY,
                        ScalarType.pvInt)).put(0, 3, new int[] { 1, 2, 3 }, 0);
                    ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_STATUS, ScalarType.pvInt))
                        .put(0, 3, new int[] { 1, 2, 3 }, 0);
                    Union uu1 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u1 = PVDataFactory.getPVDataCreate().createPVUnion(uu1);
                    ScalarArray s1 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvDouble);
                    PVDoubleArray a1 = (PVDoubleArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s1);
                    a1.put(0, 3, new double[] { 1, 2, 3 }, 0);
                    u1.set(a1);
                    Union uu2 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u2 = PVDataFactory.getPVDataCreate().createPVUnion(uu2);
                    ScalarArray s2 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong);
                    PVLongArray a2 = (PVLongArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s2);
                    a2.put(0, 3, new long[] { 1, 2, 3 }, 0);
                    u2.set(a2);
                    Union uu3 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
                    PVUnion u3 = PVDataFactory.getPVDataCreate().createPVUnion(uu3);
                    Scalar s3 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
                    PVString a3 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s3);
                    a3.put("disabled");
                    u3.set(a3);
                    ((PVUnionArray) struct.getUnionArrayField(MasarConstants.P_STRUCTURE_VALUE)).put(0, 3,
                        new PVUnion[] { u1, u2, u3 }, 0);
                    PVStructure timeStruct = struct.getStructureField(MasarConstants.P_TIMESTAMP);
                    timeStruct.getLongField(MasarConstants.P_SECONDS).put(12345);
                    timeStruct.getIntField(MasarConstants.P_NANOS).put(54321);
                    timeStruct.getIntField(MasarConstants.P_USER_TAG).put(42);
                    structure = struct;
                }
                return structure;
            }
        });

        client = new MasarClient();
        java.lang.reflect.Field f = MasarClient.class.getDeclaredField("channelRPCRequester");
        f.setAccessible(true);
        f.set(client, requester);
        f = MasarClient.class.getDeclaredField("selectedService");
        f.setAccessible(true);
        f.set(client, service.getShortName());
    }

    @Test
    public void testGetSystemConfigs() throws MasarException {
        List<BaseLevel> configs = client.getSystemConfigs(service);
        assertEquals(4, configs.size());
        assertEquals(new BaseLevel(service, "all", "all"), configs.get(0));
        assertEquals(new BaseLevel(service, "config1", "config1"), configs.get(1));
        assertEquals(new BaseLevel(service, "config2", "config2"), configs.get(2));
        assertEquals(new BaseLevel(service, "config3", "config3"), configs.get(3));
    }

    @Test
    public void testGetSaveSets() throws MasarException {
        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
        BaseLevel base = new BaseLevel(service, "all", "all");
        List<SaveSet> sets = client.getSaveSets(Optional.of(base), service);
        assertEquals(3, sets.size());
        SaveSet set = sets.get(0);
        assertEquals(service, set.getBranch());
        assertEquals(base, set.getBaseLevel().get());
        assertEquals(MasarDataProvider.ID, set.getDataProviderId());
        assertArrayEquals(new String[] { "name1" }, set.getPath());
        Map<String, String> parameters = set.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_INDEX));
        assertEquals("desc1", parameters.get(MasarConstants.P_CONFIG_DESCRIPTION));
        assertEquals("ver1", parameters.get(MasarConstants.P_CONFIG_VERSION));
        assertEquals("sts1", parameters.get(MasarConstants.P_CONFIG_STATUS));
        assertEquals(format.format(date1), parameters.get(MasarConstants.P_CONFIG_DATE));

        set = sets.get(1);
        assertEquals(service, set.getBranch());
        assertEquals(base, set.getBaseLevel().get());
        assertEquals(MasarDataProvider.ID, set.getDataProviderId());
        assertArrayEquals(new String[] { "name2" }, set.getPath());
        parameters = set.getParameters();
        assertEquals("2", parameters.get(MasarConstants.P_CONFIG_INDEX));
        assertEquals("desc2", parameters.get(MasarConstants.P_CONFIG_DESCRIPTION));
        assertEquals("ver2", parameters.get(MasarConstants.P_CONFIG_VERSION));
        assertEquals("sts2", parameters.get(MasarConstants.P_CONFIG_STATUS));
        assertEquals(format.format(date2), parameters.get(MasarConstants.P_CONFIG_DATE));

        set = sets.get(2);
        assertEquals(service, set.getBranch());
        assertEquals(base, set.getBaseLevel().get());
        assertEquals(MasarDataProvider.ID, set.getDataProviderId());
        assertArrayEquals(new String[] { "name3" }, set.getPath());
        parameters = set.getParameters();
        assertEquals("3", parameters.get(MasarConstants.P_CONFIG_INDEX));
        assertEquals("desc3", parameters.get(MasarConstants.P_CONFIG_DESCRIPTION));
        assertEquals("ver3", parameters.get(MasarConstants.P_CONFIG_VERSION));
        assertEquals("sts3", parameters.get(MasarConstants.P_CONFIG_STATUS));
        assertEquals(format.format(date3), parameters.get(MasarConstants.P_CONFIG_DATE));
    }

    @Test
    public void testGetSnapshots() throws MasarException, ParseException {
        BaseLevel base = new BaseLevel(service, "all", "all");
        SaveSet set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID);
        List<Snapshot> snaps = client.getSnapshots(set);

        assertEquals(3, snaps.size());
        Snapshot snapshot = snaps.get(0);
        assertEquals(set, snapshot.getSaveSet());
        assertEquals("comment1", snapshot.getComment());
        assertEquals("bugsbunny", snapshot.getOwner());
        assertEquals(date1, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        Map<String, String> parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("1", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        snapshot = snaps.get(1);
        assertEquals(set, snapshot.getSaveSet());
        assertEquals("comment2", snapshot.getComment());
        assertEquals("elmerfudd", snapshot.getOwner());
        assertEquals(date2, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("2", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        snapshot = snaps.get(2);
        assertEquals(set, snapshot.getSaveSet());
        assertEquals("comment3", snapshot.getComment());
        assertEquals("daffyduck", snapshot.getOwner());
        assertEquals(date3, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("3", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        Map<String, String> prms = new HashMap<>();
        prms.put(MasarConstants.P_CONFIG_INDEX, "12345");
        set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID, prms);
        snaps = client.getSnapshots(set);

        assertEquals(1, snaps.size());
        snapshot = snaps.get(0);
        assertEquals(set, snapshot.getSaveSet());
        assertEquals("taz-mania rules", snapshot.getComment());
        assertEquals("taz-mania", snapshot.getOwner());
        assertEquals(date1, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("12345", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("42", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));
    }

    @Test
    public void testFindSnapshots() throws MasarException, ParseException {
        List<Snapshot> snaps = client.findSnapshots(service, "comment", true, true, Optional.empty(), Optional.empty());

        assertEquals(3, snaps.size());
        Snapshot snapshot = snaps.get(0);
        assertEquals("comment1", snapshot.getComment());
        assertEquals("bugsbunny", snapshot.getOwner());
        assertEquals(date1, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        Map<String, String> parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("1", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        snapshot = snaps.get(1);
        assertEquals("comment2", snapshot.getComment());
        assertEquals("elmerfudd", snapshot.getOwner());
        assertEquals(date2, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("2", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        snapshot = snaps.get(2);
        assertEquals("comment3", snapshot.getComment());
        assertEquals("daffyduck", snapshot.getOwner());
        assertEquals(date3, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("3", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));

        Optional<Snapshot> s = client.findSnapshotById(service, 5);
        assertTrue(s.isPresent());
        snapshot = s.get();
        assertEquals("taz-mania rules", snapshot.getComment());
        assertEquals("taz-mania", snapshot.getOwner());
        assertEquals(date1, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("12345", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("42", parameters.get(MasarConstants.PARAM_SNAPSHOT_ID));
    }

    @Test
    public void testLoadSnapshotData() throws MasarException, ParseException {
        BaseLevel base = new BaseLevel(service, "all", "all");
        SaveSet set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID);
        Map<String, String> parameters = new HashMap<>();
        parameters.put(MasarConstants.PARAM_SNAPSHOT_ID, "42");
        Snapshot snap = new Snapshot(set, new Date(time), "blabla", "taz-mania", parameters, new ArrayList<>(0));
        VSnapshot snapshot = client.loadSnapshotData(snap);

        assertEquals(Arrays.asList("channel1", "channel2", "channel3"), snapshot.getNames());
        assertEquals(time, snapshot.getTimestamp().toDate().getTime());
        List<VType> values = snapshot.getValues();

        VDoubleArray v1 = (VDoubleArray) values.get(0);
        assertEquals(3, v1.getData().size());
        assertEquals(1, v1.getData().getDouble(0), 0);
        assertEquals(2, v1.getData().getDouble(1), 0);
        assertEquals(3, v1.getData().getDouble(2), 0);
        assertEquals(AlarmSeverity.MINOR, v1.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(1).getName(), v1.getAlarmName());
        Timestamp t1 = Timestamp.of(1, 1);
        assertEquals(t1, v1.getTimestamp());

        VLongArray v2 = (VLongArray) values.get(1);
        assertEquals(3, v2.getData().size());
        assertEquals(1, v2.getData().getLong(0), 0);
        assertEquals(2, v2.getData().getLong(1), 0);
        assertEquals(3, v2.getData().getLong(2), 0);
        assertEquals(AlarmSeverity.MAJOR, v2.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(2).getName(), v2.getAlarmName());
        Timestamp t2 = Timestamp.of(2, 2);
        assertEquals(t2, v2.getTimestamp());

        VString v3 = (VString) values.get(2);
        assertEquals("disabled", v3.getValue());
        assertEquals(AlarmSeverity.INVALID, v3.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(3).getName(), v3.getAlarmName());
        Timestamp t3 = Timestamp.of(3, 3);
        assertEquals(t3, v3.getTimestamp());

        snap = new Snapshot(set, new Date(), "blabla", "taz-mania");
        try {
            snapshot = client.loadSnapshotData(snap);
            fail("Exception should occur, because the event id parameter is missing");
        } catch (MasarException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testLoadSaveSetData() throws MasarException, ParseException {
        BaseLevel base = new BaseLevel(service, "all", "all");
        SaveSet set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID);
        SaveSetData data = client.loadSaveSetData(set);
        assertEquals(Arrays.asList("channel1", "channel2", "channel3"), data.getPVList());
        assertEquals(new ArrayList<>(0), data.getDeltaList());
        assertEquals(new ArrayList<>(0), data.getReadbackList());
    }

    @Test
    public void testSaveSnapshot() throws MasarException {
        System.setProperty("user.name", "Sylvester");
        BaseLevel base = new BaseLevel(service, "all", "all");
        SaveSet set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID);
        Map<String, String> parameters = new HashMap<>();
        parameters.put(MasarConstants.PARAM_SNAPSHOT_ID, "42");
        Snapshot snap = new Snapshot(set, new Date(), "blabla", "taz-mania", parameters, new ArrayList<>(0));
        VSnapshot snapshot = client.loadSnapshotData(snap);

        VSnapshot snp = client.saveSnapshot(snapshot, "saprans succotash");
        assertNotSame(snapshot, snp);
        assertTrue(snp.equalsExceptSnapshot(snapshot));
        assertEquals("saprans succotash", snp.getSnapshot().get().getComment());
        assertEquals("Sylvester", snp.getSnapshot().get().getOwner());

        parameters.put(MasarConstants.PARAM_SNAPSHOT_ID, "45");
        snap = new Snapshot(set, new Date(), "blabla", "taz-mania", parameters, new ArrayList<>(0));
        snapshot = new VSnapshot(snap, Arrays.asList("a"), Arrays.asList(VNoData.INSTANCE), Timestamp.now(), null);
        try {
            snapshot = client.saveSnapshot(snapshot, "some comment");
            fail("Service should send an error message");
        } catch (MasarException e) {
            assertEquals("Aw, the poor puddy tat! He fall down and go... BOOM!", e.getMessage());
        }

        snap = new Snapshot(set, new Date(), "blabla", "taz-mania");
        snapshot = new VSnapshot(snap, Arrays.asList("a"), Arrays.asList(VNoData.INSTANCE), Timestamp.now(), null);
        try {
            snapshot = client.saveSnapshot(snapshot, "some comment");
            fail("Exception should occur, because snapshot id is missing");
        } catch (MasarException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testTakeSnapshot() throws MasarException {
        BaseLevel base = new BaseLevel(service, "all", "all");
        Map<String,String> parameters = new HashMap<>();
        parameters.put(MasarConstants.P_CONFIG_NAME, "granny");
        SaveSet set = new SaveSet(service, Optional.of(base), new String[] { "set" }, MasarDataProvider.ID, parameters);
        VSnapshot snapshot = client.takeSnapshot(set);

        assertEquals(Arrays.asList("channel1", "channel2", "channel3"), snapshot.getNames());
        assertEquals(Timestamp.of(12345, 54321), snapshot.getTimestamp());
        assertEquals("42", snapshot.getSnapshot().get().getParameters().get(MasarConstants.PARAM_SNAPSHOT_ID));
        List<VType> values = snapshot.getValues();

        VDoubleArray v1 = (VDoubleArray) values.get(0);
        assertEquals(3, v1.getData().size());
        assertEquals(1, v1.getData().getDouble(0), 0);
        assertEquals(2, v1.getData().getDouble(1), 0);
        assertEquals(3, v1.getData().getDouble(2), 0);
        assertEquals(AlarmSeverity.MINOR, v1.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(1).getName(), v1.getAlarmName());
        Timestamp t1 = Timestamp.of(1, 1);
        assertEquals(t1, v1.getTimestamp());

        VLongArray v2 = (VLongArray) values.get(1);
        assertEquals(3, v2.getData().size());
        assertEquals(1, v2.getData().getLong(0), 0);
        assertEquals(2, v2.getData().getLong(1), 0);
        assertEquals(3, v2.getData().getLong(2), 0);
        assertEquals(AlarmSeverity.MAJOR, v2.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(2).getName(), v2.getAlarmName());
        Timestamp t2 = Timestamp.of(2, 2);
        assertEquals(t2, v2.getTimestamp());

        VString v3 = (VString) values.get(2);
        assertEquals("disabled", v3.getValue());
        assertEquals(AlarmSeverity.INVALID, v3.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(3).getName(), v3.getAlarmName());
        Timestamp t3 = Timestamp.of(3, 3);
        assertEquals(t3, v3.getTimestamp());
    }
}
