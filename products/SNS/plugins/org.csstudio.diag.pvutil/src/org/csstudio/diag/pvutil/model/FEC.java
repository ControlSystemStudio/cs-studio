/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.model;

import org.eclipse.core.runtime.PlatformObject;

/** One Front-End-Controller (FEC) in the PVUtilDataAPI.
 *  @see PVUtilDataAPI
 *  @author Dave Purcell
 */
public class FEC extends PlatformObject
{
    final private String fec_nm;

    public FEC(final String fec_nm)
    {
        this.fec_nm = fec_nm;
    }

    @Override
    public String toString()
    {
        return "Name: " + fec_nm; //$NON-NLS-1$
    }

    public String getName()
    {
    	return fec_nm;
    }
}
