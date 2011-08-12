
/**
 * 
 */

package org.csstudio.nams.common.activatorUtils;

import java.lang.annotation.Annotation;

@SuppressWarnings("hiding")
class RequestedParam {
	
    public static enum RequestType {
		OSGiServiceRequest, ExecuteableExtensionRequest
	}

	final boolean required;
	final Class<?> type;
	final RequestedParam.RequestType requestType;
	final Annotation annotation;

	public RequestedParam(final Class<?> type, final boolean required,
			final RequestedParam.RequestType requestType,
			final Annotation annotation) {
		this.type = type;
		this.required = required;
		this.requestType = requestType;
		this.annotation = annotation;
	}
}