package org.csstudio.trends.databrowser.exportview;

import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.trends.databrowser.ploteditor.StartEndDialog;
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
 *  @author Kay Kasemir
 */
public class ExportView extends PlotAwareView
{
    public static final String ID = ExportView.class.getName();

    private Shell shell;
    private Text start_txt;
    private Text end_txt;
    private Button use_plot_time;
    private Button time_config;
    private Button source_plot, source_raw, source_avg;
    private Button add_live_samples;
    private Button format_spreadsheet;
    private Button format_severity;
    private Text filename_txt;
    private Button browse;

    private Button export;
    
    private ITimestamp start;
    private ITimestamp end;
    private ExportJob.Source source;
    
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

        /* Start:    ____________________________ [ ... ]
         * End  :    ____________________________ 
         *           [x] from plot
         *
         * Source:   ( ) plot (*) raw ... ( ) averaged archive
         *           [x] add 'live' data
         * 
         * Format:   [x] Spreadsheet  [x] .. with Severity/Status
         *
         * Filename: ________________ [ Browse ]
         *                            [ Export ]
         */
        Label l;
        
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

        // 'Source' row
        l = new Label(parent, 0);
        l.setText(Messages.Source);
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
        source_plot.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   source = ExportJob.Source.Plot; }
        });
        
        source_raw = new Button(frame, SWT.RADIO);
        source_raw.setText(Messages.Source_Raw);
        source_raw.setToolTipText(Messages.Source_Raw_TT);
        source_raw.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   source = ExportJob.Source.Raw; }
        });

        source_avg = new Button(frame, SWT.RADIO);
        source_avg.setText(Messages.Source_Average);
        source_avg.setToolTipText(Messages.Source_Average_TT);
        source_avg.addSelectionListener(new SelectionAdapter()
        {
            @Override public void widgetSelected(SelectionEvent e)
            {   source = ExportJob.Source.Average; }
        });

        // ... end of radio buttons
        
        // 'add live' row
        l = new Label(parent, 0); // placeholder
        l.setLayoutData(new GridData());
        
        add_live_samples = new Button(parent, SWT.CHECK);
        add_live_samples.setText(Messages.AddLive);
        add_live_samples.setToolTipText(Messages.AddLive_TT);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        add_live_samples.setLayoutData(gd);
        
        // 'Format' row
        l = new Label(parent, 0);
        l.setText(Messages.Format);
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
        source_raw.setSelection(true);
        source = ExportJob.Source.Raw;
        add_live_samples.setSelection(true);
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
            add_live_samples.setEnabled(false);
            
            format_spreadsheet.setEnabled(false);
            format_severity.setEnabled(false);
            
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
            add_live_samples.setEnabled(true);
            
            format_spreadsheet.setEnabled(true);
            format_severity.setEnabled(true);
            
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
        Job job = new ExportJob(model, start, end, source,
                                add_live_samples.getSelection(),
                                format_spreadsheet.getSelection(),
                                format_severity.getSelection(),
                                filename_txt.getText());
        job.schedule();
    }
}
