/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.vtype.VString;

/**
 * Formula function that returns the value of the pv matching the name
 * of the argument.
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
        // Retrieve the new name
        VString value = (VString) args.get(0);
        String newName = null;
        if (value != null) {
            newName = value.getValue();
        }
        
        // If the name does not match, disconnect and connect
        if (!Objects.equals(newName, previousName)) {
            // Disconnect previous
            if (currentExpression != null) {
                getDirector().disconnectExpression(currentExpression);
                currentExpression = null;
            }
            
            // Connect new
            if (newName != null) {
                currentExpression = new LastOfChannelExpression<Object>(newName, Object.class);
                getDirector().connectExpression(currentExpression);
            }
            previousName = newName;
        }

        // Return value
        if (newName == null) {
            return null;
        }
        return currentExpression.getFunction().readValue();
    }

    @Override
    public void dispose() {
        // Disconnect everything on dispose
        getDirector().disconnectExpression(currentExpression);
        currentExpression = null;
    }
    
}
