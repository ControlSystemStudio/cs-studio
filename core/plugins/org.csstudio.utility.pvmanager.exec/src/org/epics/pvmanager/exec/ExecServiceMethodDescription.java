/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VType;

/**
 * The description for an executor service method (i.e. a shell command).
 *
 * @author carcassi
 */
public class ExecServiceMethodDescription {
    
    final ServiceMethodDescription serviceMethodDescription;
    boolean resultAdded = false;
    ExecutorService executorService;
    String shell;
    String shellArg;
    String command;
    final List<String> orderedParameterNames = new ArrayList<>();

    /**
     * A new service method with the given name and description.
     * 
     * @param name the method name
     * @param description the method description
     */
    public ExecServiceMethodDescription(String name, String description) {
        serviceMethodDescription = new ServiceMethodDescription(name, description);
    }
    
    /**
     * Adds an argument for the query.
     * <p>
     * Arguments need to be specified in the same order as they appear in the query.
     * 
     * @param name argument name
     * @param description argument description
     * @param type the expected type of the argument
     * @return this
     */
    public ExecServiceMethodDescription addArgument(String name, String description, Class<?> type) {
        serviceMethodDescription.addArgument(name, description, type);
        orderedParameterNames.add(name);
        return this;
    }
    
    /**
     * Adds a result for the script.
     * 
     * @param name the result name
     * @param description the result description
     * @return this
     */
    public ExecServiceMethodDescription queryResult(String name, String description) {
        if (resultAdded) {
            throw new IllegalArgumentException("The query can only have one result");
        }
        serviceMethodDescription.addResult(name, description, VType.class);
        return this;
    }
    
    ExecServiceMethodDescription executorService(ExecutorService executorService) {
        if (this.executorService != null) {
            throw new IllegalArgumentException("ExecutorService was already set");
        }
        this.executorService = executorService;
        return this;
    }

    public ExecServiceMethodDescription command(String command) {
        this.command = command;
        return this;
    }
    
}
