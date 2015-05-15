package org.csstudio.dal.simple.impl;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.EnumPropertyCharacteristics;
import org.csstudio.dal.NumericPropertyCharacteristics;
import org.csstudio.dal.PatternPropertyCharacteristics;
import org.csstudio.dal.PropertyCharacteristics;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.MetaData;

public class DataUtil {
    public static final Double UNINITIALIZED_DOUBLE_VALUE = Double.NaN;

    public static <T> T castTo(final Object arg, final Class<T> type) {

        if (arg == null) {
            throw new NullPointerException("Argument can not be null.");
        }

        if (type == null) {
            throw new NullPointerException("Type can not be null.");
        }

        // TODO add support for other types, array types?

        if (type.isInstance(arg)) {
            return type.cast(arg);
        } else if (arg instanceof String && !type.equals(String.class)) {
            if (type.equals(Double.class)) {
                return type.cast(Double.parseDouble((String) arg));
            } else if (type.equals(Long.class)) {
                return type.cast(Long.parseLong((String) arg));
            }
            throw new IllegalArgumentException("Conversion from String to type "+type+" is not defined.");
        } else if (arg instanceof Number) {
            if (type.equals(Double.class)) {
                return type.cast(new Double(((Number) arg).doubleValue()));
            } else if (type.equals(Long.class)) {
                return type.cast(new Long(((Number) arg).longValue()));
            } else if (type.equals(String.class)) {
                return type.cast(arg.toString());
            }
        }
        return type.cast(arg);
    }

    public static Number castToNumber(final Object arg) {
        if (arg instanceof Number) {
            return (Number) arg;
        }
        try {
            if (arg instanceof String) {
                return new Double((String) arg);
            }
        } catch (final NumberFormatException e) {
            return UNINITIALIZED_DOUBLE_VALUE;
        }
        if (arg instanceof double[]) {
            return new Double(((double[]) arg)[0]);
        }
        if (arg instanceof long[]) {
            return new Long(((long[]) arg)[0]);
        }
        if (arg instanceof String[]) {
            return new Double(((String[]) arg)[0]);
        }
        throw new IllegalArgumentException("Object of type "+arg.getClass().getName()+" can not be cast to Number.");
    }

    @Deprecated
    public static MetaData createNumericMetaData(final double disp_low, final double disp_high,
                    final double warn_low, final double warn_high,
                    final double alarm_low, final double alarm_high,
                    final int prec, final String units)
    {
        return new NumericMetaDataImpl(disp_low, disp_high, warn_low, warn_high,
                        alarm_low, alarm_high, prec, units);
    }

    @Deprecated
    public static MetaData createEnumeratedMetaData(final String[] labels, final Object[] values) {
        return new EnumeratedMetaDataImpl(labels,values);
    }

    /**
     * Method gathers known types from the given characteristics and constructs
     * a {@link MetaData} that carries all that information. If the characteristics
     * do not contain certain data, the metadata will return null for all those
     * objects.
     *
     * @param characteristics the characteristics that carries the data
     * @return the metadata
     */
    public static MetaData createMetaData(final Map<String, Object> characteristics) {
        final String description = (String)characteristics.get(PropertyCharacteristics.C_DESCRIPTION);
        final String name = (String) characteristics.get(PropertyCharacteristics.C_DISPLAY_NAME);
        final Integer sequenceLength = (Integer) characteristics.get(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
        final Integer precision = (Integer) characteristics.get(NumericPropertyCharacteristics.C_PRECISION);
        final AccessType accessType = (AccessType) characteristics.get(PropertyCharacteristics.C_ACCESS_TYPE);
        final String hostname = (String) characteristics.get(PropertyCharacteristics.C_HOSTNAME);
        final String dataType = (String) characteristics.get(PropertyCharacteristics.C_DATATYPE);
        final String units = (String) characteristics.get(NumericPropertyCharacteristics.C_UNITS);
        final Number dispMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MIN);
        final Number dispMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MAX);
        final Number warningMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_WARNING_MIN);
        final Number warningMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_WARNING_MAX);
        final Number alarmMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_ALARM_MIN);
        final Number alarmMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_ALARM_MAX);
        final String format = (String) characteristics.get(NumericPropertyCharacteristics.C_FORMAT);
        final String[] enumDescriptions = (String[]) characteristics.get(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
        final Object[] enumValues = (Object[]) characteristics.get(EnumPropertyCharacteristics.C_ENUM_VALUES);
        return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
                alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
                sequenceLength,precision,dataType,accessType,hostname);
    }

    /**
     * Method gathers known types from the given proxy and constructs
     * a {@link MetaData} that carries all that information. If the characteristics
     * do not contain certain data, the metadata will return null for all those
     * objects.
     *
     * @param proxy the proxy with charactoristics
     * @return the metadata
     * @throws DataExchangeException
     */
    public static MetaData createMetaData(final DirectoryProxy<?> proxy) throws DataExchangeException {
        final String description = (String)proxy.getCharacteristic(PropertyCharacteristics.C_DESCRIPTION);
        final String name = (String) proxy.getCharacteristic(PropertyCharacteristics.C_DISPLAY_NAME);
        final Integer sequenceLength = (Integer) proxy.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
        final Integer precision = (Integer) proxy.getCharacteristic(NumericPropertyCharacteristics.C_PRECISION);
        final AccessType accessType = (AccessType) proxy.getCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE);
        final String hostname = (String) proxy.getCharacteristic(PropertyCharacteristics.C_HOSTNAME);
        final String dataType = (String) proxy.getCharacteristic(PropertyCharacteristics.C_DATATYPE);
        final String units = (String) proxy.getCharacteristic(NumericPropertyCharacteristics.C_UNITS);
        final Number dispMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MIN);
        final Number dispMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MAX);
        final Number warningMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_WARNING_MIN);
        final Number warningMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_WARNING_MAX);
        final Number alarmMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_ALARM_MIN);
        final Number alarmMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_ALARM_MAX);
        final String format = (String) proxy.getCharacteristic(NumericPropertyCharacteristics.C_FORMAT);
        final String[] enumDescriptions = (String[]) proxy.getCharacteristic(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
        final Object[] enumValues = (Object[]) proxy.getCharacteristic(EnumPropertyCharacteristics.C_ENUM_VALUES);
        return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
                alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
                sequenceLength,precision,dataType,accessType,hostname);
    }

    /**
     * Method gathers known types from the given characteristics and constructs
     * a {@link MetaData} that carries all that information. If the characteristics
     * do not contain certain data, the metadata will return null for all those
     * objects.
     *
     * @param characteristics the characteristics that carries the data
     * @return the metadata
     * @throws NamingException if one of the attributes cannot be read
     */
    public static MetaData createMetaData(final Attributes characteristics) throws NamingException {
        Attribute o = characteristics.get(PropertyCharacteristics.C_DESCRIPTION);
        final String description = (String) (o != null ? o.get() : null);
        o = characteristics.get(PropertyCharacteristics.C_DISPLAY_NAME);
        final String name = (String) (o != null ? o.get() : null);
        o = characteristics.get(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
        final Integer sequenceLength = (Integer) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_PRECISION);
        final Integer resolution = (Integer) (o != null ? o.get() : null);
        o = characteristics.get(PropertyCharacteristics.C_ACCESS_TYPE);
        final AccessType accessType = (AccessType) (o != null ? o.get() : null);
        o = characteristics.get(PropertyCharacteristics.C_HOSTNAME);
        final String hostname = (String)(o != null ? o.get() : null);
        o = characteristics.get(PropertyCharacteristics.C_DATATYPE);
        final String dataType = (String)(o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_UNITS);
        final String units = (String)(o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MIN);
        final Number dispMin = (Number) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MAX);
        final Number dispMax = (Number) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_WARNING_MIN);
        final Number warningMin = (Number) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_WARNING_MAX);
        final Number warningMax = (Number)(o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_ALARM_MIN);
        final Number alarmMin = (Number) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_ALARM_MAX);
        final Number alarmMax = (Number) (o != null ? o.get() : null);
        o = characteristics.get(NumericPropertyCharacteristics.C_FORMAT);
        final String format = (String) (o != null ? o.get() : null);
        o = characteristics.get(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
        final String[] enumDescriptions = (String[]) (o != null ? o.get() : null);
        o = characteristics.get(EnumPropertyCharacteristics.C_ENUM_VALUES);
        final Object[] enumValues = (Object[]) (o != null ? o.get() : null);
        return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
                alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
                sequenceLength,resolution,dataType,accessType,hostname);
    }

    public static AnyData createAnyData(final DynamicValueProperty<?> property) {
        final Class<?> type = property.getDataType();

        if (type.equals(Double.class)) {
            return new DoubleAnyDataImpl((DynamicValueProperty<Double>) property);
        }
        if (type.equals(Long.class)) {
            return new LongAnyDataImpl((DynamicValueProperty<Long>) property);
        }
        if (type.equals(String.class)) {
            return new StringAnyDataImpl((DynamicValueProperty<String>) property);
        }
        if (type.equals(double[].class)) {
            return new DoubleSeqAnyDataImpl((DynamicValueProperty<double[]>) property);
        }
        if (type.equals(long[].class)) {
            return new LongSeqAnyDataImpl((DynamicValueProperty<long[]>) property);
        }
        if (type.equals(String[].class)) {
            return new StringSeqAnyDataImpl((DynamicValueProperty<String[]>) property);
        } else {
            return null;
        }
    }

    public static long[] toLongSeq(final double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final long[] output = new long[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = Math.round(input[i]);
        }
        return output;
    }

    public static double[] toDoubleSeq(final long[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final double[] output = new double[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static String[] toStringSeq(final Object[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final String[] output = new String[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = input[i].toString();
        }
        return output;
    }

    public static String[] toStringSeq(final double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final String[] output = new String[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Double(input[i]).toString();
        }
        return output;
    }

    public static String[] toStringSeq(final long[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final String[] output = new String[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Long(input[i]).toString();
        }
        return output;
    }

    public static Number[] toNumberSeq(final long[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final Number[] output = new Number[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Long(input[i]);
        }
        return output;
    }

    public static Number[] toNumberSeq(final double[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final Number[] output = new Number[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Double(input[i]);
        }
        return output;
    }

    public static Number[] toNumberSeq(final String[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final Number[] output = new Number[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Double(input[i]);
        }
        return output;
    }

    public static double[] toDoubleSeq(final String[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final double[] output = new double[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Double(input[i]);
        }
        return output;
    }

    public static long[] toLongSeq(final String[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }
        final long[] output = new long[input.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new Long(input[i]);
        }
        return output;
    }

}
