package org.csstudio.alarm.service.internal;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.junit.Test;

import com.cosylab.util.CommonException;

import static org.junit.Assert.*;

import static org.mockito.Matchers.*;
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
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        AlarmConnectionDALImpl connectionUnderTest = new AlarmConnectionDALImpl(simpleDALBroker);
        
        assertFalse(connectionUnderTest.canHandleTopics());
    }
    
    @Test
    public void testConnectDisconnectOne() throws Exception {
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        AlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(simpleDALBroker);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
        verify(connectionMonitor, times(1)).onConnect();
        verifyRegister(simpleDALBroker, "mypv", 1);
        
        connectionUnderTest.disconnect();
        verifyDeregister(simpleDALBroker, "mypv", 1);
        
        // disconnect is robust
        connectionUnderTest.disconnect();
        verifyDeregister(simpleDALBroker, "mypv", 1);
    }
    
    @Test
    public void testConnectAndRegister() throws Exception {
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        AlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(simpleDALBroker);
        
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
        
        verify(connectionMonitor, times(1)).onConnect();
        verifyRegister(simpleDALBroker, "mypv", 1);
        
        connectionUnderTest.registerPV("pv1");
        verifyRegister(simpleDALBroker, "mypv", 1);
        verifyRegister(simpleDALBroker, "pv1", 1);
        
        // Registering the 2nd time is ignored
        connectionUnderTest.registerPV("pv1");
        verifyRegister(simpleDALBroker, "mypv", 1);
        verifyRegister(simpleDALBroker, "pv1", 1);
        
        connectionUnderTest.registerPV("pv2");
        verifyRegister(simpleDALBroker, "mypv", 1);
        verifyRegister(simpleDALBroker, "pv1", 1);
        verifyRegister(simpleDALBroker, "pv2", 1);
        
        connectionUnderTest.deregisterPV("pv1");
        verifyDeregister(simpleDALBroker, "pv1", 1);
        
        connectionUnderTest.disconnect();
        verifyDeregister(simpleDALBroker, "mypv", 1);
        verifyDeregister(simpleDALBroker, "pv1", 1);
        verifyDeregister(simpleDALBroker, "pv2", 1);
    }
    
    @Test
    public void testReloadPVsFromResource() throws Exception {
        Set<String> pvSetStart = new HashSet<String>(Arrays.asList("pv1", "pv2", "pv3"));
        
        SimpleDALBroker simpleDALBroker = mock(SimpleDALBroker.class);
        TestAlarmConnectionDALImpl connectionUnderTest = new TestAlarmConnectionDALImpl(simpleDALBroker,
                                                                                        pvSetStart);
        connect(connectionUnderTest);
        verifyRegister(simpleDALBroker, "pv1", 1);
        verifyRegister(simpleDALBroker, "pv2", 1);
        verifyRegister(simpleDALBroker, "pv3", 1);
        
        Set<String> pvSetReload = new HashSet<String>(Arrays.asList("pv2", "pv3", "pv4", "pv5"));
        connectionUnderTest.setPvNames(pvSetReload);
        
        // the implementation takes care of the difference when de/registering
        connectionUnderTest.reloadPVsFromResource();
        verifyDeregister(simpleDALBroker, "pv1", 1);
        verifyDeregister(simpleDALBroker, "pv2", 0);
        verifyDeregister(simpleDALBroker, "pv3", 0);
        
        verifyRegister(simpleDALBroker, "pv1", 1);
        verifyRegister(simpleDALBroker, "pv2", 1);
        verifyRegister(simpleDALBroker, "pv3", 1);
        verifyRegister(simpleDALBroker, "pv4", 1);
        verifyRegister(simpleDALBroker, "pv5", 1);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void verifyRegister(@Nonnull final SimpleDALBroker simpleDALBroker,
                                @Nonnull final String pvName,
                                final int times) throws InstantiationException, CommonException {
        if (times == 0) {
            verify(simpleDALBroker, never()).registerListener(eq(newConnectionParameters(pvName)),
                                                              (DynamicValueListener) any(),
                                                              (Map<String, Object>) any());
            
        } else {
            verify(simpleDALBroker, times(times))
                    .registerListener(eq(newConnectionParameters(pvName)),
                                      (DynamicValueListener) any(),
                                      (Map<String, Object>) any());
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void verifyDeregister(@Nonnull final SimpleDALBroker simpleDALBroker,
                                  @Nonnull final String pvName,
                                  final int times) throws InstantiationException, CommonException {
        if (times == 0) {
            verify(simpleDALBroker, never())
                    .deregisterListener(eq(newConnectionParameters(pvName)),
                                        (DynamicValueListener) any(),
                                        (Map<String, Object>) any());
        } else {
            verify(simpleDALBroker, times(times))
                    .deregisterListener(eq(newConnectionParameters(pvName)),
                                        (DynamicValueListener) any(),
                                        (Map<String, Object>) any());
        }
    }
    
    private void connect(@Nonnull final AlarmConnectionDALImpl connectionUnderTest,
                         @Nonnull final IAlarmConnectionMonitor connectionMonitor) throws AlarmConnectionException {
        IAlarmListener listener = mock(IAlarmListener.class);
        AlarmResource resource = mock(AlarmResource.class);
        connectionUnderTest.connect(connectionMonitor, listener, resource);
    }
    
    private void connect(@Nonnull final AlarmConnectionDALImpl connectionUnderTest) throws AlarmConnectionException {
        IAlarmConnectionMonitor connectionMonitor = mock(IAlarmConnectionMonitor.class);
        connect(connectionUnderTest, connectionMonitor);
    }
    
    private ConnectionParameters newConnectionParameters(@Nonnull final String pvName) {
        return new ConnectionParameters(newRemoteInfo(pvName), String.class);
    }
    
    private RemoteInfo newRemoteInfo(@Nonnull final String pvName) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, null, null);
    }
    
    /**
     * Used for testing only. This subclass overrides the method in the object-under-test, which uses the eclipse
     * framework for retrieval of preferences and access to ldap or file system. This way this test can be run
     * as a simple unit test.
     */
    private static class TestAlarmConnectionDALImpl extends AlarmConnectionDALImpl {
        
        private Set<String> _pvSet;
        
        public TestAlarmConnectionDALImpl(@Nonnull final SimpleDALBroker simpleDALBroker) {
            this(simpleDALBroker, Collections.singleton("mypv"));
        }
        
        public TestAlarmConnectionDALImpl(@Nonnull final SimpleDALBroker simpleDALBroker,
                                          @Nonnull final Set<String> pvSet) {
            super(simpleDALBroker);
            _pvSet = pvSet;
        }
        
        protected void setPvNames(@Nonnull final Set<String> pvSet) {
            _pvSet = pvSet;
        }
        
        @Override
        protected Set<String> getPVNamesFromResource() throws AlarmConnectionException {
            return _pvSet;
        }
        
    }
    
}
