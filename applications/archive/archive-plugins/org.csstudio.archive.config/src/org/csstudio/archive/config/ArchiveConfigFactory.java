/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Factory for obtaining an {@link ArchiveConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveConfigFactory
{
    /** ID of the extension point that implementing plugins use. */
    private static final String EXTENSION_ID =
                    "org.csstudio.archive.config.ArchiveConfig";

    /** Obtain archive configuration interface from plugin registry
     *  @return {@link ArchiveConfig}
     *  @throws Exception on error: No implementation found, or error initializing it
     */
    public static ArchiveConfig getArchiveConfig() throws Exception
	{
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
        	.getConfigurationElementsFor(EXTENSION_ID);
		// Need exactly one implementation
		if (configs.length != 1)
			throw new Exception("Need 1 extension to " + EXTENSION_ID + ", found " + configs.length);
		
		final IConfigurationElement config = configs[0];
		// final String plugin = config.getContributor().getName();
		final ArchiveConfig arch_config = (ArchiveConfig)config.createExecutableExtension("class");
		return arch_config;
	}
}
