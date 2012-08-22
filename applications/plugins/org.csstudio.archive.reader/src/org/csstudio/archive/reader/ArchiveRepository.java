/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

/** Factory class that locates {@link ArchiveReader} implementations
 *  from the Eclipse registry based on URLs.
 *
 *  @author Kay Kasemir
 *  @author Jan Hatje, Albert Kagarmanov: Previous org.csstudio.archive.ArchiveImplementationRegistry
 */
@SuppressWarnings("nls")
public class ArchiveRepository
{
    /** ID of the extension point that implementing plugins use. */
    private static final String EXTENSION_ID =
                    "org.csstudio.archive.reader.ArchiveReader"; //$NON-NLS-1$

    /** Singleton instance */
    private static ArchiveRepository instance = null;

    /** ArchiveReader implementations found in extension registry mapped by prefix */
    final private static Map<String, ArchiveReaderFactory> reader_factories =
                            new HashMap<String, ArchiveReaderFactory>();

    /** @return Singleton instance of the ArchiveRepository
     *  @throws Exception on error
     */
    public static ArchiveRepository getInstance() throws Exception
    {
        if (instance == null)
            instance = new ArchiveRepository();
        return instance;
    }

    /** Initialize from Eclipse plugin registry.
     *  @throws Exception on error
     */
    private ArchiveRepository() throws Exception
    {
        final IExtensionRegistry registry = RegistryFactory.getRegistry();
        if (registry == null)
            throw new Exception("Not running as plugin");
        final IConfigurationElement[] configs = registry.getConfigurationElementsFor(EXTENSION_ID);
        // Need at least one implementation
        if (configs.length < 1)
            throw new Exception("No extensions to " + EXTENSION_ID + " found");
        for (IConfigurationElement config : configs)
        {
//            final String plugin = config.getContributor().getName();
//            final String name = config.getAttribute("name");
            final String prefix = config.getAttribute("prefix");
//          System.out.println(plugin + " provides '" + name +
//          "', prefix '" + prefix + "'");
            final ArchiveReaderFactory factory =
                (ArchiveReaderFactory)config.createExecutableExtension("class");
            reader_factories.put(prefix, factory);
        }
    }

    /** Only meant for tests, not public API
     *  @return Supported URL prefixes
     */
    public String [] getSupportedPrefixes()
    {
        final Set<String> keys = reader_factories.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /** Create archive reader for URL
     *  @param url Archive URL
     *  @return ArchiveReader for given URL
     *  @throws Exception on error (no suitable reader, or internal error)
     */
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
        // Determine prefix
        final int delim = url.indexOf(':');
        if (delim < 0)
            throw new Exception("Missing prefix in URL " + url);
        final String prefix = url.substring(0, delim);

        // Locate implementation for that prefix
        final ArchiveReaderFactory factory = reader_factories.get(prefix);
        if (factory == null)
            throw new Exception("Unkown archive reader URL " + url);
        return factory.getArchiveReader(url);
    }
}
