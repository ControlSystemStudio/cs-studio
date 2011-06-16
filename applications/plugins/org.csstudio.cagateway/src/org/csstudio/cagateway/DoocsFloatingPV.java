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

package org.csstudio.cagateway;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Monitor;
import gov.aps.jca.cas.ProcessVariableEventCallback;
import gov.aps.jca.cas.ProcessVariableReadCallback;
import gov.aps.jca.cas.ProcessVariableWriteCallback;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DOUBLE;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import java.util.GregorianCalendar;

import org.csstudio.cagateway.jmsmessage.JmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ttf.doocs.clnt.EqAdr;
import ttf.doocs.clnt.EqCall;
import ttf.doocs.clnt.EqData;

import com.cosylab.epics.caj.cas.handlers.AbstractCASResponseHandler;
import com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable;

/**
 * Example implementation of process variable - counter.
 * Counter starts counting at <code>startValue</code> incrementing by <code>incrementValue</code> every <code>periodInMS</code> milliseconds.
 * When counter value riches <code>envValue</code> counter is reset to <code>startValue</code>.
 * Implementation also triggers alarms (seting status and severity) regarding to set warning and alarm limits. 
 * @author msekoranja
 */
public class DoocsFloatingPV extends FloatingDecimalProcessVariable implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(DoocsFloatingPV.class);
    
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

	/**
	 * DOOCS device name
	 */
	private final String doocsName;
	
	/**
	 * whether dooc readback was successful 
	 */
	boolean doocsReadOk = false;
	
	/**
	 * Archive Update Trigger
	 */
	private final boolean archiveUpdateTrigger = false;
	
	/**
	 * Display Update Trigger
	 */
	private final boolean displayUpdateTrigger = false;
	
	/**
	 * Alarm Update Trigger
	 */
	private boolean alarmUpdateTrigger = false;
	
	/**
	 * class contains all define meta data for this channel
	 */
	private MetaData metaData = null;
	
	/**
	 * Construct a DOOCS record instance.
	 * @param name PV name.
	 * @param doocsName DOOCS name.
	 * @param eventCallback	event callback, where to report value change events.
	 * @param metaData all of the meta data of this channel - to be filled in before!
	 */
	public DoocsFloatingPV(String name, String doocsName,
			ProcessVariableEventCallback eventCallback, MetaData metaData) {
		
		super(name, eventCallback);
		
		this.metaData = metaData;
		
		this.name = name;
		
		this.doocsName = doocsName;
		
		this.precision = metaData.get_precision();
		
		// ?? egu ?? 

		this.lowerWarningValue = metaData.get_lowerWarningValue();;
		this.upperWarningValue = metaData.get_upperWarningValue();
		
		this.lowerAlarmValue = metaData.get_lowerAlarmValue();
		this.upperAlarmValue = metaData.get_upperAlarmValue();

		this.lowerControlValue = metaData.get_lowerControlValue();
		this.upperControlValue = metaData.get_upperControlValue();
		
		this.lowerDisplayValue = metaData.get_lowerDisplayValue();
		this.upperDisplayValue = metaData.get_upperDisplayValue();

		initialize();
	}

	/**
	 * Initialize PV.
	 * Sets initial counter state and spawns a counter thread.
	 */
	protected void initialize()
	{
		value = 0;
		timestamp = new TimeStamp();
//		LOG.error( this, "caGateway DOOCS channel: " + doocsName + " initialize()");
		checkForAlarms();
		
		Thread thread = new Thread(this, getName());
//		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Checks for alarms (sets <code>status</code> and <code>severity</code>).
	 */
	protected void checkForAlarms() {
		String jmsMessageSeverity = null;
		String jmsMessageStatus = null;
		
		// check if doocs read was successful
		if ( !isDoocsReadOk()) {
			status = Status.READ_ACCESS_ALARM;
        	severity = Severity.INVALID_ALARM;
        	alarmUpdateTrigger = true;
        	// TODO: MCL generate JMS alarm message
        	JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_GW_ALARM, 		// type
					doocsName,								// name
					""+value, 								// value
					JmsMessage.SEVERITY_INVALID, 			// severity
					"READ_ACCESS", 							// status
					"DOOCS-Gateway", 						// host
					metaData.get_facility(), 				// facility
					metaData.get_descriptor());				// text
        	return;
		}
		
		severity = Severity.MINOR_ALARM;

		if (value >= upperAlarmValue.intValue()) {
			status = Status.HIHI_ALARM;
			severity = Severity.MAJOR_ALARM;
			
			jmsMessageSeverity = JmsMessage.SEVERITY_MAJOR;
			jmsMessageStatus = JmsMessage.STATUS_HIHI_ALARM;
		} else if (value >= upperWarningValue.intValue()) {
			status = Status.HIGH_ALARM;
			severity = Severity.MINOR_ALARM;
			
			jmsMessageSeverity = JmsMessage.SEVERITY_MINOR;
			jmsMessageStatus = JmsMessage.STATUS_HIGH_ALARM;
		} else if (value <= lowerAlarmValue.intValue()) {
			status = Status.LOLO_ALARM;
			severity = Severity.MAJOR_ALARM;
			
			jmsMessageSeverity = JmsMessage.SEVERITY_MAJOR;
			jmsMessageStatus = JmsMessage.STATUS_LOLO_ALARM;
		} else if (value <= lowerWarningValue.intValue()) {
			status = Status.LOW_ALARM;
			severity = Severity.MINOR_ALARM;
			
			jmsMessageSeverity = JmsMessage.SEVERITY_MINOR;
			jmsMessageStatus = JmsMessage.STATUS_LOW_ALARM;
		} 			
		else
		{
			status = Status.NO_ALARM;
			severity = Severity.NO_ALARM;
			
			jmsMessageSeverity = JmsMessage.SEVERITY_NO_ALARM;
			jmsMessageStatus = JmsMessage.STATUS_NO_ALARM;
		}
		if ( (status != Status.NO_ALARM) || ( severity != Severity.NO_ALARM)) {
			alarmUpdateTrigger = true;
			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_GW_ALARM, 		// type
					doocsName,								// name
					""+value, 								// value
					jmsMessageSeverity, 					// severity
					jmsMessageStatus, 						// status
					"DOOCS-Gateway", 						// host
					metaData.get_facility(), 				// facility
					metaData.get_descriptor());				// text
		}
	}
	
	/**
	 * Return <code>DBRType.INT</code> type as native type.
	 * @see gov.aps.jca.cas.ProcessVariable#getType()
	 */
	@Override
    public DBRType getType() {
		return DBRType.DOUBLE;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerAlarmLimit()
	 */
	@Override
    public Number getLowerAlarmLimit() {
		return lowerAlarmValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerCtrlLimit()
	 */
	@Override
    public Number getLowerCtrlLimit() {
		return lowerControlValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerDispLimit()
	 */
	@Override
    public Number getLowerDispLimit() {
		return lowerDisplayValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getLowerWarningLimit()
	 */
	@Override
    public Number getLowerWarningLimit() {
		return lowerWarningValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUnits()
	 */
	@Override
    public String getUnits() {
		return GR.EMPTYUNIT;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperAlarmLimit()
	 */
	@Override
    public Number getUpperAlarmLimit() {
		return upperAlarmValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperCtrlLimit()
	 */
	@Override
    public Number getUpperCtrlLimit() {
		return upperControlValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperDispLimit()
	 */
	@Override
    public Number getUpperDispLimit() {
		return upperDisplayValue;
	}

	/**
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#getUpperWarningLimit()
	 */
	@Override
    public Number getUpperWarningLimit() {
		return upperWarningValue;
	}
	
	/**
	 * @see com.cosylab.epics.caj.cas.util.FloatingDecimalProcessVariable#getPrecision()
	 */
	@Override
    public short getPrecision() {
		return precision;
	}

	/**
	 * Read value.
	 * DBR is already filled-in by <code>com.cosylab.epics.caj.cas.util.NumericProcessVariable#read()</code> method.
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#readValue(gov.aps.jca.dbr.DBR, gov.aps.jca.cas.ProcessVariableReadCallback)
	 */
	@Override
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
		// TODO: MCL use DOOCS time stamp instead
		timeDBR.setTimeStamp(timestamp);
	}

	/**
	 * Write value.
	 * @see com.cosylab.epics.caj.cas.util.NumericProcessVariable#writeValue(gov.aps.jca.dbr.DBR, gov.aps.jca.cas.ProcessVariableWriteCallback)
	 */
	@Override
    protected synchronized CAStatus writeValue(DBR value,
			ProcessVariableWriteCallback asyncWriteCallback) throws CAException {
		
		// TODO: MCL 2010-07-23
		// add putLogging
		
		// it is always at least DBR_Int DBR
		DBR_Double doubleDBR = (DBR_Double)value;
		
		// check value
		double val = doubleDBR.getDoubleValue()[0];
//		if (val < startValue || val > endValue)
//			return CAStatus.PUTFAIL;

		// set value, status and alarm
		this.value = val;
		// TODO: MCL use DOOCS time stamp instead
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
	// initialize DBR for writing
	final DBR_Double valueHolder = new DBR_Double(1);
	final double[] valueArray = valueHolder.getDoubleValue();
		
		synchronized (this)
		{
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
	 * @see java.lang.Runnable#run()
	 */
	@Override
    public void run() {
		
		//ToDo: MCL replace by preference value
		int maxDoocsErrorCount = 10;
		int maxDoocsTotalErrorCount = 1000;
		int doocsUpdateRate = 5000;
		int modulusForTotalErrorCount = 100;
		
		// things we need for DOOCS
		EqAdr doocsAddr = new EqAdr( doocsName);
		EqCall eq = new EqCall();
		EqData data = new EqData();
		EqData ed = new EqData();
		
		int errorCount = 0;
		int totalErrorCount = 0;
		
//		LOG.info( this, "caGateway DOOCS create thread for: " + doocsName); 
		
		/*
		 * initialize meta data
		 */
		ed.init();
		data.init();
		
		
		while (!Thread.interrupted())
		{
			try {
				Thread.sleep(doocsUpdateRate);
			} catch (InterruptedException e) {
				break;
			}
			
			synchronized (this)
			{
				if ( eq == null) {
					eq = new EqCall();
				}
				// necessary if in a loop
				ed.init();
				data.init();
				
		        data = eq.get(doocsAddr, ed);
		        if (data.error() == 0) {
		        	doocsReadOk = true;
		        	value = data.get_double();
//		        	System.out.println (doocsName + " - double = " + value);
//		        	System.out.println (doocsName + " - float  = " + data.get_float());
		        	
		        	setDoubleValue(value);
		        	
		        	status = Status.NO_ALARM;
		        	severity = Severity.NO_ALARM;
		        	
		        	if ( errorCount > 0) {
		        		errorCount--;
		        	}
		        } else {
		        	doocsReadOk = false;
		        	errorCount++;
		        	totalErrorCount++;
		        	/*
		        	 * Alternating connection errors:
		        	 * - errors do not occur consecutively. 
		        	 * Correct connections happen in-between so the errorCount does not reach
		        	 * the maxDoocsErrorCount limit which puts the channel to the blackList.
		        	 * 
		        	 * alternating errors would blow up the log file
		        	 * so we limit this to the first messages generated here
		        	 * after this we generate a message every 100th time
		        	 */
		        	if ( (totalErrorCount < maxDoocsErrorCount) || (totalErrorCount%modulusForTotalErrorCount == 0)) {
		        	    Object[] args = new Object[] {doocsName, data.get_string(), errorCount, totalErrorCount};
		        		LOG.error( "caGateway DOOCS read-error: {} {} errorCount: {} total: {}",args );
		        	}		        	
		        }
		        /*
		         * put channel on black list if
		         * - errorCount reached its limit - or
		         * - totalErrorCount reached its limit
		         */
		        if ( (errorCount > maxDoocsErrorCount) || (totalErrorCount > maxDoocsTotalErrorCount)) {
		        	// add channel to black list
		        	LOG.warn( "caGateway DOOCS error count > {} stop and put {} on blackList", maxDoocsErrorCount, name);
		        	DoocsClient.getInstance().addToBlackList(name, new GregorianCalendar());
		        	
		        	// just in case we do not stop the first time ...
		        	errorCount = 0;
		        	
		        	
		        	// remove from HashMap of existing channel names
		        	CaServer.getGatewayInstance().removeAvailableRemoteDevices(name);
		        	
		        	// remove from CA-Server list of existing records
		        	CaServer.getGatewayInstance().getServer().unregisterProcessVaribale(name);

		        	// stop this thread here
		        	break;
		        }
			}
		}
	}

	public boolean isDoocsReadOk() {
		return doocsReadOk;
	}

	public void setDoocsReadOk(boolean doocsReadOk) {
		this.doocsReadOk = doocsReadOk;
	}

}
