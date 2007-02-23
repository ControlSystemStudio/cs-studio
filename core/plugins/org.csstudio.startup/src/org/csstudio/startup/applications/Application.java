/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.startup.applications;

import java.util.ArrayList;
import java.util.List;
import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.LocaleService;
import org.csstudio.platform.ui.workbench.CssWorkbenchAdvisor;
import org.csstudio.startup.ServiceProxy;
import org.csstudio.startup.StartupServiceEnumerator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The RCP application for the Control System Studio.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public class Application implements IPlatformRunnable {
	/**
	 * {@inheritDoc}
	 */
	public final Object run(final Object args) throws Exception {
		IPreferenceStore coreStore = new ScopedPreferenceStore(
				new InstanceScope(), CSSPlatformPlugin.getDefault().getBundle()
						.getSymbolicName());

		applyLocaleSetting(coreStore);
		
		Display display = PlatformUI.createDisplay();
		
		try {
//			boolean canLogin = handleLogin(display, coreStore);
//
			int returnCode = EXIT_OK;
//
//			if (canLogin) {
//				// create the workbench with this advisor and run it until it
//				// exits
//				// N.B. createWorkbench remembers the advisor, and also
//				// registers
//				// the workbench globally so that all UI plug-ins can find it
//				// using
//				// PlatformUI.getWorkbench() or AbstractUIPlugin.getWorkbench()
//				returnCode = PlatformUI.createAndRunWorkbench(display,
//						new CssWorkbenchAdvisor());
//
//				// the workbench doesn't support relaunch yet (bug 61809) so
//				// for now restart is used, and exit data properties are checked
//				// here to substitute in the relaunch return code if needed
//				if (returnCode != PlatformUI.RETURN_RESTART) {
//					return EXIT_OK;
//				}
//
//				// if the exit code property has been set to the relaunch code,
//				// then
//				// return that code now, otherwise this is a normal restart
//				return EXIT_RESTART;
//			}
			
			// we will run all the services
			// TODO: implement checking if there is a service the user
			// does not wish to run
			ServiceProxy[] proxies = StartupServiceEnumerator.getServices();
			List<ServiceProxy> lowPriorityProxy = new ArrayList<ServiceProxy>();
			
			
			for(ServiceProxy proxy : proxies) {
				if(proxy.isHighPriority())
					proxy.run();
				else
					lowPriorityProxy.add(proxy);
			}
			
			// TODO: implement this so that each low priority proxy
			// is created and ran in a separate thread
			for(ServiceProxy proxy : lowPriorityProxy) {
				// TODO: add thread creation code here!
				proxy.run();
			}

			returnCode = PlatformUI.createAndRunWorkbench(display,
					new CssWorkbenchAdvisor());
			
			if (returnCode != PlatformUI.RETURN_RESTART) {
				return EXIT_OK;
			}
					
			return EXIT_RESTART;
		} finally {
			if (display != null) {
				display.dispose();
			}
		}
	}

	/**
	 * Set the system's default locate according to the CSS settings.
	 * 
	 * @param coreStore
	 *            The core preference store.
	 */
	private void applyLocaleSetting(final IPreferenceStore coreStore) {
		String locale = coreStore.getString(LocaleService.PROP_LOCALE);
		LocaleService.setSystemLocale(locale);
	}
}
