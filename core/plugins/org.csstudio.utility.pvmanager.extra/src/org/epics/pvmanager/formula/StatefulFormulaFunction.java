/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 *
 * @author carcassi
 */
public abstract class StatefulFormulaFunction implements FormulaFunction {

    @Override
    public final boolean isPure() {
        return false;
    }
    
    public void dispose() {
        // Default implementation does nothing;
    }
    
}
