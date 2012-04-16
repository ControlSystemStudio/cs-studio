/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.WriteBuffer;
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
    
    /**
     * The buffer that will contain the data to write.
     *
     * @return the data buffer
     */
    public WriteBuffer createWriteBuffer();
    
    /**
     * The implementation of this expression.
     * 
     * @return the implementation
     */
    public WriteExpressionImpl<W> getWriteExpressionImpl();
}
