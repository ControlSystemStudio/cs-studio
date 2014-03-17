/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.ReadFunction;

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
    public static List<ReadFunction<?>> functionsOf(DesiredRateExpressionList<?> list) {
        List<ReadFunction<?>> result = new ArrayList<ReadFunction<?>>();
        for (DesiredRateExpression<?> desiredRateExpression : list.getDesiredRateExpressions()) {
            result.add(desiredRateExpression.getFunction());
        }
        return result;
    }
}
