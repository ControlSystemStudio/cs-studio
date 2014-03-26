/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.WriteRecipeBuilder;

/**
 * A write expression for a key/value map.
 * <p>
 * This expression will take the values from the map and will write them
 * to each child expression matching the key to the name of the child expression.
 * The map is dynamic: the child expressions can be added and removed
 * while the reader is active.
 * <p>
 * There is currently no way to retrieve the individual errors for each
 * element of the map.
 *
 * @param <T> the type for the values in the map
 * @author carcassi
 */
public class WriteMap<T> extends WriteExpressionImpl<Map<String, T>> {

    private final Object lock = new Object();
    private final Map<String, WriteExpression<T>> expressions = new HashMap<>();
    private PVWriterDirector<?> director;

    /**
     * Creates a new group.
     */
    public WriteMap() {
        super(new WriteExpressionListImpl<Object>(), new MapOfWriteFunction<T>(new QueueCollector<MapUpdate<T>>(1000)), "map");
    }

    MapOfWriteFunction<T> getMapOfWriteFunction() {
        return (MapOfWriteFunction<T>) getWriteFunction();
    }

    /**
     * Removes all the expressions currently in the map.
     * 
     * @return this expression
     */
    public WriteMap<T> clear() {
        synchronized(lock) {
            getMapOfWriteFunction().getMapUpdateCollector().writeValue(MapUpdate.<T>clear());
            if (director != null) {
                for (WriteExpression<T> desiredRateExprewritession : expressions.values()) {
                    director.disconnectExpression(desiredRateExprewritession);
                }
            }
            expressions.clear();
            return this;
        }
    }

    /**
     * Returns the number of expressions in the group.
     * 
     * @return number of expressions in the group
     */
    public int size() {
        synchronized(lock) {
            return expressions.size();
        }
    }

    /**
     * Adds the expression to the map.
     * 
     * @param expression the expression to be added
     * @return this expression
     */
    public WriteMap<T> add(WriteExpression<T> expression) {
        synchronized(lock) {
            if (expression.getName() == null) {
                throw new NullPointerException("Expression has a null name");
            }
            if (expressions.containsKey(expression.getName())) {
                throw new IllegalArgumentException("MapExpression already contain an expression named '" + expression.getName() + "'");
            }
            
            getMapOfWriteFunction().getMapUpdateCollector().writeValue(MapUpdate.addWriteFunction(expression.getName(), expression.getWriteFunction()));
            expressions.put(expression.getName(), expression);
            if (director != null) {
                director.connectExpression(expression);
            }
            return this;
        }
    }
    
    /**
     * Adds the expressions to the map.
     *
     * @param expressions the new list of expressions
     * @return this expression
     */
    public WriteMap<T> add(WriteExpressionList<T> expressions) {
        synchronized(lock) {
            for (WriteExpression<T> writeExpression : expressions.getWriteExpressions()) {
                add(writeExpression);
            }
            return this;
        }
    }

    /**
     * Removes the expression with the given name.
     * 
     * @param name the name of the expression to remove
     * @return this expression
     */
    public WriteMap<T> remove(String name) {
        synchronized(lock) {
            if (!expressions.containsKey(name)) {
                throw new IllegalArgumentException("MapExpression does not contain an expression named '" + name + "'");
            }
            
            getMapOfWriteFunction().getMapUpdateCollector().writeValue(MapUpdate.<T>removeFunction(name));
            WriteExpression<T> expression = expressions.remove(name);
            if (director != null) {
                director.disconnectExpression(expression);
            }
            return this;
        }
    }
    
    /**
     * Removes the expressions from the map.
     *
     * @param names the names of the expressions to remove
     * @return this expression
     */
    public WriteMap<T> remove(Collection<String> names) {
        synchronized(lock) {
            for (String name : names) {
                remove(name);
            }
            return this;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillWriteRecipe(PVWriterDirector director, WriteRecipeBuilder builder) {
        synchronized(lock) {
            this.director = director;
            for (Map.Entry<String, WriteExpression<T>> entry : expressions.entrySet()) {
                WriteExpression<T> writeExpression = entry.getValue();
                director.connectExpression(writeExpression);
            }
        }
    }
    
}
