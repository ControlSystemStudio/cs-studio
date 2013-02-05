package org.csstudio.opibuilder.pvmanager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.datadefinition.DataType;
import org.csstudio.opibuilder.datadefinition.FormatEnum;
import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.vtype.Array;
import org.epics.vtype.Display;
import org.epics.vtype.Scalar;
import org.epics.vtype.VByte;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VShort;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueUtil;

/**
 * A helper for PVManager related operations which are not 
 * provided by {@link ValueUtil}
 * @author Xihui Chen, Kay Kasemir
 *
 */
public class PVManagerHelper{	
	
	public static final int DEFAULT_PRECISION = 4;//$NON-NLS-1$
	public static final String HEX_PREFIX = "0x"; //$NON-NLS-1$
	/**
	 * The max count of values to be formatted into string.
	 * The value beyond this count will be omitted.
	 */
	public final static int MAX_FORMAT_VALUE_COUNT = 10;
	final public static String ARRAY_ELEMENT_SEPARATOR = ", "; //$NON-NLS-1$
	
	private static PVManagerHelper instance;
	private Map<Integer,NumberFormat> formatCacheMap;
	
	private PVManagerHelper(){
		formatCacheMap = new HashMap<Integer, NumberFormat>();
	}
	
	public synchronized static PVManagerHelper getInstance(){
		if(instance == null)
			instance = new PVManagerHelper();
		return instance;
		
	}
	
	public static DataType getDataType(Object obj){
		Class<?> typeClass = ValueUtil.typeOf(obj);
		if(typeClass == VByte.class)
			return DataType.BYTE;
		if(typeClass == VEnum.class)
			return DataType.ENUM;
		if(typeClass == VDouble.class)
			return DataType.DOUBLE;
		if(typeClass == VFloat.class)
			return DataType.FLOAT;
		if(typeClass == VInt.class)
			return DataType.INT;
		if(typeClass == VShort.class)
			return DataType.SHORT;
		if(typeClass == VString.class)
			return DataType.STRING;
		
		if(typeClass == VByteArray.class)
			return DataType.BYTE_ARRAY;		
		if(typeClass == VDoubleArray.class)
			return DataType.DOUBLE_ARRAY;
		if(typeClass == VEnumArray.class)
			return DataType.ENUM_ARRAY;
		if(typeClass == VFloatArray.class)
			return DataType.FLOAT_ARRAY;
		if(typeClass == VIntArray.class)
			return DataType.INT_ARRAY;
		if(typeClass == VShortArray.class)
			return DataType.SHORT_ARRAY;
		if(typeClass == VStringArray.class)
			return DataType.STRING_ARRAY;	
		
		return DataType.UNKNOWN;
	}
	
	/**Get size of the PVManager object value.
	 * @param obj
	 * @return 1 for scalar. Otherwise return size of the array.
	 */
	public static int getSize(Object obj){
		if (obj instanceof Scalar) {
            return 1;
        }

        if (obj instanceof Array) {
            return ((Array) obj).getSizes().getInt(0);
        }
        return 1;
	}
	
	/**Get double array from a PVManager object value.
	 *  @param obj an object implementing a standard type
	 * @return
	 */
	public static double[] getDoubleArray(Object obj){
		if (obj instanceof Scalar) {
            Object v = ((Scalar) obj).getValue();
            if (v instanceof Number)
                return new double[]{((Number) v).doubleValue()};
        }
		if(obj instanceof Array){
			Object array = ((Array) obj).getData();
			if(array instanceof ListNumber)
				return ListNumberToDoubleArray((ListNumber) array);
			if(obj instanceof VEnumArray){
				ListInt tArray = ((VEnumArray) obj).getIndexes();
				return ListNumberToDoubleArray(tArray);           
			}				
		}		
		
        return new double[0];
	}
	
	private static double[] ListNumberToDoubleArray(ListNumber listNumber){
		Object wrappedArray = CollectionNumbers.wrappedArray(listNumber);
		if(wrappedArray instanceof double[])
			return (double[])wrappedArray;
			
		final double[] result = new double[listNumber.size()];
		 for(int i=0; i<result.length; i++){
        	result[i] = listNumber.getDouble(i);
        }
        return result;		
	}
	
	
	/**Format a PVManager PV value to string.
	 * @param formatEnum the format
	 * @param pmValue PVManager value, such as VDouble, VEnum, V....
	 * @param precision decimal precision. If it is -1, it will use the precision from
	 * PV.
	 * @return
	 */
	public String formatValue(FormatEnum formatEnum, Object pmValue, int precision) {
		if (pmValue instanceof Scalar) {
			Object value = ((Scalar)pmValue).getValue();
			if (value instanceof Number) {
				return formatScalarNumber(formatEnum, pmValue, (Number)value, precision);
			}else if(value instanceof String){
				if(pmValue instanceof VEnum)
					return formatScalarEnum(formatEnum, (VEnum)pmValue);
				return (String) value;
			}			
		}else if(pmValue instanceof Array){
			
			if(pmValue instanceof VNumberArray)
				return formatNumberArray(formatEnum, (VNumberArray)pmValue, precision);
			else {
				Object array = ((Array) pmValue).getData();
				if (array instanceof List){
					return formatObjectArray(((List<?>)array).toArray());
				}				
			}
		}
		if(pmValue != null)
			return pmValue.toString();
		return "no value"; //$NON-NLS-1$
	}

	public static boolean isPrimaryNumberArray(Object array) {
		return array instanceof byte[] || array instanceof int[] 
				|| array instanceof long[]
				|| array instanceof double[] 
				|| array instanceof float[] 
				|| array instanceof short[] 
				|| array instanceof char[];
	}

	private String formatObjectArray(Object[] array) {
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
		
	
	private String formatNumberArray(FormatEnum formatEnum, VNumberArray pmArray, int precision){
		ListNumber data = ((VNumberArray)pmArray).getData();
		StringBuilder sb = new StringBuilder(data.size());
		if(formatEnum == FormatEnum.STRING){			
			for(int i=0; i<data.size(); i++){
				final char c = getDisplayChar((char) data.getInt(i));
				if (c == 0)
					break;
				sb.append(c);
			}
			return sb.toString();			
		}else{		
		   sb.append(formatScalarNumber(formatEnum, data.getDouble(0), precision));
           for(int i=1; i<data.size(); i++){        	   	
        	   	sb.append(ARRAY_ELEMENT_SEPARATOR);
        	   	sb.append(formatScalarNumber(formatEnum, data.getDouble(i), precision));
				if (i >= MAX_FORMAT_VALUE_COUNT)
				{
					sb.append(ARRAY_ELEMENT_SEPARATOR);
					sb.append("..."); //$NON-NLS-1$
					sb.append(formatScalarNumber(formatEnum, 
							data.getDouble(data.size()-1), precision));
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
	
	private String formatScalarEnum(FormatEnum formatEnum, VEnum enumValue){
		switch (formatEnum) {
		case DECIMAL:
		case EXP:
		case COMPACT:
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
	private String formatScalarNumber(FormatEnum formatEnum, Number numValue,
			int precision){
		return formatScalarNumber(formatEnum, null, numValue, precision);
	}
	
	/**
	 * @param formatEnum
	 * @param pmValue The PVManager V Value. It must be a scalar with number value.
	 * @param numValue the number value. not necessary if pmValue is given.
	 * @param precision
	 * @return
	 */
	private String formatScalarNumber(FormatEnum formatEnum, Object pmValue, Number numValue,
			int precision){
		if(pmValue !=null)
			numValue = (Number) ((Scalar)pmValue).getValue();
		switch (formatEnum) {
		case DECIMAL:
		case DEFAULT:
		default:
			if (precision == -1 && pmValue !=null
				&& pmValue instanceof Display
				&& ((Display) pmValue).getFormat() != null) {
				return ((Display) pmValue).getFormat().format(
						((Number) numValue).doubleValue());
			} else {
				if(precision == -1)
					return formatScalarNumber(FormatEnum.COMPACT, numValue, precision);
				else{
					NumberFormat numberFormat = formatCacheMap.get(precision);
					if(numberFormat == null){
						numberFormat = new DecimalFormat("0"); //$NON-NLS-1$
						numberFormat.setMinimumFractionDigits(0);
						numberFormat.setMaximumFractionDigits(precision);
						formatCacheMap.put(precision, numberFormat);
					}
					return numberFormat.format(numValue.doubleValue());
				}
			}			

		case COMPACT:
			double dValue = numValue.doubleValue();
			if ( ((dValue > 0.0001) && (dValue < 10000))||
					((dValue < -0.0001) && (dValue > -10000)) ||
					dValue == 0.0){
				return formatScalarNumber(FormatEnum.DECIMAL, numValue, precision == -1? 4 : precision);
			}else{
				return formatScalarNumber(FormatEnum.EXP, numValue, precision == -1? 4 : precision);					
			}
		case EXP:
			if (precision == -1 && numValue instanceof Display) {
				precision = ((Display) numValue).getFormat()
						.getMinimumFractionDigits();
			}
			NumberFormat numberFormat;
			if (precision == -1)
				precision = DEFAULT_PRECISION;

			// Assert positive precision
			precision = Math.abs(precision);
			// Exponential notation itentified as 'negative' precision in cached
			numberFormat = formatCacheMap.get(-precision);
			if (numberFormat == null) {
				final StringBuffer pattern = new StringBuffer(10);
				pattern.append("0."); //$NON-NLS-1$
				for (int i = 0; i < precision; ++i)
					pattern.append('#'); //$NON-NLS-1$
				pattern.append("E0"); //$NON-NLS-1$
				numberFormat = new DecimalFormat(pattern.toString());
				formatCacheMap.put(-precision, numberFormat);
			}
			return numberFormat.format(numValue.doubleValue());
		case HEX:
			return HEX_PREFIX + Integer.toHexString(numValue.intValue()).toUpperCase();
		case HEX64:
			return HEX_PREFIX + Long.toHexString(numValue.longValue());
		case STRING:
			return new String(new char[]{getDisplayChar((char)numValue.intValue())});
		}		
	}
	
	/** Convert char into printable character for Format.String
	 *  @param c Char, 0 for end-of-string
	 *  @return Printable version
	 */
	private char getDisplayChar(final char c)
	{
		if (c == 0) {
            return 0;
        }
		if (Character.getType(c) != Character.CONTROL) {
            return c;
        }
		return '?';
	}

}
