/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation class for {@link SourceRateReadWriteExpressionList}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class SourceRateReadWriteExpressionListImpl<R, W> implements SourceRateReadWriteExpressionList<R, W> {
    
    private List<SourceRateReadWriteExpression<R, W>> sourceRateReadWriteExpressions = new ArrayList<SourceRateReadWriteExpression<R, W>>();
    
    final void addThis() {
        sourceRateReadWriteExpressions.add((SourceRateReadWriteExpression<R, W>) this);
    }

    @Override
    public final SourceRateReadWriteExpressionList<R, W> and(SourceRateReadWriteExpressionList<? extends R, ? extends W> expressions) {
        @SuppressWarnings("unchecked")
        SourceRateReadWriteExpressionList<R, W> newExpression = (SourceRateReadWriteExpressionList<R, W>) (SourceRateReadWriteExpressionList) expressions;
        sourceRateReadWriteExpressions.addAll(newExpression.getSourceRateReadWriteExpressions());
        return this;
    }

    @Override
    public final List<SourceRateReadWriteExpression<R, W>> getSourceRateReadWriteExpressions() {
        return sourceRateReadWriteExpressions;
    }

    @Override
    public final SourceRateExpressionList<R> and(SourceRateExpressionList<? extends R> expressions) {
        @SuppressWarnings("unchecked")
        SourceRateExpressionList<R> newExpression = (SourceRateExpressionList<R>) (SourceRateExpressionList) expressions;
        return new SourceRateExpressionListImpl<R>(sourceRateReadWriteExpressions).and(newExpression);
    }

    @Override
    public final List<SourceRateExpression<R>> getSourceRateExpressions() {
        return Collections.<SourceRateExpression<R>>unmodifiableList(sourceRateReadWriteExpressions);
    }

    @Override
    public final WriteExpressionList<W> and(WriteExpressionList<? extends W> expressions) {
        @SuppressWarnings("unchecked")
        WriteExpressionList<W> newExpression = (WriteExpressionList<W>) (WriteExpressionList) expressions;
        return new WriteExpressionListImpl<W>(sourceRateReadWriteExpressions).and(newExpression);
    }

    @Override
    public final List<WriteExpression<W>> getWriteExpressions() {
        return Collections.<WriteExpression<W>>unmodifiableList(sourceRateReadWriteExpressions);
    }
    
}
