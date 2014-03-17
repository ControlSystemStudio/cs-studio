/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A PV that can be both read and written. In general, the read payload will be
 * different from the write payload.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class PV<R, W> implements PVReader<R>, PVWriter<W> {
    
    // This class is a wrapper around a reader and a write. It has no logic by
    // itself, and just forwards the messages to the appropriate object.
    
    private final PVReader<R> reader;
    private final PVWriter<W> writer;

    PV(PVReader<R> reader, PVWriter<W> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void addPVWriterListener(PVWriterListener<? extends W> listener) {
        writer.addPVWriterListener(listener);
    }

    @Override
    public void removePVWriterListener(PVWriterListener<? extends W> listener) {
        writer.removePVWriterListener(listener);
    }

    @Override
    public void write(W newValue) {
        writer.write(newValue);
    }

    @Override
    public Exception lastWriteException() {
        return writer.lastWriteException();
    }

    @Override
    public void addPVReaderListener(PVReaderListener<? super R> listener) {
        reader.addPVReaderListener(listener);
    }

    @Override
    public void addPVReaderListener(Class<?> clazz, PVReaderListener listener) {
        reader.addPVReaderListener(clazz, listener);
    }

    @Override
    public void removePVReaderListener(PVReaderListener<? super R> listener) {
        reader.removePVReaderListener(listener);
    }

    @Override
    public String getName() {
        return reader.getName();
    }

    @Override
    public R getValue() {
        return reader.getValue();
    }

    @Override
    public void close() {
        reader.close();
        writer.close();
    }

    @Override
    public boolean isClosed() {
        return reader.isClosed();
    }

    @Override
    public Exception lastException() {
        return reader.lastException();
    }

    @Override
    public boolean isPaused() {
        return reader.isPaused();
    }

    @Override
    public void setPaused(boolean paused) {
        reader.setPaused(paused);
    }

    @Override
    public boolean isConnected() {
        return reader.isConnected();
    }

    @Override
    public boolean isWriteConnected() {
        return writer.isWriteConnected();
    }
    
}
