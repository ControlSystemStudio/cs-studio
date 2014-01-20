/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;

/**
 * A list of expressions to read at the rate of the source.
 * <p>
 * Don't implement objects with this interface, use {@link SourceRateExpressionListImpl}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public interface SourceRateExpressionList<R> {
    
    /**
     * Adds the given expressions to this list.
     * 
     * @param expressions a list of expressions
     * @return this
     */
    public SourceRateExpressionList<R> and(SourceRateExpressionList<? extends R> expressions);

    /**
     * The expressions of this list.
     * 
     * @return a list of expressions
     */
    public List<SourceRateExpression<R>> getSourceRateExpressions();
    
}
