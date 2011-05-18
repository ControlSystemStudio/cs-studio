/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.internal.EnumMetaDataHelper;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.ValueFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test EnumMetaDataHelper
 *  @author Kay Kasemir
 */
public class EnumMetaDataTest
{
    private static RDBArchive archive;

    @BeforeClass
    public static void connect() throws Exception
    {
        archive = RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);
    }

    @AfterClass
    public static void disconnect()
    {
        archive.close();
    }

    @SuppressWarnings("nls")
    @Test
    public void testGet() throws Exception
    {
        final ChannelConfig channel = archive.getChannel(TestSetup.TestType.ENUM.getPvName());
        assertNotNull(channel);

        IEnumeratedMetaData meta = EnumMetaDataHelper.get(archive, channel);

        meta = ValueFactory.createEnumeratedMetaData(
                new String [] { "One", "Two" });
        EnumMetaDataHelper.set(archive, channel, meta);

        meta = EnumMetaDataHelper.get(archive, channel);
        assertEquals(2, meta.getStates().length);
        assertEquals("One", meta.getState(0));
    }
}
