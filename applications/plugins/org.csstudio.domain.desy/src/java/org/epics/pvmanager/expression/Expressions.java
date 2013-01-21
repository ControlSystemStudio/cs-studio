/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.Function;

/**
 * Utility class for expressions.
 *
 * @author carcassi
 */
public class Expressions {

    /**
     * Extract the list of functions from an expression list.
     * 
     * @param list the expressions
     * @return the functions of the expression
     */
    public static List<Function<?>> functionsOf(DesiredRateExpressionList<?> list) {
        List<Function<?>> result = new ArrayList<Function<?>>();
        for (DesiredRateExpression<?> desiredRateExpression : list.getDesiredRateExpressions()) {
            result.add(desiredRateExpression.getFunction());
        }
        return result;
    }
}
