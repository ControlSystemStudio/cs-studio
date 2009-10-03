package org.epics.css.dal.tango;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DoubleSeqProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.LongSeqProperty;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.DoubleSeqPropertyImpl;
import org.epics.css.dal.impl.LongPropertyImpl;
import org.epics.css.dal.impl.LongSeqPropertyImpl;
import org.epics.css.dal.impl.StringPropertyImpl;
import org.epics.css.dal.impl.StringSeqPropertyImpl;
import org.epics.css.dal.proxy.PropertyProxy;

import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.TangoConst;

public class ProxyUtilities {

	static Class<? extends PropertyProxy<?>> getProxyImplementationClass(Class<? extends SimpleProperty<?>> type) {
		if (DoubleProperty.class.isAssignableFrom(type) || DoublePropertyImpl.class.isAssignableFrom(type)) {
			return DoublePropertyProxyImpl.class;
		} else if (DoubleSeqProperty.class.isAssignableFrom(type) || DoubleSeqPropertyImpl.class.isAssignableFrom(type)) {
			return DoubleSeqPropertyProxyImpl.class;
		} else if (LongProperty.class.isAssignableFrom(type) || LongPropertyImpl.class.isAssignableFrom(type)) {
			return LongPropertyProxyImpl.class;
		} else if (LongSeqProperty.class.isAssignableFrom(type) || LongSeqPropertyImpl.class.isAssignableFrom(type)) {
			return LongSeqPropertyProxyImpl.class;
		} 
		return null;
	}
	
	static Class<? extends PropertyProxy<?>> getPropertyProxyImplementationClass(String devName, String name, Class<? extends SimpleProperty<?>> propertyType) throws DevFailed {
		return getPropertyProxyImplementationClass(devName,name);
	}
	
	static Class<? extends PropertyProxy<?>> getPropertyProxyImplementationClass(String devName, String name) throws DevFailed {
		DeviceProxy proxy = new DeviceProxy(devName);
		AttributeInfo ai = proxy.get_attribute_info(name);
		AttrDataFormat adf = ai.data_format;
		int type = ai.data_type;
		if (adf == AttrDataFormat.SCALAR) {
			switch(type) {
    			case 3 : return LongPropertyProxyImpl.class;
    			case 5 : return DoublePropertyProxyImpl.class;
    			case 8 : return StringPropertyProxyImpl.class;
    			case 23 : return LongPropertyProxyImpl.class;
			}
		} else if (adf == AttrDataFormat.SPECTRUM) {
			switch(type) {
				case 3 : return LongSeqPropertyProxyImpl.class;
				case 5 : return DoubleSeqPropertyProxyImpl.class;
				case 8 : return StringSeqPropertyProxyImpl.class;
				case 23 : return LongSeqPropertyProxyImpl.class;
			}
		}
		
		throw new UnsupportedOperationException("Type " + TangoConst.Tango_CmdArgTypeName[type] + " not supported for data format " +adfToString(adf) +".");
		
	}
	
	private static String adfToString(AttrDataFormat adf) {
		if (adf == AttrDataFormat.SCALAR) {
			return "scalar";
		} else if (adf == AttrDataFormat.SPECTRUM) {
			return "spectrum";
		} else if (adf == AttrDataFormat.IMAGE) {
			return "image";
		} else if (adf == AttrDataFormat.FMT_UNKNOWN) {
			return "fmtUnknown";
		}
		return null;
	}
	
	static Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String devName, String name) throws DevFailed {
		DeviceProxy proxy = new DeviceProxy(devName);
		AttributeInfo ai = proxy.get_attribute_info(name);
		AttrDataFormat adf = ai.data_format;
		int type = ai.data_type;
		if (adf == AttrDataFormat.SCALAR) {
			switch(type) {
    			case 3 : return LongPropertyImpl.class;
    			case 5 : return DoublePropertyImpl.class;
    			case 8 : return StringPropertyImpl.class;
    			case 23 : return LongPropertyImpl.class;
			}
		} else if (adf == AttrDataFormat.SPECTRUM) {
			switch(type) {
				case 3 : return LongSeqPropertyImpl.class;
				case 5 : return DoubleSeqPropertyImpl.class;
				case 8 : return StringSeqPropertyImpl.class;
				case 23 : return LongSeqPropertyImpl.class;
			}
		}
		throw new UnsupportedOperationException("Type " + TangoConst.Tango_CmdArgTypeName[type] + " not supported for data format " +adfToString(adf) +".");		
	}
		
	static <T extends Object> T cast(String value, Class<T> type) {
		Object retVal = null;
		try {
			if (type == String.class) retVal = value;
			else if (type == double.class || type == Double.class) retVal =  Double.parseDouble(value);
			else if (type == float.class || type == Float.class) retVal =  Float.parseFloat(value);
			else if (type == long.class || type == Long.class) retVal =  Long.parseLong(value);
			else if (type == int.class || type == Integer.class) retVal =  Integer.parseInt(value);
			else if (type == short.class || type == Short.class) retVal =  Short.parseShort(value);
			else if (type == byte.class || type == Byte.class) retVal =  Byte.parseByte(value);
			else if (type == char.class || type == Character.class) retVal =  value.charAt(0);
			else if (type == boolean.class || type == Boolean.class) retVal =  Boolean.parseBoolean(value);
			else retVal =  value;
			return type.cast(retVal);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
}
