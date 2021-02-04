/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/** Dialog for adding annotation to a trace
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public class AddAnnotationDialog<XTYPE extends Comparable<XTYPE>> extends Dialog
{
    final private RTPlot<XTYPE> plot;
    // Thread-save snapshot of traces at time dialog was opened
    final private List<Trace<XTYPE>> traces = new ArrayList<>();
    private SWTMediaPool media;
    private TableViewer trace_list;
    private Text text;

    /** 'styled' provider that always shows the annotation's color,
     *  even while table row is selected
     */
    class MyLabelProvider extends StyledCellLabelProvider
    {
        MyLabelProvider()
        {
            super(COLORS_ON_SELECTION);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void update(final ViewerCell cell)
        {
            final Trace<XTYPE> trace = (Trace) cell.getElement();
            cell.setText(trace.getName());
            cell.setForeground(media.get(trace.getColor()));
            super.update(cell);
        }
    }

    public AddAnnotationDialog(final Shell shell, final RTPlot<XTYPE> plot)
    {
        super(shell);
        this.plot = plot;
        for (Trace<XTYPE> trace : plot.getTraces())
            traces.add(trace);
    }

    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.AddAnnotation);
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        media = new SWTMediaPool(parent);
        composite.setLayout(new GridLayout(2, false));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.AddAnnotation_Trace);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        trace_list = new TableViewer(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Table table = trace_list.getTable();
        table.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumHeight = 100;
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        trace_list.setContentProvider(new ArrayContentProvider());
        trace_list.setLabelProvider(new MyLabelProvider());
        trace_list.setInput(traces);
        table.setToolTipText(Messages.AddAnnotation_Trace_TT);

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.AddAnnotation_Content);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        text = new Text(composite, SWT.MULTI);
        text.setText(Messages.AddAnnotation_DefaultText);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumHeight = 100;
        text.setLayoutData(gd);
        text.setToolTipText(Messages.AddAnnotation_Text_TT);

        final Text info = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
        info.setBackground(composite.getBackground());
        info.setText(Messages.AddAnnotation_Content_Help);
        gd = new GridData(SWT.FILL, 0, true, false, 2, 1);
        gd.widthHint = 500;
        info.setLayoutData(gd);
        return composite;
    }

    @Override
    protected void okPressed()
    {
        int selected = trace_list.getTable().getSelectionIndex();
        if (selected < 0)
            selected = 0;

        if (traces.isEmpty())
            MessageDialog.openInformation(getShell(), Messages.AddAnnotation, Messages.AddAnnotation_NoTraces);
        else {
          try {
            Date date = Date.from((Instant) Instant.now());
            // Check that the format would work.
            MessageFormat.format(text.getText(), "temp", date, 2.0);
            plot.addAnnotation(traces.get(selected), text.getText());
          } catch (IllegalArgumentException ex) {
            // Keep dialog open to fix 
            MessageDialog.openInformation(getShell(), Messages.AddAnnotation_Error, "Invalid entry: " + ex.getMessage());
            return;
          }
        }
        super.okPressed();
    }
}
