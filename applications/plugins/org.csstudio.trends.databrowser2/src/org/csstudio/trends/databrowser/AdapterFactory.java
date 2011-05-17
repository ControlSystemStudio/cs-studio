/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt Data Browser model items to CSS PV
 *
 *  <p>plugin.xml only registers this for PVItem,
 *  but this class handles all data browser ModelItems.
 *
 *  @author Kay Kasemir
 */
public class AdapterFactory implements IAdapterFactory
{
    @Override
    public Class<?>[] getAdapterList()
    {
        return new Class<?>[] { String.class, ProcessVariable.class };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        final ModelItem item = (ModelItem) adaptableObject;
        if (adapterType == String.class)
            return item.getName();
        else if (adapterType == ProcessVariable.class)
            return new ProcessVariable(item.getName());
        return null;
    }
}
