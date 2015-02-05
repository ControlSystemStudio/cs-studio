/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.dal.tine;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.dal.AccessType;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DoubleSeqProperty;
import org.csstudio.dal.LongProperty;
import org.csstudio.dal.LongSeqProperty;
import org.csstudio.dal.NumericPropertyCharacteristics;
import org.csstudio.dal.PropertyCharacteristics;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.impl.DoublePropertyImpl;
import org.csstudio.dal.impl.DoubleSeqPropertyImpl;
import org.csstudio.dal.impl.LongPropertyImpl;
import org.csstudio.dal.impl.LongSeqPropertyImpl;
import org.csstudio.dal.impl.StringPropertyImpl;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.simple.impl.DataUtil;

import de.desy.tine.addrUtils.TSrvEntry;
import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TAccess;
import de.desy.tine.definitions.TArrayType;
import de.desy.tine.definitions.TFormat;
import de.desy.tine.queryUtils.TQuery;
import de.desy.tine.queryUtils.XPropertyQuery;
import de.desy.tine.structUtils.TTaggedStructure;
import de.desy.tine.types.ADDRESS;
import de.desy.tine.types.DBLDBL;
import de.desy.tine.types.DBLINT;
import de.desy.tine.types.FILTER;
import de.desy.tine.types.FLTFLT;
import de.desy.tine.types.FLTFLTINT;
import de.desy.tine.types.FLTINT;
import de.desy.tine.types.FLTINTINT;
import de.desy.tine.types.FWINDOW;
import de.desy.tine.types.INTFLTFLTFLT;
import de.desy.tine.types.INTFLTINT;
import de.desy.tine.types.INTINTFLTFLT;
import de.desy.tine.types.INTINTINTINT;
import de.desy.tine.types.LNGINT;
import de.desy.tine.types.NAME16;
import de.desy.tine.types.NAME16DBLDBL;
import de.desy.tine.types.NAME16FI;
import de.desy.tine.types.NAME16I;
import de.desy.tine.types.NAME16II;
import de.desy.tine.types.NAME32;
import de.desy.tine.types.NAME32DBLDBL;
import de.desy.tine.types.NAME32I;
import de.desy.tine.types.NAME48;
import de.desy.tine.types.NAME48I;
import de.desy.tine.types.NAME64;
import de.desy.tine.types.NAME64DBLDBL;
import de.desy.tine.types.NAME64I;
import de.desy.tine.types.NAME8;
import de.desy.tine.types.POINT;
import de.desy.tine.types.SPECTRUM;
import de.desy.tine.types.TDS;
import de.desy.tine.types.UNAME;
import de.desy.tine.types.USTRING;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class PropertyProxyUtilities {
	
	public static void main(String[] args) throws ConnectionFailed {
		String name = "TEST/DswTestValues/Device0/textValue";
		System.out.println(PropertyProxyUtilities.getCharacteristics(new PropertyNameDissector(name), null));
	}
	
	static Class<? extends PropertyProxy<?,?>> getProxyImplementationClass(Class<? extends SimpleProperty<?>> type) {
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
	
	static TDataType toTDataType(Object data, int length, boolean isBound)
	{
	    if (data == null) {
			return null;  // return null if Object is null !
		}
	    if (data instanceof double[])
	    {
	      return isBound ? new TDataType((double[])data, length) : new TDataType(((double[])data).length,TFormat.CF_DOUBLE);
	    }
	    if (data instanceof float[])
	    {
	      return isBound ? new TDataType((float[])data, length) : new TDataType(length,TFormat.CF_FLOAT);
	    }
	    if (data instanceof int[])
	    {
	      return isBound ? new TDataType((int[])data, length) : new TDataType(((int[])data).length,TFormat.CF_LONG);
	    }
	    if (data instanceof short[])
	    {
	      return isBound ? new TDataType((short[])data, length) : new TDataType(((short[])data).length,TFormat.CF_SHORT);
	    }
	    if (data instanceof byte[])
	    {
	      return isBound ? new TDataType((byte[])data, length) : new TDataType(((byte[])data).length,TFormat.CF_BYTE);
	    }
	    if (data instanceof DBLINT[])
	    {
	      return isBound ? new TDataType((DBLINT[])data, length) : new TDataType(((DBLINT[])data).length,TFormat.CF_DBLINT);
	    }
	    if (data instanceof DBLDBL[])
	    {
	      return isBound ? new TDataType((DBLDBL[])data, length) : new TDataType(((DBLDBL[])data).length,TFormat.CF_DBLDBL);
	    }
	    if (data instanceof FLTINT[])
	    {
	      return isBound ? new TDataType((FLTINT[])data, length) : new TDataType(((FLTINT[])data).length,TFormat.CF_FLTINT);
	    }
	    if (data instanceof FLTFLT[])
	    {
	      return isBound ? new TDataType((FLTFLT[])data, length) : new TDataType(((FLTFLT[])data).length,TFormat.CF_FLTFLT);
	    }
	    if (data instanceof POINT[])
	    {
	      return isBound ? new TDataType((POINT[])data, length) : new TDataType(((POINT[])data).length,TFormat.CF_POINT);
	    }
	    if (data instanceof LNGINT[])
	    {
	      return isBound ? new TDataType((LNGINT[])data, length) : new TDataType(((LNGINT[])data).length,TFormat.CF_LNGINT);
	    }
	    if (data instanceof NAME8[])
	    {
	      return isBound ? new TDataType((NAME8[])data, length) : new TDataType(((NAME8[])data).length,TFormat.CF_NAME8);
	    }
	    if (data instanceof NAME16[])
	    {
	      return isBound ? new TDataType((NAME16[])data, length) : new TDataType(((NAME16[])data).length,TFormat.CF_NAME16);
	    }
	    if (data instanceof NAME32[])
	    {
	      return isBound ? new TDataType((NAME32[])data, length) : new TDataType(((NAME32[])data).length,TFormat.CF_NAME32);
	    }
	    if (data instanceof NAME48[])
	    {
	      return isBound ? new TDataType((NAME48[])data, length) : new TDataType(((NAME48[])data).length,TFormat.CF_NAME48);
	    }
	    if (data instanceof NAME64[])
	    {
	      return isBound ? new TDataType((NAME64[])data, length) : new TDataType(((NAME64[])data).length,TFormat.CF_NAME64);
	    }
	    if (data instanceof NAME16I[])
	    {
	      return isBound ? new TDataType((NAME16I[])data, length) : new TDataType(((NAME16I[])data).length,TFormat.CF_NAME16I);
	    }
	    if (data instanceof NAME16FI[])
	    {
	      return isBound ? new TDataType((NAME16FI[])data, length) : new TDataType(((NAME16FI[])data).length,TFormat.CF_NAME16FI);
	    }
	    if (data instanceof NAME16II[])
	    {
	      return isBound ? new TDataType((NAME16II[])data, length) : new TDataType(((NAME16II[])data).length,TFormat.CF_NAME16II);
	    }
	    if (data instanceof NAME16DBLDBL[])
	    {
	      return isBound ? new TDataType((NAME16DBLDBL[])data, length) : new TDataType(((NAME16DBLDBL[])data).length,TFormat.CF_NAME16DBLDBL);
	    }
	    if (data instanceof NAME32DBLDBL[])
	    {
	      return isBound ? new TDataType((NAME32DBLDBL[])data, length) : new TDataType(((NAME32DBLDBL[])data).length,TFormat.CF_NAME32DBLDBL);
	    }
	    if (data instanceof NAME64DBLDBL[])
	    {
	      return isBound ? new TDataType((NAME64DBLDBL[])data, length) : new TDataType(((NAME64DBLDBL[])data).length,TFormat.CF_NAME64DBLDBL);
	    }
	    if (data instanceof NAME32I[])
	    {
	      return isBound ? new TDataType((NAME32I[])data, length) : new TDataType(((NAME32I[])data).length,TFormat.CF_NAME32I);
	    }
	    if (data instanceof NAME48I[])
	    {
	      return isBound ? new TDataType((NAME48I[])data, length) : new TDataType(((NAME48I[])data).length,TFormat.CF_NAME48I);
	    }
	    if (data instanceof NAME64I[])
	    {
	      return isBound ? new TDataType((NAME64I[])data, length) : new TDataType(((NAME64I[])data).length,TFormat.CF_NAME64I);
	    }
	    if (data instanceof TDS[])
	    {
	      return isBound ? new TDataType((TDS[])data, length) : new TDataType(((TDS[])data).length,TFormat.CF_TDS);
	    }
	    if (data instanceof INTFLTINT[])
	    {
	      return isBound ? new TDataType((INTFLTINT[])data, length) : new TDataType(((INTFLTINT[])data).length,TFormat.CF_INTFLTINT);
	    }
	    if (data instanceof FLTFLTINT[])
	    {
	      return isBound ? new TDataType((FLTFLTINT[])data, length) : new TDataType(((FLTFLTINT[])data).length,TFormat.CF_FLTFLTINT);
	    }
	    if (data instanceof FLTINTINT[])
	    {
	      return isBound ? new TDataType((FLTINTINT[])data, length) : new TDataType(((FLTINTINT[])data).length,TFormat.CF_FLTINTINT);
	    }
	    if (data instanceof USTRING[])
	    {
	      return isBound ? new TDataType((USTRING[])data, length) : new TDataType(((USTRING[])data).length,TFormat.CF_USTRING);
	    }
	    if (data instanceof UNAME[])
	    {
	      return isBound ? new TDataType((UNAME[])data, length) : new TDataType(((UNAME[])data).length,TFormat.CF_UNAME);
	    }
	    if (data instanceof ADDRESS[])
	    {
	      return isBound ? new TDataType((ADDRESS[])data, length) : new TDataType(((ADDRESS[])data).length,TFormat.CF_ADDRESS);
	    }
	    if (data instanceof INTINTINTINT[])
	    {
	      return isBound ? new TDataType((INTINTINTINT[])data, length) : new TDataType(((INTINTINTINT[])data).length,TFormat.CF_INTINTINTINT);
	    }
	    if (data instanceof FILTER[])
	    {
	      return isBound ? new TDataType((FILTER[])data, length) : new TDataType(((FILTER[])data).length,TFormat.CF_FILTER);
	    }
	    if (data instanceof INTFLTFLTFLT[])
	    {
	      return isBound ? new TDataType((INTFLTFLTFLT[])data, length) : new TDataType(((INTFLTFLTFLT[])data).length,TFormat.CF_INTFLTFLTFLT);
	    }
	    if (data instanceof FWINDOW[])
	    {
	      return isBound ? new TDataType((FWINDOW[])data, length) : new TDataType(((FWINDOW[])data).length,TFormat.CF_FWINDOW);
	    }
	    if (data instanceof INTINTFLTFLT[])
	    {
	      return isBound ? new TDataType((INTINTFLTFLT[])data, length) : new TDataType(((INTINTFLTFLT[])data).length,TFormat.CF_INTINTFLTFLT);
	    }
	    if (data instanceof SPECTRUM)
	    {
	      return isBound ? new TDataType((SPECTRUM)data) : new TDataType(((SPECTRUM)data).length,TFormat.CF_SPECTRUM);
	    }
	    if (data instanceof String)
	    {
	      return isBound ? new TDataType((String)data, length) : new TDataType(((String[])data).length,TFormat.CF_TEXT);
	    }
	    if (data instanceof StringBuffer)
	    {
	      return isBound ? new TDataType((StringBuffer)data, length) : new TDataType(((StringBuffer)data).length(),TFormat.CF_TEXT);
	    }
	    if (data instanceof char[])
	    {
	      return isBound ? new TDataType((char[])data, length) : new TDataType(((char[])data).length,TFormat.CF_TEXT);
	    }
	    if (data instanceof TTaggedStructure)
	    {
	      return isBound ? new TDataType((TTaggedStructure)data) : new TDataType(((TTaggedStructure)data).getSizeInBytes(),TFormat.CF_STRUCT);
	    }
	    if (data instanceof TTaggedStructure[])
	    {
	      return isBound ? new TDataType((TTaggedStructure[])data) : new TDataType(((TTaggedStructure[])data)[0].getSizeInBytes()*((TTaggedStructure[])data).length,TFormat.CF_STRUCT);
	    }
	    return null;
	}
	
	static int getObjectSize(Object data) {
	    if (data == null) {
			return 0;
		}
	    if (data instanceof char[]) {
			return ((char[])data).length;
		}
	    if (data instanceof TTaggedStructure[]) {
			return ((TTaggedStructure[])data).length;
		}
	    if (data instanceof TTaggedStructure) {
			return 1;
		}
	    return TDataType.getObjectSize(data);
	}
	
	static String makeDeviceName(PropertyNameDissector dissector) {
	    if (dissector.getDeviceContext() != null && dissector.getDeviceContext().length() > 0) {
	    	return new String("/" + dissector.getDeviceContext() + 
	                           "/" + dissector.getDeviceGroup() + 
	                           "/" + dissector.getDeviceName());
	    } else {
	    	return new String(dissector.getDeviceGroup() + "/" + dissector.getDeviceName());
	    }
	}
	
	static Map<String, Object> getCharacteristics(PropertyNameDissector dissector, Class numberType) throws ConnectionFailed {
        XPropertyQuery[] info = TQuery.getDevicePropertyInformationX(dissector.getDeviceContext(),
        		dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
        //is property a StockProperty
        if (info==null) {
        	info = TQuery.getStockPropertyInformationX(dissector.getDeviceContext(),
        			dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
        } 
        
        if (info==null) {
        	throw new ConnectionFailed(dissector.getRemoteName());
        }
        Map<String, Object> characteristics = new HashMap<String, Object>();
        characteristics.put(PropertyCharacteristics.C_DESCRIPTION, info[0].prpDescription);
        characteristics.put(PropertyCharacteristics.C_DISPLAY_NAME, dissector.getRemoteName());

        Object max;
        Object min;
        if (Long.class.equals(numberType)) {
        	max= new Long((long) info[0].prpMaxValue);
        	min= new Long((long) info[0].prpMinValue);
        } else {
        	max= new Double((double) info[0].prpMaxValue);
        	min= new Double((double) info[0].prpMinValue);
        }
        
        characteristics.put(NumericPropertyCharacteristics.C_MAXIMUM, max);
        characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MAX,max);
        characteristics.put(NumericPropertyCharacteristics.C_WARNING_MAX,max);
        characteristics.put(NumericPropertyCharacteristics.C_ALARM_MAX,max);

        characteristics.put(NumericPropertyCharacteristics.C_MINIMUM, (long)info[0].prpMinValue);
        characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MIN,min);
        characteristics.put(NumericPropertyCharacteristics.C_WARNING_MIN,min);
        characteristics.put(NumericPropertyCharacteristics.C_ALARM_MIN,min);

        try {
            TSrvEntry entry = new TSrvEntry(dissector.getDeviceGroup(), dissector.getDeviceContext());
            characteristics.put(PropertyCharacteristics.C_HOSTNAME,entry.getFecAddr().fecHost.getHostName());
        } catch (Exception e) {
        	characteristics.put(PropertyCharacteristics.C_HOSTNAME,"unknown");
        }
        
        TAccess access = TAccess.valueOf(info[0].prpAccess);
        characteristics.put(PropertyCharacteristics.C_ACCESS_TYPE,AccessType.getAccess(access.isRead(),access.isWrite()));
        TFormat format = TFormat.valueOf(info[0].prpFormat);
        characteristics.put(PropertyCharacteristics.C_DATATYPE,format.toString());
        characteristics.put("dataFormat", format);
        characteristics.put(NumericPropertyCharacteristics.C_UNITS, info[0].prpUnits);
        characteristics.put(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH,info[0].prpSize);
        characteristics.put("redirection",info[0].prpRedirection);
        characteristics.put("tag",info[0].prpTag);
        characteristics.put("rows",(int)info[0].numRows);
        characteristics.put("rowSize",(int)info[0].rowSize);
        characteristics.put("access",TAccess.valueOf(info[0].prpAccess));
        characteristics.put("editable",access.isWrite());
        characteristics.put("settable",access.isWrite());
        characteristics.put(PropertyCharacteristics.C_ACCESS_TYPE, AccessType.getAccess(access.isRead(),access.isWrite()));
        TArrayType arrayType = TArrayType.valueOf(info[0].prpArrayType);
       	if (arrayType.isChannel()) {
       		String[] names = TQuery.getDeviceNames(dissector.getDeviceContext(), dissector.getDeviceGroup(), dissector.getDeviceProperty());
       		if (names != null) {
	       		String[] newNames = new String[names.length];
	       		int j = -1;
	       		int startPoint = -1;
	       		for (int i = 0; i < 2*names.length; i++) {
	       			if (i%names.length == startPoint) {
						break;
					}
	       			if (names[i%names.length].equals(dissector.getDeviceName())) {
	       				j = 0;
	       				startPoint = i;
	       			}
	       			if (j >= 0) {
	       				newNames[j] = names[i%names.length];
	       				j++;
	       			}
	       		}
	       		characteristics.put("xLabels",newNames);
       		}
       	}
       	//fall fallback for names
       	if (characteristics.get("xLabels") == null) {
       		String[] data = new String[(Integer)characteristics.get("sequenceLength")];
			for (int i = 0; i < data.length; i++) {
				data[i] = String.valueOf(i);
			}
			characteristics.put("xLabels", data);
       	}
       	characteristics.put("arrayType",arrayType);
       	
       	// mandatory characteristics with bogus data.
       	characteristics.put(CharacteristicInfo.C_POSITION.getName(),arrayType);
       	characteristics.put(CharacteristicInfo.C_PROPERTY_TYPE.getName(),dissector.getDeviceProperty());
       	characteristics.put(CharacteristicInfo.C_RESOLUTION.getName(),0xFFFF);
        if (Long.class.equals(numberType)) {
           	characteristics.put(CharacteristicInfo.C_FORMAT.getName(),"%d");
        } else {
           	characteristics.put(CharacteristicInfo.C_FORMAT.getName(),"%5.3f");
        }
       	characteristics.put(CharacteristicInfo.C_SCALE_TYPE.getName(),"linear");
    	characteristics.put(CharacteristicInfo.C_META_DATA.getName(),DataUtil.createMetaData(characteristics));
       	
        return characteristics;
	}
		
	static long toUTC(double stamp) {
    	return (long)(stamp*1E3);
    }	
	
//	static Class<? extends SimpleProperty> getPropertyImplementationClass(String name) {
//		PropertyNameDissector dissector = new PropertyNameDissector(name);
//		XPropertyQuery[] info = TQuery.getDevicePropertyInformationX(dissector.getDeviceContext(),
//	        		dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
//        //is property a StockProperty
//        if (info==null) {
//        	info = TQuery.getStockPropertyInformationX(dissector.getDeviceContext(),
//        			dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
//        } 
//        
//        if (info==null) {
//        	return null;
//        }
//        int length = info[0].prpSize;
//        TFormat format = TFormat.valueOf(info[0].prpFormat);
//        switch(format.getValue()) {
//	        case TFormat.CF_DOUBLE : {
//	        	if (length > 1)
//	        		return DoubleSeqPropertyImpl.class;
//	        	else
//	        		return DoublePropertyImpl.class;
//	        }
//			case TFormat.CF_SHORT : {
//				if (length > 1)
//	        		return LongSeqPropertyImpl.class;
//	        	else
//	        		return LongPropertyImpl.class;
//			}
//			case TFormat.CF_BYTE : {
//				if (length > 1)
//	        		return LongSeqPropertyImpl.class;
//	        	else
//	        		return LongPropertyImpl.class;
//			}
//			case TFormat.CF_LONG: {
//				if (length > 1)
//	        		return LongSeqPropertyImpl.class;
//	        	else
//	        		return LongPropertyImpl.class;
//			}
//			case TFormat.CF_FLOAT : {
//				if (length > 1)
//	        		return DoubleSeqPropertyImpl.class;
//	        	else
//	        		return DoublePropertyImpl.class;
//			}
//			case TFormat.CF_SPECTRUM : {
//				return DoubleSeqPropertyImpl.class;
//			}
//			case TFormat.CF_NAME8 : {
//				return StringPropertyImpl.class;
//			}
//			case TFormat.CF_NAME16 : {
//				return StringPropertyImpl.class;
//			}
//			case TFormat.CF_NAME32 : {
//				return StringPropertyImpl.class;
//			}
//			case TFormat.CF_NAME48 : {
//				return StringPropertyImpl.class;
//			}
//			case TFormat.CF_NAME64 : {
//				return StringPropertyImpl.class;
//			}
//			default: return null;
//        }
//        
//    }
//	
//	static Class<? extends PropertyProxy> getPropertyProxyImplementationClass(String name) {
//		PropertyNameDissector dissector = new PropertyNameDissector(name);
//		XPropertyQuery[] info = TQuery.getDevicePropertyInformationX(dissector.getDeviceContext(),
//	        		dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
//        //is property a StockProperty
//        if (info==null) {
//        	info = TQuery.getStockPropertyInformationX(dissector.getDeviceContext(),
//        			dissector.getDeviceGroup(), dissector.getDeviceName(), dissector.getDeviceProperty());
//        } 
//        
//        if (info==null) {
//        	return null;
//        }
//        int length = info[0].prpSize;
//        TFormat format = TFormat.valueOf(info[0].prpFormat);
//        switch(format.getValue()) {
//	        case TFormat.CF_DOUBLE : {
//	        	if (length > 1)
//	        		return DoubleSeqPropertyProxyImpl.class;
//	        	else
//	        		return DoublePropertyProxyImpl.class;
//	        }
//			case TFormat.CF_SHORT : {
//				if (length > 1)
//	        		return LongSeqPropertyProxyImpl.class;
//	        	else
//	        		return LongPropertyProxyImpl.class;
//			}
//			case TFormat.CF_BYTE : {
//				if (length > 1)
//	        		return LongSeqPropertyProxyImpl.class;
//	        	else
//	        		return LongPropertyProxyImpl.class;
//			}
//			case TFormat.CF_LONG: {
//				if (length > 1)
//	        		return LongSeqPropertyProxyImpl.class;
//	        	else
//	        		return LongPropertyProxyImpl.class;
//			}
//			case TFormat.CF_FLOAT : {
//				if (length > 1)
//	        		return DoubleSeqPropertyProxyImpl.class;
//	        	else
//	        		return DoublePropertyProxyImpl.class;
//			}
//			case TFormat.CF_SPECTRUM : {
//				return DoubleSeqPropertyProxyImpl.class;
//			}
//			case TFormat.CF_NAME8 : {
//				return StringPropertyProxyImpl.class;
//			}
//			case TFormat.CF_NAME16 : {
//				return StringPropertyProxyImpl.class;
//			}
//			case TFormat.CF_NAME32 : {
//				return StringPropertyProxyImpl.class;
//			}
//			case TFormat.CF_NAME48 : {
//				return StringPropertyProxyImpl.class;
//			}
//			case TFormat.CF_NAME64 : {
//				return StringPropertyProxyImpl.class;
//			}
//			default: return null;
//        }
//        
//    }
	
	static Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String name) {
		if (!TINEPlug.getInstance().containsName(name)) {
			try {
				TINEPlug.getInstance().getCharacteristics(name);
			} catch (ConnectionFailed e) {
				Logger.getLogger(PropertyProxyUtilities.class).error("Characteristics failed.", e);
				return null;
			}
		}
		TFormat format = TINEPlug.getInstance().getTFormat(name);
		int sequenceLength = TINEPlug.getInstance().getSequenceLength(name);
		switch(format.getValue()) {
			case TFormat.CF_DOUBLE : {
	        	if (sequenceLength > 1) {
					return DoubleSeqPropertyImpl.class;
				} else {
					return DoublePropertyImpl.class;
				}
	        }
			case TFormat.CF_SHORT : {
				if (sequenceLength > 1) {
					return LongSeqPropertyImpl.class;
				} else {
					return LongPropertyImpl.class;
				}
			}
			case TFormat.CF_BYTE : {
				if (sequenceLength > 1) {
					return LongSeqPropertyImpl.class;
				} else {
					return LongPropertyImpl.class;
				}
			}
			case TFormat.CF_LONG: {
				if (sequenceLength > 1) {
					return LongSeqPropertyImpl.class;
				} else {
					return LongPropertyImpl.class;
				}
			}
			case TFormat.CF_FLOAT : {
				if (sequenceLength > 1) {
					return DoubleSeqPropertyImpl.class;
				} else {
					return DoublePropertyImpl.class;
				}
			}
			case TFormat.CF_SPECTRUM : {
				return DoubleSeqPropertyImpl.class;
			}
			case TFormat.CF_NAME8 : {
				return StringPropertyImpl.class;
			}
			case TFormat.CF_NAME16 : {
				return StringPropertyImpl.class;
			}
			case TFormat.CF_NAME32 : {
				return StringPropertyImpl.class;
			}
			case TFormat.CF_NAME48 : {
				return StringPropertyImpl.class;
			}
			case TFormat.CF_NAME64 : {
				return StringPropertyImpl.class;
			}
			case TFormat.CF_TEXT : {
				return StringPropertyImpl.class;
			}
			default: return null;
		}
		
	}
	
	static Class<? extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(String name) {
		return getPropertyProxyImplementationClass(name, null);
	}
	
	static Class<? extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(String name, Class<? extends SimpleProperty<?>> propertyType) {
		if (!TINEPlug.getInstance().containsName(name)) {
			try {
				TINEPlug.getInstance().getCharacteristics(name);
			} catch (ConnectionFailed e) {
				Logger.getLogger(PropertyProxyUtilities.class).error("Characteristics failed.", e);
				return null;
			}
		}
		TFormat format = TINEPlug.getInstance().getTFormat(name);
		int sequenceLength = TINEPlug.getInstance().getSequenceLength(name);
		if (sequenceLength > 1 && !propertyType.getName().contains("Seq")) {
			sequenceLength = 1;
		}
		switch(format.getValue()) {
	        case TFormat.CF_DOUBLE : {
	        	if (sequenceLength > 1) {
					return DoubleSeqPropertyProxyImpl.class;
				} else {
					return DoublePropertyProxyImpl.class;
				}
	        }
			case TFormat.CF_SHORT : {
				if (sequenceLength > 1) {
					return LongSeqPropertyProxyImpl.class;
				} else {
					return LongPropertyProxyImpl.class;
				}
			}
			case TFormat.CF_BYTE : {
				if (sequenceLength > 1) {
					return LongSeqPropertyProxyImpl.class;
				} else {
					return LongPropertyProxyImpl.class;
				}
			}
			case TFormat.CF_LONG: {
				if (sequenceLength > 1) {
					return LongSeqPropertyProxyImpl.class;
				} else {
					return LongPropertyProxyImpl.class;
				}
			}
			case TFormat.CF_FLOAT : {
				if (sequenceLength > 1) {
					return DoubleSeqPropertyProxyImpl.class;
				} else {
					return DoublePropertyProxyImpl.class;
				}
			}
			case TFormat.CF_SPECTRUM : {
				return DoubleSeqPropertyProxyImpl.class;
			}
			case TFormat.CF_NAME8 : {
				return StringPropertyProxyImpl.class;
			}
			case TFormat.CF_NAME16 : {
				return StringPropertyProxyImpl.class;
			}
			case TFormat.CF_NAME32 : {
				return StringPropertyProxyImpl.class;
			}
			case TFormat.CF_NAME48 : {
				return StringPropertyProxyImpl.class;
			}
			case TFormat.CF_NAME64 : {
				return StringPropertyProxyImpl.class;
			}
			case TFormat.CF_TEXT : {
				return StringPropertyProxyImpl.class;
			}
			default: return null;
		}
	}
}


/* __oOo__ */

