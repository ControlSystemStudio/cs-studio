/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.ReadRecipeBuilder;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;

/**
 *
 * @author carcassi
 */
class ErrorDesiredRateExpression<T> extends DesiredRateExpressionImpl<T> {
    private final RuntimeException error;

    public ErrorDesiredRateExpression(final RuntimeException ex, String defaultName) {
        super(new DesiredRateExpressionListImpl<>(), new ReadFunction<T>() {
            @Override
            public T readValue() {
                return null;
            }
        }, defaultName);
        this.error = ex;
    }

    @Override
    public void fillReadRecipe(PVReaderDirector director, ReadRecipeBuilder builder) {
        super.fillReadRecipe(director, builder); //To change body of generated methods, choose Tools | Templates.
        director.connectStatic(error, false, getName());
    }
    
}
