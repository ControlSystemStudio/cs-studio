/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.PVReaderDirector;

/**
 * Formula function that can add and remove dynamically access to
 * pvs.
 * <p>
 * This formula function is given a director which can be used to open/close
 * expressions that read real-time data.
 *
 * @author carcassi
 */
public abstract class DynamicFormulaFunction extends StatefulFormulaFunction {
    
    private PVReaderDirector<?> director;

    /**
     * The director to use to connect/disconnect live data expressions.
     * 
     * @return the director
     */
    public final PVReaderDirector<?> getDirector() {
        return director;
    }
    
    /**
     * Changes the director. This is not part of the public API: the director
     * is set by the infrastructure.
     * 
     * @param director the new director
     */
    void setDirector(PVReaderDirector<?> director) {
        this.director = director;
    }
    
}
