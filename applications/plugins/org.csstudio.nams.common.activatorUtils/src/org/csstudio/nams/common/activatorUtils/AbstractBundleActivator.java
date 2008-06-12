package org.csstudio.nams.common.activatorUtils;

import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public abstract class AbstractBundleActivator implements BundleActivator {

	final public void start(BundleContext context) throws Exception {
		Method bundleStartMethod = AnnotatedActivatorUtils.findAnnotatedMethod(this,
				OSGiBundleActivationMethod.class);

		if (bundleStartMethod == null) {
			throw new RuntimeException("No Activator-start-method present! (start-method has to be annotated with: @OSGiBundleActivationMethod)");
		}

		RequestedParam[] requestedParams = AnnotatedActivatorUtils.getAllRequestedMethodParams(bundleStartMethod);

		// check is all requested params are valid...
		for (RequestedParam requestedParam : requestedParams) {
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
		Class<?> returnType = bundleStartMethod.getReturnType();
		if (!Void.TYPE.isAssignableFrom(returnType)) {
			if (!OSGiServiceOffers.class.isAssignableFrom(returnType)) {
				throw new RuntimeException(
						"illegal return value of start-method. "
								+ returnType.getName()
								+ "; currently only OSGiServiceOffers and void is supported.");
			}
		}

		// INVOKE
		Object[] paramValues = AnnotatedActivatorUtils.evaluateParamValues(context, requestedParams);
		Object result = bundleStartMethod.invoke(this, paramValues);

		if (result != null
				&& OSGiServiceOffers.class.isAssignableFrom(result.getClass())) {
			OSGiServiceOffers offers = (OSGiServiceOffers) result;
			for (Class<?> key : offers.keySet()) {
				Object service = offers.get(key);
				if (service == null) {
					throw new RuntimeException(
							"illegal service offer for type. " + key.getName()
									+ "; offer may not be null.");
				}
				context.registerService(key.getName(), service, null);
			}
		}
	}

	final public void stop(BundleContext context) throws Exception {
		Method bundleSopMethod = AnnotatedActivatorUtils.findAnnotatedMethod(this,
				OSGiBundleDeactivationMethod.class);

		if (bundleSopMethod != null) {
			RequestedParam[] requestedParams = AnnotatedActivatorUtils.getAllRequestedMethodParams(bundleSopMethod);

			// check is all requested params are valid...
			for (RequestedParam requestedParam : requestedParams) {
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
			Class<?> returnType = bundleSopMethod.getReturnType();
			if (!Void.TYPE.isAssignableFrom(returnType)) {
				throw new RuntimeException(
						"Illegal return value of stop-method. "
								+ returnType.getName()
								+ "; currently only void is supported.");
			}

			// INVOKE
			Object[] paramValues = AnnotatedActivatorUtils.evaluateParamValues(context, requestedParams);
			bundleSopMethod.invoke(this, paramValues);
		}
	}
}
