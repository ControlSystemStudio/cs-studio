/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory methods for ThreadFactory. The default implementations unfortunately
 * only use generic names for the thread, which makes it harder to debug.
 *
 * @author carcassi
 */
public class ThreadFactories {
    
    /**
     * A thread factory where each new thread starts with the given name. The
     * name of the thread will be poolname + number
     * 
     * @param poolName name of the pool
     * @return a new factory
     */
    public static ThreadFactory namedPool(String poolName) {
        return new DefaultThreadFactory(poolName);
    }
    
    /**
     * Taken from {@link Executors#defaultThreadFactory() }.
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = poolName;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }    
}
