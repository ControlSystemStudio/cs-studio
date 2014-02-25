/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapter from PV Tree model to {@link ProcessVariable}
 *  @author Kay Kasemir
 */
public class PVTreeItemAdapter implements IAdapterFactory
{
    final private Class<?>[] targets = new  Class<?>[]
    {
        String.class,
        ProcessVariable.class
    };

    @Override
    public Class<?>[] getAdapterList()
    {
        return targets;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        final String pv_name = ((PVTreeItem)adaptableObject).getPVName();

        if (adapterType == String.class)
            return pv_name;
        else if (adapterType == ProcessVariable.class)
            return new ProcessVariable(pv_name);
        else
            return null;
    }
}
