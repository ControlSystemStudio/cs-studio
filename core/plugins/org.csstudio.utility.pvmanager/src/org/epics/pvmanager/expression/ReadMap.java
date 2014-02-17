/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadRecipeBuilder;

/**
 * A read expression for a key/value map.
 * <p>
 * This expression returns a map where the key is the name of the child
 * expression and the value is the value returned by the child expression.
 * The map is dynamic: the child expressions can be added and removed
 * while the reader is active.
 * <p>
 * There is currently no way to retrieve the individual errors for each
 * element of the map. If the value is a VType, the connection can be
 * retrieved by looking at the alarm.
 *
 * @param <T> the type for the values in the map
 * @author carcassi
 */
public class ReadMap<T> extends DesiredRateExpressionImpl<Map<String, T>> {

    private final Object lock = new Object();
    private final Map<String, DesiredRateExpression<T>> expressions = new HashMap<>();
    private PVReaderDirector<?> director;

    /**
     * Creates a new group.
     */
    public ReadMap() {
        super(new DesiredRateExpressionListImpl<Object>(), new MapOfReadFunction<T>(new QueueCollector<MapUpdate<T>>(1000)), "map");
    }

    MapOfReadFunction<T> getMapOfFunction() {
        return (MapOfReadFunction<T>) getFunction();
    }

    /**
     * Removes all the expressions currently in the map.
     * 
     * @return this expression
     */
    public ReadMap<T> clear() {
        synchronized(lock) {
            getMapOfFunction().getMapUpdateCollector().writeValue(MapUpdate.<T>clear());
            if (director != null) {
                for (DesiredRateExpression<T> desiredRateExpression : expressions.values()) {
                    director.disconnectExpression(desiredRateExpression);
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
    public ReadMap<T> add(DesiredRateExpression<T> expression) {
        synchronized(lock) {
            if (expression.getName() == null) {
                throw new NullPointerException("Expression has a null name");
            }
            if (expressions.containsKey(expression.getName())) {
                throw new IllegalArgumentException("MapExpression already contain an expression named '" + expression.getName() + "'");
            }
            
            getMapOfFunction().getMapUpdateCollector().writeValue(MapUpdate.addReadFunction(expression.getName(), expression.getFunction()));
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
    public ReadMap<T> add(DesiredRateExpressionList<T> expressions) {
        synchronized(lock) {
            for (DesiredRateExpression<T> desiredRateExpression : expressions.getDesiredRateExpressions()) {
                add(desiredRateExpression);
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
    public ReadMap<T> remove(String name) {
        synchronized(lock) {
            if (!expressions.containsKey(name)) {
                throw new IllegalArgumentException("MapExpression does not contain an expression named '" + name + "'");
            }
            
            getMapOfFunction().getMapUpdateCollector().writeValue(MapUpdate.<T>removeFunction(name));
            DesiredRateExpression<T> expression = expressions.remove(name);
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
    public ReadMap<T> remove(Collection<String> names) {
        synchronized(lock) {
            for (String name : names) {
                remove(name);
            }
            return this;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fillReadRecipe(PVReaderDirector director, ReadRecipeBuilder builder) {
        synchronized(lock) {
            this.director = director;
            for (Map.Entry<String, DesiredRateExpression<T>> entry : expressions.entrySet()) {
                DesiredRateExpression<T> readExpression = entry.getValue();
                director.connectExpression(readExpression);
            }
        }
    }
    
}
