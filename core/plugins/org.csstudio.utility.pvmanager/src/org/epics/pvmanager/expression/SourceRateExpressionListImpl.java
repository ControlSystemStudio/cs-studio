/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation class for {@link SourceRateExpressionList}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class SourceRateExpressionListImpl<R> implements SourceRateExpressionList<R> {
    
    private List<SourceRateExpression<R>> sourceRateExpressions;
    
    final void addThis() {
        sourceRateExpressions.add((SourceRateExpression<R>) this);
    }

    /**
     * Creates a new empty expression list.
     */
    public SourceRateExpressionListImpl() {
        this.sourceRateExpressions = new ArrayList<SourceRateExpression<R>>();
    }

    SourceRateExpressionListImpl(Collection<? extends SourceRateExpression<R>> sourceRateExpressions) {
        this.sourceRateExpressions = new ArrayList<SourceRateExpression<R>>(sourceRateExpressions);
    }
    
    @Override
    public final SourceRateExpressionListImpl<R> and(SourceRateExpressionList<? extends R> expressions) {
        @SuppressWarnings("unchecked")
        SourceRateExpressionList<R> newExpression = (SourceRateExpressionList<R>) (SourceRateExpressionList) expressions;
        sourceRateExpressions.addAll(newExpression.getSourceRateExpressions());
        return this;
    }

    @Override
    public final List<SourceRateExpression<R>> getSourceRateExpressions() {
        return sourceRateExpressions;
    }
    
}
