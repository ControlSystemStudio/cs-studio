package org.csstudio.saverestore;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.csstudio.saverestore.data.Threshold;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.IteratorNumber;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListNumber;
import org.diirt.util.array.ListShort;
import org.diirt.util.text.NumberFormats;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.Array;
import org.diirt.vtype.SimpleValueFormat;
import org.diirt.vtype.Time;
import org.diirt.vtype.VBoolean;
import org.diirt.vtype.VBooleanArray;
import org.diirt.vtype.VByte;
import org.diirt.vtype.VByteArray;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VEnumArray;
import org.diirt.vtype.VFloat;
import org.diirt.vtype.VFloatArray;
import org.diirt.vtype.VInt;
import org.diirt.vtype.VIntArray;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VLongArray;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VShort;
import org.diirt.vtype.VShortArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.diirt.vtype.ValueFormat;

/**
 *
 * <code>Utilities</code> provides common methods to transform between different data types used by the save and
 * restore. This class also provides methods to transform the timestamps into human readable formats. All methods are
 * thread safe.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Utilities {

    /**
     * <code>VTypeComparison</code> is the result of comparison of two {@link VType} values. The {@link #string} field
     * provides the textual representation of the comparison and the {@link #valuesEqual} provides information whether
     * the values are equal (0), the first value is greater than second (1), or the first value is less than second
     * (-1).
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static class VTypeComparison {
        /** The string representation of the comparison result. May include delta character etc. */
        public final String string;
        /** 0 if values are identical, -1 if first value is less than second or 1 otherwise */
        public final int valuesEqual;
        /** Indicates if the values are within the allowed threshold */
        public final boolean withinThreshold;

        VTypeComparison(String string, int equal, boolean withinThreshold) {
            this.string = string;
            this.valuesEqual = equal;
            this.withinThreshold = withinThreshold;
        }
    }

    /** The character code for the greek delta letter */
    public static final char DELTA_CHAR = '\u0394';
    // All formats use thread locals, to avoid problems if any of the static methods is invoked concurrently
    private static final ThreadLocal<ValueFormat> FORMAT = ThreadLocal.withInitial(() -> {
        ValueFormat vf = new SimpleValueFormat(3);
        vf.setNumberFormat(NumberFormats.toStringFormat());
        return vf;
    });
    private static final ThreadLocal<NumberFormat> COMPARE_FORMAT = ThreadLocal
        .withInitial(() -> NumberFormats.format(2));
    private static final ThreadLocal<DateFormat> LE_TIMESTAMP_FORMATTER = ThreadLocal
        .withInitial(() -> new SimpleDateFormat("HH:mm:ss.SSS MMM dd"));
    private static final ThreadLocal<DateFormat> BE_TIMESTAMP_FORMATTER = ThreadLocal
        .withInitial(() -> new SimpleDateFormat("MMM dd HH:mm:ss"));
    private static final ThreadLocal<DateFormat> SBE_TIMESTAMP_FORMATTER = ThreadLocal
        .withInitial(() -> new SimpleDateFormat("yyyy MMM dd HH:mm:ss"));

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private Utilities() {
    }

    /**
     * Transform the string <code>data</code> to a {@link VType} which is of identical type as the parameter
     * <code>type</code>. The data is expected to be in a proper format so that it can be parsed into the requested
     * type. The alarm of the returned object is none and the timestamp of the object is now.
     *
     * @param data the data to parse and transform into VType
     * @param type the type of the destination object
     * @return VType representing the data
     */
    public static VType valueFromString(String data, VType type) {
        Alarm alarm = ValueFactory.alarmNone();
        Time time = ValueFactory.timeNow();
        if (type instanceof VNumberArray) {
            ListNumber list = null;
            String[] elements = data.split("\\,");
            if (type instanceof VDoubleArray) {
                double[] array = new double[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Double.parseDouble(elements[i]);
                }
                list = new ArrayDouble(array);
            } else if (type instanceof VFloatArray) {
                float[] array = new float[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Float.parseFloat(elements[i]);
                }
                list = new ArrayFloat(array);
            } else if (type instanceof VLongArray) {
                long[] array = new long[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Long.parseLong(elements[i]);
                }
                list = new ArrayLong(array);
            } else if (type instanceof VIntArray) {
                int[] array = new int[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Integer.parseInt(elements[i]);
                }
                list = new ArrayInt(array);
            } else if (type instanceof VShortArray) {
                short[] array = new short[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Short.parseShort(elements[i]);
                }
                list = new ArrayShort(array);
            } else if (type instanceof VByteArray) {
                byte[] array = new byte[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    array[i] = Byte.parseByte(elements[i]);
                }
                list = new ArrayByte(array);
            }

            return ValueFactory.newVNumberArray(list, alarm, time, (VNumberArray) type);
        } else if (type instanceof VEnumArray) {
            String[] elements = data.split("\\,");
            int[] array = new int[elements.length];
            List<String> labels = ((VEnumArray) type).getLabels();
            for (int i = 0; i < elements.length; i++) {
                array[i] = labels.indexOf(elements[i]);
            }
            ListInt list = new ArrayInt(array);
            return ValueFactory.newVEnumArray(list, labels, alarm, time);
        } else if (type instanceof VStringArray) {
            String[] elements = data.split("\\,");
            List<String> list = Arrays.asList(elements);
            return ValueFactory.newVStringArray(list, alarm, time);
        } else if (type instanceof VBooleanArray) {
            String[] elements = data.split("\\,");
            boolean[] array = new boolean[elements.length];
            for (int i = 0; i < elements.length; i++) {
                array[i] = Boolean.parseBoolean(elements[i]);
            }
            ListBoolean list = new ArrayBoolean(array);
            return ValueFactory.newVBooleanArray(list, alarm, time);
        } else if (type instanceof VDouble) {
            return ValueFactory.newVDouble(Double.parseDouble(data), alarm, time, (VDouble) type);
        } else if (type instanceof VFloat) {
            return ValueFactory.newVFloat(Float.parseFloat(data), alarm, time, (VFloat) type);
        } else if (type instanceof VLong) {
            return ValueFactory.newVLong(Long.parseLong(data), alarm, time, (VLong) type);
        } else if (type instanceof VInt) {
            return ValueFactory.newVInt(Integer.parseInt(data), alarm, time, (VInt) type);
        } else if (type instanceof VShort) {
            return ValueFactory.newVShort(Short.parseShort(data), alarm, time, (VShort) type);
        } else if (type instanceof VByte) {
            return ValueFactory.newVByte(Byte.parseByte(data), alarm, time, (VByte) type);
        } else if (type instanceof VEnum) {
            List<String> labels = ((VEnum) type).getLabels();
            return ValueFactory.newVEnum(labels.indexOf(data), labels, alarm, time);
        } else if (type instanceof VString) {
            return ValueFactory.newVString(data, alarm, time);
        } else if (type instanceof VBoolean) {
            return ValueFactory.newVBoolean(Boolean.parseBoolean(data), alarm, time);
        }
        return type;
    }

    /**
     * Extracts the raw value from the given data object. The raw value is either one of the primitive wrappers or some
     * kind of a list type if the value is an {@link Array}.
     *
     * @param type the value to extract the raw data from
     * @return the raw data
     */
    public static Object toRawValue(VType type) {
        if (type == null) {
            return null;
        }
        if (type instanceof VNumberArray) {
            return ((VNumberArray) type).getData();
        } else if (type instanceof VEnumArray) {
            return ((VEnumArray) type).getData();
        } else if (type instanceof VStringArray) {
            return ((VStringArray) type).getData();
        } else if (type instanceof VBooleanArray) {
            return ((VBooleanArray) type).getData();
        } else if (type instanceof VNumber) {
            return ((VNumber) type).getValue();
        } else if (type instanceof VEnum) {
            return ((VEnum) type).getValue();
        } else if (type instanceof VString) {
            return ((VString) type).getValue();
        } else if (type instanceof VBoolean) {
            return ((VBoolean) type).getValue();
        }
        return null;
    }

    /**
     * Transforms the vtype to a string representing the raw value in the vtype. If the value is an array it is
     * encapsulated into rectangular parenthesis and individual items are separated by semi-colon. In case of enums the
     * value is followd by a tilda and another rectangular parenthesis containing all possible enumaration values.
     *
     * @param type the type to transform
     * @return the string representing the raw value
     */
    public static String toRawStringValue(VType type) {
        if (type instanceof VNumberArray) {
            ListNumber list = ((VNumberArray) type).getData();
            StringBuilder sb = new StringBuilder(list.size() * 10);
            sb.append('[');
            IteratorNumber it = list.iterator();
            Pattern pattern = Pattern.compile("\\,");
            if (type instanceof VDoubleArray) {
                while (it.hasNext()) {
                    String str = String.valueOf(it.nextDouble());
                    sb.append(pattern.matcher(str).replaceAll("\\.")).append(';');
                }
            } else if (type instanceof VFloatArray) {
                while (it.hasNext()) {
                    String str = String.valueOf(it.nextFloat());
                    sb.append(pattern.matcher(str).replaceAll("\\.")).append(';');
                }
            } else if (type instanceof VLongArray) {
                while (it.hasNext()) {
                    sb.append(it.nextLong()).append(';');
                }
            } else if (type instanceof VIntArray) {
                while (it.hasNext()) {
                    sb.append(it.nextInt()).append(';');
                }
            } else if (type instanceof VShortArray) {
                while (it.hasNext()) {
                    sb.append(it.nextShort()).append(';');
                }
            } else if (type instanceof VByteArray) {
                while (it.hasNext()) {
                    sb.append(it.nextByte()).append(';');
                }
            }
            if (list.size() == 0) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        } else if (type instanceof VEnumArray) {
            List<String> list = ((VEnumArray) type).getData();
            List<String> labels = ((VEnumArray) type).getLabels();
            final StringBuilder sb = new StringBuilder((list.size() + labels.size()) * 10);
            sb.append('[');
            list.forEach(s -> sb.append(s).append(';'));
            if (list.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            sb.append('~').append('[');
            labels.forEach(s -> sb.append(s).append(';'));
            if (labels.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        } else if (type instanceof VStringArray) {
            List<String> list = ((VStringArray) type).getData();
            final StringBuilder sb = new StringBuilder(list.size() * 20);
            sb.append('[');
            list.forEach(s -> sb.append(s).append(';'));
            if (list.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        } else if (type instanceof VBooleanArray) {
            ListBoolean list = ((VBooleanArray) type).getData();
            final StringBuilder sb = new StringBuilder(list.size() * 6);
            sb.append('[');
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.getBoolean(i)).append(';');
            }
            if (list.size() == 0) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        } else if (type instanceof VNumber) {
            return String.valueOf(((VNumber) type).getValue());
        } else if (type instanceof VEnum) {
            List<String> labels = ((VEnum) type).getLabels();
            final StringBuilder sb = new StringBuilder((labels.size() + 1) * 10);
            sb.append(((VEnum) type).getValue());
            sb.append('~').append('[');
            labels.forEach(s -> sb.append(s).append(';'));
            if (labels.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        } else if (type instanceof VString) {
            return ((VString) type).getValue();
        } else if (type instanceof VBoolean) {
            return String.valueOf(((VBoolean) type).getValue());
        }
        return null;
    }

    /**
     * Transforms the value of the given {@link VType} to a human readable string.
     *
     * @param type the data to transform
     * @return string representation of the data
     */
    public static String valueToString(VType type) {
        if (type == null) {
            return null;
        }
        if (type instanceof VNumberArray) {
            return FORMAT.get().format((VNumberArray) type);
        } else if (type instanceof VEnumArray) {
            return FORMAT.get().format((VEnumArray) type);
        } else if (type instanceof VStringArray) {
            return FORMAT.get().format((VStringArray) type);
        } else if (type instanceof VBooleanArray) {
            return FORMAT.get().format((VBooleanArray) type);
        } else if (type instanceof VNumber) {
            return String.valueOf(((VNumber) type).getValue());
        } else if (type instanceof VEnum) {
            return ((VEnum) type).getValue();
        } else if (type instanceof VString) {
            return ((VString) type).getValue();
        } else if (type instanceof VBoolean) {
            return String.valueOf(((VBoolean) type).getValue());
        }
        return type.toString();
    }

    /**
     * Transforms the value of the given {@link VType} to a string and makes a comparison to the <code>baseValue</code>.
     * If the base value and the transformed value are both of a {@link VNumber} type, the difference of the transformed
     * value to the base value is added to the returned string.
     *
     * @param value the value to compare
     * @param baseValue the base value to compare the value to
     * @param threshold the threshold values to use for comparing the values, if defined and difference is within
     *            threshold limits the values are equal
     * @return string representing the value and the difference from the base value together with the flag indicating
     *         the comparison result
     */
    @SuppressWarnings("unchecked")
    public static VTypeComparison valueToCompareString(VType value, VType baseValue, Optional<Threshold<?>> threshold) {
        if (value == null && baseValue == null) {
            return new VTypeComparison("---", 0, true);
        } else if (value == null || baseValue == null) {
            return value == null ? new VTypeComparison(valueToString(baseValue), -1, false)
                : new VTypeComparison(valueToString(value), 1, false);
        }
        if (value instanceof VNumber && baseValue instanceof VNumber) {
            StringBuilder sb = new StringBuilder(20);
            int diff = 0;
            boolean withinThreshold = threshold.isPresent();
            sb.append(FORMAT.get().format((VNumber) value));
            if (value instanceof VDouble) {
                double data = ((VDouble) value).getValue();
                double base = ((VNumber) baseValue).getValue().doubleValue();
                double newd = data - base;
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Double>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Double.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.get().format(newd));
            } else if (value instanceof VFloat) {
                float data = ((VFloat) value).getValue();
                float base = ((VNumber) baseValue).getValue().floatValue();
                float newd = data - base;
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Float>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Float.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(FORMAT.get().getNumberFormat().format(newd));
            } else if (value instanceof VLong) {
                long data = ((VLong) value).getValue();
                long base = ((VNumber) baseValue).getValue().longValue();
                long newd = data - base;
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Long>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Long.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.get().format(newd));
            } else if (value instanceof VInt) {
                int data = ((VInt) value).getValue();
                int base = ((VNumber) baseValue).getValue().intValue();
                int newd = data - base;
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Integer>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Integer.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(FORMAT.get().getNumberFormat().format(newd));
            } else if (value instanceof VShort) {
                short data = ((VShort) value).getValue();
                short base = ((VNumber) baseValue).getValue().shortValue();
                short newd = (short) (data - base);
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Short>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Short.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.get().format(newd));
            } else if (value instanceof VByte) {
                byte data = ((VByte) value).getValue();
                byte base = ((VNumber) baseValue).getValue().byteValue();
                byte newd = (byte) (data - base);
                if (threshold.isPresent()) {
                    withinThreshold = ((Threshold<Byte>) threshold.get()).isWithinThreshold(data, base);
                }
                diff = Byte.compare(data, base);
                sb.append(' ').append(DELTA_CHAR);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.get().format(newd));
            }
            return new VTypeComparison(sb.toString(), diff, withinThreshold);
        } else if (value instanceof VBoolean && baseValue instanceof VBoolean) {
            String str = valueToString(value);
            boolean b = ((VBoolean) value).getValue();
            boolean c = ((VBoolean) baseValue).getValue();
            return new VTypeComparison(str, Boolean.compare(b, c), b == c);
        } else if (value instanceof VEnum && baseValue instanceof VEnum) {
            String str = valueToString(value);
            int b = ((VEnum) value).getIndex();
            int c = ((VEnum) baseValue).getIndex();
            return new VTypeComparison(str, Integer.compare(b, c), b == c);
        } else if (value instanceof VNumberArray && baseValue instanceof VNumberArray) {
            StringBuilder sb = new StringBuilder(20);
            boolean equal = true;
            sb.append(FORMAT.get().format(value));
            if (value instanceof VDoubleArray) {
                ListDouble data = ((VDoubleArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Double.compare(data.getDouble(i), base.getDouble(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            } else if (value instanceof VFloatArray) {
                ListFloat data = ((VFloatArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Float.compare(data.getFloat(i), base.getFloat(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            } else if (value instanceof VLongArray) {
                ListLong data = ((VLongArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Long.compare(data.getLong(i), base.getLong(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            } else if (value instanceof VIntArray) {
                ListInt data = ((VIntArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Integer.compare(data.getInt(i), base.getInt(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            } else if (value instanceof VShortArray) {
                ListShort data = ((VShortArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Short.compare(data.getShort(i), base.getShort(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            } else if (value instanceof VByteArray) {
                ListByte data = ((VByteArray) value).getData();
                ListNumber base = ((VNumberArray) baseValue).getData();
                equal = data.size() == base.size();
                if (equal) {
                    for (int i = 0; i < data.size(); i++) {
                        if (Byte.compare(data.getByte(i), base.getByte(i)) != 0) {
                            equal = false;
                            break;
                        }
                    }
                }
            }
            return new VTypeComparison(sb.toString(), equal ? 0 : 1, equal);
        } else {
            String str = valueToString(value);
            String base = valueToString(baseValue);
            int diff = str.compareTo(base);
            return new VTypeComparison(str, diff, diff == 0);
        }
    }

    /**
     * Transforms the timestamp to string, using the format HH:mm:ss.SSS MMM dd.
     *
     * @param t the timestamp to transform
     * @return string representation of the timestamp using the above format
     */
    public static String timestampToString(Timestamp t) {
        if (t == null) {
            return null;
        }
        synchronized (LE_TIMESTAMP_FORMATTER) {
            return LE_TIMESTAMP_FORMATTER.get().format(t.toDate());
        }
    }

    /**
     * Transforms the date to string formatted as yyyy MMM dd HH:mm:ss. Year is only included if the parameter
     * <code>includeYear</code> is true.
     *
     * @param t the date to transform
     * @param includeYear true if the year should be included or false otherwise
     * @return string representation of the date
     */
    public static String timestampToBigEndianString(Date t, boolean includeYear) {
        if (t == null) {
            return null;
        }
        synchronized (BE_TIMESTAMP_FORMATTER) {
            return includeYear ? SBE_TIMESTAMP_FORMATTER.get().format(t) : BE_TIMESTAMP_FORMATTER.get().format(t);
        }
    }

    /**
     * Checks if the values of the given vtype are equal and returns true if they are or false if they are not.
     * Timestamps, alarms and other parameters are ignored.
     *
     * @param v1 the first value to check
     * @param v2 the second value to check
     * @param threshold the threshold values which define if the difference is within limits or not
     * @return true if the values are equal or false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean areValuesEqual(VType v1, VType v2, Optional<Threshold<?>> threshold) {
        if (v1 == null && v2 == null) {
            return true;
        } else if (v1 == null || v2 == null) {
            return false;
        }
        if (v1 instanceof VNumber && v2 instanceof VNumber) {
            if (v1 instanceof VDouble) {
                double data = ((VDouble) v1).getValue();
                double base = ((VNumber) v2).getValue().doubleValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Double>) threshold.get()).isWithinThreshold(data, base);
                }
                return Double.compare(data, base) == 0;
            } else if (v1 instanceof VFloat) {
                float data = ((VFloat) v1).getValue();
                float base = ((VNumber) v2).getValue().floatValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Float>) threshold.get()).isWithinThreshold(data, base);
                }
                return Float.compare(data, base) == 0;
            } else if (v1 instanceof VLong) {
                long data = ((VLong) v1).getValue();
                long base = ((VNumber) v2).getValue().longValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Long>) threshold.get()).isWithinThreshold(data, base);
                }
                return Long.compare(data, base) == 0;
            } else if (v1 instanceof VInt) {
                int data = ((VInt) v1).getValue();
                int base = ((VNumber) v2).getValue().intValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Integer>) threshold.get()).isWithinThreshold(data, base);
                }
                return Integer.compare(data, base) == 0;
            } else if (v1 instanceof VShort) {
                short data = ((VShort) v1).getValue();
                short base = ((VNumber) v2).getValue().shortValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Short>) threshold.get()).isWithinThreshold(data, base);
                }
                return Short.compare(data, base) == 0;
            } else if (v1 instanceof VByte) {
                byte data = ((VByte) v1).getValue();
                byte base = ((VNumber) v2).getValue().byteValue();
                if (threshold.isPresent()) {
                    return ((Threshold<Byte>) threshold.get()).isWithinThreshold(data, base);
                }
                return Byte.compare(data, base) == 0;
            }
        } else if (v1 instanceof VBoolean && v2 instanceof VBoolean) {
            boolean b = ((VBoolean) v1).getValue();
            boolean c = ((VBoolean) v2).getValue();
            return b == c;
        } else if (v1 instanceof VEnum && v2 instanceof VEnum) {
            int b = ((VEnum) v1).getIndex();
            int c = ((VEnum) v2).getIndex();
            return b == c;
        } else {
            String str = valueToString(v1);
            String base = valueToString(v2);
            return str.equals(base);
        }
        return false;
    }
}
