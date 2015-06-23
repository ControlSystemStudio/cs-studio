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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;


/**
 * Helper class which helps manage different plug implementations.<p>This
 * class expects specifc keywords in configuration properties, which define
 * which plugs are present and which instances can be used for particular
 * plug.</p>
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public final class Plugs
{

    /*
     *
     * STATIC DECLARATIONS, COMMON TO ALL PLUGS OBJECTS
     *
     */

    /**
     * Keyword used in System properties to configure DAL logging.
     * Valie values are:
     * <ul>
     * <li>true - DAL will call basic log4j configuration, which enables logging to console.</li>
     * <li>false - DAL will not configure logging to console.</li>
     * </ul>
     *
     * By default true is assumed and logging will be configured.
     *
     * <p>
     * Regardless of this setting an application can configure own appenders for DAL logging.
     * See {@link Plugs#getLogger()} for details.
     * </p>
     *
     * @see #getLogger()
     */
    public static final String PLUGS_LOGGING = "dal.logging";

    /**
     * Keyword for list of plugs. Plugs in list must be separated with
     * comman.
     */
    public static final String PLUGS = "dal.plugs";

    /** Keyword for default plug name. */
    public static final String PLUGS_DEFAULT = "dal.plugs.default";

    /** Keyword declaring device factory class name for particular plug. */
    public static final String PLUG_DEVICE_FACTORY_CLASS = "dal.devicefactory.";

    /** Keyword declaring property factory class name for particular plug. */
    public static final String PLUG_PROPERTY_FACTORY_CLASS = "dal.propertyfactory.";

    /**
     * Optional system configuration property which tells to plug implementations
     * what is expected timeout for remote operations in milliseconds.
     */
    public static final String CONNECTION_TIMEOUT = "dal.connectionTimeout";

    /**
     * Optional system configuration property, which defines the timeout for
     * establishing or initializing the connections in milliseconds.
     */
    public static final String INITIAL_CONNECTION_TIMEOUT = "dal.initialConnectionTimeout";

    /**
     * System configuration property which tells to property factory implementations
     * to use properties from cache when connection is requested. By default new property is created.
     */
    public static final String PROPERTIES_FROM_CACHE = "dal.propertiesFromCache";

    /**
     * An optional application configuration property name under which is in AbstractAppplicationContext stored an instance of
     * PropertyFactoryService. This service implementation must be able to produce PropertyFactory
     * implementations for all supported and available plug types within application runtime context.
     */
    public static final String PROPERTY_FACTORY_SERVICE_IMPLEMENTATION = "dal.propertyfactoryServiceImplementation";

    /**
     * DAL default connection timeout value in milliseconds. Used if CONNECTION_TIMEOUT is not defined.
     */
    public static final long DEFAULT_CONNECTION_TIMEOUT = 30000;

    /**
     * DAL default initial connection timeout value in milliseconds. Used if INITIAL_CONNECTION_TIMEOUT is not defined.
     */
    public static final long DEFAULT_INITIAL_CONNECTION_TIMEOUT = 200;

    public static final String SIMULATOR_PLUG_TYPE = "Simulator";

    private static Plugs plugs;


    /**
     * Returns an instance of <code>Plugs</code> with System <code>Properties</code> as storage.
     * @return an instance of <code>Plugs</code> with System <code>Properties</code> as storage
     */
    public static final synchronized Plugs getInstance() {
        if (plugs == null) {
            plugs = new Plugs(System.getProperties());
        }
        return plugs;
    }

    /**
     * Returns an instance of <code>Plugs</code> with the specified list
     * of <code>Properties</code>.
     * @return an instance of <code>Plugs</code>
     */
    public static Plugs getInstance(Properties properties) {
        return new Plugs(properties);
    }

    /**
     * Copies the <code>Properties</code> to this <code>Plugs</code>.
     * @param properties the <code>Properties</code> to copy from
     */
    public void putAll(Properties properties) {
        this.properties.putAll(properties);
    }

    /**
     * Returns array with plug names defined in provided properties.
     *
     * @param prop properties
     *
     * @return array with plug names
     */
    public static String[] getPlugNames(Properties prop)
    {
        String names = prop.getProperty(PLUGS);

        if (names == null) {
            return new String[0];
        }

        String[] r = names.split(",");

        if (r == null) {
            return new String[0];
        }

        return r;
    }

    /**
     * Returns property factory class for plug name. Class name is
     * obtained from provided configuration.
     *
     * @param name plug name
     * @param prop properties
     *
     * @return property factory class
     *
     * @throws IllegalArgumentException if properties does not contin proper configuration
     */
    public static Class getPropertyFactoryClassForPlug(String name,
        Properties prop)
    {
        String cn = prop.getProperty(PLUG_PROPERTY_FACTORY_CLASS + name);

        if (cn != null) {
            Class cl = null;

            try {
                cl = Class.forName(cn);
            } catch (Throwable t) {
                throw new IllegalArgumentException("Could not load class '"
                    + cn + "' for factory implementation: " + t);
            }

            if (!PropertyFactory.class.isAssignableFrom(cl)) {
                throw new IllegalArgumentException("Class '" + cn
                    + "' is not a factory implementation!");
            }

            return cl;
        }

        throw new IllegalArgumentException(
            "No factory class defined for plug '" + name + "'.");
    }

    /**
     * Returns device factory class for plug name. Class name is
     * obtained from provided configuration.
     *
     * @param name plug name
     * @param prop properties
     *
     * @return device factory class
     *
     * @throws IllegalArgumentException if properties does not contin proper configuration
     */
    public static Class getDeviceFactoryClassForPlug(String name,
        Properties prop)
    {
        String cn = prop.getProperty(PLUG_DEVICE_FACTORY_CLASS + name);

        if (cn != null) {
            Class cl = null;

            try {
                cl = Class.forName(cn);
            } catch (Throwable t) {
                throw new IllegalArgumentException("Could not load class '"
                    + cn + "' for factory implementation: " + t);
            }

            if (!DeviceFactory.class.isAssignableFrom(cl)) {
                throw new IllegalArgumentException("Class '" + cn
                    + "' is not a factory implementation!");
            }

            return cl;
        }

        throw new IllegalArgumentException(
            "No factory class defined for plug '" + name + "'.");
    }

    /**
     * Tries to locate and load default property factory class. First
     * tries to find  <code>PropertyFactoryService.DEFAULT_FACTORY_IMPL</code>
     * key of factory key for  <code>PLUGS_DEFAULT</code> (in this order) in
     * provided properties. If this fails or properties are null, then System
     * properties are searched.
     *
     * @param prop properties, may be <code>null</code>
     *
     * @return property factory class or <code>null</code> if no configuration
     *         found
     *
     * @throws ClassNotFoundException if class loading failed
     */
    public static Class getDefaultPropertyFactory(Properties prop)
        throws ClassNotFoundException
    {
        String cn = null;

        if (prop != null) {
            cn = prop.getProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL);

            if (cn == null) {
                String pl = prop.getProperty(PLUGS_DEFAULT);

                if (pl != null) {
                    cn = prop.getProperty(PLUG_PROPERTY_FACTORY_CLASS + pl);
                }
            }
        }

        if (cn == null) {
            prop = System.getProperties();

            cn = prop.getProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL);

            if (cn == null) {
                String pl = prop.getProperty(PLUGS_DEFAULT);

                if (pl != null) {
                    cn = prop.getProperty(PLUG_PROPERTY_FACTORY_CLASS + pl);
                }
            }
        }

        if (cn == null) {
            return null;
        }

        Class cl = Class.forName(cn);

        if (!PropertyFactory.class.isAssignableFrom(cl)) {
            throw new ClassNotFoundException("Class '" + cn
                + "' is not a factory implementation!");
        }

        return cl;
    }

    /**
     * Tries to locate default plug name. First
     * tries to find key for  <code>PLUGS_DEFAULT</code> in
     * provided properties. If this fails or properties are null, then System
     * properties are searched.
     *
     * @param prop properties, may be <code>null</code>
     *
     * @return default plug name or <code>null</code> if no configuration
     *         found
     *
     * @throws ClassNotFoundException if class loading failed
     */
    public static String getDefaultPlug(Properties prop)
    {
        if (prop != null) {
            String pl = prop.getProperty(PLUGS_DEFAULT);
            if (pl!=null) {
                return pl;
            }
        }

        prop = System.getProperties();

        String pl = prop.getProperty(PLUGS_DEFAULT);
        return pl;
    }

    /**
     * Tries to locate and load default device factory class. First
     * tries to find  <code>DeviceFactoryService.DEFAULT_DEVICE_IMPL</code>
     * key of factory key for  <code>PLUGS_DEFAULT</code> (in this order) in
     * provided properties. If this fails or properties are null, then System
     * properties are searched.
     *
     * @param prop properties, may be <code>null</code>
     *
     * @return device factory class or <code>null</code> if no configuration
     *         found
     *
     * @throws ClassNotFoundException if class loading failed
     */
    public static Class getDefaultDeviceFactory(Properties prop)
        throws ClassNotFoundException
    {
        String cn = null;

        if (prop != null) {
            cn = prop.getProperty(DeviceFactoryService.DEFAULT_FACTORY_IMPL);

            if (cn == null) {
                String pl = prop.getProperty(PLUGS_DEFAULT);

                if (pl != null) {
                    cn = prop.getProperty(PLUG_DEVICE_FACTORY_CLASS + pl);
                }
            }
        }

        if (cn == null) {
            prop = System.getProperties();

            cn = prop.getProperty(DeviceFactoryService.DEFAULT_FACTORY_IMPL);

            if (cn == null) {
                String pl = prop.getProperty(PLUGS_DEFAULT);

                if (pl != null) {
                    cn = prop.getProperty(PLUG_DEVICE_FACTORY_CLASS + pl);
                }
            }
        }

        if (cn == null) {
            return null;
        }

        Class cl = Class.forName(cn);

        if (!DeviceFactory.class.isAssignableFrom(cl)) {
            throw new ClassNotFoundException("Class '" + cn
                + "' is not a factory implementation!");
        }

        return cl;
    }

    /**
     * Convenience method which tries to get connection timeout first from provided properties,
     * then from system properties and if both fails returns provided default value.
     * @param p properties, may be null
     * @param def default fallback values
     * @return connection timeout propety value
     */
    public static final long getConnectionTimeout(Properties p, long def)
    {
        String s = null;

        if (p != null) {
            s = p.getProperty(CONNECTION_TIMEOUT);

            if (s != null) {
                try {
                    return Long.parseLong(s);
                } catch (Exception e) {
                    Logger.getLogger(Plugs.class).warn("System defined property "+CONNECTION_TIMEOUT+" could not be parsed as long.", e);
                }
            }
        }

        return Long.getLong(CONNECTION_TIMEOUT, def);
    }
    /**
     * Convenience method which tries to get connection timeout first from provided properties,
     * then from system properties and if both fails returns DAL default value.
     * @param p properties, may be null
     * @param def default fallback values
     * @return connection timeout propety value
     */
    public static final long getConnectionTimeout(Properties p)
    {
        return getConnectionTimeout(p, DEFAULT_CONNECTION_TIMEOUT);
    }

    public long getConnectionTimeout() {
        return getConnectionTimeout(properties);
    }

    /**
     * Convenience method which tries to get initial connection timeout first from provided properties,
     * then from system properties and if both fails returns provided default value.
     * @param p properties, may be null
     * @param def default fallback values
     * @return connection timeout property value
     */
    public static final long getInitialConnectionTimeout(Properties p, long def)
    {
        String s = null;

        if (p != null) {
            s = p.getProperty(INITIAL_CONNECTION_TIMEOUT);

            if (s != null) {
                try {
                    return Long.parseLong(s);
                } catch (Exception e) {
                    Logger.getLogger(Plugs.class).warn("System defined property "+INITIAL_CONNECTION_TIMEOUT+" could not be parsed as long.", e);
                }
            }
        }

        return Long.getLong(INITIAL_CONNECTION_TIMEOUT, def);
    }
    /**
     * Convenience method which tries to get initial connection timeout first from provided properties,
     * then from system properties and if both fails returns DAL default value.
     * @param p properties, may be null
     * @param def default fallback values
     * @return initial connection timeout property value
     */
    public static final long getInitialConnectionTimeout(Properties p)
    {
        return getInitialConnectionTimeout(p, DEFAULT_INITIAL_CONNECTION_TIMEOUT);
    }

    /**
     * Return logger, which is parent for all DAL plug loggers.
     *
     * <p>
     * DAL Plug loggers collect and distribute messages, which are intended for general plublic.
     * E.g. application which is not interested in internal structure, but wants to display progress when some channel
     * was connected or some user initiated action failed.
     * </p>
     *
     * <p>
     * Parent DAL logger name is 'DAL'. Names of plug loggers are 'DAL.PLUG_NAME', for example 'DAL.EPICS'.
     * </p>
     *
     * <p>
     * Default configuration of appenders is controlled with System parameter {@link Plugs#PLUGS_LOGGING}.
     * </p>
     *
     * @return parent logger for all DAL plug loggers.
     *
     * @see #PLUGS_LOGGING
     * @see #getPlugLogger(String)
     */
    public static final Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("DAL");

            boolean log= Boolean.parseBoolean(System.getProperty(PLUGS_LOGGING, Boolean.TRUE.toString()));

            if (log) {
                BasicConfigurator.configure();
            } else {
                // supresses log4j warning about nonconfigured logging
                logger.addAppender(new NullAppender());
            }
        }
        return logger;
    }

    /**
     * Returns logger for particular DAL plug, this logger shuld be used for general messages about plug activity.
     *
     * @param plugName the name ov the plug for which logger is to be obtained.
     *
     * @return logger for DAL plug
     *
     */
    public static final Logger getPlugLogger(String plugName) {
        return getLogger().getLogger("DAL."+plugName);
    }





    /*
     *
     * INSTANCE SPECIFIC ONLY DECLARATIONS
     *
     */

    private Properties properties;
    private static Logger logger;

    /**
     * Gets the <code>Properties</code> of this <code>Plug</code>.
     * @return the <code>Properties</code>
     */
    public Properties getProperties() {
        return properties;
    }

    /**
         *
         */
    private Plugs(Properties properties)
    {
        this.properties = properties;
        configureSimulatorPlug(properties);
    }

    /**
     * Returns array with plug names defined in properties for this Plugs instance.
     *
     * @return array with plug names
     */
    public String[] getPlugNames()
    {
        return getPlugNames(properties);
    }

    /**
     * Returns property factory class for plug name. Class name is
     * obtained from properties.
     *
     * @param name plug name
     *
     * @return property factory class
     */
    public Class getPropertyFactoryClassForPlug(String name)
    {
        return getPropertyFactoryClassForPlug(name, properties);
    }

    /**
     * Returns device factory class for plug name. Class name is
     * obtained from properties.
     *
     * @param name plug name
     *
     * @return device factory class
     */
    public Class getDeviceFactoryClassForPlug(String name)
    {
        return getDeviceFactoryClassForPlug(name, properties);
    }

    public long getInitialConnectionTimeout() {
        return getInitialConnectionTimeout(properties);
    }

    public Class getDefaultDeviceFacotry() throws ClassNotFoundException {
        return getDefaultDeviceFactory(properties);
    }

    public Class getDefaultPropertyFactory() throws ClassNotFoundException {
        return getDefaultPropertyFactory(properties);
    }

    public void setConnectionTimeout(long timeout) {
        properties.setProperty(CONNECTION_TIMEOUT, (new Long(timeout)).toString());
    }

    public void registerDefaultDeviceFactory(Class<? extends DeviceFactory> cl) {
        properties.setProperty(DeviceFactoryService.DEFAULT_FACTORY_IMPL, cl.getCanonicalName());
    }

    public void registerDefaultPropertyFactory(Class<? extends PropertyFactory> cl) {
        properties.setProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL, cl.getCanonicalName());
    }

    public void registerDeviceFactoryClassForPlug(String name, Class<? extends DeviceFactory> cl) {
        properties.setProperty(PLUG_DEVICE_FACTORY_CLASS+name, cl.getCanonicalName());
    }

    public void registerPropertyFactoryClassForPlug(String name, Class<? extends PropertyFactory> cl) {
        properties.setProperty(PLUG_PROPERTY_FACTORY_CLASS+name, cl.getCanonicalName());
    }

    public void setPlugNames(String[] names) {
        StringBuilder sb = new StringBuilder();
        if (names != null && names.length > 0) {
            sb.append(names[0]);
            for (int i = 1; i < names.length; i++) {
                sb.append(','+names[i]);
            }
        }
        properties.setProperty(PLUGS, sb.toString());
    }

    /**
     * Loads to properties configuration, which enables EPICS plug.
     * @param p configuration
     */
    public static void configureSimulatorPlug(Properties p)
    {
        String[] s = getPlugNames(p);
        Set<String> set = new HashSet<String>(Arrays.asList(s));

        if (!set.contains(SIMULATOR_PLUG_TYPE)) {
            set.add(SIMULATOR_PLUG_TYPE);

            StringBuffer sb = new StringBuffer();

            for (Iterator iter = set.iterator(); iter.hasNext();) {
                if (sb.length() > 0) {
                    sb.append(',');
                }

                sb.append(iter.next());
            }

            p.put(PLUGS, sb.toString());
        }

        p.put(PLUGS_DEFAULT, SIMULATOR_PLUG_TYPE);
        p.put(PLUG_PROPERTY_FACTORY_CLASS + SIMULATOR_PLUG_TYPE,
            "org.csstudio.dal.simulation.PropertyFactoryImpl");
        p.put(PLUG_DEVICE_FACTORY_CLASS + SIMULATOR_PLUG_TYPE,
            "org.csstudio.dal.simulation.DeviceFactoryImpl");
    }

}

/* __oOo__ */
