/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import static org.junit.Assert.assertNotNull;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.writer.WriteChannel;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of the archive writer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBArchiveWriterTest
{
	private RDBArchiveWriter writer = null;
	private String name, array_name;

    @Before
	public void connect() throws Exception
	{
		final TestProperties settings = new TestProperties();
		final String url = settings.getString("archive_rdb_url");
		final String user = settings.getString("archive_rdb_user");
		final String password = settings.getString("archive_rdb_password");
		final String schema = settings.getString("archive_rdb_schema");
		name = settings.getString("archive_channel");
		array_name = settings.getString("archive_array_channel");
		if (url == null  ||  user == null  ||  password == null  ||  name == null)
		{
			System.out.println("Skipping test, no archive_rdb_url, user, password");
			return;
		}
		final boolean use_blob = Boolean.parseBoolean(settings.getString("archive_use_blob"));
		if (use_blob)
			System.out.println("Running write test with BLOB");
		else
			System.out.println("Running write test with old array_val table");
		writer = new RDBArchiveWriter(url, user, password, schema, use_blob);
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
		WriteChannel channel = writer.getChannel(name);
		System.out.println(channel);
		assertNotNull(channel);

		if (array_name == null)
			return;
		channel = writer.getChannel(array_name);
		System.out.println(channel);
		assertNotNull(channel);

	}

	@Test
	public void testWriteDouble() throws Exception
	{
		if (writer == null)
			return;
		System.out.println("Writing double sample for channel " + name);
		final WriteChannel channel = writer.getChannel(name);
		final IValue sample;
		sample = ValueFactory.createDoubleValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 10, 1, "a.u."),
				IValue.Quality.Original,
				new double[] { 3.14 });

		writer.addSample(channel, sample);
		writer.flush();
	}

	@Test
	public void testWriteDoubleArray() throws Exception
	{
		if (writer == null  ||  array_name == null)
			return;
		System.out.println("Writing double array sample for channel " + array_name);
		final WriteChannel channel = writer.getChannel(array_name);
		final IValue sample;
		sample = ValueFactory.createDoubleValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 10, 1, "a.u."),
				IValue.Quality.Original,
				new double[] { 3.14, 6.28, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });

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

	final private static int TEST_DURATION_SECS = 60;
	final private static long FLUSH_COUNT = 500;

	/* PostgreSQL 9 Test Results:
	 *
	 * HP Compact 8000 Elite Small Form Factor,
	 * Intel Core Duo, 3GHz, Windows 7, 32 bit,
	 * Hitachi Hds721025cla382 250gb Sata 7200rpm
	 *
	 * Flush Count  100, 500, 1000: ~7000 samples/sec, no big difference
	 *
	 * After deleting the constraints of sample.channel_id to channel,
	 * severity_id and status_id to sev. and status tables: ~12000 samples/sec,
	 * i.e. almost twice as much.
	 *
	 * JProfiler shows most time spent in 'flush', some in addSample()'s call to setTimestamp(),
	 * but overall time is in RDB, not Java.
	 *
	 *
	 * MySQL Test Results:
	 *
	 * iMac8,1    2.8GHz Intel Core 2 Duo, 4GB RAM
	 *
	 * Without rewriteBatchedStatements=true:  ~7000 samples/sec
	 * With rewriteBatchedStatements=true   : ~21000 samples/sec
	 */
 	@Ignore
	@Test
	public void testWriteSpeedDouble() throws Exception
	{
		if (writer == null)
			return;

		System.out.println("Write test: Adding samples to " + name + " for " + TEST_DURATION_SECS + " secs");
		final WriteChannel channel = writer.getChannel(name);
		final INumericMetaData meta =
			ValueFactory.createNumericMetaData(0, 10, 2, 8, 1, 10, 1, "a.u.");

		long count = 0;
		final BenchmarkTimer timer = new BenchmarkTimer();
		final long start = System.currentTimeMillis();
		final long end = start + TEST_DURATION_SECS*1000L;
		do
		{
			++count;
			final IValue sample = ValueFactory.createDoubleValue(TimestampFactory.now(),
				ValueFactory.createOKSeverity(), "OK",
				meta,
				IValue.Quality.Original,
				new double[] { count });
			writer.addSample(channel, sample);
			if (count % FLUSH_COUNT == 0)
				writer.flush();
		}
		while (System.currentTimeMillis() < end);
		writer.flush();
		timer.stop();

		System.out.println("Wrote " + count + " samples in " + timer);
		System.out.println(count / timer.getSeconds() + " samples/sec");
	}
}
