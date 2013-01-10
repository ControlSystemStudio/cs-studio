package org.csstudio.servicelocator;

import org.junit.Before;
import org.junit.Test;
import org.osgi.util.tracker.ServiceTracker;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Test of the service locator
 * 
 * @author jpenning
 * @since 03.04.2012
 */
public class ServiceLocatorUnitTest {
    
    @Before
    public void setUp() {
        ServiceLocator.reset();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testAccessAfterCloseIsDetected() {
        ServiceLocator.registerService(IService.class, new MyService());
        ServiceLocator.getService(IService.class);
        ServiceLocator.close();
        ServiceLocator.getService(IService.class);
    }
    
    @Test
    public void testRegisterService() {
        MyService myService = new MyService();
        ServiceLocator.registerService(IService.class, myService);
        
        IService service = ServiceLocator.getService(IService.class);
        assertSame(myService, service);
    }
    
    @SuppressWarnings({ "unchecked" })
    @Test
    public void testRegisterServiceFromTracker() {
        final MyService myService = new MyService();
        final ServiceTracker<IService, Object> myServiceTracker = mock(ServiceTracker.class);
        when(myServiceTracker.getService()).thenReturn(myService);
        ServiceLocator.registerServiceTracker(IService.class, myServiceTracker);
        
        final IService service = ServiceLocator.getService(IService.class);
        assertSame(myService, service);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testGetServiceFailsWithoutRegistration() {
        ServiceLocator.getService(IService.class);
    }
    
    @Test
    public void testGetServiceReturnsNullIfNotAvailable() {
        ServiceLocator.registerService(IService.class, null);
        IService service = ServiceLocator.getService(IService.class);
        assertNull(service);
    }
    
    /**
     * example service
     */
    private static interface IService {
        // nothing to implement
    }
    
    /**
     * example service implementation
     */
    private static class MyService implements IService {
        
        public MyService() {
            // nothing to implement
        }
    }
    
}
