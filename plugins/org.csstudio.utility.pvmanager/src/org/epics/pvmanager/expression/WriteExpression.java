/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.WriteRecipeBuilder;

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
    
    /**
     * Prepares the recipe to connect the channels needed by this expression.
     * <p>
     * A dynamic expression, one for which the child expressions can change,
     * can keep a reference to the director to connect/disconnect new child
     * expressions.
     *
     * @param director the director for the reader
     * @param builder the recipe to fill
     */
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder);

    
    /**
     * The implementation of this expression.
     * 
     * @return the implementation
     */
    public WriteExpressionImpl<W> getWriteExpressionImpl();
}
