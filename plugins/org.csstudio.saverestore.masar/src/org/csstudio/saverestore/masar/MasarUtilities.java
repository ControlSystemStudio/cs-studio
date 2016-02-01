package org.csstudio.saverestore.masar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.security.auth.Subject;

import org.csstudio.saverestore.ValueType;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.security.SecuritySupport;
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
import org.epics.pvdata.pv.BooleanArrayData;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.LongArrayData;
import org.epics.pvdata.pv.PVBooleanArray;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.StructureArrayData;

import gov.aps.jca.dbr.Severity;

/**
 *
 * <code>MasarUtilities</code> provides utility methods for converting the MASAR data to save and restore data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class MasarUtilities implements MasarConstants {

    private MasarUtilities() {
    }

    /**
     * Transform the result of the <code>retrieveServiceEvents</code> to a list of snapshots.
     *
     * @param result the V4 result structure
     * @param beamlineSetSupplier the function providing the beamline set for each snapshot; the function receives
     *          the ID of the configuration (beamline set) and returns the best possible value for this id
     * @return the list of snapshots
     * @throws ParseException if the date of snapshot could not be parsed
     */
    static List<Snapshot> parseSnapshots(PVStructure result, Function<String, BeamlineSet> beamlineSetSupplier)
        throws ParseException {
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
        SimpleDateFormat format = DATE_FORMAT.get();
        for (int i = 0; i < events.data.length; i++) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(P_EVENT_ID, String.valueOf(events.data[i]));
            parameters.put(P_CONFIG_ID, String.valueOf(configs.data[i]));
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
     * @param snapshotTaken true if the result structure is a result of the saveSnapshot command or false if it is
     *            result of the retrieveSnapshot command
     * @return the VSnapshot
     */
    static VSnapshot resultToSnapshot(PVStructure result, Snapshot snapshot, Timestamp snapshotTime,
        boolean snapshotTaken) {

        // These 3 items are identical for take snapshot and load snapshot
        PVStringArray pvAlarmMessage = (PVStringArray) result.getScalarArrayField(P_ALARM_MESSAGE, ScalarType.pvString);
        PVLongArray pvSeconds = (PVLongArray) result.getScalarArrayField(P_SECONDS, ScalarType.pvLong);
        PVStructureArray pvArrayData = result.getStructureArrayField(P_ARRAY_VALUE);

        StringArrayData alarmMessage = new StringArrayData();
        pvAlarmMessage.get(0, pvAlarmMessage.getLength(), alarmMessage);
        LongArrayData seconds = new LongArrayData();
        pvSeconds.get(0, pvSeconds.getLength(), seconds);
        StructureArrayData arrayData = new StructureArrayData();
        pvArrayData.get(0, pvArrayData.getLength(), arrayData);

        // these 4 items have different field names (when doing take snapshot and load snapshot)
        PVStringArray pvPVName = (PVStringArray) result.getScalarArrayField(snapshotTaken ? P_S_CHANNEL_NAME : P_PVNAME,
            ScalarType.pvString);
        PVStringArray pvStringValue = (PVStringArray) result
            .getScalarArrayField(snapshotTaken ? P_S_STRING_VALUE : P_STRING_VALUE, ScalarType.pvString);
        PVDoubleArray pvDoubleValue = (PVDoubleArray) result
            .getScalarArrayField(snapshotTaken ? P_S_DOUBLE_VALUE : P_DOUBLE_VALUE, ScalarType.pvDouble);
        PVLongArray pvLongValue = (PVLongArray) result
            .getScalarArrayField(snapshotTaken ? P_S_LONG_VALUE : P_LONG_VALUE, ScalarType.pvLong);

        StringArrayData pvName = new StringArrayData();
        pvPVName.get(0, pvPVName.getLength(), pvName);
        StringArrayData stringValue = new StringArrayData();
        pvStringValue.get(0, pvStringValue.getLength(), stringValue);
        DoubleArrayData doubleValue = new DoubleArrayData();
        pvDoubleValue.get(0, pvDoubleValue.getLength(), doubleValue);
        LongArrayData longValue = new LongArrayData();
        pvLongValue.get(0, pvLongValue.getLength(), longValue);

        int length = pvName.data.length;
        List<String> names = new ArrayList<>(length);
        List<VType> values = new ArrayList<>(length);

        if (snapshotTaken) {
            // all other items are of different types (when doing take snapshot and load snapshot)
            PVIntArray pvDBRType = (PVIntArray) result.getScalarArrayField(P_S_DBR_TYPE, ScalarType.pvInt);
            PVBooleanArray pvIsConnected = (PVBooleanArray) result.getScalarArrayField(P_IS_CONNECTED,
                ScalarType.pvBoolean);
            PVIntArray pvNanos = (PVIntArray) result.getScalarArrayField(P_NANOS, ScalarType.pvInt);
            PVIntArray pvTimestampTag = (PVIntArray) result.getScalarArrayField(P_TIMESTAMP_TAG, ScalarType.pvInt);
            PVIntArray pvAlarmSeverity = (PVIntArray) result.getScalarArrayField(P_ALARM_SEVERITY, ScalarType.pvInt);
            PVIntArray pvAlarmStatus = (PVIntArray) result.getScalarArrayField(P_ALARM_STATUS, ScalarType.pvInt);
            PVBooleanArray pvIsArray = (PVBooleanArray) result.getScalarArrayField(P_IS_ARRAY, ScalarType.pvInt);

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
            BooleanArrayData isArray = new BooleanArrayData();
            pvIsArray.get(0, pvIsArray.getLength(), isArray);

            for (int i = 0; i < length; i++) {
                names.add(pvName.data[i]);
                Time time = ValueFactory.newTime(Timestamp.of(seconds.data[i], nanos.data[i]));
                Alarm alarm = ValueFactory.newAlarm(fromEpics(alarmSeverity.data[i]),
                    gov.aps.jca.dbr.Status.forValue(alarmStatus.data[i]).getName());
                ValueType vt = toValueType(dbrType.data[i], isArray.data[i]);
                values.add(vt.isArray() ? toValue(arrayData.data[i], vt, time, alarm)
                    : toValue(stringValue.data[i], doubleValue.data[i], longValue.data[i], vt, time, alarm));
            }
        } else {
            PVLongArray pvDBRType = (PVLongArray) result.getScalarArrayField(P_DBR_TYPE, ScalarType.pvLong);
            PVLongArray pvIsConnected = (PVLongArray) result.getScalarArrayField(P_IS_CONNECTED, ScalarType.pvLong);
            PVLongArray pvNanos = (PVLongArray) result.getScalarArrayField(P_NANOS, ScalarType.pvLong);
            PVLongArray pvTimestampTag = (PVLongArray) result.getScalarArrayField(P_TIMESTAMP_TAG, ScalarType.pvLong);
            PVLongArray pvAlarmSeverity = (PVLongArray) result.getScalarArrayField(P_ALARM_SEVERITY, ScalarType.pvLong);
            PVLongArray pvAlarmStatus = (PVLongArray) result.getScalarArrayField(P_ALARM_STATUS, ScalarType.pvLong);
            PVLongArray pvIsArray = (PVLongArray) result.getScalarArrayField(P_IS_ARRAY, ScalarType.pvLong);

            LongArrayData dbrType = new LongArrayData();
            pvDBRType.get(0, pvDBRType.getLength(), dbrType);
            LongArrayData isConnected = new LongArrayData();
            pvIsConnected.get(0, pvIsConnected.getLength(), isConnected);
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

            for (int i = 0; i < length; i++) {
                names.add(pvName.data[i]);
                Time time = ValueFactory.newTime(Timestamp.of(seconds.data[i], (int) nanos.data[i]));
                Alarm alarm = ValueFactory.newAlarm(fromEpics((int) alarmSeverity.data[i]),
                    gov.aps.jca.dbr.Status.forValue((int) alarmStatus.data[i]).getName());
                ValueType vt = toValueType((int) dbrType.data[i], isArray.data[i] != 0);
                values.add(vt.isArray() ? toValue(arrayData.data[i], vt, time, alarm)
                    : toValue(stringValue.data[i], doubleValue.data[i], longValue.data[i], vt, time, alarm));
            }
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
     * Transform EPICS dbr type to the save and restore {@link ValueType}.
     *
     * @param dbrType epics type
     * @param isArray true if is is an array or false if scalar
     * @return the save and restore type
     */
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

    /**
     * Transform array data to DIIRT value.
     *
     * @param val the V4 structure with values
     * @param type the save restore value type to transform to
     * @param time the time for the returned value
     * @param alarm the alarm for the returned value
     * @return the DIIRT value
     */
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

    /**
     * Transform the scalar value to DIIRT value.
     *
     * @param sval the string representation of the value
     * @param dval double representation of the value
     * @param lval integer representation of the value
     * @param type destination value type
     * @param time the time for the returned value
     * @param alarm the alarm for the returned value
     * @return the DIIRT value
     */
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

    /**
     * Returns the name of the currently logged in user or the system username if no user is logged in.
     *
     * @return the username
     */
    static final String getUser() {
        Subject subj = null;
        try {
            subj = SecuritySupport.getSubject();
        } catch (Exception e) {
            // ignore
        }
        return subj == null ? System.getProperty("user.name") : SecuritySupport.getSubjectName(subj);
    }
}
