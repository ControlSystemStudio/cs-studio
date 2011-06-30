/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.junit.Test;

/** JUnit test of the ChannelArchiverReader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelArchiverReaderTest
{
    // FIXME (kasemir) : remove syso, use assertions, parameterize URL and Channel
    final private static String URL =
        "xnds://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";
    @Test
    public void testChannelArchiverReader() throws Exception
    {
        // Connect, dump basic info
        final ChannelArchiverReader reader = new ChannelArchiverReader(URL);

        System.out.println(reader.getServerName());
        System.out.println(reader.getURL());
        System.out.println(reader.getDescription());
        for (final ArchiveInfo info : reader.getArchiveInfos()) {
            System.out.println(info);
        }

        // Locate names
        final String names[] = reader.getNamesByPattern(4600, "CCL_LLRF:IOC?:Load");
        for (final String name : names) {
            System.out.println(name);
        }

        // Get Values
        final DateFormat parser = new SimpleDateFormat("yyyy/MM/dd");
        final ITimestamp end = TimestampFactory.fromMillisecs(parser.parse("2009/06/29").getTime());
        final ITimestamp start = TimestampFactory.fromDouble(end.toDouble() - 60*60*0.5); // 0.5 hours

        System.out.println("Get one batch of samples directly:");
        final IValue[] samples = reader.getSamples(4600,
                                                   "CCL_LLRF:IOC1:Load",
                                                   start,
                                                   end,
                                                   false,
                                                   10);
        for (final IValue sample : samples) {
            System.out.println(sample);
        }

        System.out.println("Use ValueIterator:");
        final ValueIterator values = reader.getRawValues(4600, "CCL_LLRF:IOC1:Load",
                start, end);
        IMetaData meta = null;
        while (values.hasNext())
        {
            final IValue value = values.next();
            System.out.println(value);
            if (meta == null) {
                meta = value.getMetaData();
            }
        }
        values.close();
        System.out.println("Meta data: " + meta);


        reader.close();
    }
}
