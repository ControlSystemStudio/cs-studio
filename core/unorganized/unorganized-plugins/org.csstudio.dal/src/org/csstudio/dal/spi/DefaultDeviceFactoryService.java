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

package org.csstudio.dal.spi;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.LifecycleState;
import org.csstudio.dal.impl.DefaultApplicationContext;


/**
 * This is the default implementation of the DeviceFactoryService
 * interface.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DefaultDeviceFactoryService implements DeviceFactoryService
{
    private static DefaultDeviceFactoryService service;

    /**
     * Returns the DeviceFactoryService object.
     *
     * @return DeviceFactoryService object.
     */
    public static final synchronized DeviceFactoryService getDeviceFactoryService()
    {
        if (service == null) {
            service = new DefaultDeviceFactoryService();
        }

        return service;
    }

    private ArrayList<AbstractApplicationContext> ctxList = new ArrayList<AbstractApplicationContext>();

    protected DefaultDeviceFactoryService()
    {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run()
                {
                    shutdown();
                }
                ;
            });
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.PropertyFactoryService#getPropertyFactory(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.context.PropertyFamily, org.csstudio.dal.spi.PropertyFactory.LinkPolicy)
     */
    public DeviceFactory getDeviceFactory(AbstractApplicationContext ctx,
        LinkPolicy linkPolicy)
    {
        return getDeviceFactory(ctx, linkPolicy, null);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactoryService#getDeviceFactory(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.spi.LinkPolicy, java.lang.String)
     */
    public DeviceFactory getDeviceFactory(AbstractApplicationContext ctx,
        LinkPolicy linkPolicy, String plugName)
    {
        ctxList.add(ctx);

        if (plugName == null) {
            Class cl = null;

            try {
                cl = Plugs.getDefaultDeviceFactory(ctx.getConfiguration());
            } catch (Throwable t) {
                throw new IllegalArgumentException(
                    "Could not load factory implementation: " + t);
            }

            if (cl != null) {
                try {
                    DeviceFactory df = (DeviceFactory)cl.newInstance();
                    df.initialize(ctx, linkPolicy);

                    return df;
                } catch (Throwable t) {
                    throw new IllegalArgumentException(
                        "Could not instantiate '" + cl.getName()
                        + "' factory implementation: " + t);
                }
            } else {
                throw new IllegalArgumentException(
                        "Could not find factory implementation information in configuration.");
            }

            //DeviceFactoryImpl simulator = new DeviceFactoryImpl();
            //simulator.initialize(ctx, linkPolicy);
            //return simulator;
        }

        Class cl;

        try {
            cl = Plugs.getDeviceFactoryClassForPlug(plugName,
                    ctx.getConfiguration());
        } catch (Throwable t) {
            throw new IllegalArgumentException(
                "Failed to load factory implementation: " + t);
        }

        try {
            DeviceFactory df = (DeviceFactory)cl.newInstance();
            df.initialize(ctx, linkPolicy);

            return df;
        } catch (Throwable t) {
            throw new IllegalArgumentException("Could not instantiate '"
                + cl.getName() + "' factory implementation: " + t);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactoryService#getDefaultDeviceFactory()
     */
    public DeviceFactory getDefaultDeviceFactory()
    {
        String defaultCtx = System.getProperty(DEFAULT_APPLICATION_CONTEXT);
        AbstractApplicationContext context = null;

        if (defaultCtx != null) {
            try {
                Class appContextClass = Class.forName(defaultCtx);
                context = (AbstractApplicationContext)appContextClass.getConstructor(String.class)
                    .newInstance("DefaultContext");
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).info("System defined context '"+defaultCtx+"' failed to instantiate, internal defautl will be used instead.", e);
            }
        }

        if (context == null) {
            context = new DefaultApplicationContext("DefaultContext");
        }

        return getDeviceFactory(context, null);
    }

    /*
     * This method is called when JVM is shuting down and service is celaning all it's
     * resources.
     */
    private void shutdown()
    {
        AbstractApplicationContext[] l = ctxList.toArray(new AbstractApplicationContext[0]);

        for (int i = 0; i < l.length; i++) {
            if (l[i].getLifecycleState() != LifecycleState.DESTROYING
                && l[i].getLifecycleState() != LifecycleState.DESTROYED) {
                l[i].destroy();
            }
        }
    }
} /* __oOo__ */


/* __oOo__ */
