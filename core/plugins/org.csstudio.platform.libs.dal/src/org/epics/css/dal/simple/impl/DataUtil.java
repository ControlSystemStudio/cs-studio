package org.epics.css.dal.simple.impl;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.epics.css.dal.AccessType;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EnumPropertyCharacteristics;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.PatternPropertyCharacteristics;
import org.epics.css.dal.PropertyCharacteristics;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SequencePropertyCharacteristics;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class DataUtil {
	public static final Double UNINITIALIZED_DOUBLE_VALUE = Double.NaN;
	
	public static <T> T castTo(Object arg, Class<T> type) {
		
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
	
	public static Number castToNumber(Object arg) {
		if (arg instanceof Number) return (Number) arg;
		try {
			if (arg instanceof String) return new Double((String) arg);
		} catch (NumberFormatException e) {
			return UNINITIALIZED_DOUBLE_VALUE;
		}
		if (arg instanceof double[]) return new Double(((double[]) arg)[0]);
		if (arg instanceof long[]) return new Long(((long[]) arg)[0]);
		if (arg instanceof String[]) return new Double(((String[]) arg)[0]);
		throw new IllegalArgumentException("Object of type "+arg.getClass().getName()+" can not be cast to Number.");
	}
	
	@Deprecated
    public static MetaData createNumericMetaData(double disp_low, double disp_high,
                    double warn_low, double warn_high,
                    double alarm_low, double alarm_high,
                    int prec, String units)
	{
	    return new NumericMetaDataImpl(disp_low, disp_high, warn_low, warn_high,
	                    alarm_low, alarm_high, prec, units);
	}
    
	@Deprecated
    public static MetaData createEnumeratedMetaData(String[] labels, Object[] values) {
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
    public static MetaData createMetaData(Map<String, Object> characteristics) {
    	String description = (String)characteristics.get(PropertyCharacteristics.C_DESCRIPTION);
		String name = (String) characteristics.get(PropertyCharacteristics.C_DISPLAY_NAME);
		Integer sequenceLength = (Integer) characteristics.get(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
		Integer resolution = (Integer) characteristics.get(NumericPropertyCharacteristics.C_RESOLUTION);
		AccessType accessType = (AccessType) characteristics.get(PropertyCharacteristics.C_ACCESS_TYPE);
		String hostname = (String) characteristics.get(PropertyCharacteristics.C_HOSTNAME);
		String dataType = (String) characteristics.get(PropertyCharacteristics.C_DATATYPE);
		String units = (String) characteristics.get(NumericPropertyCharacteristics.C_UNITS);
		Number dispMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MIN);
		Number dispMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MAX);
		Number warningMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_WARNING_MIN);
		Number warningMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_WARNING_MAX);
		Number alarmMin = (Number) characteristics.get(NumericPropertyCharacteristics.C_ALARM_MIN);
		Number alarmMax = (Number) characteristics.get(NumericPropertyCharacteristics.C_ALARM_MAX);
		String format = (String) characteristics.get(NumericPropertyCharacteristics.C_FORMAT);
		String[] enumDescriptions = (String[]) characteristics.get(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
		Object[] enumValues = (Object[]) characteristics.get(EnumPropertyCharacteristics.C_ENUM_VALUES);
		return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
				alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
				sequenceLength,resolution,dataType,accessType,hostname);
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
    public static MetaData createMetaData(DirectoryProxy<?> proxy) throws DataExchangeException {
    	String description = (String)proxy.getCharacteristic(PropertyCharacteristics.C_DESCRIPTION);
		String name = (String) proxy.getCharacteristic(PropertyCharacteristics.C_DISPLAY_NAME);
		Integer sequenceLength = (Integer) proxy.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
		Integer resolution = (Integer) proxy.getCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION);
		AccessType accessType = (AccessType) proxy.getCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE);
		String hostname = (String) proxy.getCharacteristic(PropertyCharacteristics.C_HOSTNAME);
		String dataType = (String) proxy.getCharacteristic(PropertyCharacteristics.C_DATATYPE);
		String units = (String) proxy.getCharacteristic(NumericPropertyCharacteristics.C_UNITS);
		Number dispMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MIN);
		Number dispMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MAX);
		Number warningMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_WARNING_MIN);
		Number warningMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_WARNING_MAX);
		Number alarmMin = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_ALARM_MIN);
		Number alarmMax = (Number) proxy.getCharacteristic(NumericPropertyCharacteristics.C_ALARM_MAX);
		String format = (String) proxy.getCharacteristic(NumericPropertyCharacteristics.C_FORMAT);
		String[] enumDescriptions = (String[]) proxy.getCharacteristic(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
		Object[] enumValues = (Object[]) proxy.getCharacteristic(EnumPropertyCharacteristics.C_ENUM_VALUES);
		return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
				alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
				sequenceLength,resolution,dataType,accessType,hostname);
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
    public static MetaData createMetaData(Attributes characteristics) throws NamingException {
    	Attribute o = characteristics.get(PropertyCharacteristics.C_DESCRIPTION);
    	String description = (String) (o != null ? o.get() : null);
		o = characteristics.get(PropertyCharacteristics.C_DISPLAY_NAME);
		String name = (String) (o != null ? o.get() : null);
		o = characteristics.get(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
		Integer sequenceLength = (Integer) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_RESOLUTION);
		Integer resolution = (Integer) (o != null ? o.get() : null);
		o = characteristics.get(PropertyCharacteristics.C_ACCESS_TYPE);
		AccessType accessType = (AccessType) (o != null ? o.get() : null);
		o = characteristics.get(PropertyCharacteristics.C_HOSTNAME);
		String hostname = (String)(o != null ? o.get() : null);
		o = characteristics.get(PropertyCharacteristics.C_DATATYPE);
		String dataType = (String)(o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_UNITS);
		String units = (String)(o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MIN);
		Number dispMin = (Number) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MAX);
		Number dispMax = (Number) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_WARNING_MIN);
		Number warningMin = (Number) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_WARNING_MAX);
		Number warningMax = (Number)(o != null ? o.get() : null); 
		o = characteristics.get(NumericPropertyCharacteristics.C_ALARM_MIN);
		Number alarmMin = (Number) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_ALARM_MAX);
		Number alarmMax = (Number) (o != null ? o.get() : null);
		o = characteristics.get(NumericPropertyCharacteristics.C_FORMAT);
		String format = (String) (o != null ? o.get() : null);
		o = characteristics.get(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS);
		String[] enumDescriptions = (String[]) (o != null ? o.get() : null);
		o = characteristics.get(EnumPropertyCharacteristics.C_ENUM_VALUES);
		Object[] enumValues = (Object[]) (o != null ? o.get() : null);
		return new MetaDataImpl(name,description,dispMin,dispMax,warningMin,warningMax,
				alarmMin,alarmMax,enumDescriptions,enumValues,format,units,
				sequenceLength,resolution,dataType,accessType,hostname);
	}
    
    public static AnyData createAnyData(DynamicValueProperty<?> property) {
    	Class<?> type = property.getDataType();
    	
    	if (type.equals(Double.class)) return new DoubleAnyDataImpl((DynamicValueProperty<Double>) property);
    	if (type.equals(Long.class)) return new LongAnyDataImpl((DynamicValueProperty<Long>) property);
    	if (type.equals(String.class)) return new StringAnyDataImpl((DynamicValueProperty<String>) property);
    	if (type.equals(double[].class)) return new DoubleSeqAnyDataImpl((DynamicValueProperty<double[]>) property);
    	if (type.equals(long[].class)) return new LongSeqAnyDataImpl((DynamicValueProperty<long[]>) property);
    	if (type.equals(String[].class)) return new StringSeqAnyDataImpl((DynamicValueProperty<String[]>) property);
    	// TODO add other types...
    	else return null;
    }
    
    public static long[] toLongSeq(double[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	long[] output = new long[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = Math.round(input[i]);
		}
    	return output;
    }
    
    public static double[] toDoubleSeq(long[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	double[] output = new double[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = input[i];
		}
    	return output;
    }
    
    public static String[] toStringSeq(Object[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	String[] output = new String[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = input[i].toString();
		}
    	return output;
    }
    
    public static String[] toStringSeq(double[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	String[] output = new String[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = (new Double(input[i])).toString();
		}
    	return output;
    }
    
    public static String[] toStringSeq(long[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	String[] output = new String[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = (new Long(input[i])).toString();
		}
    	return output;
    }
    
    public static Number[] toNumberSeq(long[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	Number[] output = new Number[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = new Long(input[i]);
		}
    	return output;
    }
    
    public static Number[] toNumberSeq(double[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	Number[] output = new Number[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = new Double(input[i]);
		}
    	return output;
    }
    
    public static Number[] toNumberSeq(String[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	Number[] output = new Number[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = new Double(input[i]);
		}
    	return output;
    }
    
    public static double[] toDoubleSeq(String[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	double[] output = new double[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = new Double(input[i]);
		}
    	return output;
    }
    
    public static long[] toLongSeq(String[] input) {
    	if (input == null) throw new IllegalArgumentException("null argument");
    	long[] output = new long[input.length];
    	for (int i = 0; i < output.length; i++) {
			output[i] = new Long(input[i]);
		}
    	return output;
    }
	
}
