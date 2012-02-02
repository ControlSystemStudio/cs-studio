package org.csstudio.servicelocator;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.osgi.util.tracker.ServiceTracker;

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
        final MyService myService = new MyService();
        ServiceLocator.registerService(IService.class, myService);

        final IService service = ServiceLocator.getService(IService.class);
        assertSame(myService, service);
    }

    @Test
    public void testRegisterServiceFromTracker() {
        final MyService myService = new MyService();
        final ServiceTracker myServiceTracker = mock(ServiceTracker.class);
        when(myServiceTracker.getService()).thenReturn(myService);
        ServiceLocator.registerServiceTracker(IService.class, myServiceTracker);

        final IService service = ServiceLocator.getService(IService.class);
        assertSame(myService, service);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetServiceFailsWithoutRegistration() {
        ServiceLocator.getService(IService.class);
    }

    /**
     * example service
     */
    private interface IService {
        // nothing to implement
    }

    /**
     * example service implementation
     */
    private static class MyService implements IService {

        public MyService() {
            // TODO Auto-generated constructor stub
        }
        // nothing to implement
    }

}
