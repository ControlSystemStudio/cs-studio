/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.DataRecipeBuilder;
import org.epics.pvmanager.Function;

/**
 * Implementation class for {@link DesiredRateExpression}.
 *
 * @param <R> type of the read payload
 * @author carcassi
 */
public class DesiredRateExpressionImpl<R> extends DesiredRateExpressionListImpl<R> implements DesiredRateExpression<R> {

    private final DataRecipeBuilder recipe;
    private final Function<R> function;
    private String name;
    
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
    public DesiredRateExpressionImpl(SourceRateExpression<?> expression, Function<R> collector, String defaultName) {
        if (!(collector instanceof Collector)){
            throw new IllegalArgumentException("collector must be of type Collector");
        }
        this.recipe = expression.getSourceRateExpressionImpl().createDataRecipe((Collector) collector);
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
    public DesiredRateExpressionImpl(DesiredRateExpressionList<?> childExpressions, Function<R> function, String defaultName) {
        this.recipe = combineRecipes(childExpressions);
        this.function = function;
        this.name = defaultName;
    }

    private static DataRecipeBuilder combineRecipes(DesiredRateExpressionList<?> expressions) {
        if (expressions == null || expressions.getDesiredRateExpressions().isEmpty())
            return new DataRecipeBuilder();

        DataRecipeBuilder recipe = expressions.getDesiredRateExpressions().get(0).getDesiredRateExpressionImpl().recipe;
        for (int i = 1; i < expressions.getDesiredRateExpressions().size(); i++) {
            DataRecipeBuilder newRecipe = expressions.getDesiredRateExpressions().get(i).getDesiredRateExpressionImpl().recipe;
            recipe.addAll(newRecipe);
        }

        return recipe;
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

    /**
     * The recipe for connect the channels for this expression.
     *
     * @return a data recipe
     */
    @Override
    public final DataRecipe getDataRecipe() {
        return recipe.build();
    }

    /**
     * The function that calculates new values for this expression.
     *
     * @return a function
     */
    @Override
    public final Function<R> getFunction() {
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
