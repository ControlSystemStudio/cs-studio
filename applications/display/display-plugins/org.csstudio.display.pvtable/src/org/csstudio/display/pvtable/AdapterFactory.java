/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapter from PV Table to CSS types
 *  @author Kay Kasemir
 */
@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory
{
    @Override
    public Class[] getAdapterList() {
        return new Class[] { ProcessVariable.class };
    }

    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType) {
        if (adaptableObject instanceof PVTableItem  &&  adapterType == ProcessVariable.class) {
            return new ProcessVariable(((PVTableItem)adaptableObject).getName());
        }
        return null;
    }
}
