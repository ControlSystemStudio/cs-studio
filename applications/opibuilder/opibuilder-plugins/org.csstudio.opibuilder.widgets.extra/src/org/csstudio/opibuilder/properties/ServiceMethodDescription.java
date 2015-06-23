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
    private String description;
    private Map<String, String> argumentPvs;
    private Map<String, String> resultPvs;

    /**
     * @param serviceMethod
     * @param argumentPvs
     * @param resultPvs
     */
    private ServiceMethodDescription(String service, String method,
        String description, Map<String, String> argumentPvs,
        Map<String, String> resultPvs) {
    this.service = service;
    this.method = method;
    this.description = description;
    this.argumentPvs = argumentPvs;
    this.resultPvs = resultPvs;
    }

    public static ServiceMethodDescription createServiceMethodDescription() {
    return new ServiceMethodDescription("", "", "",
        Collections.<String, String> emptyMap(),
        Collections.<String, String> emptyMap());
    }

    public static ServiceMethodDescription createServiceMethodDescription(
        String service, String method, String description,
        Map<String, String> argumentPvs, Map<String, String> resultPvs) {
    return new ServiceMethodDescription(service, method, description,
        new HashMap<String, String>(argumentPvs),
        new HashMap<String, String>(resultPvs));
    }

    public static ServiceMethodDescription createServiceMethodDescription(
        String service, ServiceMethod serviceMethod) {
    return new ServiceMethodDescription(service, serviceMethod.getName(),
        serviceMethod.getDescription(), new HashMap<String, String>(
            serviceMethod.getArgumentDescriptions()),
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the arguments
     */
    public Map<String, String> getArgumentPvs() {
    return argumentPvs;
    }

    /**
     * @param argumentPvs
     *            the argumentPvs to set
     */
    public void setArgumentPvs(Map<String, String> argumentPvs) {
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
    public Map<String, String> getResultPvs() {
    return resultPvs;
    }

    /**
     * @param resultPvs
     *            the resultPvs to set
     */
    public void setResultPvs(Map<String, String> resultPvs) {
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
