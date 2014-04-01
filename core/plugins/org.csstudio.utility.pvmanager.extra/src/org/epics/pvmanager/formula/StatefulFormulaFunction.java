/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

/**
 * A formula function that maintains a state. Each instance of the function
 * will have its own instance.
 * <p>
 * One object will be created for each instance of the function. The member
 * variable can be accessed with the guarantee that each object will be isolated
 * from the others. There is no need of synchronization.
 * 
 *
 * @author carcassi
 */
public abstract class StatefulFormulaFunction implements FormulaFunction {

    @Override
    public final boolean isPure() {
        return false;
    }
    
    /**
     * Called when this instance of the formula is not needed anymore.
     */
    public void dispose() {
        // Default implementation does nothing;
    }
    
}
