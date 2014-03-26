/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.vtype.VType;

/** Helper (base) for implementing a {@link PVListener}
 *  @author Kay Kasemir
 */
public class PVListenerAdapter implements PVListener
{
    @Override
    public void permissionsChanged(PV pv, boolean readonly)
    {
        // NOP
    }

    @Override
    public void valueChanged(PV pv, VType value)
    {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, pv.getName() + " value changed to " + value);
    }

    @Override
    public void disconnected(PV pv)
    {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, pv.getName() + " disconnected");
    }
}
