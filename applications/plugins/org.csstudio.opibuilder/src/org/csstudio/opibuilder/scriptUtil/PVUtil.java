/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.pvmanager.BOYPVFactory;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;

/**The utility class to facilitate Javascript programming
 * for PV operation.
 * @author Xihui Chen
 *
 */
public class PVUtil{
	
	/**Create a PV and start it. PVListener can be added to the PV to monitor its
	 * value change, but please note that the listener is executed in non-UI thread.
	 * If the code need be executed in UI thread, please use {@link ScriptUtil#execInUI(Runnable, AbstractBaseEditPart)}.
	 * The monitor's maximum update rate is 50hz. If the PV updates faster than this rate, some updates
	 * will be discarded.  
	 * <br>Example Jython script:
	 * 
	 *  <pre>
	from org.csstudio.opibuilder.scriptUtil import PVUtil
	from org.csstudio.utility.pv import PVListener
		
	class MyPVListener(PVListener):
		def pvValueUpdate(self, pv):
			widget.setPropertyValue("text", PVUtil.getString(pv))
		
	pv = PVUtil.createPV("sim://noise", widget)
	pv.addListener(MyPVListener())  
	 *  </pre>
	 * 
	 * @param name name of the PV.
	 * @param widget the reference widget. The PV will stop when the widget is deactivated,
	 * so it is not needed to stop the pv in script.
	 * @return the PV.
	 * @throws Exception the exception that might happen while creating the pv.
	 */
	public final static PV createPV(String name, AbstractBaseEditPart widget) throws Exception{
		
		final PV pv = BOYPVFactory.createPV(name, false, 2);
		pv.start();
		widget.addEditPartListener(new EditPartListener.Stub(){
			
			@Override
			public void partDeactivated(EditPart arg0) {
				pv.stop();
			}	
			
		});
		return pv;		
	}

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
		checkPVValue(pv);
		return ValueUtil.getDouble(pv.getValue());
	}

	protected static void checkPVValue(PV pv) {
		if(pv.getValue() == null)
			throw new RuntimeException("PV " + pv.getName() + " has no value.");
	}

	 /** Try to get a long integer number from the PV.
     *  <p>
     *  Some applications only deal with numeric data,
     *  so they want to interprete integer, enum and double values
     *  all the same.
     *  @param pv the PV.
     *  @return A long integer.
     */
	public final static Long getLong(PV pv){
		checkPVValue(pv);
		return (long) ValueUtil.getDouble(pv.getValue());
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
		checkPVValue(pv);
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
		checkPVValue(pv);
		return ValueUtil.getDoubleArray(pv.getValue());
	}

	 /** Try to get an integer-typed array from the pv.
     *  @param pv the pv.
     *  @see #getSize(PV)
     *  @see #getLong(PV)
     *  @return A long integer array, or an empty long integer array in case the value type
     *          does not decode into a number, or if the value's severity
     *          indicates that there happens to be no useful value.
     */
	public final static long[] getLongArray(PV pv){
		checkPVValue(pv);
		double[] dblArray = ValueUtil.getDoubleArray(pv.getValue());
		long[] longArray = new long[dblArray.length];
		int i=0;
		for(double d : dblArray){
			longArray[i++] = (long) d;
		}
		return longArray;
	}

    /**Get the size of the pv's value
     * @param pv the pv.
     * @return Array length of the pv value. <code>1</code> for scalars. */
	public final static double getSize(PV pv){
		checkPVValue(pv);
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
		checkPVValue(pv);
		return ValueUtil.getString(pv.getValue());
	}

	/**Get the full info from the pv in this format
	 * <pre>timestamp value severity, status</pre>
	 * @param pv
	 * @return the full info string
	 */
	public final static String getFullString(PV pv){
		checkPVValue(pv);
		return pv.getValue().toString();
	}


	/**Get the timestamp string of the pv
	 * @param pv the pv
	 * @return the timestamp in string.
	 */
	public final static String getTimeString(PV pv){
		checkPVValue(pv);
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
		checkPVValue(pv);
		ITimestamp timestamp = pv.getValue().getTime();
		double result = timestamp.seconds()*1000 + timestamp.nanoseconds()/1000000;
		return result;
	}


	/**Get severity of the pv as an integer value.
	 * @param pv the PV.
	 * @return 0:OK; -1: Invalid; 1: Major; 2:Minor.
	 */
	public final static int getSeverity(PV pv){
		checkPVValue(pv);
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
	
	/**Get severity of the PV as a string.
	 * @param pv the PV.
	 * @return The string representation of the severity.
	 */
	public final static String getSeverityString(PV pv){
		checkPVValue(pv);
		return pv.getValue().getSeverity().toString();
	}
	
	/**Get the status text that might describe the severity.
	 * @param pv the PV.
	 * @return the status string.
	 */
	public final static String getStatus(PV pv){
		checkPVValue(pv);
		return pv.getValue().getStatus();
	}




}
