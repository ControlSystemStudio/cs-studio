/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine2.model;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.junit.Test;

/** JUnit test of the {@link SampleBuffer}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SampleBufferUnitTest
{
	private static final int CAPACITY = 10;
    final private SampleBuffer buffer = new SampleBuffer("Demo", CAPACITY);

	/** Add and remove one value */
	@Test
	public void testAddRemove()
	{
		assertEquals(0, buffer.size());
		buffer.add(TestValueFactory.getDouble(1)); // without blocking
		assertEquals(1, buffer.size());
		assertEquals(1.0, ValueUtil.getDouble(buffer.poll()), 0.01);
		assertEquals(0, buffer.size());
		buffer.clear();
	}

	/** Check Overrun */
	@Test
	public void testOverrun()
	{
		// Fill buffer
		assertEquals(0, buffer.size());
		for (int i = 0; i < buffer.getCapacity(); ++i) {
            buffer.add(TestValueFactory.getDouble(i)); // without blocking
        }
		assertEquals(CAPACITY, buffer.size());
		assertEquals(0, buffer.getBufferStats().getOverruns());

		// Cause overrun
		buffer.add(TestValueFactory.getDouble(-1.0));
		assertEquals(CAPACITY, buffer.size());
		assertEquals(1, buffer.getBufferStats().getOverruns());

		// Value 0 was dropped by overrun, oldest sample now 1
		final IValue value = buffer.poll();
		assertEquals(1.0, ValueUtil.getDouble(value), 0.01);

		buffer.clear();
	}


	private final static long TEST_RUNS = 1000;

	class BlockingFillThread extends Thread {
		private final SampleBuffer _buffer;
        /**
         * Constructor.
         * @param buffer
         */
        public BlockingFillThread(final SampleBuffer buffer) {
            _buffer = buffer;
        }

        @Override
        public void run()
        {
			for (long i=1; i<=TEST_RUNS; ++i) {
				try {
				    _buffer.put(TestValueFactory.getDouble(i)); // blocking
	                sleep(5);
                } catch (final InterruptedException e) {
	                e.printStackTrace();
                }
			}
        }
	}
	class NonBlockingFillThread extends Thread {
	    private final SampleBuffer _buffer;
	    /**
	     * Constructor.
	     * @param buffer
	     */
	    public NonBlockingFillThread(final SampleBuffer buffer) {
	        _buffer = buffer;
	    }

	    @Override
	    public void run()
	    {
	        for (long i=1; i<=TEST_RUNS; ++i) {
	            try {
	                _buffer.add(TestValueFactory.getDouble(i)); // non blocking
	                sleep(5);
	            } catch (final InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}


	/** Check tread access
	 * @throws Exception on thread error
	 */
	@Test
	public void testBlocking() throws Exception
	{
	    final SampleBuffer sb = new SampleBuffer("Demo2", 100);

	    // Fill buffer in background thread
		final BlockingFillThread fill = new BlockingFillThread(sb);
		fill.start();

		int number = 1;
		// Empty it in this thread
		while (true) {
		    final double value = ValueUtil.getDouble(sb.take()); // blocking now enabled
		    Assert.assertEquals((double) number, value);

		    if (++number >= TEST_RUNS) {
		        break;
		    }
		}
		Assert.assertEquals(number, TEST_RUNS);
	}

	@Test
	public void testNonBlocking() throws Exception {


        final int cap = 100;
        final SampleBuffer sbNb = new SampleBuffer("Demo2", cap);
        // Fill buffer in background thread
        final NonBlockingFillThread fill2 = new NonBlockingFillThread(sbNb);
        fill2.start();

        int number = 1, time = 0;
        // Empty it in this thread
        while (time < 1000) {
            final IValue sample = sbNb.poll();
            if (sample != null) {
                final double value = ValueUtil.getDouble(sample);
                number++;
            } else {
                Thread.sleep(5);
                time += 5;
            }
        }
        Assert.assertTrue(number >= cap && number <= TEST_RUNS); // at least cap values, probably more

	}
}
