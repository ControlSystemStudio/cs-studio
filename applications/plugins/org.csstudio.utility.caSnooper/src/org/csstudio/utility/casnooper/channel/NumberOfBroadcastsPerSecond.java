/*
 * Copyright (c) 2004 by Cosylab
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file "LICENSE-CAJ". If the license is not included visit Cosylab web site,
 * <http://www.cosylab.com>.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.csstudio.utility.casnooper.channel;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DOUBLE;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import org.csstudio.utility.casnooper.SnooperServer;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable;

/**
 * Example implementation of process variable - counter.
 * Counter starts counting at <code>startValue</code> incrementing by <code>incrementValue</code> every <code>periodInMS</code> milliseconds.
 * When counter value riches <code>envValue</code> counter is reset to <code>startValue</code>.
 * Implementation also triggers alarms (seting status and severity) regarding to set warning and alarm limits. 
 * @author msekoranja
 */
public class NumberOfBroadcastsPerSecond extends FloatingDecimalProcessVariable {

	/**
	 * Counter start value.
	 */
	protected int startValue;
	
	/**
	 * Counter end (stop) value.
	 */
	protected int endValue;
	
	/**
	 * Precision of decimal point.
	 */
	protected short precision;
	
	/**
	 * Lower warning value.
	 */
	protected Number lowerWarningValue;
	
	/**
	 * Upper warning value.
	 */
	protected Number upperWarningValue;
	
	/**
	 * Lower alarm value.
	 */
	protected Number lowerAlarmValue;
	
	/**
	 * Upper alarm value.
	 */
	protected Number upperAlarmValue;
	
	/**
	 * Lower display value (= start value).
	 */
	protected Number lowerDisplayValue;
	
	/**
	 * Upper display value (= end value).
	 */
	protected Number upperDisplayValue;
	
	/**
	 * Lower control value (= start value).
	 */
	protected Number lowerControlValue;
	
	/**
	 * Upper control value (= end value).
	 */
	protected Number upperControlValue;
	
	/**
	 * Counter value.
	 */
	protected double value;
	
	/**
	 * Timestamp of last value change.
	 */
	protected TimeStamp timestamp;
	
	/**
	 * Value status.
	 */
	protected Status status;
	
	/**
	 * Value status severity.
	 */
	protected Severity severity;
	
	private SnooperServer server = null;
	
	/**
	 * Construct a counter PV instance.
	 * @param name PV name.
	 * @param eventCallback	event callback, where to report value change events.
	 * @param startValue counter start value, where to start counting.
	 * @param endValue	counter end value, where to stop counting.
	 * @param incrementValue	counter increment value, count steps.
	 * @param periodInMS	period in milliseconds between two increments.
	 * @param lowerWarningValue lower warning limit value.
	 * @param upperWarningValue upper warning limit value.
	 * @param lowerAlarmValue lower alarm limit value.
	 * @param upperAlarmValue upper alarm limit value.
	 */
	public NumberOfBroadcastsPerSecond(String name,
			ProcessVariableEventCallback eventCallback,
			int startValue, int endValue, short precision,
			int lowerWarningValue, int upperWarningValue,
			int lowerAlarmValue, int upperAlarmValue, SnooperServer server) {
		super(name, eventCallback);
		
		this.startValue = startValue;
		this.endValue = endValue;
		this.precision = precision;

		this.lowerWarningValue = new Integer(lowerWarningValue);
		this.upperWarningValue = new Integer(upperWarningValue);
		
		this.lowerAlarmValue = new Integer(lowerAlarmValue);
		this.upperAlarmValue = new Integer(upperAlarmValue);

		this.lowerControlValue = new Integer(startValue);
		this.upperControlValue = new Integer(endValue);
		
		this.lowerDisplayValue = lowerControlValue;
		this.upperDisplayValue = upperControlValue;
		
		this.server = server;

		initialize();
	}

	/**
	 * Initialize PV.
	 * Sets initial counter state and spawns a counter thread.
	 */
	protected void initialize()
	{
		value = startValue;
		timestamp = new TimeStamp();
		checkForAlarms();
		
//		Thread thread = new Thread(this, getName() + " counter");
//		thread.setDaemon(true);
//		thread.start();
	}

	/**
	 * Checks for alarms (sets <code>status</code> and <code>severity</code>).
	 */
	protected void checkForAlarms() {
		
		severity = Severity.MINOR_ALARM;

		if (value >= upperAlarmValue.intValue())
			status = Status.HIHI_ALARM;
		else if (value >= upperWarningValue.intValue())
			status = Status.HIGH_ALARM;
		else if (value <= lowerAlarmValue.intValue())
			status = Status.LOLO_ALARM;
		else if (value <= lowerWarningValue.intValue())
			status = Status.LOW_ALARM;
		else
		{
			status = Status.NO_ALARM;
			severity = Severity.NO_ALARM;
		}
		
	}
	
	/**
	 * Return <code>DBRType.INT</code> type as native type.
	 * @see gov.aps.jca.cas.ProcessVariable#getType()
	 */
	public DBRType getType() {
		return DBRType.DOUBLE;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerAlarmLimit()
	 */
	public Number getLowerAlarmLimit() {
		return lowerAlarmValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerCtrlLimit()
	 */
	public Number getLowerCtrlLimit() {
		return lowerControlValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerDispLimit()
	 */
	public Number getLowerDispLimit() {
		return lowerDisplayValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerWarningLimit()
	 */
	public Number getLowerWarningLimit() {
		return lowerWarningValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUnits()
	 */
	public String getUnits() {
		return GR.EMPTYUNIT;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperAlarmLimit()
	 */
	public Number getUpperAlarmLimit() {
		return upperAlarmValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperCtrlLimit()
	 */
	public Number getUpperCtrlLimit() {
		return upperControlValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperDispLimit()
	 */
	public Number getUpperDispLimit() {
		return upperDisplayValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperWarningLimit()
	 */
	public Number getUpperWarningLimit() {
		return upperWarningValue;
	}
	
	/**
	 * @see com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable#getPrecision()
	 */
	public short getPrecision() {
		return precision;
	}

	/**
	 * Read value.
	 * DBR is already filled-in by <code>com.cosylab.epics.caj.cas.util.NumericProcessVariable#read()</code> method.
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#readValue(gov.aps.jca.dbr.DBR, gov.aps.jca.cas.ProcessVariableReadCallback)
	 */
	protected synchronized CAStatus readValue(DBR value,
			ProcessVariableReadCallback asyncReadCallback) throws CAException {
		
		// it is always at least DBR_TIME_Int DBR
		DBR_TIME_Double timeDBR = (DBR_TIME_Double)value;

		// set status and time
		fillInStatusAndTime(timeDBR);

		// set scalar value
		((DOUBLE) timeDBR).getDoubleValue()[0] = this.value;
		
		 // return read completion status
		 return CAStatus.NORMAL;
	}

	/**
	 * Fill-in status and time to DBR.
	 * @param timeDBR DBR to fill-in.
	 */
	protected void fillInStatusAndTime(TIME timeDBR)
	{
		// set status and severity
		timeDBR.setStatus(status);
		timeDBR.setSeverity(severity);

		// set timestamp
		timeDBR.setTimeStamp(timestamp);
	}

	/**
	 * Write value.
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#writeValue(gov.aps.jca.dbr.DBR, gov.aps.jca.cas.ProcessVariableWriteCallback)
	 */
	protected synchronized CAStatus writeValue(DBR value,
			ProcessVariableWriteCallback asyncWriteCallback) throws CAException {
		
		// it is always at least DBR_Int DBR
		DBR_Double doubleDBR = (DBR_Double)value;
		
		// check value
		double val = doubleDBR.getDoubleValue()[0];
//		if (val < startValue || val > endValue)
//			return CAStatus.PUTFAIL;

		// set value, status and alarm
		this.value = val;
		timestamp = new TimeStamp();
		checkForAlarms();
		
		// post event if there is an interest
		if (interest)
		{
			// set event mask
			int mask = Monitor.VALUE | Monitor.LOG;
			if (status != Status.NO_ALARM)
				mask |= Monitor.ALARM;
			
			// create and fill-in DBR
			DBR monitorDBR = AbstractCASResponseHandler.createDBRforReading(this);
			((DBR_Double)monitorDBR).getDoubleValue()[0] = this.value;
			fillInDBR(monitorDBR);
			fillInStatusAndTime((TIME)monitorDBR);
			
			// port event
 	    	eventCallback.postEvent(mask, monitorDBR);
		}
		
		return CAStatus.NORMAL;
	}
	
	public void setDoubleValue (double newValue) {
	// initialize DBR for writting
	final DBR_Double valueHolder = new DBR_Double(1);
	final double[] valueArray = valueHolder.getDoubleValue();
		
		synchronized (this)
		{
//			System.out.println("broadcast-counter: " + newValue);
			
			// set value to DBR
			valueArray[0] = newValue;
			
			// write to PV
			try {
				write(valueHolder, null);
			} catch (CAException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Couting (external trigger) is done in this separate thread.
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		// initialize DBR for writting
		final DBR_Int valueHolder = new DBR_Int(1);
		final int[] valueArray = valueHolder.getIntValue();
		
		while (!Thread.interrupted())
		{
			try {
//				Thread.sleep(periodInMS);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			
			synchronized (this)
			{
				// calculate new value
//				int newValue = value + incrementValue;
//				if (newValue > endValue)
//					newValue = startValue;
				System.out.println("broadcast-counter: " + server.getBroadcastCounter());
				int newValue = server.getBroadcastCounter();
				
				// set value to DBR
				valueArray[0] = newValue;
				
				// write to PV
				try {
					write(valueHolder, null);
				} catch (CAException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
