/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.Function;

/**
 * An expression to read at the rate of the source.
 * <p>
 * Don't implement objects with this interface, use {@link SourceRateExpressionImpl}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public interface SourceRateExpression<R> extends SourceRateExpressionList<R> {
    
    /**
     * Changes the name for this expression
     * 
     * @param name new name
     * @return this
     */
    public SourceRateExpression<R> as(String name);

    /**
     * Name of the expression.
     *
     * @return the expression name
     */
    public String getName();

    /**
     * The function that calculates this expression.
     *
     * @return the expression function
     */
    public Function<R> getFunction();
    
    /**
     * The implementation of this expression.
     * 
     * @return the implementation
     */
    public SourceRateExpressionImpl<R> getSourceRateExpressionImpl();

}
