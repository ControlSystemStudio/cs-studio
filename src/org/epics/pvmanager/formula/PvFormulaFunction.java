/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.vtype.VString;

/**
 *
 * @author carcassi
 */
class PvFormulaFunction extends DynamicFormulaFunction {

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public String getName() {
        return "pv";
    }

    @Override
    public String getDescription() {
        return "Returns the value of the given pv name";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VString.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("pvName");
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }
    
    private String previousName;
    private DesiredRateExpression<?> currentExpression;

    @Override
    public Object calculate(final List<Object> args) {
        VString value = (VString) args.get(0);
        String newName = null;
        if (value != null) {
            newName = value.getValue();
        }
        
        if (!Objects.equals(newName, previousName)) {
            // Change connection
            if (currentExpression != null) {
                getDirector().disconnectExpression(currentExpression);
                currentExpression = null;
            }
            if (newName != null) {
                currentExpression = new LastOfChannelExpression<Object>(newName, Object.class);
                getDirector().connectExpression(currentExpression);
            }
            previousName = newName;
        }
        
        if (newName == null) {
            return null;
        }

        return currentExpression.getFunction().readValue();
    }

    @Override
    public void dispose() {
        getDirector().disconnectExpression(currentExpression);
        currentExpression = null;
    }
    
}
