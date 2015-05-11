/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.impl;

import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.Identifier;
import org.csstudio.dal.context.LifecycleReporterSupport;
import org.csstudio.dal.spi.Plugs;


/**
 * Default application context implementation
 */
public class DefaultApplicationContext extends LifecycleReporterSupport
    implements AbstractApplicationContext
{
    private final String name;
    protected Properties configuration;
    protected Identifier identificator;
    private Logger logger;
    private Hashtable<String, Object> properties;

    /**
     * Creates a new DefaultApplicationContext object.
     *
     * @param name application context name
     */
    public DefaultApplicationContext(final String name)
    {
        super();
        this.name = name;
        fireInitializing();
        fireInitialized();
    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.context.AbstractApplicationContext#getConfiguration()
     */
    @Override
    public Properties getConfiguration()
    {
        if (configuration == null) {
            configuration = new Properties(System.getProperties());
            Plugs.configureSimulatorPlug(configuration);
        }

        return configuration;
    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.context.Identifiable#getIdentifier()
     */
    @Override
    public Identifier getIdentifier()
    {
        if (identificator == null) {
            return null;
        }

        return identificator;

        // TODO Check if this is OK
    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.context.Identifiable#isDebug()
     */
    @Override
    public boolean isDebug()
    {
        return false;
    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.context.AbstractApplicationContext#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Destroys context by signaling all listners that lifecycle has
     * ended and  releases all resources. After this point context is not
     * valid any more.
     */
    @Override
    public void destroy()
    {
        fireDestroying();
        fireDestroyed();
    }

    /**
     * Returns a logger registered to this application context. The name of the logger is
     * the same as the name of application context.
     * @return
     */
    public Logger getLogger() {
        if (logger == null) {
            return Logger.getLogger(getClass());
        }
        return logger;
    }

    @Override
    public Object getApplicationProperty(final String keyName) {
        if (properties==null || keyName==null) {
            return null;
        }
        return properties.get(keyName);
    }

    @Override
    public synchronized void putApplicationProperty(final String keyName, final Object value) {
        if (keyName==null) {
            return;
        }
        if (properties==null) {
            properties= new Hashtable<String, Object>();
        }
        properties.put(keyName, value);
    }
}

/* __oOo__ */
