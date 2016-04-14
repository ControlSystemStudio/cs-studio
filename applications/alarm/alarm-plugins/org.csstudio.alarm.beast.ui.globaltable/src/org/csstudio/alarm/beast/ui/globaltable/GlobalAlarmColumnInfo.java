/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import java.time.Instant;

import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarm;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;

/** Helper that defines the columns for a table of global alarms
 *  @author Kay Kasemir
 */
public enum GlobalAlarmColumnInfo
{
    /** Alarm PV Name/Path */
    PV(Messages.AlarmPV, 100, 100,
        new GlobalAlarmCellLabelProvider()
        {
            @Override
            protected String getCellText(final GlobalAlarm alarm)
            {
                return alarm.getPathName();
            }
        },
        new GlobalAlarmComparator()
        {
            @Override
            public int compare(final GlobalAlarm a, final GlobalAlarm b)
            {
                return a.getPathName().compareTo(b.getPathName());

            }
        }),
    // TODO Would be nice to display description, but that's fetched
    //      in background and not available when alarm first displayed...
    //      Send descr. with alarm server update?
    //      Delay display until RDB info available?
    /** Alarm Time */
    TIME(Messages.AlarmTime, 50, 80,
        new GlobalAlarmCellLabelProvider()
        {
            @Override
            protected String getCellText(final GlobalAlarm alarm)
            {
                return alarm.getTimestampText();
            }
        },
        new GlobalAlarmComparator()
        {
            @Override
            public int compare(final GlobalAlarm a, final GlobalAlarm b)
            {
                final Instant ta = a.getTimestamp();
                final Instant tb = b.getTimestamp();
                if (ta == null  ||  tb == null)
                    return 0;
                return ta.compareTo(tb);
            }
        }),
    /** Alarm Severity */
    SEVERITY(Messages.AlarmSeverity, 30, 50,
        new GlobalAlarmCellLabelProvider()
        {
            @Override
            protected String getCellText(final GlobalAlarm alarm)
            {
                return alarm.getSeverity().getDisplayName();
            }
        },
        new GlobalAlarmComparator()
        {
            @Override
            public int compare(final GlobalAlarm a, final GlobalAlarm b)
            {
                return a.getSeverity().compareTo(b.getSeverity());
            }
        }),
    /** Alarm Message */
    MESSAGE(Messages.AlarmMessage, 30, 45,
        new GlobalAlarmCellLabelProvider()
        {
            @Override
            protected String getCellText(final GlobalAlarm alarm)
            {
                return alarm.getMessage();
            }
        },
        new GlobalAlarmComparator()
        {
            @Override
            public int compare(final GlobalAlarm a, final GlobalAlarm b)
            {
                return a.getMessage().compareTo(b.getMessage());
            }
        });

    final private String title;
    final private int weight, min;
    final private CellLabelProvider label_provider;
    final private GlobalAlarmComparator comparator;

    /** Initialize
     *  @param title
     *  @param weight
     *  @param min
     *  @param label_provider
     */
    private GlobalAlarmColumnInfo(final String title,
            final int weight, final int min,
            final CellLabelProvider label_provider,
            final GlobalAlarmComparator comparator)
    {
        this.title = title;
        this.weight = weight;
        this.min = min;
        this.label_provider = label_provider;
        this.comparator = comparator;
    }

    /** @return Column title */
    public String getTitle()
    {
        return title;
    }

    /** @return Data for TableColumnLayout */
    public ColumnLayoutData getLayoutData()
    {
        return new ColumnWeightData(weight, min);
    }

    /** @return Column's label provider */
    public CellLabelProvider getLabelProvider()
    {
        return label_provider;
    }

    /** @return Comparator */
    public GlobalAlarmComparator getComparator()
    {
        return comparator;
    }
}
