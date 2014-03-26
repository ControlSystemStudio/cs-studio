/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import org.epics.vtype.VType;

/** Listener to a {@link PV}
 *  @author Kay Kasemir
 */
public interface PVListener
{
    public void permissionsChanged(PV pv, boolean readonly);

    public void valueChanged(PV pv, VType value);

    public void disconnected(PV pv);
}
