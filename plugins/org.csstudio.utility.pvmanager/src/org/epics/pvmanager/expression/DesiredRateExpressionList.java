/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;

/**
 * A list of expressions to read at the desired rate.
 * <p>
 * Don't implement objects with this interface, use {@link DesiredRateExpressionListImpl}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public interface DesiredRateExpressionList<R> {
    
    /**
     * Adds the given expressions to this list.
     * 
     * @param expressions a list of expressions
     * @return this
     */
    public DesiredRateExpressionList<R> and(DesiredRateExpressionList<? extends R> expressions);

    /**
     * The expressions of this list.
     * 
     * @return a list of expressions
     */
    public List<DesiredRateExpression<R>> getDesiredRateExpressions();
    
}
