/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.PVReaderDirector;

/**
 *
 * @author carcassi
 */
public abstract class DynamicFormulaFunction extends StatefulFormulaFunction {
    
    private PVReaderDirector<?> director;

    public PVReaderDirector<?> getDirector() {
        return director;
    }
    
    public void setDirector(PVReaderDirector<?> director) {
        this.director = director;
    }
    
}
