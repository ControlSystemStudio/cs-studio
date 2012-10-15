package org.csstudio.opibuilder.pvmanager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.datadefinition.FormatEnum;
import org.epics.pvmanager.data.Array;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Scalar;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.ValueUtil;

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
	
	/**Get size of the PVManager object value.
	 * @param obj
	 * @return 1 for scalar. Otherwise return size of the array.
	 */
	public static int getSize(Object obj){
		if (obj instanceof Scalar) {
            return 1;
        }

        if (obj instanceof Array) {
            Object array = ((Array<?>) obj).getArray();
            if (array instanceof byte[]) {
                return ((byte[]) array).length;
            }
            if (array instanceof short[]) {
            	return ((short[]) array).length;
            }
            if (array instanceof int[]) {
            	return ((int[]) array).length;
            }
            if (array instanceof float[]) {
            	return ((float[]) array).length;
            }
            if (array instanceof double[]) {
            	return ((double[]) array).length;
            }
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

        if (obj instanceof Array) {
            Object array = ((Array<?>) obj).getArray();
            if (array instanceof byte[]) {
                byte[] tArray = (byte[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
            if (array instanceof short[]) {
                short[] tArray = (short[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
            if (array instanceof char[]) {
            	char[] tArray = (char[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
            if (array instanceof int[]) {
                int[] tArray = (int[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
            if (array instanceof long[]) {
            	long[] tArray = (long[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }            
            if (array instanceof float[]) {
                float[] tArray = (float[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
            if (array instanceof double[]) {
                double[] tArray = (double[]) array;
                final double[] result = new double[tArray.length];
                for(int i=0; i<tArray.length; i++){
                	result[i] = tArray[i];
                }
                return result;
            }
        }
        return new double[0];
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
			Object array = ((Array<?>) pmValue).getArray();
			if(isPrimaryNumberArray(array))
				return formatNumberArray(formatEnum, (Array<?>)pmValue, precision);
			else if (array instanceof Object[]){
				return formatObjectArray((Object[]) array);
			}
		}
		return pmValue.toString();
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
		for (int i = 0; i < array.length; i++) {
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
	
	
	
	private String formatNumberArray(FormatEnum formatEnum, Array<?> pmArray, int precision){
		double[] doubleArray = getDoubleArray(pmArray);
		StringBuilder sb = new StringBuilder(doubleArray.length);
		if(formatEnum == FormatEnum.STRING){			
			for(int i=0; i<doubleArray.length; i++){
				final char c = getDisplayChar((char) doubleArray[i]);
				if (c == 0)
					break;
				sb.append(c);
			}
			return sb.toString();			
		}else{		
		   sb.append(formatScalarNumber(formatEnum, doubleArray[0], precision));
           for(int i=0; i<doubleArray.length; i++){        	   	
        	   	sb.append(ARRAY_ELEMENT_SEPARATOR);
        	   	sb.append(formatScalarNumber(formatEnum, doubleArray[i], precision));
				if (i >= MAX_FORMAT_VALUE_COUNT)
				{
					sb.append(ARRAY_ELEMENT_SEPARATOR);
					sb.append("..."); //$NON-NLS-1$
					sb.append(formatScalarNumber(formatEnum, 
							doubleArray[doubleArray.length-1], precision));
					sb.append(" "); //$NON-NLS-1$
					sb.append("["); //$NON-NLS-1$
					sb.append(doubleArray.length);
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
					return Double.toString(numValue.doubleValue());
				else{
					NumberFormat numberFormat = formatCacheMap.get(precision);
					if(numberFormat == null){
						numberFormat = NumberFormat.getInstance();
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
				return formatScalarNumber(FormatEnum.DECIMAL, numValue, precision);
			}else{
				return formatScalarNumber(FormatEnum.EXP, numValue, precision);					
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
