/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.simu;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

/** PV Factory for 'constant' PVs
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConstantPVFactory implements IPVFactory
{
    /** PV type prefix */
    public static final String PREFIX = "const";

    /** Create a 'local' PV.
     *  @param name Name of the PV
     *  @throws Exception on error
     */
    @Override
    public PV createPV(final String name) throws Exception
    {
        return new ConstantPV(name);
    }
}
