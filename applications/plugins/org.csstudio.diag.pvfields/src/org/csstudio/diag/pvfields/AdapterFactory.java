/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt {@link PVInfo} to CSS PV
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
        if (adaptableObject instanceof PVInfo)
        {
            final PVInfo info = (PVInfo) adaptableObject;
            return new ProcessVariable(info.getName());
        }
        return null;
    }
}
