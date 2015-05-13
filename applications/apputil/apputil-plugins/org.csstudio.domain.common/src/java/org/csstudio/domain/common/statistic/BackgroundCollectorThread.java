/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.common.statistic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BackgroundCollectorThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundCollectorThread.class);

    private int timeout = 0;
    private final boolean runForever = true;
    static final double MB = 1024.0 * 1024.0;

    BackgroundCollectorThread(final int timeout) {
        this.timeout = timeout;
        LOG.info("BackgroundCollectorThread started");
        this.start();
    }

    @Override
    public final void run() {

        while (runForever) {

            BackgroundCollector.getInstance().getMemoryAvailableApplication()
                    .setValue(new Double(Runtime.getRuntime().freeMemory() / MB));
            BackgroundCollector.getInstance().getMemoryUsedApplication()
                    .setValue(new Double(Runtime.getRuntime().totalMemory() / MB));
            BackgroundCollector.getInstance().getMemoryUsedSystem()
                    .setValue(new Double(Runtime.getRuntime().maxMemory() / MB));
            //        TODO: find out how to fill these!
            //        before uncommenting: enable instanciating in BackgroundCollector!!
            //        BackgroundCollector.getInstance().getCpuUsedApplication().setValue
            //        BackgroundCollector.getInstance().getCpuUsedSystem().setValue

            try {
                Thread.sleep(this.timeout);

            } catch (InterruptedException e) {
                // TODO: handle exception
            } finally {
                //clean up
            }
        }
//        LOG.info("BackgroundCollectorThread stopped");

    }

}
