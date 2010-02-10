package org.epics.css.dal.simple.impl;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class DataUtil {
	
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
		if (arg instanceof String) return new Double((String) arg);
		if (arg instanceof double[]) return new Double(((double[]) arg)[0]);
		if (arg instanceof long[]) return new Long(((long[]) arg)[0]);
		if (arg instanceof String[]) return new Double(((String[]) arg)[0]);
		throw new IllegalArgumentException("Object of type "+arg.getClass().getName()+" can not be cast to Number.");
	}
	
    public static MetaData
    createNumericMetaData(double disp_low, double disp_high,
                    double warn_low, double warn_high,
                    double alarm_low, double alarm_high,
                    int prec, String units)
	{
	    return new NumericMetaDataImpl(disp_low, disp_high, warn_low, warn_high,
	                    alarm_low, alarm_high, prec, units);
	}
    
    public static MetaData createEnumeratedMetaData(String[] labels) {
    	return new EnumeratedMetaDataImpl(labels);
    }
    
    public static AnyData createAnyData(DynamicValueProperty<?> property) {
    	Class<?> type = property.getDataType();
    	
    	if (property.getLatestValueResponse() == null) {
    		return new UninitializedAnyDataImpl(property);
    	}
    	
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
