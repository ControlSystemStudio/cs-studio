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
import org.csstudio.archive.rdb.internal.NumericMetaDataHelper;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ValueFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NumericMetadataTest
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
        final ChannelConfig channel = archive.getChannel(TestSetup.TestType.DOUBLE.getPvName());
        assertNotNull(channel);

        INumericMetaData meta = NumericMetaDataHelper.get(archive, channel);
        System.out.println(channel.toString());
        if (meta != null)
            System.out.println(meta.toString());
        else
            System.out.println("No meta data");


        int prec = 3;
        if (meta != null)
            prec = meta.getPrecision() + 1;
        meta = ValueFactory.createNumericMetaData(0.0, 10.0, 2.0, 8.0, 1.0, 9.0, prec, "Volts");
        NumericMetaDataHelper.set(archive, channel, meta);

        meta = NumericMetaDataHelper.get(archive, channel);
        assertNotNull(meta);
        System.out.println(channel.toString());
        System.out.println(meta.toString());
        assertEquals(prec, meta.getPrecision());
    }
}
