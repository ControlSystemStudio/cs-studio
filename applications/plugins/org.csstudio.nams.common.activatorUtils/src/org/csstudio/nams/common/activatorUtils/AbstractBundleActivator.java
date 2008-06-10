package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractBundleActivator implements BundleActivator {

	private static class RequestedParam {
		final boolean isOSGiServiceRequest;
		final boolean required;
		final Class<?> type;

		public RequestedParam(final Class<?> type, final boolean required,
				final boolean isOSGiServiceRequest) {
			this.type = type;
			this.required = required;
			this.isOSGiServiceRequest = isOSGiServiceRequest;
		}
	}

	static private Object[] evaluateParamValues(BundleContext context,
			RequestedParam[] requestedParams) {

		Object[] result = new Object[requestedParams.length];

		for (int paramIndex = 0; paramIndex < requestedParams.length; paramIndex++) {
			result[paramIndex] = getAvailableService(context,
					requestedParams[paramIndex].type);
			if (result[paramIndex] == null
					&& requestedParams[paramIndex].required) {
				throw new RuntimeException(
						"Unable to solve required param of type: "
								+ requestedParams[paramIndex].type.getName()
								+ "; service currently not avail in the OSGi service registry!");
			}
		}

		return result;
	}

	/**
	 * Finds the method annotated with {@link OSGiBundleActivationMethod}.
	 * 
	 * @throws RuntimeException
	 *             If no or more than one matching {@link Method} is present.
	 */
	static private <T extends Annotation> Method findAnnotatedMethod(Object objectToInspect, Class<T> annotationType) {
		Method result = null;

		Method[] methods = objectToInspect.getClass().getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(annotationType) != null) {
				if (result == null) {
					// if( method.getModifiers() ) // check public
					result = method;
				} else {
					throw new RuntimeException(
							"More than one Activator-start-method present!");
				}
			}
		}

		return result;
	}

	static private RequestedParam[] getAllRequestedMethodParams(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		RequestedParam[] result = new RequestedParam[parameterTypes.length];

		for (int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++) {
			Annotation[] annotationsOfParam = parameterAnnotations[paramIndex];
			Class<?> paramType = parameterTypes[paramIndex];

			boolean isOSGiService = false;
			boolean isRequired = false;
			for (Annotation annotation : annotationsOfParam) {
				if (annotation instanceof OSGiService) {
					isOSGiService = true;
				} else if (annotation instanceof Required) {
					isRequired = true;
				}
			}
			result[paramIndex] = new RequestedParam(paramType, isRequired,
					isOSGiService);
		}
		return result;
	}

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
	static private <T> T getAvailableService(BundleContext bundleContext,
			Class<T> requestedServiceType) throws ClassCastException {
		ServiceTracker serviceTracker = new ServiceTracker(bundleContext,
				requestedServiceType.getName(), null);
		serviceTracker.open();
		Object service = serviceTracker.getService();
		T result = null;
		if (service != null) {
			result = requestedServiceType.cast(service);
		}
		serviceTracker.close();
		return result;
	}

	final public void start(BundleContext context) throws Exception {
		Method bundleStartMethod = findAnnotatedMethod(this, OSGiBundleActivationMethod.class);
		
		if (bundleStartMethod == null) {
			throw new RuntimeException("No Activator-start-method present!");
		}
		
		RequestedParam[] requestedParams = getAllRequestedMethodParams(bundleStartMethod);

		// check is all requested params are valid...
		for (RequestedParam requestedParam : requestedParams) {
			// currently only OSGiService injection is supported
			if (!requestedParam.isOSGiServiceRequest) {
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
		Object[] paramValues = evaluateParamValues(context, requestedParams);
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
		Method bundleSopMethod = findAnnotatedMethod(this, OSGiBundleDeactivationMethod.class);
		RequestedParam[] requestedParams = getAllRequestedMethodParams(bundleSopMethod);

		// check is all requested params are valid...
		for (RequestedParam requestedParam : requestedParams) {
			// currently only OSGiService injection is supported
			if (!requestedParam.isOSGiServiceRequest) {
				throw new RuntimeException(
						"Can not inject not annotated param of type "
								+ requestedParam.type.getName()
								+ "; currently only OSGi service injection is supported.");
			}
		}

		// check if return value is void.
		Class<?> returnType = bundleSopMethod.getReturnType();
		if (!Void.TYPE.isAssignableFrom(returnType)) {
			throw new RuntimeException("Illegal return value of stop-method. "
					+ returnType.getName()
					+ "; currently only void is supported.");
		}

		// INVOKE
		Object[] paramValues = evaluateParamValues(context, requestedParams);
		bundleSopMethod.invoke(this, paramValues);
	}
}
