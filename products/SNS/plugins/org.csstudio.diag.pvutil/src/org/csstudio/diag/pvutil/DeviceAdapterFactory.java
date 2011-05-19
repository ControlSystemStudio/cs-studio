/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil;

import org.csstudio.csdata.Device;
import org.csstudio.diag.pvutil.model.FEC;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapter from PVUtil type to CSS type
 *  @author Kay Kasemir
 */
@SuppressWarnings("rawtypes")
public class DeviceAdapterFactory implements IAdapterFactory
{
    @Override
    public Class[] getAdapterList()
    {
        return new Class[] { Device.class };
    }

    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        if (adaptableObject instanceof FEC  &&
            adapterType == Device.class)
            return new Device(((FEC) adaptableObject).getName());
        return null;
    }
}
