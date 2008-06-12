package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

final class AnnotatedActivatorUtils {
	private AnnotatedActivatorUtils() {
		// Ensure no instantation of this class.
	}

	static public Object[] evaluateParamValues(BundleContext context,
			RequestedParam[] requestedParams) {
	
		Object[] result = new Object[requestedParams.length];
	
		for (int paramIndex = 0; paramIndex < requestedParams.length; paramIndex++) {
			if (RequestedParam.RequestType.OSGiServiceRequest
					.equals(requestedParams[paramIndex].requestType)) {
				result[paramIndex] = AnnotatedActivatorUtils.getAvailableService(context,
						requestedParams[paramIndex].type);
				if (result[paramIndex] == null
						&& requestedParams[paramIndex].required) {
					throw new RuntimeException(
							"Unable to solve required param of type: "
									+ requestedParams[paramIndex].type
											.getName()
									+ "; service currently not avail in the OSGi service registry!");
				}
			} else if (RequestedParam.RequestType.ExecuteableExtensionRequest
					.equals(requestedParams[paramIndex].requestType)) {
				result[paramIndex] = AnnotatedActivatorUtils.getExecuteableExtension((ExecutableEclipseRCPExtension)requestedParams[paramIndex].annotation);
				if (result[paramIndex] == null
						&& requestedParams[paramIndex].required) {
					throw new RuntimeException(
							"Unable to solve required param of type: "
									+ requestedParams[paramIndex].type
											.getName()
									+ "; extension currently not avail in the extension registry!");
				}
			} else {
				throw new RuntimeException("unsupported request type: "
						+ requestedParams[paramIndex].requestType);
			}
		}
	
		return result;
	}

	static public Object getExecuteableExtension(ExecutableEclipseRCPExtension annotation) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(annotation.extensionId().getName());
			if( ! (elements.length == 1) ) {
				// TODO Decide about using:
	//			throw new RuntimeException(
	//			"One and only one extension for extension point \""
	//					+ id
	//					+ "\" should be present in current runtime configuration!");
				
				return null;
			}
			
			Object result;
			try {
				result = elements[0].createExecutableExtension(annotation.executeableName());
			} catch (CoreException e) {
				throw new RuntimeException("unable to create extension", e);
			}
			
			return result;
		}

	/**
	 * Finds the method annotated with {@link OSGiBundleActivationMethod}.
	 * 
	 * @throws RuntimeException
	 *             If no or more than one matching {@link Method} is present.
	 */
	static public <T extends Annotation> Method findAnnotatedMethod(
			Object objectToInspect, Class<T> annotationType) {
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

	static public RequestedParam[] getAllRequestedMethodParams(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	
		RequestedParam[] result = new RequestedParam[parameterTypes.length];
	
		for (int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++) {
			Annotation[] annotationsOfParam = parameterAnnotations[paramIndex];
			Class<?> paramType = parameterTypes[paramIndex];
	
			RequestedParam.RequestType requestType = null;
			boolean isRequired = false;
			Annotation requestAnnotation = null;
			for (Annotation annotation : annotationsOfParam) {
				if (annotation instanceof OSGiService) {
					requestType = RequestedParam.RequestType.OSGiServiceRequest;
					requestAnnotation = annotation;
				} else if (annotation instanceof ExecutableEclipseRCPExtension) {
					requestType = RequestedParam.RequestType.ExecuteableExtensionRequest;
					requestAnnotation = annotation;
				} else if (annotation instanceof Required) {
					isRequired = true;
				}
			}
			result[paramIndex] = new RequestedParam(paramType, isRequired,
					requestType, requestAnnotation);
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
	static public <T> T getAvailableService(BundleContext bundleContext,
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
	
	
}
