package org.csstudio.alarm.table.jms;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.servicelocator.ServiceLocator;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AlarmListenerTest {
    
    private IAlarmTableListener _listenerUnderTest;
    private AbstractMessageList _messageList;
    
    @Before
    public void setUp() throws Exception {
        _listenerUnderTest = new AlarmListener();
        
        // we have to set a message list before we can deliver messages (otherwise null pointer exception in onMessage!)
        _messageList = mock(AbstractMessageList.class);
        _listenerUnderTest.setMessageList(_messageList);
    }
    
    @Test
    public void testOnMessageCanHandleNullMessage() {
        _listenerUnderTest.onMessage(null);
        verify(_messageList, never()).addMessage(any(BasicMessage.class));
    }
    
    @Test
    public void testOnMessageWithoutFilter() {
        IAlarmMessage message = mock(IAlarmMessage.class);
        _listenerUnderTest.onMessage(message);
        verify(_messageList).addMessage(any(BasicMessage.class));
    }
    
    @Test
    public void testOnMessageWithFilterPass() throws Exception {
        setupAlarmServiceWithPv("myPv");
        _listenerUnderTest.enableFilter(true);
        
        IAlarmMessage message = mock(IAlarmMessage.class);
        when(message.getString(AlarmMessageKey.NAME)).thenReturn("myPv");
        _listenerUnderTest.onMessage(message);
        verify(_messageList).addMessage(any(BasicMessage.class));
    }
    
    @Test
    public void testOnMessageWithFilterDontPass() throws Exception {
        setupAlarmServiceWithPv("myPv");
        _listenerUnderTest.enableFilter(true);
        
        IAlarmMessage message = mock(IAlarmMessage.class);
        when(message.getString(AlarmMessageKey.NAME)).thenReturn("somePv");
        _listenerUnderTest.onMessage(message);
        verify(_messageList, never()).addMessage(any(BasicMessage.class));
    }
    
    @Test
    public void testOnMessageFiresListener() {
        // now register listener
        IAlarmListener alarmListener = mock(IAlarmListener.class);
        _listenerUnderTest.registerAlarmListener(alarmListener);
        
        IAlarmMessage message = mock(IAlarmMessage.class);
        _listenerUnderTest.onMessage(message);
        verify(alarmListener).onMessage(message);
        _listenerUnderTest.onMessage(message);
        verify(alarmListener, times(2)).onMessage(message);
        
        // now deregister, still totally called two times
        _listenerUnderTest.deRegisterAlarmListener(alarmListener);
        _listenerUnderTest.onMessage(message);
        verify(alarmListener, times(2)).onMessage(message);
    }
    
    private void setupAlarmServiceWithPv(@Nonnull final String pvName) throws AlarmServiceException {
        IAlarmService alarmService = mock(IAlarmService.class);
        ServiceLocator.registerService(IAlarmService.class, alarmService);
        when(alarmService.getPvNames()).thenReturn(Collections.singleton(pvName));
    }
    
}
