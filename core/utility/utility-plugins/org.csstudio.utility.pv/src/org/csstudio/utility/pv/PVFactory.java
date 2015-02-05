/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** PV Factory
 *  <p>
 *  Locates implementations of the IPVFactory
 *  via an extension to the pvfactory extension point
 *  and creates the PV through it, using the PV name prefix
 *  to select an implementation.
 *
 *  <pre>
    // Create PV
    final PV pv = PVFactory.createPV(pv_name);
    // Register listener for updates
    pv.addListener(new PVListener()
    {
        public void pvDisconnected(PV pv)
        {
            System.out.println(pv.getName() + " is disconnected");
        }
        public void pvValueUpdate(PV pv)
        {
            IValue value = pv.getValue();
            System.out.println(pv.getName() + " = " + value);
            if (value instanceof IDoubleValue)
            {
                IDoubleValue dbl = (IDoubleValue) value;
                System.out.println(dbl.getValue());
            }
            // ... or use ValueUtil
        }
    });
    // Start the PV
    pv.start();

    ...

    pv.stop();
    </pre>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVFactory
{
    /** Separator between PV type indicator and rest of PV name.
     *  <p>
     *  This one is URL-ish, and works OK with EPICS PVs because
     *  those are unlikely to contain "://" themself, while
     *  just ":" for example is likely to be inside the PV name
     */
    final public static String SEPARATOR = "://";

    /** ID of the extension point */
    final private static String PVFACTORY_EXT_ID =
        "org.csstudio.utility.pv.pvfactory";

    /** Lazyly initialized PV factories found in extension registry */
    private static Map<String, IPVFactory> pv_factory = null;

    /** Default PV type, initialized from preferences */
    private static String default_type;

    /** Regular expression for numeric constants */
    final private static Pattern number = Pattern.compile("-?[0-9.]+");

    /** Initialize from preferences and extension point registry */
    final private static void initialize() throws Exception
    {
        // Get default type from preferences
        default_type = Preferences.getDefaultType();

        // Get extension point info from registry
        pv_factory = new HashMap<String, IPVFactory>();
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
            .getConfigurationElementsFor(PVFACTORY_EXT_ID);
        // Need at least one implementation
        if (configs.length < 1)
            throw new Exception("No extensions to " + PVFACTORY_EXT_ID + " found");
        final Logger logger = Logger.getLogger(PVFactory.class.getName());
        for (IConfigurationElement config : configs)
        {
            final String plugin = config.getContributor().getName();
            final String name = config.getAttribute("name");
            final String prefix = config.getAttribute("prefix");
            final IPVFactory factory = (IPVFactory) config.createExecutableExtension("class");
            logger.log(Level.CONFIG, "PV prefix {0} provided by {1} in {2}",
                new Object[] { prefix, name, plugin });
            pv_factory.put(prefix, factory);
        }
    }

    /** @return Supported PV type prefixes */
    final public static synchronized String[] getSupportedPrefixes() throws Exception
    {
        if (pv_factory == null)
            initialize();
        final ArrayList<String> prefixes = new ArrayList<String>();
        final Iterator<String> iterator = pv_factory.keySet().iterator();
        while (iterator.hasNext())
            prefixes.add(iterator.next());
        return (String[]) prefixes.toArray(new String[prefixes.size()]);
    }

    /** Create a PV for the given channel name, using the PV factory
     *  selected via the prefix of the channel name, or the default
     *  PV factory if no prefix is included in the channel name.
     *
     *  @param name Channel name, format "prefix://name" or just "name"
     *  @return PV
     *  @exception Exception on error
     */
    final public static synchronized PV createPV(String name) throws Exception
    {
        if (pv_factory == null)
            initialize();

        // Special handling of numeric constants
        if (number.matcher(name).matches())
            name = "const://x(" + name + ")";
        else
        {   // ... and text constants
            final int l = name.length();
            if (l > 2 && name.charAt(0) == '"'  &&  name.charAt(l-1) == '"')
                name = "const://x(" + name + ")";
        }

        // Identify type of PV
        // PV name = "type:...."
        final String type, base;
        final int sep = name.indexOf(SEPARATOR);
        if (sep > 0)
        {
            type = name.substring(0, sep);
            base = name.substring(sep+SEPARATOR.length());
        }
        else
        {
            type = default_type;
            base = name;
        }
        final IPVFactory factory = pv_factory.get(type);
        if (factory == null)
            throw new Exception("Unknown PV type in PV " + name);
        return factory.createPV(base);
    }
}
