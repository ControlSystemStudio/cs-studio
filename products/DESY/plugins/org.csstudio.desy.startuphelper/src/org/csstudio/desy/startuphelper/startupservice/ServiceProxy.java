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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class ServiceProxy implements IStartupServiceListener{

    private static final String ATT_CLASS = "class";
    private static final String ATT_ID = "id";
    private static final String ATT_NAME = "name";
    private static final String ATT_PRIORITY = "isHighPriority";

    private final IConfigurationElement _configElement;
    private IStartupServiceListener _serviceListener;
    private final String _id;
    private final String _name;
    private final boolean _isHighPriority;

    public ServiceProxy(IConfigurationElement configElement, int orindal) {
        this._configElement = configElement;
        // ensure that the attribute is there
        getAttribute(configElement, ATT_CLASS, null);
        _id = getAttribute(configElement, ATT_ID, null);
        _name = getAttribute(configElement, ATT_NAME, _id);
        _isHighPriority = Boolean.parseBoolean(getAttribute(configElement, ATT_PRIORITY, "false"));
    }

    private String getAttribute(
            IConfigurationElement configElem,
            String name,
            String defaultValue) {
        // get the value from the configuration element
        String value = configElem.getAttribute(name);
        // is value not null
        if(value != null)
            // ok, return that value
            return value;
        // it was null, do we have a default value?
        if(defaultValue != null)
            // return the default value
            return defaultValue;
        // we don't have any possible values, throw an exception
        throw new IllegalArgumentException("Missing " + name + " attribute!");
    }

    public void run() {
        if(_serviceListener == null)
            getServiceListener();
        // if there was an error getting the action,
        // there will be a null pointer exception here
        // TODO: think about how to avoid this exception
        _serviceListener.run();
    }

    private void getServiceListener() {
        try {
            this._serviceListener =
                (IStartupServiceListener)
                this._configElement.createExecutableExtension(ATT_CLASS);
        } catch (CoreException e) {
            e.printStackTrace();
            _serviceListener = null;
        }
    }

    public String getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public boolean isHighPriority() {
        return this._isHighPriority;
    }

}
