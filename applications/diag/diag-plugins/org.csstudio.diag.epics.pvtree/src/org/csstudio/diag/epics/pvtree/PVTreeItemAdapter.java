/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.epics.pvtree.model.TreeModelItem;
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

    @Override
    public <T> T getAdapter(final Object adaptableObject, final Class<T> adapterType)
    {
        final String pv_name = ((TreeModelItem)adaptableObject).getPVName();
        if (adapterType == String.class)
            return adapterType.cast(pv_name);
        else if (adapterType == ProcessVariable.class)
            return adapterType.cast(new ProcessVariable(pv_name));
        else
            return null;
    }
}
