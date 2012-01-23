package org.csstudio.alarm.service.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for the DAL implementation of the alarm connection
 * 
 * @author jpenning
 * @since 08.11.2010
 */
public class AlarmConnectionDALImplUnitTest {
    
    @Test
    public void testCreate() throws Exception {
        IAlarmConfigurationService alarmConfigService = mock(IAlarmConfigurationService.class);
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        AlarmConnectionDALImpl connectionUnderTest = new AlarmConnectionDALImpl(alarmConfigService, simpleDALBroker);
        
        assertFalse(connectionUnderTest.canHandleTopics());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testConnectDisconnectOne() throws Exception {
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        IAlarmConfigurationService alarmConfigService = mock(IAlarmConfigurationService.class);
        AlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(alarmConfigService, simpleDALBroker);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
        verify(connectionMonitor, times(1)).onConnect();
        verify(simpleDALBroker, times(1)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());

        connectionUnderTest.disconnect();
        verify(simpleDALBroker, times(1)).deregisterListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());

        // disconnect is robust
        connectionUnderTest.disconnect();
        verify(simpleDALBroker, times(1)).deregisterListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testConnectAndRegister() throws Exception {
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        IAlarmConfigurationService alarmConfigService = mock(IAlarmConfigurationService.class);
        AlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(alarmConfigService, simpleDALBroker);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);

        verify(connectionMonitor, times(1)).onConnect();
        verify(simpleDALBroker, times(1)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());

        connectionUnderTest.registerPV("pv1");
        verify(simpleDALBroker, times(2)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());

        // Registering the 2nd time is ignored, because the listener will deliver to all nodes in the tree
        connectionUnderTest.registerPV("pv1");
        verify(simpleDALBroker, times(2)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
        
        connectionUnderTest.registerPV("pv2");
        verify(simpleDALBroker, times(3)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
        
        connectionUnderTest.deregisterPV("pv1");
        verify(simpleDALBroker, times(1)).deregisterListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
        
        
        connectionUnderTest.disconnect();
        verify(simpleDALBroker, times(3)).deregisterListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
    }

    private void connect(@Nonnull final AlarmConnectionDALImpl connectionUnderTest,
                         @Nonnull final IAlarmConnectionMonitor connectionMonitor) throws AlarmConnectionException {
        IAlarmListener listener = mock(IAlarmListener.class);
        IAlarmResource resource = mock(IAlarmResource.class);
        connectionUnderTest.connect(connectionMonitor, listener, resource);
    }

    /**
     * Used for testing only. This subclass overrides the method in the object-under-test, which uses the eclipse
     * framework for retrieval of preferences and access to ldap or file system. This way this test can be run
     * as a simple unit test.
     */
    private static class TestAlarmConnectionDALImpl extends AlarmConnectionDALImpl {

        public TestAlarmConnectionDALImpl(@Nonnull final IAlarmConfigurationService alarmConfigService,
                                             @Nonnull final SimpleDALBroker simpleDALBroker) {
            super(alarmConfigService, simpleDALBroker);
        }
        
        @Override
        protected Set<String> getPVNamesFromResource() throws AlarmConnectionException {
            return Collections.singleton("mypv");
        }
        
    }
    
}
