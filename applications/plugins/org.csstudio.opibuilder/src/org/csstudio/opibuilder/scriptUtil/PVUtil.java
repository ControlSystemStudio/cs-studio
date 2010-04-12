package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;

/**The utility class to facilitate Javascript programming
 * for PV operation. 
 * @author Xihui Chen
 *
 */
public class PVUtil{
	
	 /** Try to get a double number from the PV.
     *  <p>
     *  Some applications only deal with numeric data,
     *  so they want to interprete integer, enum and double values
     *  all the same.
     *  @param pv the PV.
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
	public final static double getDouble(PV pv){
		return ValueUtil.getDouble(pv.getValue());
	}
	
	
	
	  /** Try to get a double-typed array element from the Value.
     *  @param pv The PV.
     *  @param index The array index, 0 ... getSize()-1.
     *  @see #getSize(PV)
     *  @see #getDouble(PV)
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
	public final static double getDouble(PV pv, int index){
		return ValueUtil.getDouble(pv.getValue(), index);
	}
	
	
	 /** Try to get a double-typed array from the pv.
     *  @param pv the pv.
     *  @see #getSize(PV)
     *  @see #getDouble(PV)
     *  @return A double array, or an empty double array in case the value type
     *          does not decode into a number, or if the value's severity
     *          indicates that there happens to be no useful value.
     */
	public final static double[] getDoubleArray(PV pv){
		return ValueUtil.getDoubleArray(pv.getValue());
	}
	
    /**Get the size of the pv's value
     * @param pv the pv. 
     * @return Array length of the pv value. <code>1</code> for scalars. */
	public final static double getSize(PV pv){
		return ValueUtil.getSize(pv.getValue());
	}
	
	
	  /**
	 * Converts the given pv's value into a string representation. For string values,
	 * returns the value. For numeric (double and long) values, returns a
	 * non-localized string representation. Double values use a point as the
	 * decimal separator. For other types of values, the value's
	 * {@link IValue#format()} method is called and its result returned.
	 * 
	 * @param pv
	 *            the pv.
	 * @return a string representation of the value.
	 */
	public final static String getString(PV pv){
		return ValueUtil.getString(pv.getValue());
	}
	
	/**Get the full info from the pv in this format
	 * <pre>timestamp value severity, status</pre>
	 * @param pv
	 * @return the full info string 
	 */
	public final static String getFullString(PV pv){
		return pv.getValue().toString();
	}
	
	
	/**Get the timestamp string of the pv
	 * @param pv the pv
	 * @return the timestamp in string.
	 */
	public final static String getTimeString(PV pv){
		return pv.getValue().getTime().toString();
	}
	
	 /** Get milliseconds since epoch, i.e. 1 January 1970 0:00 UTC.
     *  <p>
     *  Note that we always return milliseconds relative to this UTC epoch,
     *  even if the original control system data source might use a different
     *  epoch (example: EPICS uses 1990), because the 1970 epoch is most
     *  compatible with existing programming environments.
     *  @param pv the pv
     *  @return milliseconds since 1970.
     */
	public final static double getTimeInMilliseconds(PV pv){
		ITimestamp timestamp = pv.getValue().getTime();
		double result = timestamp.seconds()*1000 + timestamp.nanoseconds()/1000000;
		return result;
	}
	
	
	/**The severity of the pv.
	 * @param pv
	 * @return 0:OK; -1: Invalid; 1: Major; 2:Minor.
	 */
	public final static int getSeverity(PV pv){
		ISeverity severity = pv.getValue().getSeverity();
		if(severity.isInvalid())
			return -1;
		if(severity.isMajor())
			return 1;
		if(severity.isMinor())
			return 2;
		if (severity.isOK()) {
			return 0;
		}
		return -1;
	}
	
	
	
	
}
