/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation class for {@link DesiredRateExpressionList}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class DesiredRateExpressionListImpl<R> implements DesiredRateExpressionList<R> {
    
    private List<DesiredRateExpression<R>> desiredRateExpressions;
    
    final void addThis() {
        desiredRateExpressions.add((DesiredRateExpression<R>) this);
    }

    /**
     * Creates a new empty expression list.
     */
    public DesiredRateExpressionListImpl() {
        this.desiredRateExpressions = new ArrayList<DesiredRateExpression<R>>();
    }

    DesiredRateExpressionListImpl(Collection<? extends DesiredRateExpression<R>> desiredRateExpressions) {
        this.desiredRateExpressions = new ArrayList<DesiredRateExpression<R>>(desiredRateExpressions);
    }
    
    @Override
    public final DesiredRateExpressionListImpl<R> and(DesiredRateExpressionList<? extends R> expressions) {
        @SuppressWarnings("unchecked")
        DesiredRateExpressionList<R> newExpression = (DesiredRateExpressionList<R>) (DesiredRateExpressionList) expressions;
        desiredRateExpressions.addAll(newExpression.getDesiredRateExpressions());
        return this;
    }

    @Override
    public final List<DesiredRateExpression<R>> getDesiredRateExpressions() {
        return desiredRateExpressions;
    }
    
}
