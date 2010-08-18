package org.remotercp.util.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiServiceLocatorUtil {

	/**
	 * FIXME (bknerr) : Major! Use service tracker.
	 *
	 * @deprecated This method incorrectly assumes that services are always
	 *             available when this method is called. Use the standard OSGi
	 *             <code>ServiceTracker</code> instead.
	 */
	@Deprecated
	public synchronized static <T> T getOSGiService(final BundleContext context, final Class<T> service)
			throws ClassCastException {

		// This is NOT a recommended way to use the ServiceTracker!

		final ServiceTracker serviceTracker = new ServiceTracker(context, service
				.getName(), null);

		serviceTracker.open();
		final T serviceObject = service.cast(serviceTracker.getService());
		serviceTracker.close();

		return serviceObject;
	}
}
