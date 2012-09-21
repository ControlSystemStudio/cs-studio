package org.csstudio.opibuilder.pvmanager;

import org.epics.pvmanager.data.Array;
import org.epics.pvmanager.data.Scalar;
import org.epics.pvmanager.data.ValueUtil;

/**
 * A helper for PVManager related operations which are not 
 * provided by {@link ValueUtil}
 * @author Xihui Chen
 *
 */
public class PVManagerHelper{
	
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
            if (array instanceof int[]) {
                int[] tArray = (int[]) array;
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

}
