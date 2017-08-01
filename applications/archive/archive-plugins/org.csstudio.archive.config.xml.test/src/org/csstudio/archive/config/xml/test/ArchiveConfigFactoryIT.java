/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.xml.test;

import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-in test/demo of the {@link ArchiveConfigFactory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveConfigFactoryIT
{
    @Ignore
    @Test
    public void demoArchiveConfig() throws Exception
    {
        final ArchiveConfig config = ArchiveConfigFactory.getArchiveConfig();
        assertNotNull(config);
        System.out.println("Found " + config.getClass().getName());
        config.close();
    }
}
