/*
 * Copyright 2008-2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.DesiredRateExpression;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.PVManager;

/**
 *
 * @author carcassi
 */
public class DynamicGroup extends DesiredRateExpression<List<Object>> {

    private final DataSource dataSource = PVManager.getDefaultDataSource();
    private final List<DataRecipe> recipes = new ArrayList<DataRecipe>();
    
    public DynamicGroup() {
        super((DesiredRateExpression<?>) null, new DynamicGroupFunction(), "dynamic group");
    }

    DynamicGroupFunction getGroup() {
        return (DynamicGroupFunction) getFunction();
    }
    
    public List<Exception> lastExceptions() {
        synchronized (getGroup()) {
            return new ArrayList<Exception>(getGroup().getExceptions());
        }
    }
    
    public synchronized DynamicGroup add(DesiredRateExpression<?> expression) {
        DataRecipe recipe = expression.getDataRecipe();
        recipe = recipe.withExceptionHandler(handlerFor(recipes.size()));
        synchronized (getGroup()) {
            getGroup().getArguments().add(expression.getFunction());
            getGroup().getExceptions().add(null);
            getGroup().getPreviousValues().add(null);
        }
        dataSource.connect(recipe);
        recipes.add(recipe);
        return this;
    }
    
    public synchronized DynamicGroup remove(int index) {
        DataRecipe recipe = recipes.remove(index);
        dataSource.disconnect(recipe);
        synchronized (getGroup()) {
            getGroup().getArguments().remove(index);
            getGroup().getExceptions().remove(index);
            getGroup().getPreviousValues().remove(index);
        }
        return this;
    }
    
    public synchronized DynamicGroup set(int index, DesiredRateExpression<?> expression) {
        DataRecipe recipe = expression.getDataRecipe();
        recipe = recipe.withExceptionHandler(handlerFor(index));
        DataRecipe oldRecipe = recipes.get(index);
        dataSource.disconnect(oldRecipe);
        
        synchronized (getGroup()) {
            getGroup().getArguments().set(index, expression.getFunction());
            getGroup().getExceptions().set(index, null);
            getGroup().getPreviousValues().set(index, null);
        }
        dataSource.connect(recipe);
        recipes.set(index, recipe);
        return this;
    }
    
    private ExceptionHandler handlerFor(final int index) {
        return new ExceptionHandler() {

            @Override
            public void handleException(Exception ex) {
                synchronized (getGroup()) {
                    getGroup().getExceptions().set(index, ex);
                }
            }
            
        };
    }
}
