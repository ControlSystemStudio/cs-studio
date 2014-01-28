/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.epics.pvmanager.service.ServiceDescription;

/**
 * The description on how to construct an exec service.
 * <p>
 * This class encapsulate the description of a service, including:
 * <ul>
 *   <li>A number of commands</li>
 *   <li>The arguments for each command and how should they be mapped</li>
 *   <li>The results of each command</li>
 * </ul>
 *
 * @author carcassi
 */
public class ExecServiceDescription {
    
    final ServiceDescription serviceDescription;
    ExecutorService executorService;
    String shell = GenericExecServiceMethod.defaultShell();
    String shellArg = GenericExecServiceMethod.defaultShellArg();
    private List<ExecServiceMethodDescription> execServiceMethodDescriptions = new ArrayList<>();
    
    /**
     * A new service description with the given service name and description.
     * 
     * @param name the name of the service
     * @param description a brief description
     */
    public ExecServiceDescription(String name, String description) {
        serviceDescription = new ServiceDescription(name, description);
    }

    /**
     * Adds a service method (i.e. a query) to the service.
     * 
     * @param jdbcServiceMethodDescription a method description
     * @return this
     */
    public ExecServiceDescription addServiceMethod(ExecServiceMethodDescription jdbcServiceMethodDescription) {
        execServiceMethodDescriptions.add(jdbcServiceMethodDescription);
        return this;
    }
    
    public ExecServiceDescription shell(String shell) {
        this.shell = shell;
        return this;
    }
    
    public ExecServiceDescription shellArg(String shellArg) {
        this.shellArg = shellArg;
        return this;
    }
    
    /**
     * The ExecutorService on which to execute the query.
     * 
     * @param executorService an executor service
     * @return this
     */
    public ExecServiceDescription executorService(ExecutorService executorService) {
        if (this.executorService != null) {
            throw new IllegalArgumentException("ExecutorService was already set");
        }
        this.executorService = executorService;
        return this;
    }
    
    ServiceDescription createService() {
        for (ExecServiceMethodDescription jdbcServiceMethodDescription : execServiceMethodDescriptions) {
            jdbcServiceMethodDescription.executorService(executorService);
            jdbcServiceMethodDescription.shell = shell;
            jdbcServiceMethodDescription.shellArg = shellArg;
            serviceDescription.addServiceMethod(new ExecServiceMethod(jdbcServiceMethodDescription));
        }
        return serviceDescription;
    }
}
