/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.WriteRecipe;
import org.epics.pvmanager.WriteRecipeBuilder;
import org.epics.pvmanager.WriteFunction;

/**
 * An expression to write.
 * <p>
 * Don't implement objects with this interface, use {@link WriteExpressionImpl}.
 *
 * @param <W> the write payload
 * @author carcassi
 */
public interface WriteExpression<W> extends WriteExpressionList<W> {

    /**
     * Name of this expression.
     *
     * @return the expression name
     */
    public String getName();
    
    /**
     * The function that implements this expression.
     *
     * @return the expression function
     */
    public WriteFunction<W> getWriteFunction();
    
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder);

    
    /**
     * The implementation of this expression.
     * 
     * @return the implementation
     */
    public WriteExpressionImpl<W> getWriteExpressionImpl();
}
