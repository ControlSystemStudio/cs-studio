package org.csstudio.alarm.dal2jms;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Dal2JmsApplication implements IApplication {
    
    /** TODO implement remote command to stop the server **/
    private volatile boolean _stopped = false;
    
    @Override
    public Object start(IApplicationContext context) throws Exception {
        CentralLogger.getInstance().debug(this, "da2jms");
        
        AlarmHandler alarmHandler = new AlarmHandler();
        
        synchronized (this) {
            while (!_stopped) {
                wait();
            }
        }
        return IApplication.EXIT_OK;
    }
    
    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }
    
}
