/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.List;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.WriteBuffer;
import org.epics.pvmanager.WriteFunction;

/**
 * Implementation class for {@link DesiredRateReadWriteExpression}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * @author carcassi
 */
public class DesiredRateReadWriteExpressionImpl<R, W> extends DesiredRateReadWriteExpressionListImpl<R, W> implements DesiredRateReadWriteExpression<R, W> {
    
    private final DesiredRateExpression<R> desiredRateExpression;
    private final WriteExpression<W> writeExpression;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    @Override
    public final DesiredRateReadWriteExpressionImpl<R, W> as(String name) {
        desiredRateExpression.as(name);
        writeExpression.getWriteExpressionImpl().as(name);
        return this;
    }

    /**
     * Creates an expression that can be both read at the desired rate and written.
     * 
     * @param desiredRateExpression the read part of the expression
     * @param writeExpression the write part of the expression
     */
    public DesiredRateReadWriteExpressionImpl(DesiredRateExpression<R> desiredRateExpression, WriteExpression<W> writeExpression) {
        this.desiredRateExpression = desiredRateExpression;
        this.writeExpression = writeExpression;
    }

    @Override
    public final String getName() {
        return desiredRateExpression.getName();
    }

    @Override
    public final Function<R> getFunction() {
        return desiredRateExpression.getFunction();
    }
    
    @Override
    public final DesiredRateExpressionImpl<R> getDesiredRateExpressionImpl() {
        return desiredRateExpression.getDesiredRateExpressionImpl();
    }
    
    @Override
    public final WriteExpressionImpl<W> getWriteExpressionImpl() {
        return writeExpression.getWriteExpressionImpl();
    }

    @Override
    public final DataRecipe getDataRecipe() {
        return desiredRateExpression.getDataRecipe();
    }

    @Override
    public final WriteFunction<W> getWriteFunction() {
        return writeExpression.getWriteFunction();
    }

    @Override
    public final WriteBuffer createWriteBuffer() {
        return writeExpression.createWriteBuffer();
    }
    
    
}
