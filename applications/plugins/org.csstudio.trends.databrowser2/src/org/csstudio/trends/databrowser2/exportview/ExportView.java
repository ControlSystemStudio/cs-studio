/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.exportview;

import java.io.File;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.ui.swt.ScrolledContainerHelper;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue.Format;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser2.export.ExportErrorHandler;
import org.csstudio.trends.databrowser2.export.MatlabFileExportJob;
import org.csstudio.trends.databrowser2.export.MatlabScriptExportJob;
import org.csstudio.trends.databrowser2.export.PlainExportJob;
import org.csstudio.trends.databrowser2.export.Source;
import org.csstudio.trends.databrowser2.export.SpreadsheetExportJob;
import org.csstudio.trends.databrowser2.export.ValueFormatter;
import org.csstudio.trends.databrowser2.export.ValueWithInfoFormatter;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.util.SingleSourceHelper;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** View for exporting data from the current Data Browser plot
 *  to a file.
 *  @author Kay Kasemir
 */
public class ExportView extends DataBrowserAwareView implements ExportErrorHandler
{
    /** View ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.exportview.ExportView"; //$NON-NLS-1$

    /** Model of the currently selected Data Browser or <code>null</code> */
    private Model model;

    /** GUI Elements */
    private Text filename;
    private Text start;
    private Text end;
    private Button use_plot_times;
    private Button export;
    private Button source_raw;
    private Button source_opt;
    private Text optimize;
    private Button type_matlab;
    private Button tabular;
    private Button min_max_col;
    private Button sev_stat;
    private Button format_decimal;
    private Button format_expo;
    private Text format_digits;

    private Button sel_times;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite real_parent)
    {
        final Composite parent =
            ScrolledContainerHelper.create(real_parent, 700, 320);
        parent.setLayout(new GridLayout());

        // * Samples To Export *
        // Start:  ___start_______________________________________________________________ [select]
        // End  :  ___end_________________________________________________________________ [x] Use start/end time of Plot
        // Source: ( ) Plot  (*) Raw Archived Data  ( ) Averaged Archived Data  __time__   {ghost}

        // * Format *
        // (*) Spreadsheet ( ) Matlab
        // [x] Tabular [x] ... with min/max column [x] ... with Severity/Status
        // (*) Default format  ( ) decimal notation  ( ) exponential notation _digits_ fractional digits

        // * Output *
        // Filename: ______________ [Browse] [EXPORT]

        // * Samples To Export *
        Group group = new Group(parent, 0);
        group.setText(Messages.ExportGroupSource);
        group.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        GridLayout layout = new GridLayout(6, false);
        group.setLayout(layout);

        // Start:  ___start____________________________________________________ [select]
        Label l = new Label(group, 0);
        l.setText(Messages.StartTimeLbl);
        l.setLayoutData(new GridData());

        start = new Text(group, SWT.BORDER);
        start.setToolTipText(Messages.StartTimeTT);
        start.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-2, 1));
        start.setText(new RelativeTime(- Preferences.getTimeSpan()).toString());
        start.setEnabled(false);

        sel_times = new Button(group, SWT.PUSH);
        sel_times.setText(Messages.StartEndDialogBtn);
        sel_times.setToolTipText(Messages.StartEndDialogTT);
        sel_times.setLayoutData(new GridData());
        sel_times.setEnabled(false);
        sel_times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                promptForTimerange();
            }
        });

        // End  :  ___end______________________________________________________ [x] Use start/end time of Plot
        l = new Label(group, 0);
        l.setText(Messages.EndTimeLbl);
        l.setLayoutData(new GridData());

        end = new Text(group, SWT.BORDER);
        end.setToolTipText(Messages.EndTimeTT);
        end.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-2, 1));
        end.setText(RelativeTime.NOW);
        end.setEnabled(false);

        use_plot_times = new Button(group, SWT.CHECK);
        use_plot_times.setText(Messages.ExportPlotStartEnd);
        use_plot_times.setToolTipText(Messages.ExportPlotStartEndTT);
        use_plot_times.setLayoutData(new GridData());
        use_plot_times.setSelection(true);
        use_plot_times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateStartEnd();
            }
        });

        // Source: ( ) Plot  (*) Raw Archived Data  (*) Averaged Archived Data  __time__
        l = new Label(group, 0);
        l.setText(Messages.ExportSource);
        l.setLayoutData(new GridData());

        final Button source_plot = new Button(group, SWT.RADIO);
        source_plot.setText(Messages.ExportSource_Plot);
        source_plot.setToolTipText(Messages.ExportSource_PlotTT);
        source_plot.setLayoutData(new GridData());

        source_raw = new Button(group, SWT.RADIO);
        source_raw.setText(Messages.ExportSource_RawArchive);
        source_raw.setToolTipText(Messages.ExportSource_RawArchiveTT);
        source_raw.setLayoutData(new GridData());
        source_raw.setSelection(true);

        source_opt = new Button(group, SWT.RADIO);
        source_opt.setText(Messages.ExportSource_OptimizedArchive);
        source_opt.setToolTipText(Messages.ExportSource_OptimizedArchiveTT);
        source_opt.setLayoutData(new GridData());

        optimize = new Text(group, SWT.BORDER);
        optimize.setText(Messages.ExportDefaultOptimization);
        optimize.setToolTipText(Messages.ExportOptimizationTT);
        optimize.setLayoutData(new GridData());
        // Enable only when using optimized export
        optimize.setEnabled(source_opt.getSelection());

        source_plot.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                min_max_col.setEnabled(minMaxAllowed());
            }
        });
        source_raw.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                min_max_col.setEnabled(minMaxAllowed());
            }
        });
        source_opt.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final boolean use_optimized = source_opt.getSelection();
                optimize.setEnabled(use_optimized);
                min_max_col.setEnabled(minMaxAllowed());
                if (use_optimized)
                    optimize.setFocus();
            }
        });

        // Ghost label to fill the last column
        l = new Label(group, 0);
        l.setLayoutData(new GridData());

        // * Format *
        // Since there are actually 2 sets of radio buttons, use
        // one Composite per row.
        group = new Group(parent, 0);
        group.setText(Messages.ExportGroupFormat);
        group.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        group.setLayout(new RowLayout(SWT.VERTICAL));

        // (*) Spreadsheet ( ) Matlab
        Composite box = new Composite(group, 0);
        box.setLayout(new RowLayout());
        final Button type_spreadsheet = new Button(box, SWT.RADIO);
        type_spreadsheet.setText(Messages.ExportTypeSpreadsheet);
        type_spreadsheet.setToolTipText(Messages.ExportTypeSpreadsheetTT);
        type_spreadsheet.setSelection(true);

        type_matlab = new Button(box, SWT.RADIO);
        type_matlab.setText(Messages.ExportTypeMatlab);
        type_matlab.setToolTipText(Messages.ExportTypeMatlabTT);

        // [x] Tabular [x] ... with min/max column [x] ... with Severity/Status
        box = new Composite(group, 0);
        box.setLayout(new RowLayout());
        tabular = new Button(box, SWT.CHECK);
        tabular.setText(Messages.ExportTabular);
        tabular.setToolTipText(Messages.ExportTabularTT);
        tabular.setSelection(true);

        min_max_col = new Button(box, SWT.CHECK);
        min_max_col.setText(Messages.ExportMinMaxCol);
        min_max_col.setToolTipText(Messages.ExportMinMaxColTT);
        min_max_col.setSelection(true);
        min_max_col.setEnabled(false);

        sev_stat = new Button(box, SWT.CHECK);
        sev_stat.setText(Messages.ExportValueInfo);
        sev_stat.setToolTipText(Messages.ExportValueInfoTT);
        sev_stat.setSelection(true);

        // ( ) Default format  (*) decimal notation  ( ) exponential notation _digits_ fractional digits
        box = new Composite(group, 0);
        box.setLayout(new RowLayout());
        final Button format_default = new Button(box, SWT.RADIO);
        format_default.setText(Messages.Format_Default);
        format_default.setToolTipText(Messages.ExportFormat_DefaultTT);

        format_decimal = new Button(box, SWT.RADIO);
        format_decimal.setText(Messages.Format_Decimal);
        format_decimal.setToolTipText(Messages.ExportFormat_DecimalTT);
        format_decimal.setSelection(true);

        format_expo = new Button(box, SWT.RADIO);
        format_expo.setText(Messages.Format_Exponential);
        format_expo.setToolTipText(Messages.ExportFormat_ExponentialTT);

        format_digits = new Text(box, SWT.BORDER);
        format_digits.setText(Messages.ExportDefaultDigits);
        format_digits.setToolTipText(Messages.ExportDigitsTT);
        format_digits.setEnabled(false);

        l = new Label(box, 0);
        l.setText(Messages.ExportDigits);

        // Selection and enablement handling of the buttons
        type_spreadsheet.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                tabular.setEnabled(true);
                sev_stat.setEnabled(true);
                format_default.setEnabled(true);
                format_decimal.setEnabled(true);
                format_expo.setEnabled(true);
                format_digits.setEnabled(!format_default.getSelection());
                min_max_col.setEnabled(!source_raw.getSelection());
            }
        });
        type_matlab.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                tabular.setEnabled(false);
                sev_stat.setEnabled(false);
                format_default.setEnabled(false);
                format_decimal.setEnabled(false);
                format_expo.setEnabled(false);
                format_digits.setEnabled(false);
                min_max_col.setEnabled(minMaxAllowed());
            }
        });
        final SelectionAdapter digit_enabler = new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final boolean use_digits = !format_default.getSelection();
                format_digits.setEnabled(use_digits);
                if (use_digits)
                    format_digits.setFocus();
            }
        };
        format_default.addSelectionListener(digit_enabler);
        format_decimal.addSelectionListener(digit_enabler);
        format_expo.addSelectionListener(digit_enabler);

        // * Output *
        group = new Group(parent, 0);
        group.setText(Messages.ExportGroupOutput);
        group.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        layout = new GridLayout(4, false);
        group.setLayout(layout);

        // Filename: ______________ [Browse]
        l = new Label(group, 0);
        l.setText(Messages.ExportFilename);
        l.setLayoutData(new GridData());

        filename = new Text(group, SWT.BORDER);
        filename.setToolTipText(Messages.ExportFilenameTT);
        filename.setText(Messages.ExportDefaultFilename);
        filename.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        filename.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                if (e.character == '\r')
                    startExportJob();
            }
        });

        final Button sel_filename = new Button(group, SWT.PUSH);
        sel_filename.setText(Messages.ExportBrowse);
        sel_filename.setToolTipText(Messages.ExportBrowseTT);
        sel_filename.setLayoutData(new GridData());
        sel_filename.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
            	final String name =  SingleSourceHelper.openFileBrowser(parent.getShell(), SWT.SAVE);
                if (name != null)
                    filename.setText(name.trim());
            }
        });

        export = new Button(group, SWT.PUSH);
        export.setText(Messages.ExportStartExport);
        export.setToolTipText(Messages.ExportStartExportTT);
        export.setLayoutData(new GridData(SWT.RIGHT, 0, true, true));
        export.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                startExportJob();
            }
        });
    }

    /** @return <code>true</code> if the min/max (error) column option should be enabled */
    private boolean minMaxAllowed()
    {
        return !type_matlab.getSelection()  &&   !source_raw.getSelection();
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        filename.setFocus();
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model oldModel, final Model model)
    {
        this.model = model;
        if (model == null)
        {
            start.setEnabled(false);
            end.setEnabled(false);
            use_plot_times.setEnabled(false);
            export.setEnabled(false);
        }
        else
        {
            use_plot_times.setEnabled(true);
            export.setEnabled(true);
            updateStartEnd();
        }
    }

    /** Run start/end time dialog */
    protected void promptForTimerange()
    {
        final String start_time = start.getText();
        final String end_time = end.getText();
        final StartEndDialog dlg = new StartEndDialog(start.getShell(),
                start_time, end_time);
        if (dlg.open() != Window.OK)
            return;
        start.setText(dlg.getStartSpecification());
        end.setText(dlg.getEndSpecification());
    }

    /** Update the start/end time texts */
    private void updateStartEnd()
    {
        if (use_plot_times.getSelection())
        {   // Disable start/end time when using info from plot
            start.setEnabled(false);
            end.setEnabled(false);
            sel_times.setEnabled(false);
            // Show plot's time range
            start.setText(model.getStartSpecification());
            end.setText(model.getEndSpecification());
        }
        else
        {   // Allow direct start/end entry
            start.setEnabled(true);
            end.setEnabled(true);
            sel_times.setEnabled(true);
        }
    }

    /** Start export job with parameters from GUI */
    private void startExportJob()
    {
        if (model == null)
            return;

        // Determine start/end time
        final ITimestamp start_time, end_time;
        if (use_plot_times.getSelection())
        {
            start_time = model.getStartTime();
            end_time = model.getEndTime();
        }
        else
        {
            try
            {
                final StartEndTimeParser times =
                    new StartEndTimeParser(start.getText(), end.getText());
                start_time = TimestampFactory.fromCalendar(times.getStart());
                end_time = TimestampFactory.fromCalendar(times.getEnd());
            }
            catch (final Exception ex)
            {
                handleExportError(ex);
                return;
            }
        }

        // Determine source: Plot, archive, ...
        final Source source;
        int optimize_count = -1;
        if (source_raw.getSelection())
            source = Source.RAW_ARCHIVE;
        else if (source_opt.getSelection())
        {
            source = Source.OPTIMIZED_ARCHIVE;
            try
            {
                optimize_count = Integer.parseInt(optimize.getText());
            }
            catch (Exception ex)
            {
                MessageDialog.openError(optimize.getShell(), Messages.Error,
                      Messages.ExportOptimizeCountError);
                return;
            }
        }
        else
            source = Source.PLOT;

        // Get remaining export parameters
        final String filename = this.filename.getText().trim();
        if (filename.equalsIgnoreCase(Messages.ExportDefaultFilename))
        {
            MessageDialog.openConfirm(optimize.getShell(), Messages.Error, Messages.ExportEnterFilenameError);
            this.filename.setFocus();
            return;
        }
        if (new File(filename).exists())
        {
            if (!MessageDialog.openConfirm(optimize.getShell(), Messages.ExportFileExists,
                    NLS.bind(Messages.ExportFileExistsFmt, filename)))
                return;
        }

        // Construct appropriate export job
        final Job export;
        if (type_matlab.getSelection())
        {   // Matlab file export

            if (filename.endsWith(".m")) //$NON-NLS-1$
                export = new MatlabScriptExportJob(model, start_time, end_time, source,
                        optimize_count, filename, this);
            else if (filename.endsWith(".mat")) //$NON-NLS-1$
                export = new MatlabFileExportJob(model, start_time, end_time, source,
                        optimize_count, filename, this);
            else
            {
                MessageDialog.openError(type_matlab.getShell(), Messages.Error,
                        Messages.ExportMatlabFilenameError);
                return;
            }
        }
        else
        {   // Spreadsheet file export
            final Format format;
            if (format_decimal.getSelection())
                format = Format.Decimal;
            else if (format_expo.getSelection())
                format = Format.Exponential;
            else
                format = Format.Default;
            final int precision;
            if (format == Format.Default)
                precision = 0; // Not used
            else
            {
                try
                {
                    precision = Integer.parseInt(format_digits.getText().trim());
                }
                catch (Exception ex)
                {
                    MessageDialog.openError(optimize.getShell(), Messages.Error,
                    Messages.ExportDigitsError);
                    return;
                }
            }
            final ValueFormatter formatter;
            if (sev_stat.getSelection())
                formatter = new ValueWithInfoFormatter(format, precision);
            else
                formatter = new ValueFormatter(format, precision);
            formatter.useMinMaxColumn(minMaxAllowed() && min_max_col.getSelection());
            if (tabular.getSelection())
                export = new SpreadsheetExportJob(model, start_time, end_time, source,
                        optimize_count, formatter, filename, this);
            else
                export = new PlainExportJob(model, start_time, end_time, source,
                        optimize_count, formatter, filename, this);
        }
        export.schedule();
    }

    /** Display error. Can be called from non-GUI thread
     *  @see ExportErrorHandler
     */
    @Override
    public void handleExportError(final Exception ex)
    {
        if (start.isDisposed())
            return;
        start.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (start.isDisposed())
                    return;
                ExceptionDetailsErrorDialog.openError(start.getShell(), Messages.Error, ex);
            }
        });
    }
}
