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
 package org.csstudio.dal;

import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactory;
import org.csstudio.dal.spi.PropertyFactoryService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DalPlugin extends Plugin implements PropertyFactoryService {

    {
        CharacteristicInfo.registerCharacteristicInfo(CharacteristicInfo.C_SEVERITY);
        CharacteristicInfo.registerCharacteristicInfo(CharacteristicInfo.C_TIMESTAMP);
        CharacteristicInfo.registerCharacteristicInfo(CharacteristicInfo.C_STATUS);
    }

    /**
     * The ID of this plugin.
     */
    public static final String ID = "org.csstudio.dal"; //$NON-NLS-1$

    /**
     * The ID of the <code>plugs</code> extension point.
     */
    public static final String EXTPOINT_PLUGS = ID + ".plugs"; //$NON-NLS-1$

    /**
     * The shared instance.
     */
    private static DalPlugin plugin;

    private AbstractApplicationContext applicationContext;

    private SimpleDALBroker broker;

    /**
     * The constructor
     */
    public DalPlugin() {
        plugin = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static DalPlugin getDefault() {
        if (plugin == null) {
            plugin = new DalPlugin();
        }
        return plugin;
    }

    /**
     * Returns the simple dal broker to be used with this DAL plugin.
     * The broker is configured to use all available property factories
     * registered with this plugin.
     *
     * @return the simple dal broker
     */
    public synchronized SimpleDALBroker getSimpleDALBroker() {
        if (broker == null) {
            broker = SimpleDALBroker.newInstance(getApplicationContext(),this);
        }
        return broker;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        if (applicationContext!=null) {
            applicationContext.destroy();
        }
        super.stop(context);
    }

    /**
     * Returns the application context.
     *
     * @return the context
     */
    public synchronized AbstractApplicationContext getApplicationContext() {
        if (applicationContext==null) {
            applicationContext = new CssApplicationContext("CSS");
        }
        return applicationContext;
    }

    /**
     * Creates factory for default plug by further calling {@link DalPlugin#getPropertyFactory(AbstractApplicationContext, LinkPolicy, String)}.
     *
     */
    @Override
    public PropertyFactory getPropertyFactory(final AbstractApplicationContext ctx,
            final LinkPolicy linkPolicy) {

        // find default
        final String plug= Plugs.getDefaultPlug(ctx.getConfiguration());

        return getPropertyFactory(ctx, linkPolicy, plug);
    }

    /**
     * Creates factory in following order:
     * <ul>
     * <li>first tries to lookup delegate service defined by RCP extension point, this is done trough {@link PlugRegistry}. If this fails, then...</li>
     * <li>tries to load factory by calling {@link DefaultPropertyFactoryService}, this is fallback procedure, it does no use extension points.</li>
     * </ul>
     */
    @Override
    public PropertyFactory getPropertyFactory(final AbstractApplicationContext ctx,
            final LinkPolicy linkPolicy, final String plugName) {

        final PropertyFactoryService pfs= PlugRegistry.getInstance().getPropertyFactoryService(plugName);

        if (pfs!=null) {
            return pfs.getPropertyFactory(ctx, linkPolicy);
        }

        return DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, linkPolicy, plugName);
    }

}
