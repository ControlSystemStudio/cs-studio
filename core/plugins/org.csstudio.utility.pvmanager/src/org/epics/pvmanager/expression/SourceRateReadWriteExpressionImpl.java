/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.WriteRecipeBuilder;

/**
 * Implementation class for {@link SourceRateReadWriteExpression}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class SourceRateReadWriteExpressionImpl<R, W> extends SourceRateReadWriteExpressionListImpl<R, W> implements SourceRateReadWriteExpression<R, W> {
    
    private final SourceRateExpression<R> sourceRateExpression;
    private final WriteExpression<W> writeExpression;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    @Override
    public SourceRateReadWriteExpressionImpl<R, W> as(String name) {
        sourceRateExpression.as(name);
        writeExpression.getWriteExpressionImpl().as(name);
        return this;
    }

    /**
     * Creates an expression that can be both read and written.
     * 
     * @param sourceRateExpression the read part of the expression
     * @param writeExpression the write part of the expression
     */
    public SourceRateReadWriteExpressionImpl(SourceRateExpression<R> sourceRateExpression, WriteExpression<W> writeExpression) {
        this.sourceRateExpression = sourceRateExpression;
        this.writeExpression = writeExpression;
    }

    @Override
    public final String getName() {
        return sourceRateExpression.getName();
    }

    @Override
    public final ReadFunction<R> getFunction() {
        return sourceRateExpression.getFunction();
    }
    
    @Override
    public final SourceRateExpressionImpl<R> getSourceRateExpressionImpl() {
        return sourceRateExpression.getSourceRateExpressionImpl();
    }
    
    @Override
    public final WriteExpressionImpl<W> getWriteExpressionImpl() {
        return writeExpression.getWriteExpressionImpl();
    }

    @Override
    public final WriteFunction<W> getWriteFunction() {
        return writeExpression.getWriteFunction();
    }

    @Override
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder) {
        writeExpression.fillWriteRecipe(director, builder);
    }
    
}
