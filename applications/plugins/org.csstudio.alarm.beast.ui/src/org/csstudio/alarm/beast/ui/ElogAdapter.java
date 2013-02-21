/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt currently selected alarms to logbook
 *  @author Kay Kasemir
 *  @author Kunal Shroff - Original version
 */
@SuppressWarnings("nls")
public class ElogAdapter implements IAdapterFactory
{
    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList()
    {
        return new Class[] { LogEntryBuilder.class };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object object, final Class type)
    {
        if (type != LogEntryBuilder.class)
            return null;

        final StringBuilder text = new StringBuilder();
        text.append(Messages.SendToElogTitle);
        text.append("\n");
        if (object instanceof AlarmTreeItem)
            addAlarmLeafs(text, ((AlarmTreeItem) object));
        if (text.length() <= 0)
            return null;
        return LogEntryBuilder.withText(text.toString());
    }

    private void addAlarmLeafs(final StringBuilder text, final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreeLeaf)
            text.append(((AlarmTreeLeaf) item).getVerboseDescription()).append("\n\n");
        else
        {
            text.append(item.getPathName()).append(":\n");
            final int N = item.getChildCount();
            for (int i=0; i<N; ++i)
                addAlarmLeafs(text, item.getChild(i));
        }
    }
}
