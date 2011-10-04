
package org.csstudio.nams.common.activatorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
 *     public {@link OSGiServiceOffers} initializeApplication
 *             throws {@link InitialisationFailedError} (
 *         &#064;{@link OSGiService} MyService service, 
 *         &#064;{@link ExecutableEclipseRCPExtension}(extensionId = MyExtensionType.class)
 *             Object myExtension)
 *     {
 *         // Do s.th. Extension are only avail here!!!
 *     }
 *     
 *     &#064;{@link ApplicationReInitializer}
 *     public {@link OSGiServiceOffers} updateConfiguration
 *             throws {@link InitialisationFailedError} (
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
 *     &#064;{@link ApplicationShutdownMethod}
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

	/**
	 * This method should only be called by the Eclipse-framework; do never call
	 * it directly!
	 */
	final public void start(final BundleContext bundleContext) throws Exception {
		AbstractEclipseRCPApplication.bundleContext = bundleContext;

	}

	/**
	 * This method should only be called by the Eclipse-framework; do never call
	 * it directly!
	 */
	final public Object start(final IApplicationContext context)
			throws Exception {

		// Run Steps....
		final Method applicationStepperMethod = AnnotatedActivatorUtils
				.findAnnotatedMethod(this, ApplicationStep.class);

		// check result
		final Class<?> applicationStartMethodReturnType = applicationStepperMethod
				.getReturnType();
		if (!Void.TYPE.equals(applicationStartMethodReturnType)) {
			if (!ApplicationStepResult.class
					.equals(applicationStartMethodReturnType)) {
				throw new RuntimeException(
						"Invalid return type of application-start-method: \""
								+ applicationStartMethodReturnType.getName()
								+ "\"; only "
								+ ApplicationStepResult.class.getName()
								+ " and void are supportet.");
			}
		}

		// get params...
		final RequestedParam[] requestedParams = AnnotatedActivatorUtils
				.getAllRequestedMethodParams(applicationStepperMethod);

		// check is all requested params are valid...
		for (final RequestedParam requestedParam : requestedParams) {
			// currently only OSGiService injection is supported
			if (!RequestedParam.RequestType.OSGiServiceRequest
					.equals(requestedParam.requestType)) {
				throw new RuntimeException(
						"Can not inject not annotated param of type "
								+ requestedParam.type.getName()
								+ "; currently only OSGi service injection is supported.");
			}
		}

		// INVOKE
		final Object[] paramValues = AnnotatedActivatorUtils
				.evaluateParamValues(
						AbstractEclipseRCPApplication.bundleContext,
						requestedParams);

		ApplicationStepResult stepResult = null;
		do {
			try {
				stepResult = (ApplicationStepResult) applicationStepperMethod
						.invoke(this, paramValues);
			} catch (final InvocationTargetException wrappedApplicationStepError) {
				final Throwable applicationStepError = wrappedApplicationStepError
						.getCause();
				// TODO If Exception handler is avail, call exception handler
				// with
				// applicationStepError as parameter or if unavail stop stepping
				// and
				// continue throwing to the framework.
			}
		} while (ApplicationStepResult.CONTINUE.equals(stepResult));

		// Analyzes result and perform...
		// TODO Handle reconfigure ...
		Object iAppResult = null;
		if (ApplicationStepResult.DONE.equals(stepResult)) {
			iAppResult = IApplication.EXIT_OK;
		} else if (ApplicationStepResult.RELAUNCH.equals(stepResult)) {
			iAppResult = IApplication.EXIT_RELAUNCH;
		}
		if (ApplicationStepResult.RESTART.equals(stepResult)) {
			iAppResult = IApplication.EXIT_RESTART;
		}
		return iAppResult;
	}

	/**
	 * This method should only be called by the Eclipse-framework; do never call
	 * it directly!
	 */
	final public void stop() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet.");

	}

	/**
	 * This method should only be called by the Eclipse-framework; do never call
	 * it directly!
	 */
	final public void stop(final BundleContext bundleContext) throws Exception {
		AbstractEclipseRCPApplication.bundleContext = null;
	}

}
