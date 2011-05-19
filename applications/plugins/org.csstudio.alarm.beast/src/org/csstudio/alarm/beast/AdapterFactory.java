/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt alarm model item to CSS data types
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AdapterFactory implements IAdapterFactory
{
    @Override
    public Class<?>[] getAdapterList()
    {
        return new Class<?>[] { ProcessVariable.class, String.class };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        final AlarmTreeLeaf leaf = (AlarmTreeLeaf) adaptableObject;
        if (adapterType == ProcessVariable.class)
            return new ProcessVariable(leaf.getName());
        // Else: provide String
        return leaf.getVerboseDescription() + "\n";
    }
}
