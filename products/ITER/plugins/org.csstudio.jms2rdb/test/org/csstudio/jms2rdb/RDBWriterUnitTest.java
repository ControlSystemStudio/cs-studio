/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.jms2rdb.rdb.RDBWriter;
import org.junit.Test;

/** JUnit test of the {@link RDBWriter}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBWriterUnitTest
{
    private static final String MSG_LOG_URL = "msg_log_url";

    @Test
    public void testWriteMapMessage() throws Exception
    {
        final TestProperties settings = new TestProperties();
        final String url = settings.getString(MSG_LOG_URL);
        final String schema = settings.getString("msg_log_schema", "");

        if (url == null)
        {
            System.out.println("Skipping test, need " + MSG_LOG_URL);
            return;
        }

        final RDBWriter rdb = new RDBWriter(url, schema);
        rdb.write("Testing...");

        rdb.close();
    }
}
