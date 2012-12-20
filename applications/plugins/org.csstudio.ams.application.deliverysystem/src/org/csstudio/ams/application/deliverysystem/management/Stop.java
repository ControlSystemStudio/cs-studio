
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.application.deliverysystem.management;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ams.application.deliverysystem.Activator;
import org.csstudio.ams.application.deliverysystem.RemotelyManageable;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author mmoeller
 * @version 1.0
 * @since 10.12.2011
 */
public class Stop implements IManagementCommand {
    
    private static RemotelyManageable remoteObject = null;
    
    public static void staticInject(RemotelyManageable o) {
        remoteObject = o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(CommandParameters parameters) {
        
        if (remoteObject != null) {
            remoteObject.setRestart(false);
        }
        
        CommandResult result = null;
        ApplicationHandle thisHandle = null;
        BundleContext bundleContext = Activator.getContext();

        String serviceFilter = "(&(objectClass=" +
        ApplicationHandle.class.getName() + ")"
        + "(application.descriptor=" + Activator.PLUGIN_ID + "*))";
        
        // Get the application from the Application Admin Service
        ServiceTracker<ApplicationHandle, IApplicationContext> tracker = null;
        try {
            tracker = new ServiceTracker<ApplicationHandle, IApplicationContext>(bundleContext, bundleContext.createFilter(serviceFilter), null);
            tracker.open();
        
            Object[] allServices = tracker.getServices();
            if(allServices != null) {
                
                List<Object> services = Arrays.asList(allServices);
                ApplicationHandle[] regApps = services.toArray(new ApplicationHandle[0]);
                
                for(ApplicationHandle o : regApps) {
                    
                    if(o.getInstanceId().contains("DeliverySystemApplication")) {
                        thisHandle = o;
                        break;
                    }
                }
            } else {
                result = CommandResult.createFailureResult("\nCannot get the application entry from the service.");
            }
            
            tracker.close();
        } catch(InvalidSyntaxException e) {
            result = CommandResult.createFailureResult(e.getMessage());
        }
        
        if(thisHandle != null) {

            result = CommandResult.createMessageResult("OK: [0] - Stopping DeliverySystem...");
            thisHandle.destroy();
        } else {
            result = CommandResult.createFailureResult("ERROR: [1] - Cannot get the application entry from the service.");
        }
        
        return result;
    }
}
