package org.csstudio.saverestore.masar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;

/**
 *
 * <code>MasarConstants</code> provides a set of string and other constants that are used by MASAR. These include
 * different field and function names, as well as V4 structures and date parsers
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface MasarConstants {

    // used for transforming the date string from the MASAR format string to Date and vice versa
    final static ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal
        .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // Output parameters IDs
    static final String P_NAME = "config_name";
    static final String P_INDEX = "config_idx";
    static final String P_DESCRIPTION = "config_desc";
    static final String P_DATE = "config_create_date";
    static final String P_VERSION = "config_version";
    static final String P_STATUS = "status";
    static final String P_BASE_LEVEL_NAME = "system_val";
    static final String P_EVENT_ID = "event_id";
    static final String P_CONFIG_ID = "config_id";
    static final String P_COMMENT = "comments";
    static final String P_EVENT_TIME = "event_time";
    static final String P_USER = "user_name";
    static final String P_USER_TAG = "userTag";
    static final String P_TIMESTAMP = "timeStamp";
    static final String P_ALARM = "alarm";
    static final String P_MESSAGE = "message";
    // Snapshot data output parameters
    static final String P_PVNAME = "pv name";
    static final String P_STRING_VALUE = "string value";
    static final String P_DOUBLE_VALUE = "double value";
    static final String P_LONG_VALUE = "long value";
    static final String P_DBR_TYPE = "dbr type";
    static final String P_IS_CONNECTED = "isConnected";
    static final String P_SECONDS = "secondsPastEpoch";
    static final String P_NANOS = "nanoSeconds";
    static final String P_TIMESTAMP_TAG = "timeStampTag";
    static final String P_ALARM_SEVERITY = "alarmSeverity";
    static final String P_ALARM_STATUS = "alarmStatus";
    static final String P_ALARM_MESSAGE = "alarmMessage";
    static final String P_IS_ARRAY = "is_array";
    static final String P_ARRAY_VALUE = "array_value";
    static final String P_A_STRING = "stringVal";
    static final String P_A_DOUBLE = "doubleVal";
    static final String P_A_INT = "intVal";
    // Take Snapshot data output parameters
    static final String P_S_CHANNEL_NAME = "channelName";
    static final String P_S_STRING_VALUE = "stringValue";
    static final String P_S_DOUBLE_VALUE = "doubleValue";
    static final String P_S_LONG_VALUE = "longValue";
    static final String P_S_DBR_TYPE = "dbrType";
    // The input parameter IDS
    static final String F_FUNCTION = "function";
    static final String F_SYSTEM = "system";
    static final String F_CONFIGNAME = "configname";
    static final String F_CONFIGID = "configid";
    static final String F_EVENTID = "eventid";
    static final String F_COMMENT = "comment";
    static final String F_START = "start";
    static final String F_END = "end";
    static final String F_USER = "user";
    static final String F_DESCRIPTION = "desc";
    // Masar function names
    static final String FC_TAKE_SNAPSHOT = "saveSnapshot";
    static final String FC_LOAD_SNAPSHOT_DATA = "retrieveSnapshot";
    static final String FC_LOAD_BEAMLINE_SETS = "retrieveServiceConfigs";
    static final String FC_LOAD_BASE_LEVELS = "retrieveServiceConfigProps";
    static final String FC_SAVE_SNAPSHOT = "updateSnapshotEvent";
    static final String FC_LOAD_SNAPSHOTS = "retrieveServiceEvents";
    static final String FC_FIND_SNAPSHOTS = FC_LOAD_SNAPSHOTS;

    // Structure description for loading the base levels
    final static Structure STRUCT_BASE_LEVEL = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION }, new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the list of beamline sets
    final static Structure STRUCT_BEAMLINE_SET = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_SYSTEM, F_CONFIGNAME },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the list of snapshots
    final static Structure STRUCT_SNAPSHOT = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_CONFIGID },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for loading the snapshot data
    final static Structure STRUCT_SNAPSHOT_DATA = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_EVENTID },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for taking a snapshot
    final static Structure STRUCT_SNAPSHOT_TAKE = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_CONFIGNAME },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString) });

    // Structure description for saving a snapshot
    final static Structure STRUCT_SNAPSHOT_SAVE = FieldFactory.getFieldCreate().createStructure(
        new String[] { F_FUNCTION, F_EVENTID, F_USER, F_DESCRIPTION },
        new Field[] { FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
            FieldFactory.getFieldCreate().createScalar(ScalarType.pvString),
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
    default Structure createSearchStructure(boolean comment, boolean user, boolean start, boolean end) {
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
}
