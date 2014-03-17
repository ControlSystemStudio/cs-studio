/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.Collection;
import java.util.Map;

/**
 * A read/write expression for a key/value map.
 * <p>
 * This expression is a combination of a {@link ReadMap} and a {@link WriteMap}.
 * The map is dynamic: the child expressions can be added and removed
 * while the reader is active.
 * <p>
 * There is currently no way to retrieve the individual errors for each
 * element of the map. If the value is a VType, the connection can be
 * retrieved by looking at the alarm.
 *
 * @param <R> the type for the values in the read map
 * @param <W> the type for the values in the write map
 * @author carcassi
 */
public class ReadWriteMap<R, W> extends DesiredRateReadWriteExpressionImpl<Map<String, R>, Map<String, W>> {

    /**
     * Creates a new group.
     */
    public ReadWriteMap() {
        super(new ReadMap<R>(), new WriteMap<W>());
    }
    
    private ReadMap<R> getReadMap() {
        return (ReadMap<R>) getDesiredRateExpressionImpl();
    }
    
    private WriteMap<W> getWriteMap() {
        return (WriteMap<W>) getWriteExpressionImpl();
    }

    /**
     * Removes all the expressions currently in the map.
     * 
     * @return this expression
     */
    public ReadWriteMap<R, W> clear() {
        getReadMap().clear();
        getWriteMap().clear();
        return this;
    }

    /**
     * Returns the number of expressions in the group.
     * 
     * @return number of expressions in the group
     */
    public int size() {
        return getReadMap().size();
    }

    /**
     * Adds the expression to the map.
     * 
     * @param expression the expression to be added
     * @return this expression
     */
    public ReadWriteMap<R, W> add(DesiredRateReadWriteExpression<R, W> expression) {
        getReadMap().add(expression);
        getWriteMap().add(expression);
        return this;
    }
    
    /**
     * Adds the expressions to the map.
     *
     * @param expressions the new list of expressions
     * @return this expression
     */
    public ReadWriteMap<R, W> add(DesiredRateReadWriteExpressionList<R, W> expressions) {
        getReadMap().add(expressions);
        getWriteMap().add(expressions);
        return this;
    }

    /**
     * Removes the expression with the given name.
     * 
     * @param name the name of the expression to remove
     * @return this expression
     */
    public ReadWriteMap<R, W> remove(String name) {
        getReadMap().remove(name);
        getWriteMap().remove(name);
        return this;
    }
    
    /**
     * Removes the expressions from the map.
     *
     * @param names the names of the expressions to remove
     * @return this expression
     */
    public ReadWriteMap<R, W> remove(Collection<String> names) {
        getReadMap().remove(names);
        getWriteMap().remove(names);
        return this;
    }
    
}
