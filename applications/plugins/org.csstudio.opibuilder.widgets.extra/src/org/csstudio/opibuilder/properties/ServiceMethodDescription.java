/**
 * 
 */
package org.csstudio.opibuilder.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.epics.pvmanager.service.ServiceMethod;

/**
 * @author shroffk
 * 
 */

public class ServiceMethodDescription {

    private String service;
    private String method;
    private Map<String, String> argumentPvs;
    private Map<String, String> resultPvs;

    /**
     * @param serviceMethod
     * @param argumentPvs
     * @param resultPvs
     */
    private ServiceMethodDescription(String service, String method,
	    Map<String, String> argumentPvs, Map<String, String> resultPvs) {
	this.service = service;
	this.method = method;
	this.argumentPvs = argumentPvs;
	this.resultPvs = resultPvs;
    }

    public static ServiceMethodDescription createServiceMethodDescription() {
	return new ServiceMethodDescription("", "",
		Collections.<String, String> emptyMap(),
		Collections.<String, String> emptyMap());
    }

    public static ServiceMethodDescription createServiceMethodDescription(
	    String service, String method, Map<String, String> argumentPvs, Map<String, String> resultPvs) {
	return new ServiceMethodDescription(service, method,
		new HashMap<String, String>(argumentPvs),
		new HashMap<String, String>(resultPvs));
    }
    
    public static ServiceMethodDescription createServiceMethodDescription(
	    String service, ServiceMethod serviceMethod) {
	return new ServiceMethodDescription(service, serviceMethod.getName(),
		new HashMap<String, String>(serviceMethod
			.getArgumentDescriptions()),
		new HashMap<String, String>(serviceMethod
			.getResultDescriptions()));
    }

    /**
     * @return the service
     */
    public String getService() {
	return service;
    }
    
    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the arguments
     */
    public Map<String, String> getArgumentPvs() {
	return argumentPvs;
    }

    /**
     * @return the results
     */
    public Map<String, String> getResultPvs() {
	return resultPvs;
    }

    /**
     * 
     * @param argName
     * @param value
     */
    public void setArgumentPv(String argName, String value) {
	if (argumentPvs.containsKey(argName)) {
	    argumentPvs.put(argName, value);
	} else {
	    throw new IllegalArgumentException("argument " + argName
		    + " does not exist for service method " + service + "/"
		    + method);
	}
    }

    /**
     * 
     * @param resultName
     * @param value
     */
    public void setResultPv(String resultName, String value) {
	if (resultPvs.containsKey(resultName)) {
	    resultPvs.put(resultName, value);
	} else {
	    throw new IllegalArgumentException("result " + resultName
		    + " does not exist for service method " + service + "/"
		    + method);
	}
    }

}
