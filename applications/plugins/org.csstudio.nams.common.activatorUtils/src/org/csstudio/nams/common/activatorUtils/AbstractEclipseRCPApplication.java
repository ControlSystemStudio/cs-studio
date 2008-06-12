package org.csstudio.nams.common.activatorUtils;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * 
 * Note: There will be 2 instances of this class: BundleActivator and
 * IApplication; all sharing fields have to be static.
 * 
 * Note: It is forbidden to declare a constructor! (To ensure initialisation is
 * only done in the method ment for it!)
 * 
 * Default Application
 * 
 * <pre>
 * public class MyApplication extends {@link AbstractEclipseRCPApplication} {
 * 
 *     &#064;{@link ApplicationInitializer}
 *     public void initializeApplication(
 *         &#064;{@link OSGiService} MyService service, 
 *         &#064;{@link ExecutableEclipseRCPExtension}(extensionId = MyExtensionType.class)
 *             Object myExtension)
 *     {
 *         // Do s.th. Extension are only avail here!!!
 *     }
 *     
 *     &#064;{@link ApplicationStep}
 *     public ApplicationStepResult oneStepOfIteratingCallsToPerformApplicationsOperation(
 *         &#064;{@link OSGiService} MyService service ) throws {@link Throwable}
 *     {
 *         // Do one(!) step of work...
 *         
 *         // Continue work, call this method again...
 *         return ApplicationStepResult.CONTINUE;
 *     }
 *     
 *     &#064;{@link ApplicationCleanUpPoint}
 *     public void shutdownApplication(
 *         &#064;{@link OSGiService} MyService service )
 *     {
 *         // Do some clean ups...
 *     }
 *     
 *     &#064;{@link ApplicationStepExceptionHandler}
 *     public ApplicationStepResult handleExceptionInApplicationStep(
 *         Throwable occurred, 
 *         // optional additional params...
 *         &#064;{@link OSGiService} MyService service 
 *     ) throws {@link Throwable} // if can't be handled.
 *     {
 *         // handle exception/error...
 *         
 *         // Continue work, call step-method again... (or s.th. matching.)
 *         return ApplicationStepResult.CONTINUE;
 *     }
 * }
 * </pre>
 */
public abstract class AbstractEclipseRCPApplication implements BundleActivator,
		IApplication {

	static private BundleContext bundleContext;

	final public void start(final BundleContext bundleContext) throws Exception {
		AbstractEclipseRCPApplication.bundleContext = bundleContext;

	}

	public void stop(final BundleContext bundleContext) throws Exception {
		AbstractEclipseRCPApplication.bundleContext = null;
	}

	public Object start(IApplicationContext context) throws Exception {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet.");
	}

	public void stop() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet.");

	}

}
