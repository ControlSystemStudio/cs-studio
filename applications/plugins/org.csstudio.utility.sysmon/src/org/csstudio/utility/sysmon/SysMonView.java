/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.sysmon;

import org.csstudio.utility.plotwidget.PlotWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/** System monitor view.
 *  @author Kay Kasemir
 */
public class SysMonView extends ViewPart
{
    /** View ID (registered in plugin.xml) */
    final public static String ID = "org.csstudio.utility.sysmon.SysMonView"; //$NON-NLS-1$

    private SysInfoBuffer sysinfos;

    /** Delay between scans */
    private int scan_delay_millis;

    // GUI Elements
    private Text time_span, free, total, max;
    private PlotWidget plot;

    /** Runnable that runs the update */
    private Runnable updater = new Runnable()
    {
        @Override
        public void run()
        {
            update();
        }
    };


    /** Create GUI */
    @Override
    public void createPartControl(Composite parent)
    {
        sysinfos = new SysInfoBuffer(PreferencePage.getHistorySize());
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        GridData gd;
        parent.setLayout(layout);

        // ........ Plot .......
        // Span:  __time span__
        // Total: __total__
        // Free:  __free___
        // Max:   __max____ [GC]
        plot = new PlotWidget(parent, 0);
        plot.setSamples(sysinfos);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        plot.setLayoutData(gd);

        // New Row
        Label l = new Label(parent, 0);
        l.setText(Messages.SysMon_SpanLabel);
        l.setLayoutData(new GridData());

        time_span = new Text(parent, SWT.READ_ONLY);
        time_span.setToolTipText(Messages.SysMon_Span_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        time_span.setLayoutData(gd);

        // Placeholder for last column
        l = new Label(parent, 0);
        l.setLayoutData(new GridData());

        // New Row
        l = new Label(parent, 0);
        l.setText(Messages.SysMon_TotalLabel);
        l.setLayoutData(new GridData());

        total = new Text(parent, SWT.READ_ONLY);
        total.setForeground(sysinfos.getTotalColor());
        total.setToolTipText(Messages.SysMon_Total_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        total.setLayoutData(gd);

        // Placeholder for last column
        l = new Label(parent, 0);
        l.setLayoutData(new GridData());

        // New Row
        l = new Label(parent, 0);
        l.setText(Messages.SysMon_FreeLabel);
        l.setLayoutData(new GridData());

        free = new Text(parent, SWT.READ_ONLY);
        free.setForeground(sysinfos.getFreeColor());
        free.setToolTipText(Messages.SysMon_Free_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        free.setLayoutData(gd);

        // Placeholder for last column
        l = new Label(parent, 0);
        l.setLayoutData(new GridData());

        // New Row
        l = new Label(parent, 0);
        l.setText(Messages.SysMon_MaxLabel);
        l.setLayoutData(new GridData());

        max = new Text(parent, SWT.READ_ONLY);
        max.setToolTipText(Messages.SysMon_Max_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        max.setLayoutData(gd);

        final Button run_gc = new Button(parent, SWT.PUSH);
        run_gc.setText(Messages.SysMon_GCLabel);
        run_gc.setToolTipText(Messages.SysMon_GC_TT);
        gd = new GridData();
        gd.verticalAlignment = SWT.BOTTOM;
        run_gc.setLayoutData(gd);

        run_gc.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Runtime.getRuntime().gc();
            }
        });

        scan_delay_millis = PreferencePage.getScanDelaySecs() * 1000;
        update();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** Update the GUI and schedule following update. */
    private void update()
    {
        if (free.isDisposed() ||  total.isDisposed())
            return;

        final SysInfo info = new SysInfo();
        sysinfos.add(info);
        // Compute displayed time span
        final double displayed_hours =
            sysinfos.getSampleCount() * scan_delay_millis/1000.0/60.0/60.0;
        time_span.setText(String.format(Messages.SysMon_TimeSpanFormat, displayed_hours));
        free.setText(String.format(Messages.SysMon_MemFormat, info.getFreeMB()));
        total.setText(String.format(Messages.SysMon_MemFormat, info.getTotalMB()));
        max.setText(String.format(Messages.SysMon_MemFormat, info.getMaxMB()));
        plot.redraw();
        Display.getDefault().timerExec(scan_delay_millis, updater);
    }
}
