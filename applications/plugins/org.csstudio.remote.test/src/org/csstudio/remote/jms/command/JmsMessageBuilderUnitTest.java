package org.csstudio.remote.jms.command;


import javax.jms.MapMessage;
import javax.jms.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class JmsMessageBuilderUnitTest {
    
    private JmsMessageBuilder _builderUnderTest;
    private Session _session;
    private MapMessage _message;
    
    @Before
    public void setUp() throws Exception {
        _builderUnderTest = new JmsMessageBuilder();
        _session = mock(Session.class);
        _message = mock(MapMessage.class);
        when(_session.createMapMessage()).thenReturn(_message);
    }
    
    @After
    public void tearDown() throws Exception {
        _builderUnderTest = null;
    }
    
    @Test
    public void testCreation() throws Exception {
        _builderUnderTest.build(_session);
        
        verify(_message).setString("TYPE", "command");
        verify(_message).setString("GROUP", ClientGroup.UNDEFINED.toString());
        verify(_message).setString("NAME", "");
        verify(_message).setString(eq("EVENTTIME"), anyString());
    }
    
    @Test
    public void testSetGroup() throws Exception {
        _builderUnderTest.setGroup(ClientGroup.DESY_MKK);
        _builderUnderTest.build(_session);
        
        verify(_message).setString("GROUP", ClientGroup.DESY_MKK.toString());
    }

    @Test
    public void testSetCommand() throws Exception {
        _builderUnderTest.setCommand("myCommand");
        _builderUnderTest.build(_session);
        
        verify(_message).setString("NAME", "myCommand");
    }

    @Test
    public void testSetProperty() throws Exception {
        _builderUnderTest.setProperty("myKey", "myValue");
        _builderUnderTest.build(_session);
        
        verify(_message).setString("myKey", "myValue");
    }
}
