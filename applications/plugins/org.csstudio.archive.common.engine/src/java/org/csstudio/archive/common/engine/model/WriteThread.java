/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.stats.Average;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
public class WriteThread implements Runnable {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(WriteThread.class);

    /** Minimum write period [seconds] */
    private static final double MIN_WRITE_PERIOD = 5.0;

    private final ConcurrentMap<String, ArchiveChannel<Object, ICssAlarmValueType<Object>>> _channelMap =
        Maps.newConcurrentMap();

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
    private Thread _thread;


    /**
     * Construct thread for writing to server
     */
    public WriteThread() {
        // Empty
    }

    /** Add a channel's buffer that this thread reads */
    public void addChannel(final ArchiveChannel<Object, ICssAlarmValueType<Object>> channel) {
        _channelMap.putIfAbsent(channel.getName(), channel);
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
            LOG.warn("Adjusting write period from "
                    + p_write_period + " to " + MIN_WRITE_PERIOD);
            write_period = MIN_WRITE_PERIOD;
        }
        millisec_delay = (int)(1000.0 * write_period);
        batch_size = p_batch_size;
        _thread = new Thread(this, "WriteThread");
        _thread.start();
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
    @Override
    @SuppressWarnings("nls")
    public void run()
    {
        LOG.info("WriteThread starts");

        // establish the connection to the archive.
//        try {
//            Activator.getDefault().getArchiveWriterService().connect(connectionPrefs);
//        } catch (final OsgiServiceUnavailableException e1) {
//            LOG.error("Archive Writer Service unavailable. Did you auto-start the service impl plugin in your launch cfg?");
//            return;
//        } catch (final ArchiveConnectionException e1) {
//            LOG.error("Archive Connection could not be established.");
//            return;
//        }

        final BenchmarkTimer timer = new BenchmarkTimer();
        boolean write_error = false;
        do_run = true;
        while (do_run)
        {
            long delay;
            try {
                // If there was an error before...
//                if (write_error)
//                {   // .. try to reconnect
//                    Activator.getDefault().getArchiveWriterService().reconnect();
//                    // If we get here, all is OK so far ...
//                    write_error = false;
//                    // .. and we continue to write.
//                }
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
                LOG.error("Error, will try to reconnect", e);
                // Use max. delay
                delay = millisec_delay;
                write_error = true;
            } catch (final ArchiveServiceException e) {
                // Error in write() or the preceding reconnect()...
                LOG.error("Error, will try to reconnect", e);
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
                        LOG.error("Interrupted wait", ex);
                    }
                }
            }
        }

//        try {
//            Activator.getDefault().getArchiveWriterService().disconnect();
//        } catch (final OsgiServiceUnavailableException e1) {
//            LOG.error("Archive Writer Service unavailable for disconnection. Ignored.");
//        } catch (final ArchiveConnectionException e1) {
//            LOG.error("Archive Disconnection could not be established. Ignored.");
//        }

        LOG.info("WriteThread exits");
    }

    /** Stop the write thread, performing a final write. */
    public void shutdown() throws Exception {
        // Stop the thread
        stop();
        // Wait for it to end
        _thread.join();
        // Then write once more.
        // Errors in this last write are passed up.
        write();
    }

    /** Write right now until all sample buffers are empty
     *  @return number of samples written
     * @throws OsgiServiceUnavailableException
     * @throws ArchiveServiceException
     */
    private //<V, T extends ICssValueType<V> & IHasAlarm>
    long write() throws OsgiServiceUnavailableException, ArchiveServiceException {
        int totalCount = 0;

        final LinkedList<IArchiveSample<Object, ICssAlarmValueType<Object>>> allSamples = Lists.newLinkedList();

        for (final ArchiveChannel<Object, ICssAlarmValueType<Object>> channel : _channelMap.values()) {
            final SampleBuffer<Object,
                               ICssAlarmValueType<Object>,
                               IArchiveSample<Object, ICssAlarmValueType<Object>>> buffer = channel.getSampleBuffer();

            // Update max buffer length etc. before we start to remove samples
            buffer.updateStats();
            totalCount += buffer.size();
            buffer.drainTo(allSamples);
        }
        final IArchiveWriterService writerService = Activator.getDefault().getArchiveWriterService();
        writerService.writeSamples(allSamples);

        return totalCount;
    }
}
