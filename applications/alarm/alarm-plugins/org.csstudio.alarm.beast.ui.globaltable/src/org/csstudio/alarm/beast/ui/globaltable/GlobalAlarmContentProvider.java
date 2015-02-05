/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModel;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for table that shows GlobalAlarmModel
 *  @author Kay Kasemir
 */
public class GlobalAlarmContentProvider implements IStructuredContentProvider
{
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object input)
    {
        // NOP
    }

    @Override
    public void dispose()
    {
        // NOP
    }

    @Override
    public Object[] getElements(final Object input)
    {
        if (! (input instanceof GlobalAlarmModel))
            throw new IllegalArgumentException();
        final GlobalAlarmModel model = (GlobalAlarmModel) input;
        return model.getAlarms();
    }
}
