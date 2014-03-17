/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation class for {@link DesiredRateReadWriteExpressionList}.
 *
 * @param <R> type of read payload
 * @param <W> type of write payload
 * @author carcassi
 */
public class DesiredRateReadWriteExpressionListImpl<R, W> implements DesiredRateReadWriteExpressionList<R, W> {
    
    private List<DesiredRateReadWriteExpression<R, W>> desiredRateReadWriteExpressions = new ArrayList<DesiredRateReadWriteExpression<R, W>>();
    
    final void addThis() {
        desiredRateReadWriteExpressions.add((DesiredRateReadWriteExpression<R, W>) this);
    }

    @Override
    public final DesiredRateReadWriteExpressionList<R, W> and(DesiredRateReadWriteExpressionList<? extends R, ? extends W> expressions) {
        @SuppressWarnings("unchecked")
        DesiredRateReadWriteExpressionList<R, W> newExpression = (DesiredRateReadWriteExpressionList<R, W>) (DesiredRateReadWriteExpressionList) expressions;
        desiredRateReadWriteExpressions.addAll(newExpression.getDesiredRateReadWriteExpressions());
        return this;
    }

    @Override
    public final List<DesiredRateReadWriteExpression<R, W>> getDesiredRateReadWriteExpressions() {
        return desiredRateReadWriteExpressions;
    }

    @Override
    public final DesiredRateExpressionList<R> and(DesiredRateExpressionList<? extends R> expressions) {
        @SuppressWarnings("unchecked")
        DesiredRateExpressionList<R> newExpression = (DesiredRateExpressionList<R>) (DesiredRateExpressionList) expressions;
        return new DesiredRateExpressionListImpl<R>(desiredRateReadWriteExpressions).and(newExpression);
    }

    @Override
    public final List<DesiredRateExpression<R>> getDesiredRateExpressions() {
        return Collections.<DesiredRateExpression<R>>unmodifiableList(desiredRateReadWriteExpressions);
    }

    @Override
    public final WriteExpressionList<W> and(WriteExpressionList<? extends W> expressions) {
        @SuppressWarnings("unchecked")
        WriteExpressionList<W> newExpression = (WriteExpressionList<W>) (WriteExpressionList) expressions;
        return new WriteExpressionListImpl<W>(desiredRateReadWriteExpressions).and(newExpression);
    }

    @Override
    public final List<WriteExpression<W>> getWriteExpressions() {
        return Collections.<WriteExpression<W>>unmodifiableList(desiredRateReadWriteExpressions);
    }
    
}
