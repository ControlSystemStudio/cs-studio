/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.common.engine.RDBArchiveEnginePreferences;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the engine model
 *  <p>
 *  RDBArchive configuration (schema) might need info from
 *  Eclipse preferences, hence Plug-in test.
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
    	final String url = settings.getString("archive_rdb_url");
    	if (url == null)
    	{
    		System.out.println("Skipping, no archive test settings");
    		return;
    	}
    	final String user = settings.getString("archive_rdb_user");
    	final String password = settings.getString("archive_rdb_password");
    	final String config = settings.getString("archive_config");
    	final int port = settings.getInteger("archive_port", 4812);

        // Connect writer to the service with the given prefs
        final Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put(RDBArchiveEnginePreferences.URL, url);
        prefs.put(RDBArchiveEnginePreferences.USER, user);
        prefs.put(RDBArchiveEnginePreferences.PASSWORD, password);

        final BenchmarkTimer timer = new BenchmarkTimer();
        final EngineModel model = new EngineModel(prefs);
        model.readConfig(config, port);
        timer.stop();

        final int count = model.getChannelCount();
        System.out.println("Channel count: " + count);
        System.out.println("Runtime      : " + timer);
        System.out.println("Channels/sec : " + count/timer.getSeconds());

        assertTrue(count > 0);
    }
}
