/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;

/**
 * An list of expressions to write and to read at the desired rate.
 * <p>
 * Don't implement objects with this interface, use {@link DesiredRateReadWriteExpressionListImpl}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public interface DesiredRateReadWriteExpressionList<R, W> extends DesiredRateExpressionList<R>, WriteExpressionList<W> {
    
    /**
     * Adds the given expressions to this list.
     * 
     * @param expressions a list of expressions
     * @return this
     */
    public DesiredRateReadWriteExpressionList<R, W> and(DesiredRateReadWriteExpressionList<? extends R, ? extends W> expressions);

    /**
     * The expressions of this list.
     * 
     * @return a list of expressions
     */
    public List<DesiredRateReadWriteExpression<R, W>> getDesiredRateReadWriteExpressions();
}
