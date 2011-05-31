/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pace.model.Cell;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt {@link Cell} in PACE model to CSS PV
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
        if (adaptableObject instanceof Cell)
        {
            final Cell cell = (Cell) adaptableObject;
            return new ProcessVariable(cell.getName());
        }
        return null;
    }
}
