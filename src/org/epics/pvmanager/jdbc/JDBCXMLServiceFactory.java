/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceFactory;

/**
 *
 * @author carcassi
 */
public class JDBCXMLServiceFactory implements ServiceFactory {
    
    private final File directory;

    public JDBCXMLServiceFactory(File directory) {
        this.directory = directory;
        if (directory.exists() && !directory.isDirectory()) {
            throw new IllegalArgumentException("Path provided is not a directory (" + directory + ")");
        }
    }
    
    @Override
    public Collection<Service> createServices() {
        List<Service> services = new ArrayList<>();
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    try {
                        services.add(JDBCServices.createFromXml(new FileInputStream(file)));
                    } catch (Exception ex) {
                            Logger.getLogger(JDBCServices.class.getName()).log(Level.INFO, "Failed creating JDBCService from " + file, ex);
                    }
                }
            }
        } else {
            Logger.getLogger(JDBCServices.class.getName()).log(Level.WARNING, "Directory " + directory + " does not exist");
        }
        return services;
    }
    
}
