package org.csstudio.platform.statistic;

public class BackgroundCollector {
	
	private static 	BackgroundCollector 	thisBackgroundCollector = null;
	private Collector	cpuUsedSystem	= null;
	private Collector	cpuUsedApplication	= null;
	private Collector	memoryUsedSystem	= null;
	private Collector	memoryUsedApplication	= null;
	private Collector	memoryAvailableApplication	= null;
	
	public BackgroundCollector () {
        /*
         * start background thread
         *  and run with 10 sec timeout
         */
        new BackgroundCollectorThread( 10000);	
	}
	
	
	public static BackgroundCollector getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisBackgroundCollector == null) {
			synchronized (BackgroundCollector.class) {
				if (thisBackgroundCollector == null) {
					thisBackgroundCollector = new BackgroundCollector();
				}
			}
		}
		return thisBackgroundCollector;
	}


	public Collector getCpuUsedApplication() {
		
		if (cpuUsedApplication == null) {
	        // CPU used by Application
	        cpuUsedApplication = new Collector();
	        cpuUsedApplication.setApplication("CSS-Core");
	        cpuUsedApplication.setDescriptor("CPU used by Application");
	        cpuUsedApplication.getAlarmHandler().setDeadband(10.0);
	        cpuUsedApplication.getAlarmHandler().setHighAbsoluteLimit(50.0);	// 90% CPU
	        cpuUsedApplication.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return cpuUsedApplication;
	}


	public void setCpuUsedApplication(Collector cpuUsedApplication) {
		this.cpuUsedApplication = cpuUsedApplication;
	}


	public Collector getCpuUsedSystem() {
		
		if (cpuUsedSystem == null) {
			// CPU used by System
	        cpuUsedSystem = new Collector();
	        cpuUsedSystem.setApplication("CSS-Core");
	        cpuUsedSystem.setDescriptor("CPU used by System");
	        cpuUsedSystem.getAlarmHandler().setDeadband(10.0);
	        cpuUsedSystem.getAlarmHandler().setHighAbsoluteLimit(90.0);	// 90% CPU
	        cpuUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return cpuUsedSystem;
	}


	public void setCpuUsedSystem(Collector cpuUsedSystem) {
		this.cpuUsedSystem = cpuUsedSystem;
	}


	public Collector getMemoryUsedApplication() {
		
		if ( memoryUsedApplication == null ) {
	        // Memory used by Application
	        memoryUsedApplication = new Collector();
	        memoryUsedApplication.setApplication("CSS-Core");
	        memoryUsedApplication.setDescriptor("Memory used by Application");
	        memoryUsedApplication.getAlarmHandler().setDeadband(10.0);
	        memoryUsedApplication.getAlarmHandler().setHighAbsoluteLimit(200.0);	// 200 MB
	        memoryUsedApplication.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryUsedApplication;
	}


	public void setMemoryUsedApplication(Collector memoryUsedApplication) {
		this.memoryUsedApplication = memoryUsedApplication;
	}


	public Collector getMemoryUsedSystem() {
		
		if (memoryUsedSystem == null) {
	        // Memory used by System
	        memoryUsedSystem = new Collector();
	        memoryUsedSystem.setApplication("CSS-Core");
	        memoryUsedSystem.setDescriptor("Memory used by System");
	        memoryUsedSystem.getAlarmHandler().setDeadband(10.0);
	        memoryUsedSystem.getAlarmHandler().setHighAbsoluteLimit(100000.0);	// biig number -> no alarm
	        memoryUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryUsedSystem;
	}


	public void setMemoryUsedSystem(Collector memoryUsedSystem) {
		this.memoryUsedSystem = memoryUsedSystem;
	}


	public Collector getMemoryAvailableApplication() {
		
		if (memoryAvailableApplication == null) {
			// Memory available for Application
	        memoryAvailableApplication = new Collector();
	        memoryAvailableApplication.setApplication("CSS-Core");
	        memoryAvailableApplication.setDescriptor("Memory available for Application");
	        memoryAvailableApplication.getAlarmHandler().setDeadband(10.0);
	        memoryAvailableApplication.getAlarmHandler().setHighAbsoluteLimit(100000.0);	// biig number -> no alarm
	        memoryAvailableApplication.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryAvailableApplication;
	}


	public void setMemoryAvailableApplication(Collector memoryAvailableApplication) {
		this.memoryAvailableApplication = memoryAvailableApplication;
	}

}
