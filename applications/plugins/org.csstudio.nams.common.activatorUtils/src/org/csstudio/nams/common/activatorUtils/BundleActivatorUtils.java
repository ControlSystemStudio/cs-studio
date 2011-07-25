
/* 
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.common.activatorUtils;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A set of utilities to be used by bundle activators
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1-2008-04-28: Created.
 * @deprecated Use {@link AbstractBundleActivator} as superclass of activator
 *             instead!
 */
@Deprecated
public final class BundleActivatorUtils {

	/**
	 * Gets the currently avail service of requested type from the bundle
	 * contexts service registry using the full
	 * {@link Class#getName() qualified class name} of requested service type as
	 * Id.
	 * 
	 * @param <T>
	 *            The local type var to identify the service instance.
	 * @param bundleContext
	 *            The context that registry is to be searched.
	 * @param requestedServiceType
	 *            The requested service type.
	 * @return The currently avail service or null if currently not avail.
	 * @throws ClassCastException
	 *             If a found service registered on the full qualified class
	 *             name is
	 *             {@linkplain Class#isAssignableFrom(Class) not assignable} to
	 *             the requested type.
	 */
	static public <T> T getAvailableService(final BundleContext bundleContext,
			final Class<T> requestedServiceType) throws ClassCastException {
		final ServiceTracker serviceTracker = new ServiceTracker(bundleContext,
				requestedServiceType.getName(), null);
		serviceTracker.open();
		final T result = requestedServiceType.cast(serviceTracker.getService());
		serviceTracker.close();
		return result;
	}

	private BundleActivatorUtils() {
		// Ensure no instantation of this class.
	}
}
