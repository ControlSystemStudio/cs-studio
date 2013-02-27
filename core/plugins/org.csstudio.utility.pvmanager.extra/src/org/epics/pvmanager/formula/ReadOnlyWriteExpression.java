/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.WriteRecipeBuilder;
import org.epics.pvmanager.expression.WriteExpressionImpl;
import org.epics.pvmanager.expression.WriteExpressionListImpl;

/**
 *
 * @author carcassi
 */
class ReadOnlyWriteExpression<T> extends WriteExpressionImpl<T> {
    private final String errorMessage;

    public ReadOnlyWriteExpression(final String errorMessage, String defaultName) {
        super(new WriteExpressionListImpl<>(), new WriteFunction<T>() {

            @Override
            public void writeValue(T newValue) {
                throw new RuntimeException(errorMessage);
            }
        }, defaultName);
        this.errorMessage = errorMessage;
    }

    @Override
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder) {
        super.fillWriteRecipe(director, builder);
        director.connectStatic(new RuntimeException(errorMessage), false, getName());
    }
    
    
}
