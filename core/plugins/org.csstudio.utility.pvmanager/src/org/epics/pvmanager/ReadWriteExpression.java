/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Represents an expression that can be both read and written.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class ReadWriteExpression<R, W> implements SourceRateExpression<R>, WriteExpression<W> {
    
    private final SourceRateExpression<R> sourceRateExpression;
    private final WriteExpression<W> writeExpression;

    /**
     * Creates an expression that can be both read and written.
     * 
     * @param sourceRateExpression the read part of the expression
     * @param writeExpression the write part of the expression
     */
    public ReadWriteExpression(SourceRateExpression<R> sourceRateExpression, WriteExpression<W> writeExpression) {
        this.sourceRateExpression = sourceRateExpression;
        this.writeExpression = writeExpression;
    }

    @Override
    public String getDefaultName() {
        return sourceRateExpression.getDefaultName();
    }

    @Override
    public Function<R> getFunction() {
        return sourceRateExpression.getFunction();
    }
    
    SourceRateExpressionImpl<R> getSourceRateExpressionImpl() {
        return SourceRateExpressionImpl.implOf(sourceRateExpression);
    }
    
    WriteExpressionImpl<W> getWriteExpressionImpl() {
        return WriteExpressionImpl.implOf(writeExpression);
    }
    
}
