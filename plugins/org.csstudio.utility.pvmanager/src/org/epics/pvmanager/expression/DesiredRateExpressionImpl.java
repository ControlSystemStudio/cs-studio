/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.Collector;
import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.ReadRecipeBuilder;

/**
 * Implementation class for {@link DesiredRateExpression}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class DesiredRateExpressionImpl<R> extends DesiredRateExpressionListImpl<R> implements DesiredRateExpression<R> {

    private final ReadFunction<R> function;
    private String name;
    private final SourceRateExpression<?> sourceRateChild;
    private final DesiredRateExpressionList<?> desiredRateChildren;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    @Override
    public final DesiredRateExpressionImpl<R> as(String name) {
        this.name = name;
        return this;
    }

    /**
     * Creates a new expression at the desired rate. Use this constructor when making
     * an DesiredRateExpression out of a collector and a SourceRateExpression.
     *
     * @param expression the original source rate expression
     * @param collector the collector for the original source
     * @param defaultName the display name of the expression
     */
    public DesiredRateExpressionImpl(SourceRateExpression<?> expression, Collector<?, R> collector, String defaultName) {
        this.sourceRateChild = expression;
        this.desiredRateChildren = null;
        this.function = collector;
        this.name = defaultName;
    }

    /**
     * Creates a new aggregated expression. Use this constructor when making
     * a {@code DesiredRateExpression} that is a function of a number of
     * {@code DesiredRateExpression}s.
     *
     * @param childExpressions expressions for the arguments of the function
     * @param function the function that calculates the value of the new expression
     * @param defaultName the display name of the expression
     */
    public DesiredRateExpressionImpl(DesiredRateExpressionList<?> childExpressions, ReadFunction<R> function, String defaultName) {
        this.sourceRateChild = null;
        this.desiredRateChildren = childExpressions;
        this.function = function;
        this.name = defaultName;
    }

    /**
     * The default name for a PV of this expression.
     *
     * @return the default name
     */
    @Override
    public final String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillReadRecipe(PVReaderDirector director, ReadRecipeBuilder builder) {
        if (sourceRateChild != null) {
            sourceRateChild.getSourceRateExpressionImpl().fillDataRecipe(director, (Collector) function, builder);
        } else if (desiredRateChildren != null) {
            for (DesiredRateExpression<?> desiredRateExpression : desiredRateChildren.getDesiredRateExpressions()) {
                desiredRateExpression.fillReadRecipe(director, builder);
            }
        }
    }

    /**
     * The function that calculates new values for this expression.
     *
     * @return a function
     */
    @Override
    public final ReadFunction<R> getFunction() {
        return function;
    }

    /**
     * The implementation for this expression.
     * 
     * @return returns the implementation for this desired rate
     */
    @Override
    public final DesiredRateExpressionImpl<R> getDesiredRateExpressionImpl() {
        return this;
    }
    
}
