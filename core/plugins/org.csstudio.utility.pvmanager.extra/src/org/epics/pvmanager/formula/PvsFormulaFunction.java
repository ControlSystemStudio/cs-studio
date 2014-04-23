/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.table.VTableFactory;

/**
 * Formula function that accepts a list of strings and returns a table where 
 * each row is the value of the pv matching the name.
 *
 * @author carcassi
 */
class PvsFormulaFunction extends DynamicFormulaFunction {

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public String getName() {
        return "pvs";
    }

    @Override
    public String getDescription() {
        return "Returns a table with the values of the given pv names";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VStringArray.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("pvNames");
    }

    @Override
    public Class<?> getReturnType() {
        return VTable.class;
    }
    
    private List<String> previousNames;
    private List<DesiredRateExpression<?>> currentExpressions;
    
    Object calculateImpl(final List<String> newNames) {
        // If the name does not match, disconnect and connect
        if (!Objects.equals(newNames, previousNames)) {
            List<DesiredRateExpression<?>> newExpressions = new ArrayList<>();
            if (newNames != null) {
                newExpressions.addAll(Collections.nCopies(newNames.size(), (DesiredRateExpression<?>) null));
            }
            
            // Iterate throgh the previous names, and extract
            // the expressions that match the new names
            if (previousNames != null) {
                if (newNames != null) {
                    for (int previousIndex = 0; previousIndex < previousNames.size(); previousIndex++) {
                        int newIndex = newNames.indexOf(previousNames.get(previousIndex));
                        if (newIndex != -1) {
                            newExpressions.set(newIndex, currentExpressions.get(previousIndex));
                            currentExpressions.set(previousIndex, null);
                        }
                    }
                }

                // Disconnect previous expressions no longer used
                for (DesiredRateExpression<?> desiredRateExpression : currentExpressions) {
                    if (desiredRateExpression != null) {
                        getDirector().disconnectExpression(desiredRateExpression);
                    }
                }
            }
            
            // Connect new expressions
            if (newNames != null) {
                for (int i = 0; i < newNames.size(); i++) {
                    if (newNames.get(i) != null && newExpressions.get(i) == null) {
                        DesiredRateExpression<?> newExpression = new LastOfChannelExpression<Object>(newNames.get(i), Object.class);
                        getDirector().connectExpression(newExpression);
                        newExpressions.set(i, newExpression);
                    }
                }
            }
            
            previousNames = newNames;
            currentExpressions = newExpressions;
        }

        // Return value
        if (newNames == null) {
            return null;
        }
        
        // Extract values
        List<VType> values = new ArrayList<>();
        for (DesiredRateExpression<?> desiredRateExpression : currentExpressions) {
            if (desiredRateExpression != null) {
                Object value = desiredRateExpression.getFunction().readValue();
                if (value != null && !(value instanceof VType)) {
                    throw new IllegalArgumentException("Only VTypes allowed in value tables");
                } else {
                    values.add((VType) value);
                }
            }
        }
        
        return VTableFactory.valueTable(previousNames, values);
    }

    @Override
    public Object calculate(final List<Object> args) {
        // Retrieve the new names
        VStringArray value = (VStringArray) args.get(0);
        List<String> newNames = null;
        if (value != null) {
            newNames = value.getData();
        }
        
        return calculateImpl(newNames);
    }

    @Override
    public void dispose() {
        // Disconnect everything on dispose
        for (DesiredRateExpression<?> desiredRateExpression : new HashSet<>(currentExpressions)) {
            getDirector().disconnectExpression(desiredRateExpression);
        }
        currentExpressions = null;
        previousNames = null;
    }
    
}
