/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.management.internal.remotecommands;

import org.csstudio.platform.management.IManagementCommandService;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * Publishes the Management Command Service as a remote service via ECF.
 * 
 * @author Joerg Rathlev
 */
public final class ManagementCommandServiceLauncher implements
		IRemoteServiceLauncher {

	/**
	 * {@inheritDoc}
	 */
	public void startServices() {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				Activator.getDefault().getBundleContext(),
				ISessionService.class);
		IManagementCommandService managementService =
			OsgiServiceLocatorUtil.getOSGiService(
					Activator.getDefault().getBundleContext(),
					IManagementCommandService.class);
		sessionService.registerRemoteService(
				IManagementCommandService.class.getName(),
				managementService, null);
	}
}
