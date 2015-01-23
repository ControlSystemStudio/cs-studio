package org.csstudio.dal.simple;

import java.util.BitSet;

import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DoubleSeqProperty;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.EnumProperty;
import org.csstudio.dal.LongProperty;
import org.csstudio.dal.LongSeqProperty;
import org.csstudio.dal.ObjectProperty;
import org.csstudio.dal.ObjectSeqProperty;
import org.csstudio.dal.PatternProperty;
import org.csstudio.dal.StringProperty;
import org.csstudio.dal.StringSeqProperty;

/**
 * Enumeration for data types supported by DAL and this broker. 
 * 
 * @author ikriznar
 *
 */
public enum DataFlavor {
	OBJECT(Object.class,ObjectProperty.class),
	OBJECTS(Object[].class,ObjectSeqProperty.class),
	DOUBLE(Double.class,DoubleProperty.class),
	DOUBLES(double[].class,DoubleSeqProperty.class),
	LONG(Long.class,LongProperty.class),
	LONGS(long[].class,LongSeqProperty.class),
	STRING(String.class,StringProperty.class),
	STRINGS(String[].class,StringSeqProperty.class),
	PATTERN(BitSet.class,PatternProperty.class),
	ENUM(Long.class,EnumProperty.class),
	ANYDATA(AnyData.class,null);
	
	private Class<? extends DynamicValueProperty<?>> dalType;
	private Class<?> javaType;

	private DataFlavor(Class<?> javaType, Class<? extends DynamicValueProperty<?>> dalType) {
		this.javaType=javaType;
		this.dalType=dalType;
	}

	public Class<? extends DynamicValueProperty<?>> getDALType() {
		return dalType;
	}
	
	public Class<?> getJavaType() {
		return javaType;
	}

	public static DataFlavor fromJavaType(Class<?> javaType) {
		Class<?> c= toDALDataType(javaType);
		DataFlavor[] d= DataFlavor.values();
		for (int i = 0; i < d.length; i++) {
			if (d[i].getJavaType() == c) {
				return d[i];
			}
		}
		return null;
	}
	
	/**
	 * Converts Java data type to one of DAL supported Java data types,
	 * such as <code>Double</code>, <code>Long</code>, <code>String</code> and similar.
	 * @param type Java data type
	 * @return DAL supported Java data type
	 */
	public static Class<?> toDALDataType(Class<?> type) {
		
		if (type==null) {
			return null;
		}
		
		if (type == Double.class 
				|| type == Float.class
				|| type == double.class
				|| type == float.class) {
			return Double.class;
		}
		
		if (type == Double[].class 
				|| type == Float[].class
				|| type == double[].class
				|| type == float[].class) {
			return double[].class;
		}

		if (type == Long.class 
				|| type == Integer.class
				|| type == Short.class
				|| type == long.class
				|| type == int.class
				|| type == short.class
				|| type == char.class) {
			return Long.class;
		}
		
		if (type == Long[].class 
				|| type == Integer[].class
				|| type == Short[].class
				|| type == long[].class
				|| type == int[].class
				|| type == short[].class
				|| type == char[].class) {
			return long[].class;
		}
		
		if (type == String.class 
				|| type == String[].class
				|| type == Object.class
				|| type == Object[].class
				|| type == BitSet.class
				|| type == AnyData.class) {
			return type;
		}
		
		return null;

	}
	
	public static Class<? extends DynamicValueProperty<?>> toDALPropertyType(Class<?> javaType) {
		Class<?> t= toDALDataType(javaType);
		
		if (t == Double.class) {
			return DoubleProperty.class;
		}
		
		if (t == double[].class) {
			return DoubleSeqProperty.class;
		}

		if (t == Long.class) {
			return LongProperty.class;
		}

		if (t == long[].class) {
			return LongSeqProperty.class;
		}
 
		if (t == String.class) {
			return LongProperty.class;
		}

		if (t == String[].class) {
			return LongSeqProperty.class;
		}
		
		if (t == Object.class) {
			return ObjectProperty.class;
		}

		if (t == Object[].class) {
			return ObjectSeqProperty.class;
		}

		if (t == Object.class) {
			return ObjectProperty.class;
		}

		if (t == BitSet.class) {
			return PatternProperty.class;
		}

		return null;

	}
}