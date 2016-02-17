package org.csstudio.saverestore.masar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
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
import org.epics.pvdata.pv.Structure;
import org.epics.pvdata.pv.Union;
import org.junit.Test;

/**
 *
 * <code>MasarUtilitiesTest</code> tests the utility methods defined in the {@link MasarUtilities} class.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MasarUtilitiesTest {

    static final Structure VALUE_STRUCTURE = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_CONFIG_INDEX, MasarConstants.P_CONFIG_NAME, MasarConstants.P_CONFIG_DESCRIPTION,
            MasarConstants.P_CONFIG_DATE, MasarConstants.P_CONFIG_VERSION, MasarConstants.P_CONFIG_STATUS },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString), });

    static final Structure STRUCT_BEAMLINE_SET = FieldFactory.getFieldCreate()
        .createStructure(new String[] { MasarConstants.P_STRUCTURE_VALUE }, new Field[] { VALUE_STRUCTURE });

    static final Structure STRUCT_SNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_EVENT_ID, MasarConstants.P_CONFIG_ID, MasarConstants.P_COMMENT,
            MasarConstants.P_EVENT_TIME, MasarConstants.P_USER },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString), });

    static final Structure STRUCT_VSNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] { MasarConstants.P_SNAPSHOT_ALARM_MESSAGE, MasarConstants.P_SNAPSHOT_SECONDS,
            MasarConstants.P_SNAPSHOT_CHANNEL_NAME, MasarConstants.P_SNAPSHOT_DBR_TYPE,
            MasarConstants.P_SNAPSHOT_IS_CONNECTED, MasarConstants.P_SNAPSHOT_NANOS, MasarConstants.P_SNAPSHOT_USER_TAG,
            MasarConstants.P_SNAPSHOT_ALARM_SEVERITY, MasarConstants.P_SNAPSHOT_ALARM_STATUS,
            MasarConstants.P_STRUCTURE_VALUE },
        new Field[] { FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvBoolean),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvInt),
            FieldFactory.getFieldCreate()
                .createUnionArray(FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0])) });

    @Test
    public void testCreateBeamlineSetList() {
        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
        long time = System.currentTimeMillis();
        Date date1 = new Date(time - 1000);
        Date date2 = new Date(time - 2500);
        Date date3 = new Date(time - 3000);
        PVStructure structure = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_BEAMLINE_SET);
        PVStructure struct = structure.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_INDEX, ScalarType.pvLong)).put(0, 3,
            new long[] { 1, 2, 3 }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_NAME, ScalarType.pvString)).put(0, 3,
            new String[] { "name1", "name2", "name3" }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_DESCRIPTION, ScalarType.pvString)).put(0, 3,
            new String[] { "desc1", "desc2", "desc3" }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_DATE, ScalarType.pvString)).put(0, 3,
            new String[] { format.format(date1), format.format(date2), format.format(date3) }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_VERSION, ScalarType.pvString)).put(0, 3,
            new String[] { "ver1", "ver2", "ver3" }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_STATUS, ScalarType.pvString)).put(0, 3,
            new String[] { "sts1", "sts2", "sts3" }, 0);

        Branch branch = new Branch();
        BaseLevel base = new BaseLevel(branch, "base", "base");
        List<BeamlineSet> sets = MasarUtilities.createBeamlineSetsList(structure, branch, Optional.of(base));

        assertEquals(3, sets.size());
        BeamlineSet set = sets.get(0);
        assertEquals(branch, set.getBranch());
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
        assertEquals(branch, set.getBranch());
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
        assertEquals(branch, set.getBranch());
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
    public void testCreateSnapshotsList() throws ParseException {
        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
        long time = (System.currentTimeMillis() / 1000) * 1000; // seconds precision
        Date date1 = new Date(time - 10000);
        Date date2 = new Date(time - 25000);
        Date date3 = new Date(time - 30000);

        PVStructure struct = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_SNAPSHOT);
        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_EVENT_ID, ScalarType.pvLong)).put(0, 3,
            new long[] { 1, 2, 3 }, 0);
        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_CONFIG_ID, ScalarType.pvLong)).put(0, 3,
            new long[] { 1, 1, 1 }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_COMMENT, ScalarType.pvString)).put(0, 3,
            new String[] { "comment1", "comment2", "comment3" }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_EVENT_TIME, ScalarType.pvString)).put(0, 3,
            new String[] { format.format(date1), format.format(date2), format.format(date3) }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_USER, ScalarType.pvString)).put(0, 3,
            new String[] { "bugsbunny", "elmerfudd", "duffyduck" }, 0);

        Branch branch = new Branch();
        BaseLevel base = new BaseLevel(branch, "base", "base");
        BeamlineSet set = new BeamlineSet(branch, Optional.of(base), new String[] { "name" }, MasarDataProvider.ID);
        List<Snapshot> snaps = MasarUtilities.createSnapshotsList(struct, e -> set);

        assertEquals(3, snaps.size());
        Snapshot snapshot = snaps.get(0);
        assertEquals(set, snapshot.getBeamlineSet());
        assertEquals("comment1", snapshot.getComment());
        assertEquals("bugsbunny", snapshot.getOwner());
        assertEquals(date1, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        Map<String, String> parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("1", parameters.get(MasarConstants.P_EVENT_ID));

        snapshot = snaps.get(1);
        assertEquals(set, snapshot.getBeamlineSet());
        assertEquals("comment2", snapshot.getComment());
        assertEquals("elmerfudd", snapshot.getOwner());
        assertEquals(date2, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("2", parameters.get(MasarConstants.P_EVENT_ID));

        snapshot = snaps.get(2);
        assertEquals(set, snapshot.getBeamlineSet());
        assertEquals("comment3", snapshot.getComment());
        assertEquals("duffyduck", snapshot.getOwner());
        assertEquals(date3, snapshot.getDate());
        assertFalse(snapshot.getTagMessage().isPresent());
        assertFalse(snapshot.getTagName().isPresent());
        parameters = snapshot.getParameters();
        assertEquals("1", parameters.get(MasarConstants.P_CONFIG_ID));
        assertEquals("3", parameters.get(MasarConstants.P_EVENT_ID));
    }

    @Test
    public void testResultToVSnapshot() {
        PVStructure struct = PVDataFactory.getPVDataCreate().createPVStructure(STRUCT_VSNAPSHOT);
        ((PVLongArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_SECONDS, ScalarType.pvLong)).put(0, 3,
            new long[] { 1, 2, 3 }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_MESSAGE, ScalarType.pvString)).put(0, 3,
            new String[] { "ok1", "ok2", "ok3" }, 0);
        ((PVStringArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_CHANNEL_NAME, ScalarType.pvString)).put(0, 3,
            new String[] { "channel1", "channel2", "channel3" }, 0);
        ((PVBooleanArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_IS_CONNECTED, ScalarType.pvBoolean)).put(0, 3,
            new boolean[] { true,true,false }, 0);
        ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_DBR_TYPE, ScalarType.pvInt)).put(0, 3,
            new int[] { 1, 2, 3 }, 0);
        ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_NANOS, ScalarType.pvInt)).put(0, 3,
            new int[] { 1, 2, 3 }, 0);
        ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_USER_TAG, ScalarType.pvInt)).put(0, 3,
            new int[] { 1, 2, 3 }, 0);
        ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_SEVERITY, ScalarType.pvInt)).put(0, 3,
            new int[] { 1, 2, 3 }, 0);
        ((PVIntArray) struct.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_STATUS, ScalarType.pvInt)).put(0, 3,
            new int[] { 1, 2, 3 }, 0);
        Union uu1 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
        PVUnion u1 = PVDataFactory.getPVDataCreate().createPVUnion(uu1);
        ScalarArray s1 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvDouble);
        PVDoubleArray a1 = (PVDoubleArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s1);
        a1.put(0, 3, new double[]{1, 2,3},0);
        u1.set(a1);
        Union uu2 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
        PVUnion u2 = PVDataFactory.getPVDataCreate().createPVUnion(uu2);
        ScalarArray s2 = FieldFactory.getFieldCreate().createScalarArray(ScalarType.pvLong);
        PVLongArray a2 = (PVLongArray) PVDataFactory.getPVDataCreate().createPVScalarArray(s2);
        a2.put(0, 3, new long[]{1, 2,3},0);
        u2.set(a2);
        Union uu3 = FieldFactory.getFieldCreate().createUnion("any", new String[0], new Field[0]);
        PVUnion u3 = PVDataFactory.getPVDataCreate().createPVUnion(uu3);
        Scalar s3 = FieldFactory.getFieldCreate().createScalar(ScalarType.pvString);
        PVString a3 = (PVString) PVDataFactory.getPVDataCreate().createPVScalar(s3);
        a3.put("disabled");
        u3.set(a3);
        ((PVUnionArray) struct.getUnionArrayField(MasarConstants.P_STRUCTURE_VALUE)).put(0, 3,
            new PVUnion[] { u1, u2, u3 }, 0);

        Snapshot s = new Snapshot(new BeamlineSet());
        Timestamp time = Timestamp.now();
        VSnapshot snapshot = MasarUtilities.resultToVSnapshot(struct, s, time);

        assertEquals(Arrays.asList("channel1", "channel2", "channel3"),snapshot.getNames());
        assertEquals(time, snapshot.getTimestamp());
        List<VType> values = snapshot.getValues();

        VDoubleArray v1 = (VDoubleArray) values.get(0);
        assertEquals(3, v1.getData().size());
        assertEquals(1, v1.getData().getDouble(0),0);
        assertEquals(2, v1.getData().getDouble(1),0);
        assertEquals(3, v1.getData().getDouble(2),0);
        assertEquals(AlarmSeverity.MINOR, v1.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(1).getName(), v1.getAlarmName());
        Timestamp t1 = Timestamp.of(1, 1);
        assertEquals(t1, v1.getTimestamp());

        VLongArray v2 = (VLongArray) values.get(1);
        assertEquals(3, v2.getData().size());
        assertEquals(1, v2.getData().getLong(0),0);
        assertEquals(2, v2.getData().getLong(1),0);
        assertEquals(3, v2.getData().getLong(2),0);
        assertEquals(AlarmSeverity.MAJOR, v2.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(2).getName(), v2.getAlarmName());
        Timestamp t2 = Timestamp.of(2, 2);
        assertEquals(t2, v2.getTimestamp());

        VString v3 = (VString) values.get(2);
        assertEquals("disabled", v3.getValue());
        assertEquals(AlarmSeverity.INVALID, v3.getAlarmSeverity());
        assertEquals(gov.aps.jca.dbr.Status.forValue(3).getName(), v3.getAlarmName());
        Timestamp t3 = Timestamp.of(3,3);
        assertEquals(t3, v3.getTimestamp());
   }
}
