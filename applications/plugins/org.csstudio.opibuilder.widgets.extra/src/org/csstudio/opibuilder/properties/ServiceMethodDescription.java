/**
 * 
 */
package org.csstudio.opibuilder.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethod.DataDescription;

/**
 * @author shroffk
 * 
 */

public class ServiceMethodDescription {

    private String service;
    private String method;
    private String description;
    private Map<String, DataDescription> argumentPvs;
    private Map<String, DataDescription> resultPvs;

    /**
     * @param serviceMethod
     * @param argumentPvs
     * @param resultPvs
     */
    private ServiceMethodDescription(String service, String method,
	    String description, Map<String, DataDescription> argumentPvs,
	    Map<String, DataDescription> resultPvs) {
	this.service = service;
	this.method = method;
	this.description = description;
	this.argumentPvs = argumentPvs;
	this.resultPvs = resultPvs;
    }

    public static ServiceMethodDescription createServiceMethodDescription() {
	return new ServiceMethodDescription("", "", "",
		Collections.<String, DataDescription> emptyMap(),
		Collections.<String, DataDescription> emptyMap());
    }

    public static ServiceMethodDescription createServiceMethodDescription(
	    String service, String method, String description,
	    Map<String, DataDescription> argumentPvs, Map<String, DataDescription> resultPvs) {
	return new ServiceMethodDescription(service, method, description,
		new HashMap<String, DataDescription>(argumentPvs),
		new HashMap<String, DataDescription>(resultPvs));
    }

    public static ServiceMethodDescription createServiceMethodDescription(
	    String service, ServiceMethod serviceMethod) {
	return new ServiceMethodDescription(service, serviceMethod.getName(),
		serviceMethod.getDescription(),
		serviceMethod.getArgumentMap(),
		serviceMethod.getResultMap());
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the arguments
     */
    public Map<String, DataDescription> getArgumentPvs() {
	return argumentPvs;
    }

    /**
     * @param argumentPvs
     *            the argumentPvs to set
     */
    public void setArgumentPvs(Map<String, DataDescription> argumentPvs) {
	if (this.argumentPvs.keySet().equals(argumentPvs.keySet())) {
	    this.argumentPvs = argumentPvs;
	} else {
	    throw new IllegalArgumentException(
		    "Invalid set of arguments for method " + service + "/"
			    + method);
	}

    }

    /**
     * @return the results
     */
    public Map<String, DataDescription> getResultPvs() {
	return resultPvs;
    }

    /**
     * @param resultPvs
     *            the resultPvs to set
     */
    public void setResultPvs(Map<String, DataDescription> resultPvs) {
	if (this.resultPvs.keySet().equals(resultPvs.keySet())) {
	    this.resultPvs = resultPvs;
	} else {
	    throw new IllegalArgumentException(
		    "Invalid set of results for method " + service + "/"
			    + method);
	}
    }

    /**
     * 
     * @param argName
     * @param value
     */
    public void setArgumentPv(String argName, DataDescription value) {
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
    public void setResultPv(String resultName, DataDescription value) {
	if (resultPvs.containsKey(resultName)) {
	    resultPvs.put(resultName, value);
	} else {
	    throw new IllegalArgumentException("result " + resultName
		    + " does not exist for service method " + service + "/"
		    + method);
	}
    }

}
