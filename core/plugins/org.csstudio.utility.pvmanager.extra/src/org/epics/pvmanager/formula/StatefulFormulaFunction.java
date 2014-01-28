/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
