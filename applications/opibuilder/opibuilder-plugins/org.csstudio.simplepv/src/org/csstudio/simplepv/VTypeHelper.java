package org.csstudio.simplepv;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diirt.util.array.CollectionNumbers;
import org.diirt.util.array.ListByte;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListFloat;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListNumber;
import org.diirt.util.array.ListShort;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Array;
import org.diirt.vtype.Display;
import org.diirt.vtype.MultiScalar;
import org.diirt.vtype.Scalar;
import org.diirt.vtype.Time;
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
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VShort;
import org.diirt.vtype.VShortArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;

/**
 * A center place for VType related operations.
 *
 * @author Xihui Chen, Kay Kasemir, carcassi (some of code are learned from
 *         {@link ValueUtil})
 */
public class VTypeHelper {

    public static final int DEFAULT_PRECISION = 4;//$NON-NLS-1$
    public static final int UNSET_PRECISION = -1;//$NON-NLS-1$
    public static final String HEX_PREFIX = "0x"; //$NON-NLS-1$
    /**
     * The max count of values to be formatted into string. The value beyond
     * this count will be omitted.
     */
    public static final int MAX_FORMAT_VALUE_COUNT = 100;
    public static final String ARRAY_ELEMENT_SEPARATOR = ", "; //$NON-NLS-1$

    private static Map<Integer, NumberFormat> expFormatCacheMap = new HashMap<Integer, NumberFormat>();
    private static Map<Integer, NumberFormat> decimalFormatCacheMap = new HashMap<Integer, NumberFormat>();

    /**
     * Format a VType value to string.
     *
     * @param formatEnum
     *            the format
     * @param vValue
     *            PVManager value, such as VDouble, VEnum, V....
     * @param precision
     *            decimal precision. If it is -1, it will use the precision from
     *            PV.
     * @return the formated string
     */
    public static String formatValue(FormatEnum formatEnum, VType vValue, int precision) {
        if (vValue instanceof Scalar) {
            Object value = ((Scalar) vValue).getValue();
            if (value instanceof Number) {
                return formatScalarNumber(formatEnum, vValue, (Number) value, precision);
            } else if (value instanceof String) {
                if (vValue instanceof VEnum)
                    return formatScalarEnum(formatEnum, (VEnum) vValue);
                return (String) value;
            }
        } else if (vValue instanceof Array) {

            if (vValue instanceof VNumberArray)
                return formatNumberArray(formatEnum, (VNumberArray) vValue, precision);
            else {
                Object array = ((Array) vValue).getData();
                if (array instanceof List) {
                    return formatObjectArray(((List<?>) array).toArray());
                }
            }
        }
        if (vValue != null)
            return vValue.toString();
        return "no value"; //$NON-NLS-1$
    }

    /**Extract the alarm name from the VType obj.
     * @param obj the VType object.
     * @return the alarm name or empty if there is no alarm info from the object.
     */
    public static String getAlarmName(VType obj){
        Alarm alarmOf = ValueUtil.alarmOf(obj);
        if(alarmOf != null)
            return alarmOf.getAlarmName();
        return ""; //$NON-NLS-1$
    }

    /**Extract the {@link AlarmSeverity} from the VType obj.
     * @param obj the VType object.
     * @return the alarm severity or null if there is no alarm info from the object.
     */
    public static AlarmSeverity getAlarmSeverity(VType obj){
        Alarm alarmOf = ValueUtil.alarmOf(obj);
        if(alarmOf != null)
            return alarmOf.getAlarmSeverity();
        return null;
    }

    /**
     * Get the basic data type of a single VType value. If it is not a basic
     * data type, it will return BasicDataType.UNKNOWN.
     *
     * @param obj
     *            The PV Manager VType value.
     * @return the data type.
     */
    public static BasicDataType getBasicDataType(VType obj) {
        Class<?> typeClass = ValueUtil.typeOf(obj);
        if (typeClass == VByte.class)
            return BasicDataType.BYTE;
        if (typeClass == VEnum.class)
            return BasicDataType.ENUM;
        if (typeClass == VDouble.class)
            return BasicDataType.DOUBLE;
        if (typeClass == VFloat.class)
            return BasicDataType.FLOAT;
        if (typeClass == VInt.class)
            return BasicDataType.INT;
        if (typeClass == VShort.class)
            return BasicDataType.SHORT;
        if (typeClass == VString.class)
            return BasicDataType.STRING;

        if (typeClass == VByteArray.class)
            return BasicDataType.BYTE_ARRAY;
        if (typeClass == VDoubleArray.class)
            return BasicDataType.DOUBLE_ARRAY;
        if (typeClass == VEnumArray.class)
            return BasicDataType.ENUM_ARRAY;
        if (typeClass == VFloatArray.class)
            return BasicDataType.FLOAT_ARRAY;
        if (typeClass == VIntArray.class)
            return BasicDataType.INT_ARRAY;
        if (typeClass == VShortArray.class)
            return BasicDataType.SHORT_ARRAY;
        if (typeClass == VStringArray.class)
            return BasicDataType.STRING_ARRAY;

        return BasicDataType.UNKNOWN;
    }

    /**Extract the display information from the VType obj.
     * @param obj the VType object.
     * @return the display information or null if there is no display info from the object.
     */
    public static Display getDisplayInfo(VType obj){
        return ValueUtil.displayOf(obj);
    }

    /**
     * Get double value from a {@link VType} object. It might be casted from
     * other numeric type.
     *
     * @param obj
     *            the VType object.
     * @return double or NaN if no double value is available in the object.
     */
    public static double getDouble(final VType obj) {
        Double value = ValueUtil.numericValueOf(obj);
        return value==null? Double.NaN:value;
    }

    /**
     * Get double value from a {@link VType} object at index. It might be casted
     * from other numeric type.
     *
     * @param obj
     *            the VType object.
     * @param index
     *            index of the double value
     * @return double or NaN if no double value is available in the object at
     *         the index.
     */
    public static double getDouble(final VType obj, int index) {
        if (index == 0)
            return getDouble(obj);
        if (obj instanceof VNumberArray) {
            final ListNumber data = ((VNumberArray) obj).getData();
            if (index < data.size())
                return data.getDouble(index);
        }
        if (obj instanceof VEnumArray) {
            final ListInt data = ((VEnumArray) obj).getIndexes();
            if (index < data.size())
                return data.getDouble(index);
        }
        return Double.NaN;
    }

    /**
     * Get double array from a VType object.
     *
     * @param obj
     *            an object implementing a standard type
     * @return the double array from the VType object or empty double array if no double array
     * can be extracted from the input.
     */
    public static double[] getDoubleArray(VType obj) {
        if (obj instanceof Scalar) {
            Object v = ((Scalar) obj).getValue();
            if (v instanceof Number)
                return new double[] { ((Number) v).doubleValue() };
        }
        if (obj instanceof Array) {
            Object array = ((Array) obj).getData();
            if (array instanceof ListNumber)
                return ListNumberToDoubleArray((ListNumber) array);
            if (obj instanceof VEnumArray) {
                ListInt tArray = ((VEnumArray) obj).getIndexes();
                return ListNumberToDoubleArray(tArray);
            }
        }
        return new double[0];
    }

    /**
     * Get the original number value of the VType object without casting. If it
     * is an array, return the first element.
     *
     * @param obj
     *            the VType object value.
     * @return the number or null if it is not a Number.
     */
    @SuppressWarnings("rawtypes")
    public static Number getNumber(VType obj) {
        if (obj instanceof VNumber) {
            return ((VNumber) obj).getValue();
        } else if (obj instanceof VEnum) {
            return ((VEnum) obj).getIndex();
        } else if (obj instanceof VNumberArray) {
            ListNumber data = ((VNumberArray) obj).getData();
            if (data != null && data.size() != 0) {
                if (data instanceof ListByte)
                    return data.getByte(0);
                if (data instanceof ListDouble)
                    return data.getDouble(0);
                if (data instanceof ListFloat)
                    return data.getFloat(0);
                if (data instanceof ListInt)
                    return data.getInt(0);
                if (data instanceof ListLong)
                    return data.getLong(0);
                if (data instanceof ListShort)
                    return data.getShort(0);
                return data.getDouble(0);
            }
        } else if (obj instanceof VEnumArray) {
            ListNumber data = ((VEnumArray) obj).getIndexes();
            if (data != null && data.size() != 0) {
                return data.getInt(0);
            }
        } else if (obj instanceof MultiScalar) {
            List values = ((MultiScalar) obj).getValues();
            if (!values.isEmpty())
                return getNumber((VType) values.get(0));
        }
        return null;
    }

    /**
     * Get the original number value of the VType object at a index without
     * casting.
     *
     * @param obj
     *            the VType object value.
     * @return the number or null if it is not a Number.
     */
    public static Number getNumber(VType obj, int index) {
        if (index == 0)
            return getNumber(obj);
        if (obj instanceof VNumberArray) {
            final ListNumber data = ((VNumberArray) obj).getData();
            if (index < data.size()) {
                if (data != null && data.size() != 0) {
                    if (data instanceof ListByte)
                        return data.getByte(index);
                    if (data instanceof ListDouble)
                        return data.getDouble(index);
                    if (data instanceof ListFloat)
                        return data.getFloat(index);
                    if (data instanceof ListInt)
                        return data.getInt(index);
                    if (data instanceof ListLong)
                        return data.getLong(index);
                    if (data instanceof ListShort)
                        return data.getShort(index);
                    return data.getDouble(index);
                }
            }
        }
        if (obj instanceof VEnumArray) {
            final ListInt data = ((VEnumArray) obj).getIndexes();
            if (index < data.size())
                return data.getInt(index);
        }
        return null;
    }

    /**
     * Get size of a VType object value.
     *
     * @param obj
     * @return 1 for scalar. Otherwise return size of the array.
     */
    public static int getSize(VType obj) {
        if (obj instanceof Scalar) {
            return 1;
        }

        if (obj instanceof Array) {
            return ((Array) obj).getSizes().getInt(0);
        }

        if(obj instanceof MultiScalar){
            return ((MultiScalar<?>)obj).getValues().size();
        }
        return 1;
    }

    /**Get String from a VType value.
     * @param obj the value
     * @return the String from the VType value.
     */
    public static String getString(VType obj){
        return formatValue(FormatEnum.DEFAULT, obj, -1);
    }

    /**Extract the {@link Timestamp} from the VType obj.
     * @param obj the VType object.
     * @return the time or null if there is no time info in the object.
     */
    public static Instant getTimestamp(VType obj){
        Time timeOf = ValueUtil.timeOf(obj);
        if(timeOf != null)
            return timeOf.getTimestamp();
        return null;
    }

    /**Get wrapped array in the VNumberArray object. The wrapped array could be double[],
     * float[], int[], long[], short[], byte[] etc.
     * @param obj the {@link VType} object.
     * @return the wrapped array or null if no array is wrapped in the object.
     */
    public static Object getWrappedArray(VType obj) {
        if(obj instanceof VNumberArray)
            return CollectionNumbers.wrappedArray(((VNumberArray) obj).getData());
        return null;
    }

    /**Is an object primary array
     * @param array the object
     * @return true if it is a primary type array, such as byte[], int[], double[] etc.
     */
    public static boolean isPrimaryNumberArray(Object array) {
        return array instanceof byte[] || array instanceof int[] || array instanceof long[]
                || array instanceof double[] || array instanceof float[]
                || array instanceof short[] || array instanceof char[];
    }

    private static String formatNumberArray(FormatEnum formatEnum, VNumberArray pmArray,
            int precision) {
        ListNumber data = ((VNumberArray) pmArray).getData();
        if (formatEnum == FormatEnum.STRING) {
            final byte[] bytes = new byte[data.size()];
            // Copy bytes until end _or_ '\0'
            int len = 0;
            while (len<bytes.length)
            {
                final byte b = data.getByte(len);
                if (b == 0)
                    break;
                else
                    bytes[len++] = b;
            }
            return new String(bytes, 0, len);
        } else {
            if (data.size() <= 0)
                return "[]"; //$NON-NLS-1$

            int displayPrecision = calculatePrecision(pmArray, precision);

            StringBuilder sb = new StringBuilder(data.size());
            sb.append(formatScalarNumber(formatEnum, data.getDouble(0), displayPrecision));
            for (int i = 1; i < data.size(); i++) {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
                sb.append(formatScalarNumber(formatEnum, data.getDouble(i), displayPrecision));
                if (i >= MAX_FORMAT_VALUE_COUNT) {
                    sb.append(ARRAY_ELEMENT_SEPARATOR);
                    sb.append("..."); //$NON-NLS-1$
                    sb.append(formatScalarNumber(formatEnum, data.getDouble(data.size() - 1), displayPrecision));
                    sb.append(" "); //$NON-NLS-1$
                    sb.append("["); //$NON-NLS-1$
                    sb.append(data.size());
                    sb.append("]"); //$NON-NLS-1$
                    break;
                }
            }
            return sb.toString();
        }

    }

    private static String formatObjectArray(Object[] array) {
        StringBuilder sb = new StringBuilder(array.length);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(ARRAY_ELEMENT_SEPARATOR);
            sb.append(array[i]);
            if (i >= MAX_FORMAT_VALUE_COUNT) {
                sb.append(ARRAY_ELEMENT_SEPARATOR);
                sb.append("..."); //$NON-NLS-1$
                sb.append(array[array.length - 1]);
                sb.append(" "); //$NON-NLS-1$
                sb.append("["); //$NON-NLS-1$
                sb.append(array.length);
                sb.append("]"); //$NON-NLS-1$
                break;
            }
        }
        return sb.toString();
    }

    private static String formatScalarEnum(FormatEnum formatEnum, VEnum enumValue) {
        switch (formatEnum) {
        case DECIMAL:
        case EXP:
        case COMPACT:
        case ENG:
            return Integer.toString(enumValue.getIndex());
        case HEX:
        case HEX64:
            return HEX_PREFIX + Integer.toHexString(enumValue.getIndex());
        case DEFAULT:
        case STRING:
        default:
            return enumValue.getValue();
        }
    }

    private static String formatScalarNumber(FormatEnum formatEnum, Number numValue, int precision) {
        return formatScalarNumber(formatEnum, null, numValue, precision);
    }

    /**
     * @param formatEnum
     * @param pmValue
     *            The PVManager V Value. It must be a scalar with number value.
     * @param numValue
     *            the number value. not necessary if pmValue is given.
     * @param precision
     * @return
     */
    private static String formatScalarNumber(FormatEnum formatEnum, Object pmValue,
            Number numValue, int precision) {
        if (pmValue != null)
            numValue = (Number) ((Scalar) pmValue).getValue();

        NumberFormat numberFormat;

        int displayPrecision = calculatePrecision(pmValue, precision);

        switch (formatEnum) {
        case DECIMAL:
        case DEFAULT:
        default:
            if (precision == UNSET_PRECISION) {
                if (pmValue instanceof Display && ((Display) pmValue).getFormat() != null) {
                    return ((Display) pmValue).getFormat().format(((Number) numValue).doubleValue());
                } else {
                    return formatScalarNumber(FormatEnum.COMPACT, numValue, displayPrecision);
                }

            } else {
                // Sun's implementation of the JDK returns the Unicode replacement
                // character, U+FFFD, when asked to parse a NaN. This is more
                // consistent with the rest of CSS.
                if(Double.isNaN(numValue.doubleValue())) {
                    return Double.toString(Double.NaN);
                }

                // Also check for positive and negative infinity.
                if(Double.isInfinite(numValue.doubleValue())) {
                    return Double.toString(numValue.doubleValue());
                }

                numberFormat = getDecimalFormat(precision);
                return numberFormat.format(numValue.doubleValue());
            }

        case COMPACT:
            double dValue = numValue.doubleValue();
            if (((dValue > 0.0001) && (dValue < 10000))
                    || ((dValue < -0.0001) && (dValue > -10000)) || dValue == 0.0) {
                return formatScalarNumber(FormatEnum.DECIMAL, numValue, displayPrecision);
            } else {
                return formatScalarNumber(FormatEnum.EXP, numValue, displayPrecision);
            }

        case ENG:
            double value = numValue.doubleValue();
            if (value == 0) {
                return formatScalarNumber(FormatEnum.EXP, numValue, displayPrecision);
            }

            double log10 = Math.log10(Math.abs(value));
            int power = 3 * (int) Math.floor(log10 / 3);
            return String.format("%." + displayPrecision + "fE%d", value / Math.pow(10, power), power);

        case EXP:
            // Exponential notation identified as 'negative' precision in cached
            numberFormat = getExponentialFormat(displayPrecision);
            return numberFormat.format(numValue.doubleValue());

        case HEX:
            return HEX_PREFIX + Integer.toHexString(numValue.intValue()).toUpperCase();
        case HEX64:
            return HEX_PREFIX + Long.toHexString(numValue.longValue());
        case STRING:
            return new String(new char[] { (char) numValue.intValue()});
        }
    }

    /** Return decimal number format.
     *
     *  The formats are created if it has not previously been used.
     *  Constructed formats are cached.
     *
     * @param precision
     * @return
     */
    private static NumberFormat getDecimalFormat(int precision) {
        int absPrecision = Math.abs(precision);
        NumberFormat numberFormat = decimalFormatCacheMap.get(absPrecision);
        if (numberFormat == null) {
            numberFormat = new DecimalFormat("0"); //$NON-NLS-1$
            numberFormat.setMinimumFractionDigits(absPrecision);
            numberFormat.setMaximumFractionDigits(absPrecision);
            decimalFormatCacheMap.put(absPrecision, numberFormat);
        }
        return numberFormat;
    }

    /** Return exponential number format.
     *
     *  The formats are created if it has not previously been used.
     *  Constructed formats are cached.
     *
     * @param precision
     * @return
     */
    private static NumberFormat getExponentialFormat(int precision) {
        int absPrecision = Math.abs(precision);
        NumberFormat numberFormat = expFormatCacheMap.get(absPrecision);
        if (numberFormat == null) {
            final StringBuffer pattern = new StringBuffer(10);
            pattern.append("0"); //$NON-NLS-1$
            if (precision > 0) {
                pattern.append(".");
            }
            for (int i = 0; i < precision; ++i) {
                pattern.append('0');
            }
            pattern.append("E0"); //$NON-NLS-1$
            numberFormat = new DecimalFormat(pattern.toString());
            expFormatCacheMap.put(absPrecision, numberFormat);
        }
        return numberFormat;
    }

    /** Find the display precision for the value:
     *  - if a precision is specified use that (precision != UNSET)
     *  - if precision is UNSET, find the precision from the passed VType value
     *  - if no suitable value passed use the default
     *
     * @param pmValue
     * @param precision
     * @return
     */
    private static int calculatePrecision(Object pmValue, int precision) {
        int displayPrecision = DEFAULT_PRECISION;

        if (precision != UNSET_PRECISION) {
            displayPrecision = precision;
        }
        else if (pmValue instanceof Display) {
            final NumberFormat format = ((Display) pmValue).getFormat();
            if (format != null)
                displayPrecision = format.getMinimumFractionDigits();
        }

        return displayPrecision;
    }

    private static double[] ListNumberToDoubleArray(ListNumber listNumber) {
        Object wrappedArray = CollectionNumbers.wrappedArray(listNumber);
        if (wrappedArray instanceof double[])
            return (double[]) wrappedArray;

        final double[] result = new double[listNumber.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = listNumber.getDouble(i);
        }
        return result;
    }

}
