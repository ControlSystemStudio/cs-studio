/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.ReadRecipeBuilder;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 *
 * @author carcassi
 */
class FormulaFunctionReadExpression extends DesiredRateExpressionImpl<Object> {

    public FormulaFunctionReadExpression(DesiredRateExpressionList<?> childExpressions, FormulaReadFunction function, String defaultName) {
        super(childExpressions, function, defaultName);
    }

    @Override
    public void fillReadRecipe(PVReaderDirector director, ReadRecipeBuilder builder) {
        super.fillReadRecipe(director, builder);
        ((FormulaReadFunction) getFunction()).setDirectory(director);
    }
    
}
