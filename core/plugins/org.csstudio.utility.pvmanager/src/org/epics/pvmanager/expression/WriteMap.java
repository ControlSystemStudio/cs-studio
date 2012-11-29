/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.extra.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.ReadRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderDirector;
import org.epics.pvmanager.PVWriterDirector;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.ReadRecipeBuilder;
import org.epics.pvmanager.WriteRecipeBuilder;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;

/**
 * A write expression a dynamically managed group.
 * Once the group is created, any {@link WriteExpression} can be
 * added dynamically.
 *
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
     * Removes all the expressions currently in the group.
     * 
     * @return this
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
     * Adds the expression at the end.
     * 
     * @param expression the expression to be added
     * @return this
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
    
    public WriteMap<T> add(WriteExpressionList<T> expressions) {
        synchronized(lock) {
            for (WriteExpression<T> writeExpression : expressions.getWriteExpressions()) {
                add(writeExpression);
            }
            return this;
        }
    }

    /**
     * Removes the expression at the given location.
     * 
     * @param index the position to remove
     * @return this
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
    
    public WriteMap<T> remove(List<String> names) {
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
