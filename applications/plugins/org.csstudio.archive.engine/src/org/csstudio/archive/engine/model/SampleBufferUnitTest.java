/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import static org.junit.Assert.assertEquals;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.junit.Test;

/** JUnit test of the {@link SampleBuffer}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SampleBufferUnitTest
{
	final private SampleBuffer buffer = new SampleBuffer("Demo", 10);

	/** Add and remove one value */
	@Test
	public void testAddRemove()
	{
		assertEquals(0, buffer.getQueueSize());
		buffer.add(TestValueFactory.getDouble(1));
		assertEquals(1, buffer.getQueueSize());
		assertEquals(1.0, ValueUtil.getDouble(buffer.remove()), 0.01);
		assertEquals(0, buffer.getQueueSize());
	}

	/** Check Overrun */
	@Test
	public void testOverrun()
	{
		// Fill buffer
		assertEquals(0, buffer.getQueueSize());
		for (int i=0; i<buffer.getCapacity(); ++i)
			buffer.add(TestValueFactory.getDouble(i));
		assertEquals(buffer.getQueueSize(), buffer.getQueueSize());
		assertEquals(0, buffer.getBufferStats().getOverruns());

		// Cause overrun
		buffer.add(TestValueFactory.getDouble(-1.0));
		assertEquals(buffer.getQueueSize(), buffer.getQueueSize());
		assertEquals(1, buffer.getBufferStats().getOverruns());

		// Value 0 was dropped by overrun, oldest sample now 1
		final IValue value = buffer.remove();
		assertEquals(1.0, ValueUtil.getDouble(value), 0.01);
	}

	final private static long TEST_RUNS = 10000;

	class FillThread extends Thread
	{
		@Override
        public void run()
        {
			for (long i=1; i<=TEST_RUNS; ++i)
			{
				buffer.add(TestValueFactory.getDouble(i));
				try
                {
	                sleep(10);
                }
                catch (InterruptedException e)
                {
	                e.printStackTrace();
                }
			}
        }
	}

	/** Check tread access
	 * @throws Exception on thread error
	 */
	@Test
	public void testThreads() throws Exception
	{
		// Fill buffer in background thread
		final FillThread fill = new FillThread();
		fill.start();

		// Empty it in this thread
		long expected = 1;
		while (expected <= TEST_RUNS)
		{
			while (buffer.getQueueSize() > 0)
			{
				final double value = ValueUtil.getDouble(buffer.remove());
				// System.out.println(value);
				assertEquals((double)expected, value, 0.1);
				++expected;
			}
			Thread.sleep(50);
		}
	}
}
