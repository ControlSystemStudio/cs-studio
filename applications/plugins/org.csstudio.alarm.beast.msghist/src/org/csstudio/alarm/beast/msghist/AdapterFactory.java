/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import org.csstudio.alarm.beast.msghist.model.PVMessage;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt a message that (potentially) refers to a PV name
 *  to a {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class AdapterFactory implements IAdapterFactory
{
    @Override
    public Class<?>[] getAdapterList()
    {
        return new Class<?>[] { ProcessVariable.class };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        final PVMessage pv_msg = (PVMessage) adaptableObject;
        return new ProcessVariable(pv_msg.getName());
    }
}
