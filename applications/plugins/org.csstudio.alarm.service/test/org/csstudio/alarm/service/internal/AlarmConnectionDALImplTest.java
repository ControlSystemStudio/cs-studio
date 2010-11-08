package org.csstudio.alarm.service.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for the DAL implementation of the alarm connection
 * 
 * @author jpenning
 * @since 08.11.2010
 */
public class AlarmConnectionDALImplTest {
    
    @Test
    public void testCreate() throws Exception {
        IAlarmConfigurationService alarmConfigService = mock(IAlarmConfigurationService.class);
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        AlarmConnectionDALImpl connection = new AlarmConnectionDALImpl(alarmConfigService, simpleDALBroker);
        
        assertFalse(connection.canHandleTopics());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConnectDisconnectOne() throws Exception {
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        IAlarmConfigurationService alarmConfigService = mock(IAlarmConfigurationService.class);
        
        // object under test
        AlarmConnectionDALImpl connection = new AlarmConnectionDALImplForTest(alarmConfigService, simpleDALBroker);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        IAlarmListener listener = mock(IAlarmListener.class);
        IAlarmResource resource = mock(IAlarmResource.class);
        connection.connectWithListenerForResource(connectionMonitor, listener, resource);

        connection.disconnect();

        verify(connectionMonitor, times(1)).onConnect();
        verify(simpleDALBroker, times(1)).registerListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
        verify(simpleDALBroker, times(1)).deregisterListener((ConnectionParameters) any(), (DynamicValueListener) any(), (Map<String, Object>) any());
    }
    
    /**
     * Used for testing only. This subclass overrides the method in the object-under-test, which uses the eclipse
     * framework for retrieval of preferences and access to ldap or file system. This way this test can be run
     * as a simple unit test.
     */
    private static class AlarmConnectionDALImplForTest extends AlarmConnectionDALImpl {

        public AlarmConnectionDALImplForTest(@Nonnull final IAlarmConfigurationService alarmConfigService,
                                             @Nonnull final SimpleDALBroker simpleDALBroker) {
            super(alarmConfigService, simpleDALBroker);
        }
        
        @Override
        protected Set<String> getPVNamesFromResource(@Nonnull final IAlarmResource resource) throws AlarmConnectionException {
            return Collections.singleton("mypv");
        }
        
    }
    
}
