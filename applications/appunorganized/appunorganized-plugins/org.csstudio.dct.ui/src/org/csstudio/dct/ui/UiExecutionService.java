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
package org.csstudio.dct.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.csstudio.platform.ExecutionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * FIXME: Stattdessen besser einen Threadpool verwenden??
 *
 * A thread which bundles property and value changes, that are received from the
 * DAL layer, and forwards them to the SDS models in a single
 * Display.syncExec(). This way we avoid slow downs, that occur on several
 * operating systems, when Display.syncExec() is called very often from
 * background threads.
 *
 * This thread sleeps for a time, which is below the processing capacity of
 * human eyes and brain - so the user will not feel any delay.
 *
 * @author Sven Wende
 *
 */
public final class UiExecutionService {
    /**
     * The singleton instance.
     */
    private static UiExecutionService _instance;

    /**
     * A queue, which contains runnables that process the events that occured
     * during the last SLEEP_TIME milliseconds.
     */
    private Queue<Runnable> _queue;

    /**
     * Standard constructor.
     */
    private UiExecutionService() {
        _queue = new ConcurrentLinkedQueue<Runnable>();

        ExecutionService.getInstance().getScheduledExecutorService()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        Display display = Display.getCurrent();

                        if (display == null) {
                            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    Runnable r;

                                    while ((r = _queue.poll()) != null) {
                                        r.run();
                                    }
                                }
                            });
                        } else {
                            Runnable r;

                            while ((r = _queue.poll()) != null) {
                                r.run();
                            }
                        }
                    }
                }, 1000, 10, TimeUnit.MILLISECONDS);
    }

    /**
     * Queues a job to be run within the UI thread.
     *
     * @param runnable
     *            the runnable
     */
    public void queue(final Runnable runnable) {
        _queue.add(runnable);

    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized UiExecutionService getInstance() {
        if (_instance == null) {
            _instance = new UiExecutionService();
        }

        return _instance;
    }


}
