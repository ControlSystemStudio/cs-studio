/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation class for {@link WriteExpressionList}.
 *
 * @param <W> type of the write payload
 * @author carcassi
 */
public class WriteExpressionListImpl<W> implements WriteExpressionList<W> {
    
    private List<WriteExpression<W>> writeExpressions;
    
    final void addThis() {
        writeExpressions.add((WriteExpression<W>) this);
    }

    /**
     * Creates a new empty expression list.
     */
    public WriteExpressionListImpl() {
        this.writeExpressions = new ArrayList<WriteExpression<W>>();
    }

    WriteExpressionListImpl(Collection<? extends WriteExpression<W>> writeExpressions) {
        this.writeExpressions = new ArrayList<WriteExpression<W>>(writeExpressions);
    }
    
    @Override
    public final WriteExpressionListImpl<W> and(WriteExpressionList<? extends W> expressions) {
        @SuppressWarnings("unchecked")
        WriteExpressionList<W> newExpression = (WriteExpressionList<W>) (WriteExpressionList) expressions;
        writeExpressions.addAll(newExpression.getWriteExpressions());
        return this;
    }

    @Override
    public final List<WriteExpression<W>> getWriteExpressions() {
        return writeExpressions;
    }
    
}
