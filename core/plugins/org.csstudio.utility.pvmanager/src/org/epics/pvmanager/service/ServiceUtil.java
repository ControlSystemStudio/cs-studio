/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.service;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.epics.pvmanager.WriteFunction;

/**
 *
 * @author carcassi
 */
public class ServiceUtil {
        
    public static Map<String, Object> syncExecuteMethod(ServiceMethod method, Map<String, Object> parameters) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Map<String, Object>> result = new AtomicReference<>();
        final AtomicReference<Exception> exception = new AtomicReference<>();
        method.execute(parameters, new WriteFunction<Map<String, Object>>() {

            @Override
            public void writeValue(Map<String, Object> newValue) {
                result.set(newValue);
                latch.countDown();
            }
        }, new WriteFunction<Exception>() {

            @Override
            public void writeValue(Exception newValue) {
                exception.set(newValue);
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted", ex);
        }
        
        if (result.get() != null) {
            return result.get();
        }
        
        throw new RuntimeException("Failed", exception.get());
    }

}
