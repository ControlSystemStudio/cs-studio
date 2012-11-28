/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;

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
    private final ScheduledExecutorService executor;
    private final Executor notificationExecutor;
    private final ExceptionHandler exceptionHandler;
    private final TimeDuration timeout;
    private final String timeoutMessage;

    public WriteDirector(WriteFunction<T> writeFunction, WriteBuffer writeBuffer, DataSource dataSource,
            ScheduledExecutorService executor, Executor notificationExecutor, ExceptionHandler exceptionHandler,
            TimeDuration timeout, String timeoutMessage) {
        this.writeFunction = writeFunction;
        this.writeBuffer = writeBuffer;
        this.dataSource = dataSource;
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
        this.notificationExecutor = notificationExecutor;
        this.timeout = timeout;
        this.timeoutMessage = timeoutMessage;
    }
    
    void init() {
        dataSource.prepareWrite(writeBuffer, exceptionHandler);
    }
    
    void write(final T newValue, final PVWriterImpl<T> pvWriter) {
        WriteTask newTask = new WriteTask(pvWriter, newValue);
        executor.execute(newTask);
        if (timeout != null) {
            executor.schedule(newTask.timeout(), timeout.toNanosLong(), TimeUnit.NANOSECONDS);
        }
    }
    
    private class WriteTask implements Runnable {
        final PVWriterImpl<T> pvWriter;
        final T newValue;
        private AtomicBoolean done = new AtomicBoolean();

        public WriteTask(PVWriterImpl<T> pvWriter, T newValue) {
            this.pvWriter = pvWriter;
            this.newValue = newValue;
        }
        
        private Runnable timeout() {
            return new Runnable() {

                @Override
                public void run() {
                    if (!done.get()) {
                        exceptionHandler.handleException(new TimeoutException(timeoutMessage));
                    }
                }
            };
        }

        @Override
        public void run() {
            synchronized(writeBuffer) {
                writeFunction.setValue(newValue);
                dataSource.write(writeBuffer, new Runnable() {

                    @Override
                    public void run() {
                        done.set(true);
                        notificationExecutor.execute(new Runnable() {

                            @Override
                            public void run() {
                                pvWriter.fireWriteSuccess();
                            }
                        });
                    }
                }, new ExceptionHandler() {

                    @Override
                    public void handleException(final Exception ex) {
                        boolean previousDone = done.getAndSet(true);
                        if (!previousDone) {
                            notificationExecutor.execute(new Runnable() {

                                @Override
                                public void run() {
                                    pvWriter.fireWriteFailure(ex);
                                }
                            });
                        } else {
                            pvWriter.setLastWriteException(ex);
                        }
                    }
                    
                });
            }
        }
    
    };
    
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
                                notificationExecutor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        pvWriter.fireWriteSuccess();
                                        latch.countDown();
                                    }
                                });
                            }
                        }, new ExceptionHandler() {

                            @Override
                            public void handleException(final Exception ex) {
                                exception.set(ex);
                                notificationExecutor.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        pvWriter.fireWriteFailure(ex);
                                        latch.countDown();
                                    }
                                });
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
