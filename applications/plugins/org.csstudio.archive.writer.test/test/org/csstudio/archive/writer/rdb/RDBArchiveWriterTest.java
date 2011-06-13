/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import static org.junit.Assert.*;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.archive.writer.WriteChannel;
import org.csstudio.archive.writer.rdb.RDBArchiveWriter;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the archive writer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveWriterTest
{
	private RDBArchiveWriter writer = null;
	private String name;

    @Before
	public void connect() throws Exception
	{
		final TestProperties settings = new TestProperties();
		final String url = settings.getString("archive_rdb_url");
		final String user = settings.getString("archive_rdb_user");
		final String password = settings.getString("archive_rdb_password");
		name = settings.getString("archive_write_channel");
		if (url == null  ||  user == null  ||  password == null  ||  name == null)
		{
			System.out.println("Skipping test, no archive_rdb_url, user, password");
			return;
		}
		writer = new RDBArchiveWriter(url, user, password);
	}
	
	@After
	public void close()
	{
		if (writer != null)
			writer.close();
	}

	@Test
	public void testChannelLookup() throws Exception
	{
		if (writer == null)
			return;
		final WriteChannel channel = writer.getChannel(name);
		System.out.println(channel);
		assertNotNull(channel);
	}

	@Test
	public void testWriteDouble() throws Exception
	{
		if (writer == null)
			return;
		final WriteChannel channel = writer.getChannel(name);
		IValue sample;
		sample = ValueFactory.createDoubleValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 10, 1, "a.u."),
				IValue.Quality.Original,
				new double[] { 3.14, 6.28 });

		writer.addSample(channel, sample);
		writer.flush();
	}

	@Test
	public void testWriteLongEnumText() throws Exception
	{
		if (writer == null)
			return;
		final WriteChannel channel = writer.getChannel(name);
		IValue sample;

		// This sets enumerated meta data
		sample = ValueFactory.createEnumeratedValue(TimestampFactory.now(),
			ValueFactory.createOKSeverity(), "OK",
			ValueFactory.createEnumeratedMetaData(new String[] { "Hello", "Goodbye" }),
			IValue.Quality.Original,
			new int[] { 1 });
		writer.addSample(channel, sample);

		// This leaves the (enumerated) meta data untouched
		sample = ValueFactory.createStringValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				IValue.Quality.Original,
				new String[] { "Hello" });
		writer.addSample(channel, sample);

		// Sets numeric meta data
		sample = ValueFactory.createLongValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 10, 1, "a.u."),
				IValue.Quality.Original,
				new long[] { 42 });
		writer.addSample(channel, sample);
		
		writer.flush();
	}
}
