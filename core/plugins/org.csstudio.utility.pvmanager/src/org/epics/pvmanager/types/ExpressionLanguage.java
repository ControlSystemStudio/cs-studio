/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.types;

import org.epics.pvmanager.DesiredRateExpression;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.NotificationSupport;
import org.epics.pvmanager.data.DataTypeSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides support for the standard types and the basic building blocks of
 * the expression language.
 *
 * @author carcassi
 */
public class ExpressionLanguage {

    static {
        // Installs support for standard types
        DataTypeSupport.install();
    }

    /**
     * Converts a list of expressions to and expression that returns the list of results.
     * @param expression a list of expressions
     * @return an expression representing the list of results
     */
    public static <T> DesiredRateExpression<List<T>> listOf(DesiredRateExpression<T>... expressions) {
        return listOf(Arrays.asList(expressions));
    }

    /**
     * Converts a list of expressions to and expression that returns the list of results.
     * @param expression a list of expressions
     * @return an expression representing the list of results
     */
    public static <T> DesiredRateExpression<List<T>> listOf(List<DesiredRateExpression<T>> expressions) {
        // Calculate all the needed functions to combine
        List<Function> functions = new ArrayList<Function>();
        for (DesiredRateExpression<T> expression : expressions) {
            functions.add(expression.getFunction());
        }

        // If the list of expression is large, the name is going to be big
        // and it might trigger an OutOfMemoryException just for this.
        // We cap the list of names to 10
        String name = null;
        if (expressions.size() < 10) {
            name = "list" + expressions;
        } else {
            name = "list(...)";
        }

        @SuppressWarnings("unchecked")
        DesiredRateExpression<List<T>> expression = new DesiredRateExpression<List<T>>((List<DesiredRateExpression<?>>) (List) expressions,
                (Function<List<T>>) (Function) new ListOfFunction(functions), name);
        return expression;
    }

}
