/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.desy.startuphelper.startupservice;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * Enumerates the startup services connected to the
 * extension point provided by this plug-in.
 *
 * @author avodovnik
 *
 */
public class StartupServiceEnumerator {
    private static final String TAG_SERVICE = "startupService";
    private static ServiceProxy[] cachedServices = null;

    /**
     * Retruns the actions by parsing the extension point.
     *
     * @return An array of actions.
     */
    public static ServiceProxy[] getServices() {
        // see if the items are cached
        if (cachedServices != null)
            // returned the items already cached
            return cachedServices;

        // ok, get the extension
        IExtension[] extensions = Platform.getExtensionRegistry()
                .getExtensionPoint("org.csstudio.desy.startuphelper.startupListener")
                .getExtensions();

        // define an array
        List<ServiceProxy> found = new ArrayList<ServiceProxy>();
        // define an array to hold the configuration elements
        IConfigurationElement[] configElements;
        // define a variable to hold the parsed services
        ServiceProxy service;

        for (IExtension extension : extensions) {
            // load the config elements
            configElements = extension.getConfigurationElements();

            for (IConfigurationElement configElement : configElements) {
                // get the action proxy
                service = parseService(configElement, found
                        .size());
                if (service != null)
                    // add the action
                    found.add(service);
            }
        }

        // cache the providers
        cachedServices = found.toArray(new ServiceProxy[found
                .size()]);

        // return the cached providers
        return cachedServices;
    }

    private static ServiceProxy parseService(IConfigurationElement configElement, int i) {
        if(TAG_SERVICE.equals(configElement.getName())) {
            // get the proxy
            return new ServiceProxy(configElement, i);
        }
        // return null by default
        return null;
    }
}
