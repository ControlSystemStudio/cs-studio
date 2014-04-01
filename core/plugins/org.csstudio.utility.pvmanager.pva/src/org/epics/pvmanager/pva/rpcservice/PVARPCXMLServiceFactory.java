/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager.pva.rpcservice;

import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A pva rpcservice factory that crawls a directory for xml files, and creates
 * a pvAccessRPC rpcs ervice from each of them.
 * 
 * @author dkumar
 */
public class PVARPCXMLServiceFactory implements ServiceFactory {
    
    private final File directory;

    /**
     * Creates a new factory that reads from the given directory.
     * <p>
     * If the directory does not exist, it simply returns an empty set.
     * 
     * @param directory a directory
     */
    public PVARPCXMLServiceFactory(File directory) {
        this.directory = directory;
        if (directory.exists() && !directory.isDirectory()) {
            throw new IllegalArgumentException("Path provided is not a directory (" + directory + ")");
        }
    }
    
    /**
     * Crawls the directory and creates pvAccess RPC services.
     * <p>
     * XML files that do not parse correctly are skipped.
     * 
     * @return the created services
     */
    @Override
    public Collection<Service> createServices() {
        List<Service> services = new ArrayList<>();
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    try {
                        services.add(RPCServices.createFromXml(new FileInputStream(file)));
                    } catch (Exception ex) {
                            Logger.getLogger(RPCServices.class.getName()).log(Level.SEVERE, "Failed creating pvAccess RPC Service from " + file, ex);
                    }
                }
            }
        } else {
            Logger.getLogger(RPCServices.class.getName()).log(Level.WARNING, "Directory " + directory + " does not exist");
        }
        return services;
    }
    
}
