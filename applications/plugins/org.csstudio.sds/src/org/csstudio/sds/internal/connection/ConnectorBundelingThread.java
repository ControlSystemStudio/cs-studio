/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.connection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread which bundles connection attempts. If a connector cannot connect, it
 * re-adds itself to the queue.
 *
 * @deprecated
 * @author Sven Wende
 *
 */
public final class ConnectorBundelingThread extends Thread {
    /**
     * The singleton instance.
     */
    private static ConnectorBundelingThread _instance;

    /**
     * Flag that indicates if the thread should continue its execution.
     */
    private boolean _running;

    /**
     * A queue, which contains runnables that process the events that occured
     * during the last SLEEP_TIME milliseconds.
     */
    private Queue<Runnable> _queue;

    private static final Logger LOG = LoggerFactory.getLogger(ConnectorBundelingThread.class);

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static ConnectorBundelingThread getInstance() {
        if (_instance == null) {
            _instance = new ConnectorBundelingThread();
            _instance.start();
        }

        return _instance;
    }

    /**
     * Standard constructor.
     */
    private ConnectorBundelingThread() {
        _running = true;
        _queue = new ConcurrentLinkedQueue<Runnable>();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        while (_running) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                LOG.info(e.toString());
            }

            processQueue();
            yield();
        }
    }

    /**
     * Stops the execution of this BundelingThread.
     */
    public void stopExecution() {
        _running = false;
    }

    /**
     * Process the complete queue.
     */
    private void processQueue() {
        if (_queue.size() > 0) {
            Runnable r;

            while ((r = _queue.poll()) != null) {
                r.run();
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds the specified runnable to the queue.
     *
     * @param runnable
     *            the runnable
     */
    public void addConnector(final Runnable runnable) {
        _queue.add(runnable);
    }
}
