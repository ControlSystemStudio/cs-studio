package org.csstudio.platform.internal.simpledal.converters;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.simpledal.ValueType;

public class ConverterUtil {
	private static Map<ValueType, IValueTypeConverter> converters;

	static {
		converters = new HashMap<ValueType, IValueTypeConverter>();
		converters.put(ValueType.DOUBLE, new DoubleConverter());
		converters.put(ValueType.LONG, new LongConverter());
		converters.put(ValueType.STRING, new StringConverter());
		converters.put(ValueType.OBJECT, new ObjectConverter());
		converters
				.put(ValueType.DOUBLE_SEQUENCE, new DoubleSequenceConverter());
		converters.put(ValueType.LONG_SEQUENCE, new LongSequenceConverter());
		converters
				.put(ValueType.STRING_SEQUENCE, new StringSequenceConverter());
		converters
				.put(ValueType.OBJECT_SEQUENCE, new ObjectSequenceConverter());
		converters
		.put(ValueType.ENUM, new ObjectConverter());

	}

	public static <E> E convert(Object value, ValueType valueType) {
		IValueTypeConverter converter = converters.get(valueType);
		assert converter != null : "Converter for type ["+valueType + "] is missing!";

		// converters do always return a value (even a fall back for a provided null value)
		E result = (E) converter.convert(value);
		
		return result;
	}
}
