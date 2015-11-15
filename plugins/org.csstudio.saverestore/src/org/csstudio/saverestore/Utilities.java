package org.csstudio.saverestore;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayByte;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayFloat;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ArrayShort;
import org.diirt.util.array.IteratorNumber;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListNumber;
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
 * <code>Utilities</code> provides common routines to transform between different data types used by the
 * save and restore.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Utilities {

    private static final String DELTA = " \u0394";
    private static final ValueFormat FORMAT = new SimpleValueFormat(3);
    private static final NumberFormat COMPARE_FORMAT = NumberFormats.format(2);
    private static final DateFormat LE_TIMESTAMP_FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS MMM dd");
    private static final DateFormat BE_TIMESTAMP_FORMATTER = new SimpleDateFormat("MMM dd HH:mm:ss");
    private static final DateFormat SBE_TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");

    static {
        FORMAT.setNumberFormat(NumberFormats.toStringFormat());
    }

    /**
     * Transform the string <code>data</code> to a {@link VType} which is of identical type as the parameter
     * <code>type</code>. The data is expected to be in a proper format so that it can be parsed into the
     * requested type. The alarm of the returned object is none and the timestamp of the object is now.
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

            return ValueFactory.newVNumberArray(list, alarm, time, (VNumberArray)type);
        } else if (type instanceof VEnumArray) {
            String[] elements = data.split("\\,");
            int[] array = new int[elements.length];
            List<String> labels = ((VEnumArray)type).getLabels();
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
            return ValueFactory.newVDouble(Double.parseDouble(data), alarm, time, (VDouble)type);
        } else if (type instanceof VFloat) {
            return ValueFactory.newVFloat(Float.parseFloat(data), alarm, time, (VFloat)type);
        } else if (type instanceof VLong) {
            return ValueFactory.newVLong(Long.parseLong(data), alarm, time, (VLong)type);
        } else if (type instanceof VInt) {
            return ValueFactory.newVInt(Integer.parseInt(data), alarm, time, (VInt)type);
        } else if (type instanceof VShort) {
            return ValueFactory.newVShort(Short.parseShort(data), alarm, time, (VShort)type);
        } else if (type instanceof VByte) {
            return ValueFactory.newVByte(Byte.parseByte(data), alarm, time, (VByte)type);
        } else if (type instanceof VEnum) {
            List<String> labels = ((VEnum)type).getLabels();
            return ValueFactory.newVEnum(labels.indexOf(data), labels, alarm, time);
        } else if (type instanceof VString) {
            return ValueFactory.newVString(data, alarm, time);
        } else if (type instanceof VBoolean) {
            return ValueFactory.newVBoolean(Boolean.parseBoolean(data), alarm, time);
        }
        return type;
    }

    /**
     * Extracts the raw value from the given data object. The raw value is either one of the primitive wrappers
     * or some kind of a list type if the value is an {@link Array}.
     *
     * @param type the value to extract the raw data from
     * @return the raw data
     */
    public static Object toRawValue(VType type) {
        if (type == null) {
            return null;
        }
        if (type instanceof VNumberArray) {
            return ((VNumberArray)type).getData();
        } else if (type instanceof VEnumArray) {
            return ((VEnumArray)type).getData();
        } else if (type instanceof VStringArray) {
            return ((VStringArray)type).getData();
        } else if (type instanceof VBooleanArray) {
            return ((VBooleanArray)type).getData();
        } else if (type instanceof VNumber) {
            return ((VNumber)type).getValue();
        } else if (type instanceof VEnum) {
            return ((VEnum)type).getValue();
        } else if (type instanceof VString) {
            return ((VString)type).getValue();
        } else if (type instanceof VBoolean) {
            return ((VBoolean)type).getValue();
        }
        return null;
    }

    /**
     * Transforms the vtype to a string representing the raw value in the vtype. If the value is an array it is
     * encapsulated into rectangular parenthesis and individual items are separated by semi-colon. In case of enums
     * the value is followd by a tilda and another rectangular parenthesis containing all possible enumaration values.
     *
     * @param type the type to transform
     * @return the string representing the raw value
     */
    public static String toRawStringValue(VType type) {
        if (type instanceof VNumberArray) {
            ListNumber list = ((VNumberArray)type).getData();
            StringBuilder sb = new StringBuilder(list.size()*10);
            sb.append('[');
            IteratorNumber it = list.iterator();
            if (type instanceof VDoubleArray) {
                while (it.hasNext()) {
                    String str = String.valueOf(it.nextDouble());
                    sb.append(str.replaceAll("\\,", "\\.")).append(';');
                }
            } else if (type instanceof VFloatArray) {
                while (it.hasNext()) {
                    String str = String.valueOf(it.nextFloat());
                    sb.append(str.replaceAll("\\,", "\\.")).append(';');
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
                sb.setCharAt(list.size()-1, ']');
            }
            return sb.toString();
        } else if (type instanceof VEnumArray) {
            List<String> list = ((VEnumArray)type).getData();
            List<String> labels = ((VEnumArray)type).getLabels();
            final StringBuilder sb = new StringBuilder((list.size() + labels.size())*10);
            sb.append('[');
            list.forEach(s -> sb.append(s).append(';'));
            if (list.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length()-1, ']');
            }
            sb.append('~').append('[');
            labels.forEach(s -> sb.append(s).append(';'));
            if (labels.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length()-1, ']');
            }
            return sb.toString();
        } else if (type instanceof VStringArray) {
            List<String> list = ((VStringArray)type).getData();
            final StringBuilder sb = new StringBuilder(list.size()*20);
            sb.append('[');
            list.forEach(s -> sb.append(s).append(';'));
            if (list.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length()-1, ']');
            }
            return sb.toString();
        } else if (type instanceof VBooleanArray) {
            ListBoolean list = ((VBooleanArray)type).getData();
            final StringBuilder sb = new StringBuilder(list.size()*6);
            sb.append('[');
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.getBoolean(i)).append(';');
            }
            if (list.size() == 0) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length()-1, ']');
            }
            return sb.toString();
        } else if (type instanceof VNumber) {
            return String.valueOf(((VNumber)type).getValue());
        } else if (type instanceof VEnum) {
            List<String> labels = ((VEnum)type).getLabels();
            final StringBuilder sb = new StringBuilder((labels.size()+1)*10);
            sb.append(((VEnum)type).getValue());
            sb.append('~').append('[');
            labels.forEach(s -> sb.append(s).append(';'));
            if (labels.isEmpty()) {
                sb.append(']');
            } else {
                sb.setCharAt(sb.length()-1, ']');
            }
            return sb.toString();
        } else if (type instanceof VString) {
            return ((VString)type).getValue();
        } else if (type instanceof VBoolean) {
            return String.valueOf(((VBoolean)type).getValue());
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
            return FORMAT.format((VNumberArray)type);
        } else if (type instanceof VEnumArray) {
            return FORMAT.format((VEnumArray)type);
        } else if (type instanceof VStringArray) {
            return FORMAT.format((VStringArray)type);
        } else if (type instanceof VBooleanArray) {
            return FORMAT.format((VBooleanArray)type);
        } else if (type instanceof VNumber) {
            return String.valueOf(((VNumber)type).getValue());
        } else if (type instanceof VEnum) {
            return ((VEnum)type).getValue();
        } else if (type instanceof VString) {
            return ((VString)type).getValue();
        } else if (type instanceof VBoolean) {
            return String.valueOf(((VBoolean)type).getValue());
        }
        return type.toString();
    }

    /**
     * Transforms the value of the given {@link VType} to a string and makes a comparison to the
     * <code>baseValue</code>. If the base value and the transformed value are both of a {@link VNumber} type,
     * the difference of the transformed value to the base value is added to the returned string.
     *
     * @param type the value to transform
     * @param baseValue the base value to compare the transformed value to
     * @return string representing the value and the difference from the base value
     */
    public static String valueToCompareString(VType type, VType baseValue) {
        if (type == null || baseValue == null) {
            return null;
        }
        if (type instanceof VNumber && baseValue instanceof VNumber) {
            StringBuilder sb = new StringBuilder(20);
            sb.append(FORMAT.format((VNumber)type));
            if (type instanceof VDouble) {
                double data = ((VDouble)type).getValue();
                double base = ((VNumber)baseValue).getValue().doubleValue();
                double newd = data - base;
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.format(newd));
            } else if (type instanceof VFloat) {
                float data = ((VFloat)type).getValue();
                float base = ((VNumber)baseValue).getValue().floatValue();
                float newd = data - base;
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(FORMAT.getNumberFormat().format(newd));
            } else if (type instanceof VLong) {
                long data = ((VLong)type).getValue();
                long base = ((VNumber)baseValue).getValue().longValue();
                long newd = data - base;
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.format(newd));
            } else if (type instanceof VInt) {
                int data = ((VInt)type).getValue();
                int base = ((VNumber)baseValue).getValue().intValue();
                int newd = data - base;
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(FORMAT.getNumberFormat().format(newd));
            } else if (type instanceof VShort) {
                short data = ((VShort)type).getValue();
                short base = ((VNumber)baseValue).getValue().shortValue();
                short newd = (short)(data - base);
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.format(newd));
            } else if (type instanceof VByte) {
                byte data = ((VByte)type).getValue();
                byte base = ((VNumber)baseValue).getValue().byteValue();
                byte newd = (byte)(data - base);
                sb.append(DELTA);
                if (newd > 0) {
                    sb.append('+');
                }
                sb.append(COMPARE_FORMAT.format(newd));
            }
            return sb.toString();
        } else {
            return valueToString(type);
        }
    }

    public static String timestampToString(Timestamp t) {
        if (t == null) {
            return null;
        }
        return LE_TIMESTAMP_FORMATTER.format(t.toDate());
    }

    public static String timestampToBigEndianString(Date t, boolean includeYear) {
        if (t == null) {
            return null;
        }
        return includeYear ? SBE_TIMESTAMP_FORMATTER.format(t) : BE_TIMESTAMP_FORMATTER.format(t);
    }

}
