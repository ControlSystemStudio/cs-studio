/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.local;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVFactory;

/** Factory for creating {@link LocalPV}s
 *  @author Kay Kasemir
 */
public class LocalPVFactory implements PVFactory
{
    final public static String TYPE = "loc";
    
    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public PV createPV(final String name, final String base_name) throws Exception
    {
        return new LocalPV(name, base_name);
    }
}
