package org.csstudio.saverestore.masar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.security.auth.Subject;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.security.SecuritySupport;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.epics.pvdata.pv.BooleanArrayData;
import org.epics.pvdata.pv.ByteArrayData;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.FloatArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.LongArrayData;
import org.epics.pvdata.pv.PVArray;
import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVByte;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVDouble;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVFloat;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLong;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShort;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUByte;
import org.epics.pvdata.pv.PVUByteArray;
import org.epics.pvdata.pv.PVUInt;
import org.epics.pvdata.pv.PVUIntArray;
import org.epics.pvdata.pv.PVULong;
import org.epics.pvdata.pv.PVULongArray;
import org.epics.pvdata.pv.PVUShort;
import org.epics.pvdata.pv.PVUShortArray;
import org.epics.pvdata.pv.PVUnionArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.ShortArrayData;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.UnionArrayData;

import gov.aps.jca.dbr.Severity;

/**
 *
 * <code>MasarUtilities</code> provides utility methods for converting the MASAR data to save and restore data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class MasarUtilities {

    private MasarUtilities() {
    }

    /**
     * Transform the result structure of <code>retrieveServiceConfigs</code> call to a list of beamline sets.
     *
     * @param result the result structure
     * @param branch the service descriptor
     * @param baseLevel the selected base level
     * @return the list of beamline sets
     */
    static List<BeamlineSet> createBeamlineSetsList(PVStructure result, Branch service, Optional<BaseLevel> baseLevel) {
        PVStructure value = result.getStructureField(MasarConstants.P_STRUCTURE_VALUE);
        PVLongArray pvIndices = (PVLongArray) value.getScalarArrayField(MasarConstants.P_CONFIG_INDEX,
            ScalarType.pvLong);
        PVStringArray pvNames = (PVStringArray) value.getScalarArrayField(MasarConstants.P_CONFIG_NAME,
            ScalarType.pvString);
        PVStringArray pvDesciptions = (PVStringArray) value.getScalarArrayField(MasarConstants.P_CONFIG_DESCRIPTION,
            ScalarType.pvString);
        PVStringArray pvDates = (PVStringArray) value.getScalarArrayField(MasarConstants.P_CONFIG_DATE,
            ScalarType.pvString);
        PVStringArray pvVersions = (PVStringArray) value.getScalarArrayField(MasarConstants.P_CONFIG_VERSION,
            ScalarType.pvString);
        PVStringArray pvStatuses = (PVStringArray) value.getScalarArrayField(MasarConstants.P_CONFIG_STATUS,
            ScalarType.pvString);

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
            parameters.put(MasarConstants.P_CONFIG_NAME, names.data[i]);
            parameters.put(MasarConstants.P_CONFIG_INDEX, String.valueOf(indices.data[i]));
            parameters.put(MasarConstants.P_CONFIG_DESCRIPTION, descriptions.data[i]);
            parameters.put(MasarConstants.P_CONFIG_DATE, dates.data[i]);
            parameters.put(MasarConstants.P_CONFIG_VERSION, versions.data[i]);
            parameters.put(MasarConstants.P_CONFIG_STATUS, statuses.data[i]);
            beamlines.add(
                new BeamlineSet(service, baseLevel, new String[] { names.data[i] }, MasarDataProvider.ID, parameters));
        }
        return beamlines;
    }

    /**
     * Transform the result of the <code>retrieveServiceEvents</code> to a list of snapshots.
     *
     * @param result the V4 result structure
     * @param beamlineSetSupplier the function providing the beamline set for each snapshot; the function receives the
     *            ID of the configuration (beamline set) and returns the best possible value for this id
     * @return the list of snapshots
     * @throws ParseException if the date of snapshot could not be parsed
     */
    static List<Snapshot> createSnapshotsList(PVStructure result, Function<String, BeamlineSet> beamlineSetSupplier)
        throws ParseException {
        PVLongArray pvEvents = (PVLongArray) result.getScalarArrayField(MasarConstants.P_EVENT_ID, ScalarType.pvLong);
        PVLongArray pvConfigs = (PVLongArray) result.getScalarArrayField(MasarConstants.P_CONFIG_ID, ScalarType.pvLong);
        PVStringArray pvComments = (PVStringArray) result.getScalarArrayField(MasarConstants.P_COMMENT,
            ScalarType.pvString);
        PVStringArray pvTimes = (PVStringArray) result.getScalarArrayField(MasarConstants.P_EVENT_TIME,
            ScalarType.pvString);
        PVStringArray pvUsers = (PVStringArray) result.getScalarArrayField(MasarConstants.P_USER, ScalarType.pvString);

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
        SimpleDateFormat format = MasarConstants.DATE_FORMAT.get();
        for (int i = 0; i < events.data.length; i++) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(MasarConstants.P_EVENT_ID, String.valueOf(events.data[i]));
            parameters.put(MasarConstants.P_CONFIG_ID, String.valueOf(configs.data[i]));
            Date date = format.parse(times.data[i]);
            snapshots.add(new Snapshot(beamlineSetSupplier.apply(String.valueOf(configs.data[i])), date,
                comments.data[i].trim(), users.data[i].trim(), parameters));
        }
        return snapshots;
    }

    /**
     * Transform the result structure to a VSnapshot. The result is expected to be either the return value of the
     * saveSnapshot ({@link #takeSnapshot(BeamlineSet)}) or retrieveSnapshot ({@link #loadSnapshotData(Snapshot)}
     * command.
     *
     * @param result the result structure
     * @param snapshot the snapshot which was taken or being loaded
     * @param snapshotTime the time for the returned snapshot data
     * @return the VSnapshot
     */
    static VSnapshot resultToVSnapshot(PVStructure result, Snapshot snapshot, Timestamp snapshotTime) {
        PVStringArray pvAlarmMessage = (PVStringArray) result
            .getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_MESSAGE, ScalarType.pvString);
        PVLongArray pvSeconds = (PVLongArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_SECONDS,
            ScalarType.pvLong);
        PVStringArray pvPVName = (PVStringArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_CHANNEL_NAME,
            ScalarType.pvString);
        PVIntArray pvDBRType = (PVIntArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_DBR_TYPE,
            ScalarType.pvInt);
        PVBooleanArray pvIsConnected = (PVBooleanArray) result
            .getScalarArrayField(MasarConstants.P_SNAPSHOT_IS_CONNECTED, ScalarType.pvBoolean);
        PVIntArray pvNanos = (PVIntArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_NANOS, ScalarType.pvInt);
        PVIntArray pvTimestampTag = (PVIntArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_USER_TAG,
            ScalarType.pvInt);
        PVIntArray pvAlarmSeverity = (PVIntArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_SEVERITY,
            ScalarType.pvInt);
        PVIntArray pvAlarmStatus = (PVIntArray) result.getScalarArrayField(MasarConstants.P_SNAPSHOT_ALARM_STATUS,
            ScalarType.pvInt);
        PVUnionArray array = result.getUnionArrayField(MasarConstants.P_STRUCTURE_VALUE);

        StringArrayData pvName = new StringArrayData();
        pvPVName.get(0, pvPVName.getLength(), pvName);
        StringArrayData alarmMessage = new StringArrayData();
        pvAlarmMessage.get(0, pvAlarmMessage.getLength(), alarmMessage);
        LongArrayData seconds = new LongArrayData();
        pvSeconds.get(0, pvSeconds.getLength(), seconds);
        IntArrayData dbrType = new IntArrayData();
        pvDBRType.get(0, pvDBRType.getLength(), dbrType);
        BooleanArrayData isConnected = new BooleanArrayData();
        pvIsConnected.get(0, pvIsConnected.getLength(), isConnected);
        IntArrayData nanos = new IntArrayData();
        pvNanos.get(0, pvNanos.getLength(), nanos);
        IntArrayData timestampTag = new IntArrayData();
        pvTimestampTag.get(0, pvTimestampTag.getLength(), timestampTag);
        IntArrayData alarmSeverity = new IntArrayData();
        pvAlarmSeverity.get(0, pvAlarmSeverity.getLength(), alarmSeverity);
        IntArrayData alarmStatus = new IntArrayData();
        pvAlarmStatus.get(0, pvAlarmStatus.getLength(), alarmStatus);
        UnionArrayData data = new UnionArrayData();
        array.get(0, array.getLength(), data);

        int length = pvName.data.length;
        List<String> names = new ArrayList<>(length);
        List<VType> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            names.add(pvName.data[i]);
            Time time = ValueFactory.newTime(Timestamp.of(seconds.data[i], nanos.data[i]));
            Alarm alarm = ValueFactory.newAlarm(fromEpics(alarmSeverity.data[i]),
                gov.aps.jca.dbr.Status.forValue(alarmStatus.data[i]).getName());
            boolean isarray = data.data[i].get() instanceof PVArray;
            values.add(isarray ? toValue((PVArray) data.data[i].get(), time, alarm)
                : toValue(data.data[i].get(), time, alarm));
        }
        return new VSnapshot(snapshot, names, values, snapshotTime, null);
    }

    /**
     * Transform EPICS numeric severity to DIIRT severity.
     *
     * @param severity the epics severity code
     * @return DIIRT severity
     */
    private static AlarmSeverity fromEpics(int severity) {
        Severity s = Severity.forValue(severity);
        if (Severity.NO_ALARM.isEqualTo(s)) {
            return AlarmSeverity.NONE;
        } else if (Severity.MINOR_ALARM.isEqualTo(s)) {
            return AlarmSeverity.MINOR;
        } else if (Severity.MAJOR_ALARM.isEqualTo(s)) {
            return AlarmSeverity.MAJOR;
        } else if (Severity.INVALID_ALARM.isEqualTo(s)) {
            return AlarmSeverity.INVALID;
        } else {
            return AlarmSeverity.UNDEFINED;
        }
    }

    /**
     * Transform the value of the PVArray to a VType. The value is expected to be a {@link PVScalarArray} which means
     * that is is an array of primitive types. The {@link Display} of the value is always <code>none</code>.
     *
     * @param val the value to transform
     * @param time the time to use for the value
     * @param alarm the alarm to use for the value
     * @return the {@link VType} representing the input parameters
     */
    private static VType toValue(PVArray val, Time time, Alarm alarm) {
        if (!(val instanceof PVScalarArray)) {
            throw new IllegalArgumentException(
                "The value type should be a scalar array type, but it was not: " + val.getClass());
        }
        Display display = ValueFactory.displayNone();
        ScalarType type = ((PVScalarArray) val).getScalarArray().getElementType();
        switch (type) {
            case pvBoolean:
                BooleanArrayData booval = new BooleanArrayData();
                ((PVBooleanArray) val).get(0, val.getLength(), booval);
                return ValueFactory.newVBooleanArray(new ArrayBoolean(booval.data), alarm, time);
            case pvByte:
                ByteArrayData bval = new ByteArrayData();
                ((PVByteArray) val).get(0, val.getLength(), bval);
                return ValueFactory.newVNumberArray(new ArrayByte(bval.data), alarm, time, display);
            case pvUByte:
                ByteArrayData buval = new ByteArrayData();
                ((PVUByteArray) val).get(0, val.getLength(), buval);
                return ValueFactory.newVNumberArray(new ArrayByte(buval.data), alarm, time, display);
            case pvShort:
                ShortArrayData shval = new ShortArrayData();
                ((PVShortArray) val).get(0, val.getLength(), shval);
                return ValueFactory.newVShortArray(new ArrayShort(shval.data), alarm, time, display);
            case pvUShort:
                ShortArrayData shuval = new ShortArrayData();
                ((PVUShortArray) val).get(0, val.getLength(), shuval);
                return ValueFactory.newVShortArray(new ArrayShort(shuval.data), alarm, time, display);
            case pvInt:
                IntArrayData ival = new IntArrayData();
                ((PVIntArray) val).get(0, val.getLength(), ival);
                return ValueFactory.newVIntArray(new ArrayInt(ival.data), alarm, time, display);
            case pvUInt:
                IntArrayData iuval = new IntArrayData();
                ((PVUIntArray) val).get(0, val.getLength(), iuval);
                return ValueFactory.newVIntArray(new ArrayInt(iuval.data), alarm, time, display);
            case pvLong:
                LongArrayData lval = new LongArrayData();
                ((PVLongArray) val).get(0, val.getLength(), lval);
                return ValueFactory.newVLongArray(new ArrayLong(lval.data), alarm, time, display);
            case pvULong:
                LongArrayData luval = new LongArrayData();
                ((PVULongArray) val).get(0, val.getLength(), luval);
                return ValueFactory.newVLongArray(new ArrayLong(luval.data), alarm, time, display);
            case pvDouble:
                DoubleArrayData dval = new DoubleArrayData();
                ((PVDoubleArray) val).get(0, val.getLength(), dval);
                return ValueFactory.newVDoubleArray(new ArrayDouble(dval.data), alarm, time, display);
            case pvFloat:
                FloatArrayData fval = new FloatArrayData();
                ((PVFloatArray) val).get(0, val.getLength(), fval);
                return ValueFactory.newVFloatArray(new ArrayFloat(fval.data), alarm, time, display);
            case pvString:
                StringArrayData sval = new StringArrayData();
                ((PVStringArray) val).get(0, val.getLength(), sval);
                return ValueFactory.newVStringArray(Arrays.asList(sval.data), alarm, time);
        }
        throw new IllegalArgumentException("Cannot transform the " + val + " to vtype.");
    }

    /**
     * Transforms the given scalar value to a VType. The value is expected to be either a {@link PVScalar} or a
     * {@link PVStructure} of the {@link MasarConstants#T_ENUM} type.
     *
     * @param val the value
     * @param time the time of value
     * @param alarm the alarm of the value
     * @return the {@link VType} describing the value
     */
    private static VType toValue(PVField val, Time time, Alarm alarm) {
        if (val instanceof PVScalar) {
            Display display = ValueFactory.displayNone();
            ScalarType type = ((PVScalar) val).getScalar().getScalarType();
            switch (type) {
                case pvBoolean:
                    return ValueFactory.newVBoolean(((PVBoolean) val).get(), alarm, time);
                case pvByte:
                    return ValueFactory.newVByte(((PVByte) val).get(), alarm, time, display);
                case pvUByte:
                    return ValueFactory.newVByte(((PVUByte) val).get(), alarm, time, display);
                case pvShort:
                    return ValueFactory.newVShort(((PVShort) val).get(), alarm, time, display);
                case pvUShort:
                    return ValueFactory.newVShort(((PVUShort) val).get(), alarm, time, display);
                case pvInt:
                    return ValueFactory.newVInt(((PVInt) val).get(), alarm, time, display);
                case pvUInt:
                    return ValueFactory.newVInt(((PVUInt) val).get(), alarm, time, display);
                case pvLong:
                    return ValueFactory.newVLong(((PVLong) val).get(), alarm, time, display);
                case pvULong:
                    return ValueFactory.newVLong(((PVULong) val).get(), alarm, time, display);
                case pvDouble:
                    return ValueFactory.newVDouble(((PVDouble) val).get(), alarm, time, display);
                case pvFloat:
                    return ValueFactory.newVFloat(((PVFloat) val).get(), alarm, time, display);
                case pvString:
                    return ValueFactory.newVString(((PVString) val).get(), alarm, time);
            }
        } else if (val instanceof PVStructure) {
            PVStructure str = (PVStructure) val;
            if (MasarConstants.T_ENUM.equals(str.getStructure().getID())) {
                int index = str.getIntField(MasarConstants.P_ENUM_INDEX).get();
                PVStringArray pvLabels = (PVStringArray) str.getScalarArrayField(MasarConstants.P_ENUM_LABELS,
                    ScalarType.pvString);
                StringArrayData labels = new StringArrayData();
                pvLabels.get(0, pvLabels.getLength(), labels);
                return ValueFactory.newVEnum(index, Arrays.asList(labels.data), alarm, time);
            }
        }
        throw new IllegalArgumentException("Cannot transform the " + val + " to vtype.");
    }

    /**
     * Returns the name of the currently logged in user or the system username if no user is logged in.
     *
     * @return the username
     */
    static String getUser() {
        Subject subj = null;
        try {
            subj = SecuritySupport.getSubject();
        } catch (RuntimeException e) {
            // ignore
        }
        return subj == null ? System.getProperty("user.name") : SecuritySupport.getSubjectName(subj);
    }
}
