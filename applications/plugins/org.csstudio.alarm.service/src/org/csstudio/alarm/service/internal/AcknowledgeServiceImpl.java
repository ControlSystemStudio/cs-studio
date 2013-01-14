/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.service.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.IAcknowledgeService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.alarm.service.declaration.ITimeService;
import org.csstudio.persister.declaration.IPersistableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the acknowledge service
 * This is used concurrently, so the synchronization is considered:
 * Its internal state is hold in a concurrent hash map, which is written to through announce*-methods (typically from jms callbacks)
 * and read from through getAcknowledge*-methods (from remote callers) and getMemento (from a thread in the persistence service). 
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public class AcknowledgeServiceImpl implements IAcknowledgeService, IRemoteAcknowledgeService {
    private static final Logger LOG = LoggerFactory.getLogger(AcknowledgeServiceImpl.class);
    
    
    private Map<String, String> _pvName2Time;
    private final ITimeService _timeService;
    private IAlarmConnection _alarmConnection;
    
    public AcknowledgeServiceImpl(@Nonnull final ITimeService timeService) {
        // guard: time service must be given
        if (timeService == null) {
            throw new IllegalArgumentException("timeService must not be null");
        }
        _timeService = timeService;
        _pvName2Time = new ConcurrentHashMap<String, String>();
    }
    
    @Override
    public void announceAcknowledge(@Nonnull final String pvName) {
        guardPvName(pvName);
        
        String time = _timeService.getCurrentTimeAsString();
        _pvName2Time.put(pvName, time);
    }
    
    @Override
    public void announceAlarm(@Nonnull final String pvName) {
        guardPvName(pvName);
        _pvName2Time.remove(pvName);
    }
    
    @Override
    @Nonnull
    public String getAcknowledgeTime(@Nonnull final String pvName) {
        guardPvName(pvName);
        
        return _pvName2Time.get(pvName);
    }

    @Override
    @Nonnull
    public Collection<String> getAcknowledgedPvs() {
        return  new ArrayList<String>(_pvName2Time.keySet());
    }

    @Override
    public void connectToAcknowledgeTopic(@Nonnull final String ackTopicName) throws AlarmConnectionException {
        _alarmConnection = new AlarmConnectionJMSImpl();
        IAlarmConnectionMonitor connectionMonitor = newAlarmConnectionMonitor();
        AlarmResource resource = new AlarmResource(Collections.singletonList(ackTopicName));
        _alarmConnection.connect(connectionMonitor, newAlarmListener(), resource);
    }
    
    /**
     * To be able to save the internal state of this service, it is given outside
     * 
     * @return internal map
     */
    @Override
    @Nonnull
    public HashMap<String, String> getMemento() {
        return new HashMap<String, String>(_pvName2Time);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void restoreMemento(@Nonnull final Object memento) {
        // guard: memento must be given
        if (memento == null) {
            throw new IllegalArgumentException("memento must not be null");
        }
        // guard for the type of the memento
        if (!Map.class.isAssignableFrom(memento.getClass())) {
            throw new IllegalArgumentException("memento has wrong type: " + memento.getClass());
        }
        _pvName2Time = new HashMap<String, String>((Map<String, String>) memento);
        
    }
    
    @Override
    public void disconnect() {
        if (_alarmConnection != null) {
            _alarmConnection.disconnect();
        }
    }

    // guard: pvName must be given
    private void guardPvName(@Nonnull final String pvName) {
        if (pvName == null) {
            throw new IllegalArgumentException("pvName must not be null");
        }
    }

    @Nonnull
    private IAlarmConnectionMonitor newAlarmConnectionMonitor() {
        return new IAlarmConnectionMonitor() {
            
            @Override
            public void onDisconnect() {
                // can safely be ignored
            }
            
            @Override
            public void onConnect() {
                // can safely be ignored
            }
        };
    }

    @Nonnull
    private IAlarmListener newAlarmListener() {
        return new IAlarmListener() {
            
            @Override
            public void stop() {
                // nothing to do
            }
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void onMessage(@Nonnull final IAlarmMessage message) {
                if (message.isAcknowledgement()) {
                    LOG.trace("onMessage called from ack topic");
                    String pvName = message.getString(AlarmMessageKey.NAME);
                    announceAcknowledge(pvName);
                } else {
                    LOG.warn("onMessage called from ack topic but message was no acknowledgement");
                }
            }
        };
    }
    
}
