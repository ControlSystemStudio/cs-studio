package org.csstudio.nams.configurator.testutils;

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
	static private Method findBundleStartMethod(Object objectToInspect) {
		Method result = null;

		Method[] methods = objectToInspect.getClass().getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(OSGiBundleActivationMethod.class) != null) {
				if (result == null) {
//					if( method.getModifiers() ) // check public
					result = method;
				} else {
					throw new RuntimeException(
							"More than one Activator-start-method present!");
				}
			}
		}

		if (result == null) {
			throw new RuntimeException("No Activator-start-method present!");
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
		T result = requestedServiceType.cast(serviceTracker.getService());
		serviceTracker.close();
		return result;
	}

	final public void start(BundleContext context) throws Exception {
		Method bundleStartMethod = findBundleStartMethod(this);
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

		Object[] paramValues = evaluateParamValues(context, requestedParams);
		bundleStartMethod.invoke(this, paramValues);
	}

	final public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		// throw new RuntimeException("Not implemented yet.");

	}
}
