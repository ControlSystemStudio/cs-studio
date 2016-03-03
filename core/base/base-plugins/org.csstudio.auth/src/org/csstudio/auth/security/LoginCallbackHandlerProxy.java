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
 /**
 *
 */
package org.csstudio.auth.security;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Represents the Login Callback Proxy for any login callback
 * handlers that are enumerated through the enumerator located
 * in the utility class.
 *
 * @author avodovnik
 *
 */
public class LoginCallbackHandlerProxy implements ILoginCallbackHandler {

    private static final String ATT_CLASS = "class";
    private static final String ATT_ID = "id";
    private static final String ATT_NAME = "name";

    private IConfigurationElement _configElement;
    private ILoginCallbackHandler _lch;

    private String _id;
    private String _name;
    public LoginCallbackHandlerProxy(IConfigurationElement configElement) throws IllegalArgumentException{
        _configElement = configElement;

        // ensure the availabiliy of the attribute
        getAttribute(configElement, ATT_CLASS, null);
        _id = getAttribute(configElement, ATT_ID, null);
        _name = getAttribute(configElement, ATT_NAME, _id);
    }

    private String getAttribute(
            IConfigurationElement configElem,
            String name,
            String defaultValue) throws IllegalArgumentException {
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

    /* (non-Javadoc)
     * @see org.csstudio.platform.security.ILoginCallbackHandler#getCredentials()
     */
    public Credentials getCredentials() {
        ILoginCallbackHandler lch = getHandler();
        if(lch != null)
            return lch.getCredentials();
        return null;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    private ILoginCallbackHandler getHandler() {
        try {
            if(_lch == null)
                _lch = (ILoginCallbackHandler) this._configElement
                    .createExecutableExtension(ATT_CLASS);


            return _lch;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void signalFailedLoginAttempt() {
        _lch.signalFailedLoginAttempt();
    }

}
