
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.websuite.ams;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author mmoeller
 * @version 1.0
 * @since 06.08.2012
 */
public class AmsConfigurationService {
    
    private BundleContext bundleContext;
    
    private AmsDatabaseProperty amsDbProp;
    
    private ServiceReference<ConfigurationServiceFactory> serviceRef;
    
    private LocalStoreConfigurationService service;
    
    public AmsConfigurationService(BundleContext context, AmsDatabaseProperty dbProp) {
        bundleContext = context;
        amsDbProp = dbProp;
        serviceRef = bundleContext.getServiceReference(ConfigurationServiceFactory.class);
        ConfigurationServiceFactory factory = bundleContext.getService(serviceRef);
        service = factory.getConfigurationService(amsDbProp.getDbUrl(),
                                                  amsDbProp.getDatabaseType(),
                                                  amsDbProp.getDbUser(),
                                                  amsDbProp.getDbPassword());
    }
    
    public void close() {
        if (serviceRef != null) {
            bundleContext.ungetService(serviceRef);
            serviceRef = null;
            service = null;
        }
    }
    
    public Configuration getConfiguration() {
        
        Configuration config = null;
        
        try {
            
            System.out.println("Beginne die Konfiguration zu lesen...");
            long start = System.currentTimeMillis();
            config = service.getEntireConfiguration();
            long end = System.currentTimeMillis();
            System.out.println("Komplette Konfiguration gelesen in " + (end - start) + " ms");
            
        } catch (StorageError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InconsistentConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return config;
    }
}
