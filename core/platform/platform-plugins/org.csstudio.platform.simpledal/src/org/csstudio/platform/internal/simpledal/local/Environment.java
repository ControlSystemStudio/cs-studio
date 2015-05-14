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
package org.csstudio.platform.internal.simpledal.local;

import java.util.concurrent.LinkedBlockingQueue;

//import org.csstudio.desy.startuphelper.CSSPlatformInfo;
import org.csstudio.platform.ExecutionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private static final Logger LOG = LoggerFactory.getLogger(Environment.class);

    public static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    public static String getApplicationId() {
        return CSSPlatformInfo.getInstance().getApplicationId();
    }

    public static String getHostId() {
        return CSSPlatformInfo.getInstance().getHostId();
    }

    public static String getQualifiedHostname() {
        return CSSPlatformInfo.getInstance().getQualifiedHostname();
    }

    public static String getUserId() {
        return CSSPlatformInfo.getInstance().getUserId();
    }

    public static Integer getNumberOfActiveConnectors() {
        return ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService().getNumberOfActiveConnectors();
    }

    public static Long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static Long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static Long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    public static long getQueueSize() {
        return queue.size();
    }

    public static int getHighPriorityQueueSize() {
        return ExecutionService.getInstance().getHighPriorityQueueSize();
    }

    public static int getNormalPriorityQueueSize() {
        return ExecutionService.getInstance().getNormalPriorityQueueSize();
    }

    public static int getLowPriorityQueueSize() {
        return ExecutionService.getInstance().getLowPriorityQueueSize();
    }
}
