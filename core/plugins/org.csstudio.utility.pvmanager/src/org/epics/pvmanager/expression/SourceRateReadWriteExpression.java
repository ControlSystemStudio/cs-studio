/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

/**
 * An expression to write and to read at the rate of the source.
 * <p>
 * Don't implement objects with this interface, use {@link SourceRateReadWriteExpressionImpl}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public interface SourceRateReadWriteExpression<R, W> extends SourceRateExpression<R>, WriteExpression<W>, SourceRateReadWriteExpressionList<R, W> {
    
    // Override so that the return type is appropriate
    @Override
    public SourceRateReadWriteExpression<R, W> as(String name);
}
