/*
 * Copyright 2008-2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Orchestrates the different classes to perform writes.
 *
 * @author carcassi
 */
class WriteDirector<T> {
    
    private static final Logger log = Logger.getLogger(WriteDirector.class.getName());
    
    private final WriteFunction<T> writeFunction;
    private final WriteBuffer writeBuffer;
    private final DataSource dataSource;
    private final Executor executor;
    private final ExceptionHandler exceptionHandler;

    public WriteDirector(WriteFunction<T> writeFunction, WriteBuffer writeBuffer, DataSource dataSource, Executor executor, ExceptionHandler exceptionHandler) {
        this.writeFunction = writeFunction;
        this.writeBuffer = writeBuffer;
        this.dataSource = dataSource;
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
    }
    
    void init() {
        dataSource.prepareWrite(writeBuffer, exceptionHandler);
    }
    
    void write(final T newValue, final PVWriterImpl<T> pvWriter) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                synchronized(writeBuffer) {
                    writeFunction.setValue(newValue);
                    dataSource.write(writeBuffer, new Runnable() {

                        @Override
                        public void run() {
                            pvWriter.firePvValueWritten();
                        }
                    }, exceptionHandler);
                }
            }
        });
    }
    
    void syncWrite(final T newValue, final PVWriterImpl<T> pvWriter) {
        log.finest("Sync write: creating latch");
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Exception> exception = new AtomicReference<Exception>();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized(writeBuffer) {
                        writeFunction.setValue(newValue);
                        dataSource.write(writeBuffer, new Runnable() {

                            @Override
                            public void run() {
                                log.finest("Writing done, releasing latch");
                                pvWriter.firePvValueWritten();
                                latch.countDown();
                            }
                        }, new ExceptionHandler() {

                            @Override
                            public void handleException(Exception ex) {
                                exception.set(ex);
                                exceptionHandler.handleException(ex);
                                latch.countDown();
                            }

                        });
                    }
                } catch (RuntimeException ex) {
                    exception.set(ex);
                    latch.countDown();
                    exceptionHandler.handleException(ex);
                }
            }
        });
        log.finest("Write request submitted. Waiting.");
        
        try {
            latch.await();
        } catch(InterruptedException ex) {
            throw new RuntimeException("Interrupted", ex);
        }
        if (exception.get() != null) {
            throw new RuntimeException("Write failed", exception.get());
        }
        log.finest("Waiting done. No exceptions.");
    }
    
    void close() {
        dataSource.concludeWrite(writeBuffer, exceptionHandler);
    }
    
}
