package org.csstudio.platform.statistic;

import org.csstudio.platform.logging.CentralLogger;

public class AlarmHandler {
	
	private String logLevel = "info";
	private Double deadband = 5.0;
	private Double highAbsoluteLimit = 1000.0;
	private boolean highAbsoluteLimitIsActive = false;
	private StoredData highAbsoluteLimitLastAlarm = null;
	private Double highRelativeLimit = 1000.0;
	private boolean highRelativeLimitIsActive = false;
	private StoredData highRelativeLimitLastAlarm = null;
	private String descriptor	= null;
	private String application = null;
	
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public AlarmHandler() {
		
	}
	
	public AlarmHandler ( Double highAbsoluteLimit, Double highRelativeLimit) {
		/*
		 * set limits
		 */
		setHighAbsoluteLimit(highAbsoluteLimit);
		setHighRelativeLimit(highRelativeLimit);
	}
	
	public void process ( Double value, Collector collector) {
		/*
		 * alarm chaecking
		 */
		if ( (value > highAbsoluteLimit) && !highAbsoluteLimitIsActive) {
			/*
			 * set absolute limit on
			 */
			CentralLogger.getInstance().warn(this, getApplication() + " : " + getDescriptor() + "above absolute High limit! Value: " + value + "Info: " + collector.getInfo());
		} else {
			if ( value < ( highAbsoluteLimit * (100.0 - deadband))) {
				setHighAbsoluteLimitIsActive(false);
			}
		}
		
		if ( (value > ( collector.getMeanValuerelative() * getHighRelativeLimit()/ 100.0 ) && !highRelativeLimitIsActive)) {
			/*
			 * set absolute limit on
			 */
			CentralLogger.getInstance().warn(this, getApplication() + " : " + getDescriptor() + " : >" + getHighRelativeLimit() + "% "  + "above floating mean value (" + collector.getMeanValuerelative() + ")! Value: " + value + "Info: " + collector.getInfo());
		} else {
			if ( value < ( (collector.getMeanValuerelative() * getHighRelativeLimit()/ 100.0 ) * (100.0 - deadband))) {
				setHighRelativeLimitIsActive(false);
			}
		}
		
		
	}
	
	
	public Double getDeadband() {
		return deadband;
	}
	public void setDeadband(Double deadband) {
		this.deadband = deadband;
	}
	public void setDeadband(int deadband) {
		this.deadband = new Double(deadband);
	}
	public Double getHighAbsoluteLimit() {
		return highAbsoluteLimit;
	}
	public void setHighAbsoluteLimit(Double highAbsoluteLimit) {
		this.highAbsoluteLimit = highAbsoluteLimit;
	}
	public void setHighAbsoluteLimit(int highAbsoluteLimit) {
		this.highAbsoluteLimit = new Double(highAbsoluteLimit);
	}
	public boolean isHighAbsoluteLimitIsActive() {
		return highAbsoluteLimitIsActive;
	}
	public void setHighAbsoluteLimitIsActive(boolean highAbsoluteLimitIsActive) {
		this.highAbsoluteLimitIsActive = highAbsoluteLimitIsActive;
	}
	public StoredData getHighAbsoluteLimitLastAlarm() {
		return highAbsoluteLimitLastAlarm;
	}
	public void setHighAbsoluteLimitLastAlarm(StoredData highAbsoluteLimitLastAlarm) {
		this.highAbsoluteLimitLastAlarm = highAbsoluteLimitLastAlarm;
	}
	public Double getHighRelativeLimit() {
		return highRelativeLimit;
	}
	public void setHighRelativeLimit(Double highRelativeLimit) {
		this.highRelativeLimit = highRelativeLimit;
	}
	public void setHighRelativeLimit(int highRelativeLimit) {
		this.highRelativeLimit = new Double(highRelativeLimit);
	}
	public boolean isHighRelativeLimitIsActive() {
		return highRelativeLimitIsActive;
	}
	public void setHighRelativeLimitIsActive(boolean highRelativeLimitIsActive) {
		this.highRelativeLimitIsActive = highRelativeLimitIsActive;
	}
	public StoredData getHighRelativeLimitLastAlarm() {
		return highRelativeLimitLastAlarm;
	}
	public void setHighRelativeLimitLastAlarm(StoredData highRelativeLimitLastAlarm) {
		this.highRelativeLimitLastAlarm = highRelativeLimitLastAlarm;
	}
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	

}
