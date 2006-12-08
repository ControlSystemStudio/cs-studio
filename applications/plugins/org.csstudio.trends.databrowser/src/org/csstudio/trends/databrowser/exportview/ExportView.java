package org.csstudio.trends.databrowser.exportview;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.trends.databrowser.ploteditor.StartEndDialog;
import org.csstudio.value.Value;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** View to configure and start "export" of samples to a file.
 *  @see ExportJob
 *  @author Kay Kasemir
 */
public class ExportView extends PlotAwareView
{
    public static final String ID = ExportView.class.getName();

    // GUI Elements
    private Shell shell;
    private Text start_txt;
    private Text end_txt;
    private Button use_plot_time;
    private Button time_config;
    private Button source_plot, source_raw, source_avg;
    private Text   avg_seconds;
    private Button format_spreadsheet;
    private Button format_severity;
    private Button format_default;
    private Button format_decimal;
    private Button format_exponential;
    private Text precision;
    private Text filename_txt;
    private Button browse;
    private Button export;
    
    // Stuff that's updated by the GUI
    // (rest directly read from the GUI elements)
    private ITimestamp start;
    private ITimestamp end;
    private ExportJob.Source source;
    private Value.Format format;
    
    public ExportView()
    {
        start = TimestampFactory.now();
        end = TimestampFactory.now();
    }
    
    /** Create the GUI elements. */
    @Override
    public void createPartControl(Composite parent)
    {
        shell = parent.getShell();
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        /*           [x] from plot
         * Start:    ____________________________ [ ... ]
         * End  :    ____________________________ 
         *
         * Source:   ( ) plot (*) raw ... ( ) averaged archive
         * 
         * Output:   [x] Spreadsheet  [x] .. with Severity/Status
         * Format:   (*) default ( ) decimal ( ) exponential __ fractional digits
         *           
         * Filename: ________________ [ Browse ]
         *                            [ Export ]
         */
        Label l;
        
        // 'use plot time' row
        l = new Label(parent, 0); // placeholder
        l.setLayoutData(new GridData());

        use_plot_time = new Button(parent, SWT.CHECK);
        use_plot_time.setText(Messages.UsePlotTime);
        use_plot_time.setToolTipText(Messages.UsePlotTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns - 1;
        use_plot_time.setLayoutData(gd);
        use_plot_time.setSelection(true);
        use_plot_time.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {   conditionallyEnableTimeConfig();  }
        });

        // 'start' row
        l = new Label(parent, 0);
        l.setText(Messages.StartLabel);
        gd = new GridData();
        l.setLayoutData(gd);
        
        start_txt = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        start_txt.setLayoutData(gd);
        
        time_config = new Button(parent, SWT.CENTER);
        time_config.setText(Messages.SelectTime);
        time_config.setToolTipText(Messages.SelectTime_TT);
        gd = new GridData();
        time_config.setLayoutData(gd);
        time_config.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                StartEndDialog dlg = new StartEndDialog(shell, start, end);
                if (dlg.open() != StartEndDialog.OK)
                    return;
                start = dlg.getStart();
                end = dlg.getEnd();
                setStartEndFromTimestamps();
            }
        });
       
        // 'end' row        
        l = new Label(parent, 0);
        l.setText(Messages.EndLabel);
        gd = new GridData();
        l.setLayoutData(gd);
        
        end_txt = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end_txt.setLayoutData(gd);
        
        l = new Label(parent, 0); // placeholder
        l.setLayoutData(new GridData());
        
        // 'Source' row
        l = new Label(parent, 0);
        l.setText(Messages.SourceLabel);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);

        // ... 'source' radio button group
        Composite frame = new Composite(parent, 0);
        RowLayout row_layout = new RowLayout();
        row_layout.marginLeft = 0;
        row_layout.marginTop = 0;
        frame.setLayout(row_layout);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns - 1;
        gd.horizontalAlignment = SWT.FILL;
        frame.setLayoutData(gd);
        
        source_plot = new Button(frame, SWT.RADIO);
        source_plot.setText(Messages.Source_Plot);
        source_plot.setToolTipText(Messages.Source_Plot_TT);
        
        source_raw = new Button(frame, SWT.RADIO);
        source_raw.setText(Messages.Source_Raw);
        source_raw.setToolTipText(Messages.Source_Raw_TT);

        source_avg = new Button(frame, SWT.RADIO);
        source_avg.setText(Messages.Source_Average);
        source_avg.setToolTipText(Messages.Source_Average_TT);
        
        avg_seconds = new Text(frame, SWT.BORDER);
        avg_seconds.setText("60");  //$NON-NLS-1$
        avg_seconds.setToolTipText(Messages.Source_Seconds_TT);
        l = new Label(frame, 0);
        l.setText(Messages.Source_Seconds);
        
        source_plot.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            { 
                source = ExportJob.Source.Plot;
                avg_seconds.setEnabled(false);
            }
        });
        source_raw.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {  
                source = ExportJob.Source.Raw;
                avg_seconds.setEnabled(false);
            }
        });
        source_avg.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            { 
                source = ExportJob.Source.Average;
                avg_seconds.setEnabled(true);
            }
        });
        
        // ... end of radio buttons
        
        // 'Output' row
        l = new Label(parent, 0);
        l.setText(Messages.OutputLabel);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);

        frame = new Composite(parent, 0);
        row_layout = new RowLayout();
        row_layout.marginLeft = 0;
        row_layout.marginTop = 0;
        frame.setLayout(row_layout);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns - 1;
        frame.setLayoutData(gd);

        format_spreadsheet = new Button(frame, SWT.CHECK);
        format_spreadsheet.setText(Messages.Spreadsheet);
        format_spreadsheet.setToolTipText(Messages.Spreadsheet_TT);

        format_severity = new Button(frame, SWT.CHECK);
        format_severity.setText(Messages.ShowSeverity);
        format_severity.setToolTipText(Messages.ShowSeverity_TT);

        // Number format row
        l = new Label(parent, 0);
        l.setText(Messages.FormatLabel);
        l.setLayoutData(new GridData());
        
        frame = new Composite(parent, 0);
        row_layout = new RowLayout();
        row_layout.pack = false;
        row_layout.marginLeft = 0;
        row_layout.marginTop = 0;
        frame.setLayout(row_layout);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns - 1;
        gd.horizontalAlignment = SWT.FILL;
        frame.setLayoutData(gd);
        
        format_default = new Button(frame, SWT.RADIO);
        format_default.setText(Messages.FormatDefaultLabel);
        format_default.setToolTipText(Messages.FormatDefault_TT);
        format_default.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   format = Value.Format.Default; }
        });
        
        format_decimal = new Button(frame, SWT.RADIO);
        format_decimal.setText(Messages.FormatDecimalLabel);
        format_decimal.setToolTipText(Messages.FormatDecimal_TT);
        format_decimal.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   format = Value.Format.Decimal; }
        });

        format_exponential = new Button(frame, SWT.RADIO);
        format_exponential.setText(Messages.FormatExponentialLabel);
        format_exponential.setToolTipText(Messages.FormatExponential_TT);
        format_exponential.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   format = Value.Format.Exponential; }
        });
        // ... end of radio buttons
        
        precision = new Text(frame, SWT.BORDER);
        precision.setTextLimit(3);
        precision.setToolTipText(Messages.FormatPrecision_TT);

        l = new Label(frame, 0);
        l.setText(Messages.FormatPrecisionLabel);
        
        // 'filename' row
        l = new Label(parent, 0);
        l.setText(Messages.FilenameLabel);
        gd = new GridData();
        l.setLayoutData(gd);
        
        filename_txt = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        filename_txt.setLayoutData(gd);
        filename_txt.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                export.setEnabled(filename_txt.getText().length() > 0);
            }
        });
                
        browse = new Button(parent, SWT.CENTER);
        browse.setText(Messages.Browse);
        browse.setToolTipText(Messages.Browse_TT);
        gd = new GridData();
        browse.setLayoutData(gd);
        
        browse.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog dlg = new FileDialog(shell, SWT.SAVE);
                // Unclear, how well these two calls work across systems
                dlg.setFilterExtensions(new String [] { "*.dat" }); //$NON-NLS-1$
                
                String full = filename_txt.getText();
                int end = full.lastIndexOf('/');
                if (end >= 0)
                {
                    dlg.setFilterPath(full.substring(0, end));
                    dlg.setFileName(full.substring(end+1));
                }
                else
                    dlg.setFileName(full);
                String name = dlg.open();
                if (name != null)
                    filename_txt.setText(name);
                /* Maybe better to use the workspace dialogs?
                SaveAsDialog dlg = new SaveAsDialog(parent);
                if (dlg.open() == SaveAsDialog.OK)
                {
                    filename_txt.setText(dlg.getResult().toString());
                }
                */
            }
        });
        
        // 'export' row        
        export = new Button(parent, SWT.CENTER);
        export.setText(Messages.Export);
        export.setToolTipText(Messages.Export_TT);
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalSpan = layout.numColumns;
        gd.verticalAlignment = SWT.BOTTOM;
        export.setLayoutData(gd);
        export.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {   exportRequested();  }
        });
        
        // Initial settings
        setStartEndFromTimestamps();
        // Plot/raw/averaged data?
        source_raw.setSelection(true);
        source = ExportJob.Source.Raw;
        avg_seconds.setEnabled(source_avg.getSelection());
        
        // Format
        precision.setText("4"); //$NON-NLS-1$
        // precision 'enables' whenever non-default format selected
        format_default.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {   precision.setEnabled(! format_default.getSelection());   }
        });
        format_default.setSelection(true);
        format = Value.Format.Default;
        format_spreadsheet.setSelection(true);
        format_severity.setSelection(true);
        
        // Enable updateModel() notification:
        super.createPartControl(parent);
    }
    
    /** Set the initial focus. */
    @Override
    public void setFocus()
    {
        filename_txt.setFocus();
    }
    
    /** Enable the time start/end/select GUI
     *  unless we get that info from plot,
     */
    private void conditionallyEnableTimeConfig()
    {
        boolean allow_config = !use_plot_time.getSelection();
        start_txt.setEnabled(allow_config);
        end_txt.setEnabled(allow_config);
        time_config.setEnabled(allow_config);
    }

    // Another Model becomes current.
    // @see PlotAwareView
    @Override
    protected void updateModel(Model old_model, Model new_model)
    {
        if (new_model == null)
        {
            start_txt.setEnabled(false);
            end_txt.setEnabled(false);
            time_config.setEnabled(false);
            use_plot_time.setEnabled(false);
            
            source_plot.setEnabled(false);
            source_raw.setEnabled(false);
            source_avg.setEnabled(false);
            
            format_spreadsheet.setEnabled(false);
            format_severity.setEnabled(false);
            format_default.setEnabled(false);
            format_decimal.setEnabled(false);
            format_exponential.setEnabled(false);
            precision.setEnabled(false);
            
            filename_txt.setEnabled(false);
            browse.setEnabled(false);
            export.setEnabled(false);
        }
        else
        {
            conditionallyEnableTimeConfig();
            use_plot_time.setEnabled(true);

            source_plot.setEnabled(true);
            source_raw.setEnabled(true);
            source_avg.setEnabled(true);
            
            format_spreadsheet.setEnabled(true);
            format_severity.setEnabled(true);
            
            format_default.setEnabled(true);
            format_decimal.setEnabled(true);
            format_exponential.setEnabled(true);
            precision.setEnabled(! format_default.getSelection());
            
            filename_txt.setEnabled(true);
            browse.setEnabled(true);
            export.setEnabled(filename_txt.getText().length() > 0);
        }
        if (old_model == new_model)
            return;
    }
    
    /** Update start/end text fields from timestamps. */
    private void setStartEndFromTimestamps()
    {
        start_txt.setText(start.format(ITimestamp.FMT_DATE_HH_MM_SS));
        end_txt.setText(end.format(ITimestamp.FMT_DATE_HH_MM_SS));
    }

    /** Start data export with current settings. */
    private void exportRequested()
    {
        PlotEditor editor = getPlotEditor();
        Model model = editor.getModel();
        if (use_plot_time.getSelection())
        {   // Update start/end from plot
            start = editor.getStart();
            end = editor.getEnd();
            setStartEndFromTimestamps();
        }
        else
        {   // Update start/end from text boxes
            try
            {
                start = TimestampUtil.fromString(start_txt.getText());
            }
            catch (Exception e)
            {
                MessageBox msg = new MessageBox(shell, SWT.OK);
                msg.setText(Messages.Error);
                msg.setMessage(Messages.CannotDecodeStart);
                msg.open();
                return;
            }
            try
            {
                end = TimestampUtil.fromString(end_txt.getText());
            }
            catch (Exception e)
            {
                MessageBox msg = new MessageBox(shell, SWT.OK);
                msg.setText(Messages.Error);
                msg.setMessage(Messages.CannotDecodeEnd);
                msg.open();
                return;
            }
        }
        double secs;
        try
        {
            secs = Double.parseDouble(avg_seconds.getText());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            secs = 60.0;
        }
        int prec;
        try
        {
            prec = Integer.parseInt(precision.getText());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            prec = 0;
        }
        // Launch the actual export
        Job job = new ExportJob(model, start, end, source,
                        secs,
                        format_spreadsheet.getSelection(),
                        format_severity.getSelection(),
                        format, prec,
                        filename_txt.getText());
        job.schedule();
    }
}
