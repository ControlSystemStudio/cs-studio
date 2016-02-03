package org.csstudio.saverestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListShort;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/**
 *
 * <code>FileUtilities</code> provides utility methods for reading and writing snapshot and beamline set files. All
 * methods in this class are thread safe.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class FileUtilities {

    // the date tag for the snapshot files
    private static final String DATE_TAG = "Date:";
    // the description tag for the beamline set files
    private static final String DESCRIPTION_TAG = "Description:";
    // the names of the headers in the csv files
    public static final String H_PV_NAME = "PV";
    public static final String H_READBACK = "READBACK";
    public static final String H_READBACK_VALUE = "READBACK_VALUE";
    public static final String H_DELTA = "DELTA";
    public static final String H_SELECTED = "SELECTED";
    public static final String H_TIMESTAMP = "TIMESTAMP";
    public static final String H_STATUS = "STATUS";
    public static final String H_SEVERITY = "SEVERITY";
    public static final String H_VALUE_TYPE = "VALUE_TYPE";
    public static final String H_VALUE = "VALUE";
    // the complete snapshot file header
    public static final String SNAPSHOT_FILE_HEADER = H_PV_NAME + "," + H_SELECTED + "," + H_TIMESTAMP + "," + H_STATUS
        + "," + H_SEVERITY + "," + H_VALUE_TYPE + "," + H_VALUE + "," + H_READBACK + "," + H_READBACK_VALUE + ","
        + H_DELTA;
    public static final String BEAMLINE_SET_HEADER = H_PV_NAME + "," + H_READBACK + "," + H_DELTA;
    // delimiter of array values
    private static final String ARRAY_SPLITTER = "\\;";
    // delimiter of enum value and enum constants
    private static final String ENUM_VALUE_SPLITTER = "\\~";
    // proposed length of snapshot file data line entry (pv name only)
    private static final int SNP_ENTRY_LENGTH = 700;
    // proposed length of beamline set data line entry (pv name only)
    private static final int BSD_ENTRY_LENGTH = 250;
    // the format used to store the timestamp of when the snapshot was taken
    private static final ThreadLocal<DateFormat> TIMESTAMP_FORMATTER = ThreadLocal
        .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private FileUtilities() {
    }

    /**
     * Read the contents of the snapshot file from the given input stream.
     *
     * @param stream the source of data
     * @return the data, where the description contains the timestamp of the snapshot, names contain the pv names, and
     *         data are the pv values
     * @throws IOException if reading the file failed
     */
    public static SnapshotContent readFromSnapshot(InputStream stream) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String date = null;
        List<String> names = new ArrayList<>();
        List<VType> data = new ArrayList<>();
        List<Boolean> selected = new ArrayList<>();
        List<String> readbacks = new ArrayList<>();
        List<VType> readbackData = new ArrayList<>();
        List<String> deltas = new ArrayList<>();
        String line;
        String[] header = null;
        Map<String, Integer> headerMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            } else if (header == null && line.charAt(0) == '#') {
                int idx = line.indexOf(DATE_TAG);
                if (idx > -1) {
                    date = line.substring(idx + DATE_TAG.length()).trim();
                }
            } else if (header == null) {
                header = line.split("\\,");
                for (int i = 0; i < header.length; i++) {
                    headerMap.put(header[i].toUpperCase(Locale.UK), Integer.valueOf(i));
                }
            } else {
                if (headerMap.isEmpty()) {
                    throw new IOException("The Snapshot content is invalid. No CSV header is defined.");
                }
                // there are no fields in here that may contain a comma
                String[] split = line.split("\\,", headerMap.size());
                Integer idx = headerMap.get(H_PV_NAME);
                String name = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_SELECTED);
                String sel = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_TIMESTAMP);
                String timestamp = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_STATUS);
                String status = idx == null ? "" : trim(split[idx]);
                idx = headerMap.get(H_SEVERITY);
                String severity = idx == null ? "" : trim(split[idx]);
                idx = headerMap.get(H_VALUE_TYPE);
                String valueType = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_VALUE);
                String value = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_READBACK);
                String readback = idx == null ? "" : trim(split[idx]);
                idx = headerMap.get(H_READBACK_VALUE);
                String readbackValue = idx == null ? null : trim(split[idx]);
                idx = headerMap.get(H_DELTA);
                String delta = idx == null ? "" : trim(split[idx]);

                data.add(piecesToVType(timestamp, status, severity, value, valueType));
                readbacks.add(readback);
                deltas.add(delta);
                readbackData.add(piecesToVType(timestamp, status, severity, readbackValue, valueType));
                names.add(name);
                boolean s = true;
                try {
                    s = Integer.parseInt(sel) != 0;
                } catch (NumberFormatException e) {
                    // ignore
                }
                selected.add(s);
            }
        }
        if (date == null || date.isEmpty()) {
            throw new ParseException("Snapshot does not have a date set.", 0);
        }
        Date d = TIMESTAMP_FORMATTER.get().parse(date);
        return new SnapshotContent(d, names, selected, data, readbacks, readbackData, deltas);
    }

    /**
     * Trim the data of leading and trailing quotes and white spaces.
     *
     * @param value the value to trim
     * @return trimmed value
     */
    private static String trim(String valueToTrim) {
        String value = valueToTrim.trim();
        if (!value.isEmpty() && value.charAt(0) == '"') {
            value = value.substring(1, value.length() - 1).trim();
        }
        return value;
    }

    /**
     * Converts a single entry to the VType.
     *
     * @param timestamp the timestamp of the entry, given in sec.nano format
     * @param status the alarm status
     * @param severity the alarm severity
     * @param value the raw value
     * @param valueType the value type
     * @return VType that contains all parameters and matches the type provided by <code>valueType</code>
     */
    private static VType piecesToVType(String timestamp, String status, String severity, String value,
        String valueType) {
        if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
            return VNoData.INSTANCE;
        }
        String[] t = timestamp != null && timestamp.indexOf('.') > 0 ? timestamp.split("\\.")
            : new String[] { "0", "0" };
        Time time = ValueFactory.newTime(Timestamp.of(Long.parseLong(t[0]), Integer.parseInt(t[1])));
        Alarm alarm = ValueFactory.newAlarm(
            severity.isEmpty() ? AlarmSeverity.NONE : AlarmSeverity.valueOf(severity.toUpperCase(Locale.UK)), status);
        Display display = ValueFactory.newDisplay(0d, 0d, 0d, null, null, 0d, 0d, 0d, 0d, 0d);
        ValueType vtype = ValueType.forName(valueType);

        String[] valueAndLabels = value.split(ENUM_VALUE_SPLITTER);
        if (valueAndLabels.length > 0) {
            if (valueAndLabels[0].charAt(0) == '[') {
                valueAndLabels[0] = valueAndLabels[0].substring(1, valueAndLabels[0].length() - 1);
            }
            if (valueAndLabels.length > 1) {
                valueAndLabels[1] = valueAndLabels[1].substring(1, valueAndLabels[1].length() - 1);
            }
        }
        String theValue = valueAndLabels[0];
        switch (vtype) {
            case DOUBLE_ARRAY:
                String[] sd = theValue.split(ARRAY_SPLITTER);
                double[] dd = new double[sd.length];
                for (int i = 0; i < sd.length; i++) {
                    dd[i] = Double.parseDouble(sd[i]);
                }
                ListDouble datad = new ArrayDouble(dd);
                return ValueFactory.newVDoubleArray(datad, alarm, time, display);
            case FLOAT_ARRAY:
                String[] sf = theValue.split(ARRAY_SPLITTER);
                float[] df = new float[sf.length];
                for (int i = 0; i < sf.length; i++) {
                    df[i] = Float.parseFloat(sf[i]);
                }
                ListFloat dataf = new ArrayFloat(df);
                return ValueFactory.newVFloatArray(dataf, alarm, time, display);
            case LONG_ARRAY:
                String[] sl = theValue.split(ARRAY_SPLITTER);
                long[] dl = new long[sl.length];
                for (int i = 0; i < sl.length; i++) {
                    dl[i] = Long.parseLong(sl[i]);
                }
                ListLong datal = new ArrayLong(dl);
                return ValueFactory.newVLongArray(datal, alarm, time, display);
            case INT_ARRAY:
                String[] si = theValue.split(ARRAY_SPLITTER);
                int[] di = new int[si.length];
                for (int i = 0; i < si.length; i++) {
                    di[i] = Integer.parseInt(si[i]);
                }
                ListInt datai = new ArrayInt(di);
                return ValueFactory.newVIntArray(datai, alarm, time, display);
            case SHORT_ARRAY:
                String[] ss = theValue.split(ARRAY_SPLITTER);
                short[] ds = new short[ss.length];
                for (int i = 0; i < ss.length; i++) {
                    ds[i] = Short.parseShort(ss[i]);
                }
                ListShort datas = new ArrayShort(ds);
                return ValueFactory.newVShortArray(datas, alarm, time, display);
            case BYTE_ARRAY:
                String[] sb = theValue.split(ARRAY_SPLITTER);
                byte[] db = new byte[sb.length];
                for (int i = 0; i < sb.length; i++) {
                    db[i] = Byte.parseByte(sb[i]);
                }
                ListByte datab = new ArrayByte(db);
                return ValueFactory.newVNumberArray(datab, alarm, time, display);
            case ENUM_ARRAY:
                String[] se = theValue.split(ARRAY_SPLITTER);
                List<String> labels = Arrays.asList(valueAndLabels[1].split(ARRAY_SPLITTER));
                int[] de = new int[se.length];
                for (int i = 0; i < se.length; i++) {
                    de[i] = labels.indexOf(se[i]);
                }
                ListInt datae = new ArrayInt(de);
                return ValueFactory.newVEnumArray(datae, labels, alarm, time);
            case STRING_ARRAY:
                String[] str = theValue.split(ARRAY_SPLITTER);
                return ValueFactory.newVStringArray(Arrays.asList(str), alarm, time);
            case BOOLEAN_ARRAY:
                String[] sbo = theValue.split(ARRAY_SPLITTER);
                boolean[] dbo = new boolean[sbo.length];
                for (int i = 0; i < sbo.length; i++) {
                    dbo[i] = Boolean.parseBoolean(sbo[i]);
                }
                ListBoolean databo = new ArrayBoolean(dbo);
                return ValueFactory.newVBooleanArray(databo, alarm, time);
            case NUMBER_ARRAY:
                String[] nd = theValue.split(ARRAY_SPLITTER);
                double[] ndd = new double[nd.length];
                for (int i = 0; i < nd.length; i++) {
                    ndd[i] = Double.parseDouble(nd[i]);
                }
                ListDouble datand = new ArrayDouble(ndd);
                return ValueFactory.newVDoubleArray(datand, alarm, time, display);
            case DOUBLE:
            case NUMBER:
                return ValueFactory.newVDouble(Double.parseDouble(theValue), alarm, time, display);
            case FLOAT:
                return ValueFactory.newVFloat(Float.parseFloat(theValue), alarm, time, display);
            case LONG:
                return ValueFactory.newVLong(Long.parseLong(theValue), alarm, time, display);
            case INT:
                return ValueFactory.newVInt(Integer.parseInt(theValue), alarm, time, display);
            case SHORT:
                return ValueFactory.newVShort(Short.parseShort(theValue), alarm, time, display);
            case BYTE:
                return ValueFactory.newVByte(Byte.parseByte(theValue), alarm, time, display);
            case BOOLEAN:
                return ValueFactory.newVBoolean(Boolean.parseBoolean(theValue), alarm, time);
            case STRING:
                return ValueFactory.newVString(theValue, alarm, time);
            case ENUM:
                List<String> lbls = Arrays.asList(valueAndLabels[1].split("\\;"));
                return ValueFactory.newVEnum(lbls.indexOf(theValue), lbls, alarm, time);
            case NODATA:
                return VNoData.INSTANCE;
        }

        throw new IllegalArgumentException("Unknown data type " + valueType + ".");
    }

    /**
     * Transforms the vtype to a string representing only the type of the vtype (e.g. double, string_array etc.).
     *
     * @see ValueType#name
     * @param type the type to transform
     * @return the value type as string
     */
    private static String vtypeToStringType(VType type) {
        for (ValueType t : ValueType.values()) {
            if (t.instanceOf(type)) {
                return t.typeName;
            }
        }
        throw new IllegalArgumentException("Unknown data type " + type.getClass() + ".");
    }

    /**
     * Generates snapshot file content and returns it.
     *
     * @param data snapshot file data
     *
     * @return generated snapshot file content
     */
    public static String generateSnapshotFileContent(VSnapshot data) {
        List<VType> values = data.getValues();
        List<String> names = data.getNames();
        List<Boolean> selected = data.getSelected();
        List<String> readbacks = data.getReadbackNames();
        List<VType> readbackValues = data.getReadbackValues();
        List<String> deltas = data.getDeltas();
        StringBuilder sb = new StringBuilder(SNP_ENTRY_LENGTH * names.size());
        Timestamp timestamp = data.getTimestamp();
        if (timestamp == null) {
            timestamp = Timestamp.now();
        }
        sb.append("# Date: ").append(TIMESTAMP_FORMATTER.get().format(timestamp.toDate())).append('\n');
        sb.append(SNAPSHOT_FILE_HEADER).append('\n');
        boolean deltaEmpty = deltas.isEmpty();
        boolean noReadbacks = readbacks.isEmpty();
        for (int i = 0; i < names.size(); i++) {
            sb.append(createSnapshotFileEntry(names.get(i), selected.get(i), values.get(i),
                noReadbacks ? null : readbacks.get(i), noReadbacks ? null : readbackValues.get(i),
                deltaEmpty ? null : deltas.get(i))).append('\n');
        }
        return sb.toString();
    }

    /**
     * Converts given name and data into a string formatted for the snapshot file and returns that string
     *
     * @param name the name of the pv
     * @param selected the selected flag of the pv
     * @param data stored pv value
     * @param readbackName the name of the readback pv
     * @param readbackValue the readback pv value
     * @param delta the threshold value or function
     * @return into string converted given snapshot entry data.
     */
    private static String createSnapshotFileEntry(String name, boolean selected, VType data, String readbackName,
        VType readbackValue, String delta) {
        StringBuilder sb = new StringBuilder(SNP_ENTRY_LENGTH);
        sb.append(name).append(',');
        sb.append(selected ? 1 : 0).append(',');
        if (data instanceof Time) {
            sb.append(((Time) data).getTimestamp());
        }
        sb.append(',');
        if (data instanceof Alarm) {
            sb.append(((Alarm) data).getAlarmName()).append(',');
            sb.append(((Alarm) data).getAlarmSeverity()).append(',');
        } else {
            sb.append(",,");
        }
        sb.append(vtypeToStringType(data)).append(',');
        sb.append('\"').append(Utilities.toRawStringValue(data)).append('\"');
        sb.append(',');
        if (readbackName != null) {
            sb.append(readbackName);
        }
        sb.append(',');
        if (readbackValue != null) {
            sb.append('\"').append(Utilities.toRawStringValue(readbackValue)).append('\"');
        }
        sb.append(',');
        if (delta != null) {
            sb.append(delta);
        }
        return sb.toString();
    }

    /**
     * Read the contents of the beamline set from the input stream.
     *
     * @param stream the source of data
     * @return the data, where the description is the description read from the file and there are no data, just names
     * @throws IOException if there was an error reading the file content
     */
    public static BeamlineSetContent readFromBeamlineSet(InputStream stream) throws IOException {
        StringBuilder description = new StringBuilder(400);
        List<String> names = new ArrayList<>();
        List<String> readbacks = new ArrayList<>();
        List<String> deltas = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        boolean isDescriptionLine = false;
        String line;
        String[] header = null;
        int namesIndex = -1;
        int readbackIndex = -1;
        int deltaIndex = -1;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            } else if (header == null && line.charAt(0) == '#') {
                line = line.substring(1).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (isDescriptionLine) {
                    description.append(line).append('\n');
                } else if (line.contains(DESCRIPTION_TAG)) {
                    isDescriptionLine = true;
                }
            } else if (header == null) {
                isDescriptionLine = false;
                header = line.split("\\,");
                for (int i = 0; i < header.length; i++) {
                    if (H_PV_NAME.equals(header[i])) {
                        namesIndex = i;
                    } else if (H_READBACK.equals(header[i])) {
                        readbackIndex = i;
                    } else if (H_DELTA.equals(header[i])) {
                        deltaIndex = i;
                    }
                }
            } else {
                String[] split = line.split("\\,", header.length);
                // there are no fields in here that may contain a comma
                if (namesIndex > -1) {
                    names.add(trim(split[namesIndex]));
                } else {
                    continue;
                }
                if (readbackIndex != -1) {
                    readbacks.add(trim(split[readbackIndex]));
                }
                if (deltaIndex != -1) {
                    deltas.add(trim(split[deltaIndex]));
                }
            }
        }
        return new BeamlineSetContent(description.toString().trim(), names, readbacks, deltas);
    }

    /**
     * Generates beamline set file content and returns it.
     *
     * @param data beamline set data to transform to string
     *
     * @return generated beamline set file content
     */
    public static String generateBeamlineSetContent(BeamlineSetData data) {
        String description = data.getDescription();
        description = description.replaceAll("\n", "# ");
        List<String> pvs = data.getPVList();
        List<String> readbacks = data.getReadbackList();
        List<String> deltas = data.getDeltaList();
        final StringBuilder sb = new StringBuilder(BSD_ENTRY_LENGTH * pvs.size());
        sb.append('#').append(' ').append(DESCRIPTION_TAG).append("\n# ");
        sb.append(description).append("\n#\n");
        if (readbacks.isEmpty() && deltas.isEmpty()) {
            sb.append(H_PV_NAME).append('\n');
            pvs.forEach(e -> sb.append(e).append('\n'));
        } else if (readbacks.isEmpty()) {
            sb.append(H_PV_NAME).append(',').append(H_DELTA).append('\n');
            for (int i = 0; i < pvs.size(); i++) {
                sb.append(pvs.get(i)).append(',').append(deltas.get(i)).append('\n');
            }
        } else if (deltas.isEmpty()) {
            sb.append(H_PV_NAME).append(',').append(H_READBACK).append('\n');
            for (int i = 0; i < pvs.size(); i++) {
                sb.append(pvs.get(i)).append(',').append(readbacks.get(i)).append('\n');
            }
        } else {
            sb.append(H_PV_NAME).append(',').append(H_READBACK).append(',').append(H_DELTA).append('\n');
            for (int i = 0; i < pvs.size(); i++) {
                sb.append(pvs.get(i)).append(',').append(readbacks.get(i)).append(',').append(deltas.get(i))
                    .append('\n');
            }
        }
        return sb.toString();
    }
}
