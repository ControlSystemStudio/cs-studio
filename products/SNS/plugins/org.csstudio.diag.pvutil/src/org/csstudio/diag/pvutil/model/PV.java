/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.model;

import org.eclipse.core.runtime.PlatformObject;

/** One PV in the model: PV name and additional info text
 *
 *  Also functions as a CSS IProcessVariable
 *  @see PVUtilDataAPI
 *  @author Dave Purcell
 */
public class PV extends PlatformObject
{
    final private String pv, infoString;

    public PV(final String pv, final String infoString)
    {
        this.pv = pv;
        this.infoString = infoString;
    }

    public String getName()
    {
        return pv;
    }

    public String getInfoString()
    {
        return infoString;
    }

    @Override
    public String toString()
    {
        return "PV: " + pv; //$NON-NLS-1$
    }
}
