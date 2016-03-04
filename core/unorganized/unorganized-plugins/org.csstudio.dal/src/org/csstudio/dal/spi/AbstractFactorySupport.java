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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.LifecycleEvent;
import org.csstudio.dal.context.LifecycleListener;
import org.csstudio.dal.proxy.AbstractPlug;

import java.util.Properties;

import javax.naming.directory.DirContext;


/**
 * This is convenience implementation of common factory code.
 *
 * <p>
 * This class implements plug sharing behaviour. By default plug instance is shared.
 * There are two ways to for plug or application control plug sharing:
 * </p>
 * <ul>
 * <li>Constructor AbstractFactorySupport(boolean) is used and factory implementation actively
 * decides for only one option: or is plug share or not. Plug sharing can not be changed later
 * trough application configuration.</li>
 * <li>Sharing of plug is enabled or disabled with parameter provided with configuration with
 * application context. This is possible if this factory is created with default constructor.</li>
 * </ul>
 *
 * <p>If plug is not shared, then plug instance will be created using application
 * context as constructor parameter.</p>
 *
 *
 * @see AbstractFactorySupport#AbstractFactorySupport(boolean)
 * @see AbstractFactory#SHARE_PLUG
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class AbstractFactorySupport implements AbstractFactory
{
    protected AbstractApplicationContext ctx;
    protected LinkPolicy linkPolicy;
    private AbstractPlug plug;
    protected LifecycleListener lifecycleListener = new LifecycleListenerImpl();
    protected Boolean plugShared;
    protected boolean propertiesCached=false;

    private class LifecycleListenerImpl implements LifecycleListener
    {
        public void destroying(LifecycleEvent event)
        {
            destroyAll();

            try
            {
                if (plug != null)
                    plug.releaseInstance();
            } catch (Throwable e) {
                if (plug != null) {
                    plug.getLogger().log(Level.WARN, "Unable to release factory.",e);
                } else {
                    Logger.getLogger(this.getClass()).warn("Unable to release factory.", e);
                }
            }
        }

        public void destroyed(LifecycleEvent event)
        {
        }

        public void initialized(LifecycleEvent event)
        {
        }

        public void initializing(LifecycleEvent event)
        {
        }
    }

    /**
     * Default constructor. Plug sharing option is provided with application context or default
     * sharing policy is used.
     */
    protected AbstractFactorySupport()
    {
    }

    /**
     * Constructor for those DAL implementations, which want to force from code plug factory to
     * share or not share
     *
     */
    protected AbstractFactorySupport(boolean plugShared)
    {
        this.plugShared=plugShared;
    }

    /**
     * Must destroy all created objects
     *
     */
    protected abstract void destroyAll();

    /**
     * Must return plug implemntation class, which extends <code>AbstractPlug</code>.
     * @return plug implemntation class
     */
    protected abstract Class<?extends AbstractPlug> getPlugClass();

    /**
     * Returns instance of plug, which must be used by this factory. Plug is created if necessary.
     * @return instance of plug dedicated to this factory.
     */
    protected synchronized AbstractPlug getPlugInstance()
    {
        if (plug == null) {
            if (plugShared !=null && !plugShared)
            try {
                plug = (AbstractPlug)getPlugClass()
                        .getMethod("getInstance", new Class[]{ AbstractApplicationContext.class })
                        .invoke(null, new Object[]{ ctx });
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).debug("Heuristic plug instantiation failed, another try available.", e);
            }

            if (plug == null) {
                try {
                    plug = (AbstractPlug)getPlugClass()
                        .getMethod("getInstance", new Class[]{ Properties.class })
                        .invoke(null, new Object[]{ ctx.getConfiguration() });
                } catch (Exception e) {
                    Logger.getLogger(this.getClass()).fatal("Heuristic plug instantiation failed twice.", e);
                    throw new RuntimeException("Plug '" + getPlugClass()
                        + "' is not correctly implemented. ", e);
                }
            }
        }

        return plug;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getLinkPolicy()
     */
    public LinkPolicy getLinkPolicy()
    {
        return linkPolicy;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getApplicationContext()
     */
    public AbstractApplicationContext getApplicationContext()
    {
        return ctx;
    }

    /**
     * @see org.csstudio.dal.spi.DeviceFactory#initialize(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.spi.LinkPolicy)
     */
    public void initialize(AbstractApplicationContext ctx, LinkPolicy policy)
    {
        if (this.ctx!=null) {
            throw new IllegalStateException("Factory is already initialized.");
        }
        this.ctx = ctx;
        this.linkPolicy = policy;
        ctx.addLifecycleListener(lifecycleListener);

        if (plugShared==null) {
            String s= ctx.getConfiguration().getProperty(SHARE_PLUG);
            if (s != null) {
                plugShared=Boolean.valueOf(s);
            }
        }

        String s= ctx.getConfiguration().getProperty(Plugs.PROPERTIES_FROM_CACHE);
        if (s !=null) {

            propertiesCached= Boolean.parseBoolean(s);

        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getPlugType()
     */
    public String getPlugType()
    {
        return getPlugInstance().getPlugType();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.spi.DeviceFactory#getDefaultDirectory()
     */
    public DirContext getDefaultDirectory()
    {
        return getPlugInstance().getDefaultDirectory();
    }

    /**
     * Return plug which is used for connection. If this factory serves as facade for multiple plugs,
     * than default plug must be returned.
     *
     * @return plug which is used for connection
     */
    public AbstractPlug getPlug()
    {
        return getPlugInstance();
    }

    /**
     * Return <code>true</code> if this factory is sharing plug instance with other factories.
     * Default DAL implementation is using shared plug instance if possible. This way
     * all connections are shared among different factories and applications within same JVM.
     * @return <code>true</code> if this factory is sharing plug instance with other factories
     */
    public boolean isPlugShared() {
        // TODO Auto-generated method stub
        return false;
    }

}

/* __oOo__ */
