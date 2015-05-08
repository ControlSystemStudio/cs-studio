/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.domain.common.statistic;

/**resc.
 * @author claus
 *
 */
public class BackgroundCollector {

    /**dfgdfg.
     *
     */
    private static     BackgroundCollector     _thisBackgroundCollector = null;
    private Collector    _cpuUsedSystem    = null;
    private Collector    cpuUsedApplication    = null;
    private Collector    memoryUsedSystem    = null;
    private Collector    memoryUsedApplication    = null;
    private Collector    memoryAvailableApplication    = null;
    private String applicationName;



    public BackgroundCollector () {
        /*
         * start background thread
         *  and run with 10 sec timeout
         */
        new BackgroundCollectorThread( 10000);

        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            applicationName = "CSS-Core " + localMachine.getHostName();
        }
        catch (java.net.UnknownHostException uhe) {
        }


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
            cpuUsedApplication.setApplication(applicationName);
            cpuUsedApplication.setDescriptor("CPU used by Application");
            cpuUsedApplication.getAlarmHandler().setDeadband(10.0);
            cpuUsedApplication.getAlarmHandler().setHighAbsoluteLimit(50.0);    // 90% CPU
            cpuUsedApplication.getAlarmHandler().setHighRelativeLimit(500.0);    // 500%
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
            _cpuUsedSystem.setApplication(applicationName);
            _cpuUsedSystem.setDescriptor("CPU used by System");
            _cpuUsedSystem.getAlarmHandler().setDeadband(10.0);
            _cpuUsedSystem.getAlarmHandler().setHighAbsoluteLimit(90.0);    // 90% CPU
            _cpuUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);    // 500%
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
            memoryUsedApplication.setApplication(applicationName);
            memoryUsedApplication.setDescriptor("Memory allocated by Application");
            memoryUsedApplication.getAlarmHandler().setDeadband(10.0);
            memoryUsedApplication.getAlarmHandler().setHighAbsoluteLimit(200.0);    // 200 MB
            memoryUsedApplication.getAlarmHandler().setHighRelativeLimit(500.0);    // 500%
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
            memoryUsedSystem.setApplication(applicationName);
            memoryUsedSystem.setDescriptor("max Memory available");
            memoryUsedSystem.getAlarmHandler().setDeadband(10.0);
            memoryUsedSystem.getAlarmHandler().setHighAbsoluteLimit(100000.0);    // biig number -> no alarm
            memoryUsedSystem.getAlarmHandler().setHighRelativeLimit(500.0);    // 500%
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
            memoryAvailableApplication.setApplication(applicationName);
            memoryAvailableApplication.setDescriptor("Memory free for Application");
            memoryAvailableApplication.getAlarmHandler().setDeadband(10.0);
            memoryAvailableApplication.getAlarmHandler().setHighAbsoluteLimit(100000.0);    // biig number -> no alarm
            memoryAvailableApplication.getAlarmHandler().setHighRelativeLimit(500.0);    // 500%
        }
        return memoryAvailableApplication;
    }


    public final void setMemoryAvailableApplication(final Collector memoryAvailableApplication) {
        this.memoryAvailableApplication = memoryAvailableApplication;
    }

}
