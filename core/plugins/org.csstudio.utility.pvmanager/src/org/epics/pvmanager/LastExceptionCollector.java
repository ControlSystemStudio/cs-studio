/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * A collector that forwards exceptions to the handler.
 *
 * @author carcassi
 */
class LastExceptionCollector extends QueueCollector<Exception> {

    private final ExceptionHandler exceptionHandler;
    
    public LastExceptionCollector(int maxSize, ExceptionHandler exceptionHandler) {
        super(maxSize);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void writeValue(Exception newValue) {
        super.writeValue(newValue);
        exceptionHandler.handleException(newValue);
    }
    
}
