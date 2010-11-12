/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine2.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.engine2.Activator;
import org.csstudio.archive.service.ArchiveServiceException;
import org.csstudio.archive.service.IArchiveWriterService;
import org.csstudio.archive.service.adapter.IValueWithChannelId;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.util.stats.Average;

/** Thread that writes values from multiple <code>SampleBuffer</code>s
 *  to an <code>RDBArchiveServer</code>.
 *  <p>
 *  When there are write errors, it sets the sample buffer error state
 *  and tries to reconnect to the database and write again until successful.
 *  Since the Oracle batch mechanism doesn't tell us what exactly failed
 *  in a batch, all the samples that were part of the batch might
 *  be lost.
 *  The channels that add samples to the sample buffer supposedly notice
 *  the error condition and add a special indicator once we recover.
 *
 *  @author Kay Kasemir
 */
public class WriteThread implements Runnable
{
    /** Minimum write period [seconds] */
    private static final double MIN_WRITE_PERIOD = 5.0;

    private static final class ValueWithChannelId implements IValueWithChannelId {

        private final IValue _value;
        private final int _id;

        /**
         * Constructor.
         */
        public ValueWithChannelId(final IValue val, final int id) {
            _value = val;
            _id = id;
        }
        /**
         * {@inheritDoc}
         */
        public IValue getValue() {
            return _value;
        }
        /**
         * {@inheritDoc}
         */
        public int getChannelId() {
            return _id;
        }
    }

    /** Server to which this thread writes. */
   // final private RDBArchive archive;

    /** All the sample buffers this thread writes. */
    final private ArrayList<SampleBuffer> buffers =
        new ArrayList<SampleBuffer>();

    /** Flag that tells the write thread to run or quit. */
    private boolean do_run;

    /** Synchronization block for waiting.
     *  Signaled in stop().
     */
    private final Object wait_block = new Object();

    /** Delay between write runs. */
    private long millisec_delay = 5000;

    /** Number of values to place into one batch */
    private int batch_size = 500;

    /** Time of end of last write run */
    private ITimestamp last_write_stamp = null;

    /** Average number of values per write run */
    private final Average write_count = new Average();

    /** Average duration of write run */
    private final Average write_time = new Average();

    /** Thread the executes this.run() */
    private Thread thread;

    /** Construct thread for writing to server
     *  @param archive RDB to write to
     */
    public WriteThread() {
        // EMPTY
    }

    /** Add a channel's buffer that this thread reads */
    public void addChannel(final ArchiveChannel channel)
    {
        addSampleBuffer(channel.getSampleBuffer());
    }

    /** Add a sample buffer that this thread reads */
    void addSampleBuffer(final SampleBuffer buffer)
    {
        buffers.add(buffer);
    }

    /** Start the write thread.
     *  @param p_write_period Period between writes in seconds
     *  @param p_batch_size Number of values to batch
     */
    @SuppressWarnings("nls")
    public void start(final double p_write_period, final int p_batch_size)
    {
        double write_period = p_write_period;
        if (write_period < MIN_WRITE_PERIOD)
        {
            CentralLogger.getInstance().getLogger(this).warn("Adjusting write period from "
                    + p_write_period + " to " + MIN_WRITE_PERIOD);
            write_period = MIN_WRITE_PERIOD;
        }
        millisec_delay = (int)(1000.0 * write_period);
        batch_size = p_batch_size;
        thread = new Thread(this, "WriteThread");
        thread.start();
    }

    /** Reset statistics */
    public void reset()
    {
        write_count.reset();
        write_time.reset();
    }

    /** Ask the write thread to stop ASAP. */
    private void stop()
    {
        do_run = false;
        synchronized (wait_block)
        {
            wait_block.notify();
        }
    }

    /** @return Timestamp of end of last write run */
    public ITimestamp getLastWriteTime()
    {
        return last_write_stamp;
    }

    /** @return Average number of values per write run */
    public double getWriteCount()
    {
        return write_count.get();
    }

    /** @return  Average duration of write run in seconds */
    public double getWriteDuration()
    {
        return write_time.get();
    }

    /** 'Main loop' of the write thread.
     *  <p>
     *  Writes all values out, then waits.
     *  The idea is that waiting a little for values
     *  to accumulate actually helps, because then
     *  we may write a few values per channel,
     *  so the effort for locating a channel's ID
     *  and the batching actually pays off.
     *  <p>
     *  Since the wait time can be considerable (30 seconds?),
     *  we wait on a semaphore (wait_block), which
     *  can be notified in stop() to cause an ASAP exit.
     */
    @SuppressWarnings("nls")
    public void run()
    {
        CentralLogger.getInstance().getLogger(this).info("WriteThread starts");
        final BenchmarkTimer timer = new BenchmarkTimer();
        boolean write_error = false;
        do_run = true;
        while (do_run)
        {
            long delay;
            try
            {
                // If there was an error before...
                if (write_error)
                {   // .. try to reconnect
                    Activator.getDefault().getArchiveWriterService().reconnect();
                    // If we get here, all is OK so far ...
                    write_error = false;
                    // .. and we continue to write.
                }
                timer.start();
                // In case of a network problem, we can hang in here
                // for a long time...
                long written;
                written = write();

                timer.stop();
                last_write_stamp = TimestampFactory.now();
                write_count.update(written);
                write_time.update(timer.getSeconds());
                // How much of the scheduled delay is left after write()?
                delay = millisec_delay - timer.getMilliseconds();
            } catch (final OsgiServiceUnavailableException e) {
                // Error in write() or the preceding reconnect()...
                CentralLogger.getInstance().getLogger(this).error("Error, will try to reconnect", e);
                // Use max. delay
                delay = millisec_delay;
                write_error = true;
            } catch (final ArchiveServiceException e) {
                // Error in write() or the preceding reconnect()...
                CentralLogger.getInstance().getLogger(this).error("Error, will try to reconnect", e);
                // Use max. delay
                delay = millisec_delay;
                write_error = true;
            }

            SampleBuffer.setErrorState(write_error);
            // See if there's any time left to wait,
            // or if we already used all that time in the last 'write'
            if (delay > 0)
            {
                synchronized (wait_block)
                {
                    try
                    {
                        wait_block.wait(delay);
                    }
                    catch (final InterruptedException ex)
                    {
                        CentralLogger.getInstance().getLogger(this).error("Interrupted wait", ex);
                    }
                }
            }
        }
        CentralLogger.getInstance().getLogger(this).info("WriteThread exists");
    }

    /** Stop the write thread, performing a final write. */
    public void shutdown() throws Exception
    {
        // Stop the thread
        stop();
        // Wait for it to end
        thread.join();
        // Then write once more.
        // Errors in this last write are passed up.
        write();
    }

    /** Write right now until all sample buffers are empty
     *  @return number of samples written
     * @throws OsgiServiceUnavailableException
     * @throws ArchiveServiceException
     */
    private long write() throws OsgiServiceUnavailableException, ArchiveServiceException
    {
        int total_count = 0;

        final IArchiveWriterService writerService = Activator.getDefault().getArchiveWriterService();

        final List<IValueWithChannelId> samples =
            new ArrayList<IValueWithChannelId>(batch_size);

        for (final SampleBuffer buffer : buffers) {
            // Update max buffer length etc. before we start to remove samples
            buffer.updateStats();
            // Gather samples for one channel
            final String channelName = buffer.getChannelName();

            final int channelId = writerService.getChannelId(channelName);

            IValue sample = buffer.remove();
            while (sample != null) {

                // sample handling
                samples.add(new ValueWithChannelId(sample, channelId));
                if (samples.size() >= batch_size) {
                    writerService.writeSamples(samples);
                    total_count += batch_size;
                    samples.clear();
                }

                // metadata handling
                writerService.writeMetaData(channelName, sample);

                // next
                sample = buffer.remove();
            }
        }
        // remaining sample handling of this sample buffer
        if (!samples.isEmpty()) {
            writerService.writeSamples(samples);
            total_count += samples.size();
            samples.clear();
        }

        return total_count;
    }
}
