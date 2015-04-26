/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Factory for obtaining an {@link ArchiveWriter}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveWriterFactory
{
    /** ID of the extension point that implementing plugins use. */
    private static final String EXTENSION_ID =
                    "org.csstudio.archive.writer.ArchiveWriter";

    /** Obtain archive writer interface from plugin registry
     *  @return {@link ArchiveWriter}
     *  @throws Exception on error: No implementation found, or error initializing it
     */
    public static ArchiveWriter getArchiveWriter() throws Exception
	{
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
        	.getConfigurationElementsFor(EXTENSION_ID);
		// Need exactly one implementation
		if (configs.length != 1)
			throw new Exception("Need 1 extension to " + EXTENSION_ID + ", found " + configs.length);
		
		final IConfigurationElement config = configs[0];
		// final String plugin = config.getContributor().getName();
		final ArchiveWriter writer = (ArchiveWriter)config.createExecutableExtension("class");
		return writer;
	}
}
