package org.csstudio.nams.common.plugin.utils;

import static org.junit.Assert.assertEquals;

import org.csstudio.nams.common.activatorUtils.BundleActivatorUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class BundleActivatorUtils_Test {

	@Test
	public void testGetCurrentAvailService() throws Throwable {
		assertEquals(
				"org.csstudio.nams.common.plugin.utils.BundleActivatorUtils_Test$TestService",
				TestService.class.getName());

		// ServiceReference serviceReference = EasyMock
		// .createMock(ServiceReference.class);

		Filter filter = EasyMock.createMock(Filter.class);

		BundleContext bundleContext = EasyMock.createMock(BundleContext.class);
		EasyMock
				.expect(
						bundleContext
								.createFilter("(objectClass=org.csstudio.nams.common.plugin.utils.BundleActivatorUtils_Test$TestService)"))
				.andReturn(filter).anyTimes();

		// .expect(
		// bundleContext
		// .getServiceReference("org.csstudio.nams.common.plugin.utils.BundleActivatorUtils_Test$TestService"))
		// .andReturn(serviceReference).anyTimes();

		EasyMock.replay(filter, bundleContext);

		BundleActivatorUtils.getAvailableService(bundleContext,
				TestService.class);

		EasyMock.verify(filter, bundleContext);
	}

	/**
	 * A Test Service declaration.
	 */
	public interface TestService {
		public void aMethod();
	}

	/**
	 * A Test Service definition.
	 */
	public class TestServiceImpl implements TestService {
		public void aMethod() {
			// Do nothing.
		}
	}
}
