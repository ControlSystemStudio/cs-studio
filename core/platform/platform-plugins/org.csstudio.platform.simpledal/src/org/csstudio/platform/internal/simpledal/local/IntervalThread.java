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
package org.csstudio.platform.internal.simpledal.local;

import java.util.Date;

/**
 * Thread that encapsulates a <code>java.lang.Runnable</code> and repeatedly
 * runs it in a defined interval.
 *
 * @author Alexander Will, Sven Wende
 * @version $Revision$
 */
public final class IntervalThread extends Thread {

    /**
     * The encapsulated <code>java.lang.Runnable</code>.
     */
    private Runnable _runnable;

    /**
     * Flag that indicates if the thread should continue its execution.
     */
    private boolean _running;

    /**
     * The time periode that lies between two executions of the encapsulated
     * <code>java.lang.Runnable</code>.
     */
    private int _refreshRate;

    /**
     * Standard constructor.
     *
     * @param runnable
     *            the encapsulated <code>java.lang.Runnable</code>.
     * @param refreshRate
     *            the time periode that lies between two executions of the
     *            encapsulated <code>java.lang.Runnable</code>.
     */
    public IntervalThread(final Runnable runnable, final int refreshRate) {
        _runnable = runnable;
        _refreshRate = refreshRate;
        _running = true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        while (_running) {
            if (_runnable != null) {
                _runnable.run();
            }

            long targetMs = new Date().getTime() + _refreshRate;

            while (System.currentTimeMillis() < targetMs) {
                yield();
            }
        }
    }

    /**
     * Tell this thread that it should stop its execution.
     */
    public void stopRunning() {
        _runnable = null;

        _running = false;

    }
}
