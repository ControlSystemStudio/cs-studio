package org.csstudio.nams.common.activatorUtils;

import java.lang.reflect.Method;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public abstract class AbstractBundleActivator implements BundleActivator {

    protected static AbstractBundleActivator instance;

    protected GenericServiceTracker<ISessionService> _genericServiceTracker;
    
    public static AbstractBundleActivator getDefault() {
        return instance;
    }
    
	@Override
    final public void start(final BundleContext context) throws Exception {
		
	    instance = this;
	    
	    final Method bundleStartMethod = AnnotatedActivatorUtils
				.findAnnotatedMethod(this, OSGiBundleActivationMethod.class);

		if (bundleStartMethod == null) {
			throw new RuntimeException(
					"No Activator-start-method present! (start-method has to be annotated with: @OSGiBundleActivationMethod)");
		}

		final RequestedParam[] requestedParams = AnnotatedActivatorUtils
				.getAllRequestedMethodParams(bundleStartMethod);

		// check is all requested params are valid...
		for (final RequestedParam requestedParam : requestedParams) {
			// currently only OSGiService injection is supported
			if (!(RequestedParam.RequestType.OSGiServiceRequest
					.equals(requestedParam.requestType) || RequestedParam.RequestType.ExecuteableExtensionRequest
					.equals(requestedParam.requestType))) {
				throw new RuntimeException(
						"Can not inject not annotated param of type "
								+ requestedParam.type.getName()
								+ "; currently only OSGi service injection is supported.");
			}
		}

		// check if optional return value is valid.
		final Class<?> returnType = bundleStartMethod.getReturnType();
		if (!Void.TYPE.isAssignableFrom(returnType)) {
			if (!OSGiServiceOffers.class.isAssignableFrom(returnType)) {
				throw new RuntimeException(
						"illegal return value of start-method. "
								+ returnType.getName()
								+ "; currently only OSGiServiceOffers and void is supported.");
			}
		}

		// For XMPP login
        _genericServiceTracker = new GenericServiceTracker<ISessionService>(
                context, ISessionService.class);
        _genericServiceTracker.open();

		// INVOKE
		final Object[] paramValues = AnnotatedActivatorUtils
				.evaluateParamValues(context, requestedParams);
		final Object result = bundleStartMethod.invoke(this, paramValues);

		if ((result != null)
				&& OSGiServiceOffers.class.isAssignableFrom(result.getClass())) {
			final OSGiServiceOffers offers = (OSGiServiceOffers) result;
			for (final Class<?> key : offers.keySet()) {
				final Object service = offers.get(key);
				if (service == null) {
					throw new RuntimeException(
							"illegal service offer for type. " + key.getName()
									+ "; offer may not be null.");
				}
				context.registerService(key.getName(), service, null);
			}
		}
	}

	@Override
    final public void stop(final BundleContext context) throws Exception {
		final Method bundleSopMethod = AnnotatedActivatorUtils
				.findAnnotatedMethod(this, OSGiBundleDeactivationMethod.class);

		if (bundleSopMethod != null) {
			final RequestedParam[] requestedParams = AnnotatedActivatorUtils
					.getAllRequestedMethodParams(bundleSopMethod);

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

			// check if return value is void.
			final Class<?> returnType = bundleSopMethod.getReturnType();
			if (!Void.TYPE.isAssignableFrom(returnType)) {
				throw new RuntimeException(
						"Illegal return value of stop-method. "
								+ returnType.getName()
								+ "; currently only void is supported.");
			}

			// INVOKE
			final Object[] paramValues = AnnotatedActivatorUtils
					.evaluateParamValues(context, requestedParams);
			bundleSopMethod.invoke(this, paramValues);
		}
	}
	
    public void addSessionServiceListener(
                IGenericServiceListener<ISessionService> sessionServiceListener) {
	              _genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
