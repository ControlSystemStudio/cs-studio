package org.csstudio.config.ioconfig.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototype;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractCssPlugin{

	/**
	 *  The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.csstudio.config.ioconfig.model";

	/**
	 *  The shared instance
	 */
    private static Activator plugin;

    private List<Class<?>> _classes;

    /**
     * The constructor
     */
    public Activator() {
        plugin = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart(final BundleContext context) throws Exception {
        _classes = new ArrayList<Class<?>>();
        _classes.add(NodeImage.class);
        _classes.add(Channel.class);
        _classes.add(ChannelStructure.class);
        _classes.add(Module.class);
        _classes.add(Slave.class);
        _classes.add(Master.class);
        _classes.add(ProfibusSubnet.class);
        _classes.add(GSDModule.class);
        _classes.add(Ioc.class);
        _classes.add(Facility.class);
        _classes.add(Node.class);
        _classes.add(GSDFile.class);
        _classes.add(ModuleChannelPrototype.class);
        _classes.add(Document.class);
        _classes.add(SearchNode.class);
        _classes.add(Sensors.class);
        _classes.add(PV2IONameMatcherModel.class);
        HibernateManager.addClasses(_classes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(final BundleContext context) throws Exception {
        HibernateManager.removeClasses(_classes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
