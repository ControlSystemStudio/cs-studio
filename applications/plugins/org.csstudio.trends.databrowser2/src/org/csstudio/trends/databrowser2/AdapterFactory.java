/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt Data Browser model items to CSS PV
 *
 *  <p>plugin.xml registers this for model.PVItem and archive.ChannelInfo
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
        if (adaptableObject instanceof ModelItem)
        {
            final ModelItem item = (ModelItem) adaptableObject;
            if (adapterType == String.class)
                return item.getName();
            else if (adapterType == ProcessVariable.class)
                return new ProcessVariable(item.getName());
        }
        else if (adaptableObject instanceof ChannelInfo)
        {
            final ChannelInfo item = (ChannelInfo) adaptableObject;
            if (adapterType == String.class)
                return item.getProcessVariable().getName();
            else if (adapterType == ProcessVariable.class)
                return item.getProcessVariable();
        }
        return null;
    }
}
