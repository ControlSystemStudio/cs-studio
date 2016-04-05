/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.config.ArchiveConfig;
import org.csstudio.archive.config.ArchiveConfigFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the engine model
 *  <p>
 *  {@link ArchiveConfig} is obtained via extension point, hence Plug-in test.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineModelHeadlessTest
{
    @Test
    public void testReadConfig() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String config_name = settings.getString("archive_config");
        final int port = settings.getInteger("archive_port", 4812);
        if (config_name == null  ||  config_name.isEmpty())
            System.out.println("Skipping test, no archive_config");

        final ArchiveConfig config = ArchiveConfigFactory.getArchiveConfig();
        final BenchmarkTimer timer = new BenchmarkTimer();
        final EngineModel model = new EngineModel();
        model.readConfig(config, config_name, port, false);
        timer.stop();
        config.close();

        model.dumpDebugInfo();
        final int count = model.getChannelCount();
        System.out.println("Channel count: " + count);
        System.out.println("Runtime      : " + timer);
        System.out.println("Channels/sec : " + count/timer.getSeconds());
        assertTrue(count > 0);
    }
}
