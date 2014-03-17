/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.List;

/**
 * A list of expressions to write.
 * <p>
 * Don't implement objects with this interface, use {@link WriteExpressionListImpl}.
 *
 * @param <W> the write payload
 * @author carcassi
 */
public interface WriteExpressionList<W> {
    
    /**
     * Adds the given expressions to this list.
     * 
     * @param expressions a list of expressions
     * @return this
     */
    public WriteExpressionList<W> and(WriteExpressionList<? extends W> expressions);

    /**
     * The expressions of this list.
     * 
     * @return a list of expressions
     */
    public List<WriteExpression<W>> getWriteExpressions();
}
