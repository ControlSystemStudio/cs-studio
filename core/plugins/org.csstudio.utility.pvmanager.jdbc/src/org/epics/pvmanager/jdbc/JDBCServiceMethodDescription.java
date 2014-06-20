/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.sql.DataSource;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VTable;

/**
 * The description for a JDBC service method (i.e. a query).
 *
 * @author carcassi
 */
public class JDBCServiceMethodDescription {
    
    final ServiceMethodDescription serviceMethodDescription;
    boolean resultAdded = false;
    DataSource dataSource;
    ExecutorService executorService;
    String query;
    final List<String> orderedParameterNames = new ArrayList<>();

    /**
     * A new service method with the given name and description.
     * 
     * @param name the method name
     * @param description the method description
     */
    public JDBCServiceMethodDescription(String name, String description) {
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
    public JDBCServiceMethodDescription addArgument(String name, String description, Class<?> type) {
        serviceMethodDescription.addArgument(name, description, type);
        orderedParameterNames.add(name);
        return this;
    }
    
    /**
     * Adds a result for the query.
     * <p>
     * The result must be specified if the query returns data (i.e. it is a SELECT)
     * and must not be specified if the query does not return data (i.e. INSERT, UPDATE, DELETE, ...).
     * 
     * @param name the result name
     * @param description the result description
     * @return this
     */
    public JDBCServiceMethodDescription queryResult(String name, String description) {
        if (resultAdded) {
            throw new IllegalArgumentException("The query can only have one result");
        }
        serviceMethodDescription.addResult(name, description, VTable.class);
        return this;
    }
    
    JDBCServiceMethodDescription dataSource(DataSource dataSource) {
        if (this.dataSource != null) {
            throw new IllegalArgumentException("DataSource was already set");
        }
        this.dataSource = dataSource;
        return this;
    }
    
    JDBCServiceMethodDescription executorService(ExecutorService executorService) {
        if (this.executorService != null) {
            throw new IllegalArgumentException("ExecutorService was already set");
        }
        this.executorService = executorService;
        return this;
    }
    
    /**
     * The query mapped to this service method.
     * 
     * @param query the query
     * @return this
     */
    public JDBCServiceMethodDescription query(String query) {
        if (this.query != null) {
            throw new IllegalArgumentException("Query was already set");
        }
        this.query = query;
        return this;
    }
    
    
    
}
