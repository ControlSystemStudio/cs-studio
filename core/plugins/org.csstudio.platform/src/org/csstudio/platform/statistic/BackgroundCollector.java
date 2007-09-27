package org.csstudio.platform.statistic;

/**resc.
 * @author claus
 *
 */
public class BackgroundCollector {
	
	/**dfgdfg.
	 * 
	 */
	private static 	BackgroundCollector 	_thisBackgroundCollector = null;
	private Collector	_cpuUsedSystem	= null;
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
	
	
	public synchronized static BackgroundCollector getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( _thisBackgroundCollector == null) {
			_thisBackgroundCollector = new BackgroundCollector();
		}
		return _thisBackgroundCollector;
	}


	public final Collector getCpuUsedApplication() {
		
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


	public final void setCpuUsedApplication(final Collector cpuUsedApplication) {
		this.cpuUsedApplication = cpuUsedApplication;
	}


	public final Collector getCpuUsedSystem() {
		
		if (_cpuUsedSystem == null) {
			// CPU used by System
	        _cpuUsedSystem = new Collector();
	        _cpuUsedSystem.setApplication("CSS-Core");
	        _cpuUsedSystem.setDescriptor("CPU used by System");
	        _cpuUsedSystem.getAlarmHandler().setDeadband(10.0);
	        _cpuUsedSystem.getAlarmHandler().setHighAbsoluteLimit(90.0);	// 90% CPU
	        _cpuUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return _cpuUsedSystem;
	}


	public final void setCpuUsedSystem(final Collector cpuUsedSystem) {
		this._cpuUsedSystem = cpuUsedSystem;
	}


	public final Collector getMemoryUsedApplication() {
		
		if ( memoryUsedApplication == null ) {
	        // Memory used by Application
	        memoryUsedApplication = new Collector();
	        memoryUsedApplication.setApplication("CSS-Core");
	        memoryUsedApplication.setDescriptor("Memory allocated by Application");
	        memoryUsedApplication.getAlarmHandler().setDeadband(10.0);
	        memoryUsedApplication.getAlarmHandler().setHighAbsoluteLimit(200.0);	// 200 MB
	        memoryUsedApplication.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryUsedApplication;
	}


	public final void setMemoryUsedApplication(final Collector memoryUsedApplication) {
		this.memoryUsedApplication = memoryUsedApplication;
	}


	public final Collector getMemoryUsedSystem() {
		
		if (memoryUsedSystem == null) {
	        // Memory used by System
	        memoryUsedSystem = new Collector();
	        memoryUsedSystem.setApplication("CSS-Core");
	        memoryUsedSystem.setDescriptor("max Memory available");
	        memoryUsedSystem.getAlarmHandler().setDeadband(10.0);
	        memoryUsedSystem.getAlarmHandler().setHighAbsoluteLimit(100000.0);	// biig number -> no alarm
	        memoryUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryUsedSystem;
	}


	public final void setMemoryUsedSystem(final Collector memoryUsedSystem) {
		this.memoryUsedSystem = memoryUsedSystem;
	}


	public final Collector getMemoryAvailableApplication() {
		
		if (memoryAvailableApplication == null) {
			// Memory available for Application
	        memoryAvailableApplication = new Collector();
	        memoryAvailableApplication.setApplication("CSS-Core");
	        memoryAvailableApplication.setDescriptor("Memory free for Application");
	        memoryAvailableApplication.getAlarmHandler().setDeadband(10.0);
	        memoryAvailableApplication.getAlarmHandler().setHighAbsoluteLimit(100000.0);	// biig number -> no alarm
	        memoryAvailableApplication.getAlarmHandler().setHighRelativeLimit(500.0);	// 500%
		}
		return memoryAvailableApplication;
	}


	public final void setMemoryAvailableApplication(final Collector memoryAvailableApplication) {
		this.memoryAvailableApplication = memoryAvailableApplication;
	}

}
