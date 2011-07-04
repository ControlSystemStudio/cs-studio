
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

package org.csstudio.tine2jms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.csstudio.platform.statistic.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.desy.tine.alarmUtils.AlarmMonitor;
import de.desy.tine.alarmUtils.AlarmMonitorHandler;
import de.desy.tine.alarmUtils.TAlarmSystem;
import de.desy.tine.client.TLink;
import de.desy.tine.server.alarms.TAlarmMessage;

/**
 * @author Markus Moeller
 *
 */
public class TineAlarmMonitor extends Observable implements AlarmMonitorHandler {
    
    /** Class logger */
    private static final Logger LOG = LoggerFactory.getLogger(TineAlarmMonitor.class);

    /** */
    private String _context;

    /** */
    private TLink _tineLink;

    /** */
    private SimpleDateFormat dateFormat;
    
    /** Class that collects statistic informations. Query it via XMPP. */
    private Collector receivedMessages;

    /** */
    private long lastTimeStamp;
    
    public TineAlarmMonitor(Observer observer, String context) {
        
        this._context = context;
        this.addObserver(observer);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        receivedMessages = new Collector();
        receivedMessages.setApplication(VersionInfo.NAME);
        receivedMessages.setDescriptor(context + ": Received messages");
        receivedMessages.setContinuousPrint(false);
        receivedMessages.setContinuousPrintCount(1000.0);

        _tineLink = TAlarmSystem.monitorAlarms(context, null, "ALL", 0, new AlarmMonitor(this));
        lastTimeStamp = 0;
    }
    
    public void close() {
        _tineLink.close();
    }
    
    /**
     * @see de.desy.tine.alarmUtils.AlarmMonitorHandler#alarmsHandler(de.desy.tine.alarmUtils.AlarmMonitor)
     */
    @Override
    public void alarmsHandler(AlarmMonitor alarmMonitor) {
        
        TAlarmMessage alarm = null;
        AlarmMessage am = null;
        Date date = null;
        
        TAlarmMessage[] ams = alarmMonitor.getLastAcquiredAlarms(0, true);
        
        if(ams != null) {
            
            date = new Date(alarmMonitor.getLastAcquiredAlarmTime());
            LOG.debug("Anzahl: " + ams.length + " - Last Acquiried Alarm Time: " + dateFormat.format(date));
            date = null;
    
            if(ams.length > 0) {
                alarm = ams[ams.length - 1];
                
                if(alarm.getTimeStamp() > this.lastTimeStamp) {
                    
                    this.lastTimeStamp = alarm.getTimeStamp();
                    
                    date = new Date(alarm.getTimeStamp());
                    LOG.debug(_context + ": Neuer Alarm Timestamp: " + dateFormat.format(date));
                    
                    date = null;
                    
                    am = new AlarmMessage(alarm, _context);
                    receivedMessages.incrementValue();
                    
                    setChanged();
                    notifyObservers(am);
                }
            }
        } else {
            LOG.debug("No alarms");
        }
    }
}
