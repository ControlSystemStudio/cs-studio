/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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
 */

package org.csstudio.alarm.dal2jms;

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Remote command registered at the management command extension point: Command to stop the application.
 * 
 * To apply 'stop' the application handle is retrieved and a 'destroy' is called upon it. This is the recommended technique.
 * 
 * @author Markus Moeller, Jörg Penning
 */
public class StopCommand implements IManagementCommand {
    
    @Override
    @Nonnull
    public final CommandResult execute(@CheckForNull final CommandParameters parameters) {
        CommandResult result = null;
        ApplicationHandle thisHandle = null;
        BundleContext bundleContext = Dal2JmsActivator.getDefault().getBundleContext();
        String serviceFilter = createFilter();
        
        // Get the application from the Application Admin Service
        try {
            ServiceTracker<ApplicationHandle, IApplicationContext> tracker = new ServiceTracker<ApplicationHandle, IApplicationContext>(bundleContext,
                                                                                                                                        bundleContext
                                                                                                                                                .createFilter(serviceFilter),
                                                                                                                                        null);
            tracker.open();
        
            Object[] allServices = tracker.getServices();
            if (allServices != null) {
                List<Object> services = Arrays.asList(allServices);
                ApplicationHandle[] regApps = services.toArray(new ApplicationHandle[0]);
                for (ApplicationHandle o : regApps) {
                    if (o.getInstanceId().contains("application")) { // matches the application id as registered as an extension
                        thisHandle = o;
                        break;
                    }
                }
            } else {
                result = CommandResult
                        .createFailureResult("Stopping the alarm server failed: Cannot get the application entry from the service.");
            }
            
            tracker.close();
        } catch (InvalidSyntaxException e) {
            result = CommandResult.createFailureResult(e.getMessage());
        }
        
        if (thisHandle != null) {
            result = CommandResult.createMessageResult("The alarm server (Dal2Jms) stops now ...");
            thisHandle.destroy();
        } else {
            result = CommandResult
                    .createFailureResult("Stopping the alarm server failed: Cannot get the application entry from the service.");
        }
        
        return result;
    }
    
    @Nonnull
    private String createFilter() {
        // do not change object class, later on we will cast upon it
        return "(&(objectClass=" + ApplicationHandle.class.getName() + ")"
                + "(application.descriptor=" + Dal2JmsActivator.getDefault().getPluginId() + "*))";
    }
}
