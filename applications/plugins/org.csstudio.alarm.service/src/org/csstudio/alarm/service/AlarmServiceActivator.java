package org.csstudio.alarm.service;

import java.util.Dictionary;
import java.util.Hashtable;

import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.internal.AlarmServiceJMSImpl;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class AlarmServiceActivator implements BundleActivator {
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(final BundleContext context) throws Exception {
        _log.debug(this, "Starting AlarmService");
        
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", "Alarm service with JMS or DAL implementation");
        
        // Provide implementation for alarm service
        // TODO jp The implementation must be determined dynamically
        context.registerService(IAlarmService.class.getName(),
                                new AlarmServiceJMSImpl(),
                                properties);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(final BundleContext context) throws Exception {
        _log.debug(this, "Stopping AlarmService");
    }
    
}
