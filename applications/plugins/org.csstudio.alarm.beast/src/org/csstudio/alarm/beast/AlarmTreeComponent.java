/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Item in the alarm configuration tree which refers to a System component.
 *  This covers all layers of the alarm tree between the top-level
 *  "Area" and the "PV" leaves,
 *  i.e. elements above root level, but not PVs.
 *  @author Kay Kasemir
 */
public class AlarmTreeComponent extends AlarmTree
{
    /** Initialize
     *  @param id RDB ID
     *  @param name Name of the component
     *  @param parent Parent entry in tree
     */
    public AlarmTreeComponent(final int id, final String name, final AlarmTree parent)
    {
        super(id, name, parent);
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreePosition getPosition()
    {
        if (parent instanceof AlarmTreeRoot)
            return AlarmTreePosition.Area;
        return AlarmTreePosition.System;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Component " + super.toString();
    }
}
