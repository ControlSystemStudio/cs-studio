/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.model.ui.dnd;

import org.csstudio.model.ControlSystemObject;
import org.csstudio.model.DeviceName;
import org.csstudio.model.ProcessVariableName;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapter from
 *
 * @author Kay Kasemir
 *
 */
public class DeviceAndAPVAdapter implements IAdapterFactory, ControlSystemObject
{
    /** DeviceAndAPV can transfer as itself, but also as just Device or PV
     *  {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getSerializableTypes()
    {
        return new Class[] { DeviceAndAPV.class, DeviceName.class, ProcessVariableName.class };
    }

    /** Adapt to CS Object but also plain device and PV
     *  {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList()
    {
        return new Class[] { ControlSystemObject.class, DeviceAndAPV.class, DeviceName.class, ProcessVariableName.class };
    }

    /** Adapt to CS Object but also plain device and PV
     *  {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object obj, Class type)
    {
        final DeviceAndAPV thing = (DeviceAndAPV) obj;
        if (type.equals(ControlSystemObject.class))
            return this;
        else if (type.equals(DeviceAndAPV.class))
            return thing;
        else if (type.equals(DeviceName.class))
            return thing.getDevice();
        else if (type.equals(ProcessVariableName.class))
            return thing.getPv();
        else return null;
    }

}
