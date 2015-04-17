/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authentication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/** JAAS {@link CallbackHandler} for unattended/automatic login.
 * 
 *  <p>Passes a provided name, password to JAAS.
 *  
 *  <p>By default, text messages are logged, but derived class may override.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UnattendedCallbackHandler implements CallbackHandler
{
    final private String name, password;

    /** Initialize for login without name, password.
     *  <p>Can be used to UnixLoginModule, NTLoginModule
     */
    public UnattendedCallbackHandler()
    {
        this(null, null);
    }

    /** Initialize
     *  @param name Name to use
     *  @param password Password to use
     */
    public UnattendedCallbackHandler(final String name, final String password)
    {
        this.name = name;
        this.password = password;
    }
    
    /** {@inheritDoc} */
    @Override
    final public void handle(final Callback[] callbacks) throws IOException,
            UnsupportedCallbackException
    {
        for (Callback callback : callbacks)
        {
            if (callback instanceof NameCallback)
            {
                final NameCallback nc = (NameCallback) callback;
                if (name == null)
                    throw new UnsupportedCallbackException(callback, "Unattended login cannot provide user name");
                nc.setName(name);
            }
            else if (callback instanceof PasswordCallback)
            {
                final PasswordCallback pc = (PasswordCallback) callback;
                if (password == null)
                    throw new UnsupportedCallbackException(callback, "Unattended login cannot provide password");
                pc.setPassword(password.toCharArray());
            }
            else if (callback instanceof TextOutputCallback)
                handleText((TextOutputCallback) callback);
            else
                throw new UnsupportedCallbackException(callback);
        }
    }

    /** Handle received text
     *  
     *  <p>Default implementation logs the text.
     *  Derived class may handle it as needed.
     *  @param text {@link TextOutputCallback}
     */
    public void handleText(final TextOutputCallback text)
    {
        Logger logger = Logger.getLogger(getClass().getName());
        if (text.getMessageType() == TextOutputCallback.ERROR)
            logger.log(Level.SEVERE, text.getMessage());
        else if (text.getMessageType() == TextOutputCallback.WARNING)
            logger.log(Level.WARNING, text.getMessage());
        else
            logger.log(Level.INFO, text.getMessage());
    }
}
