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
    private ITimestamp start;
    private ITimestamp end;
    private Text start_txt;
    private Text end_txt;
    private Button use_plot_time;
    private Text filename_txt;
    private Button time_config;

    private Button browse;

    private Button export;
    
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
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        /* Start:    ____________________________ 
         * End  :    ____________________________ [ ... ]
         *           Use start/end of plot [x]
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

        l = new Label(parent, 0); // placeholder
        gd = new GridData();
        l.setLayoutData(gd);

        // 'start' row        
        l = new Label(parent, 0);
        l.setText(Messages.EndLabel);
        gd = new GridData();
        l.setLayoutData(gd);
        
        end_txt = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end_txt.setLayoutData(gd);

        // Init start/end time texts
        setStartEndFromTimestamps();

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
       
        // 'use time from plot' row
        l = new Label(parent, 0); // placeholder
        gd = new GridData();
        l.setLayoutData(gd);
       
        use_plot_time = new Button(parent, SWT.CHECK);
        use_plot_time.setText(Messages.UsePlotTime);
        use_plot_time.setToolTipText(Messages.UsePlotTime_TT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        use_plot_time.setLayoutData(gd);
        use_plot_time.setSelection(true);
        use_plot_time.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {   conditionallyEnableTimeConfig();  }
        });

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
        l = new Label(parent, 0);
        l.setText(Messages.Export_Info);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.RIGHT;
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        export = new Button(parent, SWT.CENTER);
        export.setText(Messages.Export);
        export.setToolTipText(Messages.Export_TT);
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.BOTTOM;
        export.setLayoutData(gd);
        export.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {   exportRequested();  }
        });
        
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
            filename_txt.setEnabled(false);
            browse.setEnabled(false);
            export.setEnabled(false);
        }
        else
        {
            conditionallyEnableTimeConfig();
            use_plot_time.setEnabled(true);
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
        Job job = new ExportJob(model, start, end, filename_txt.getText());
        job.schedule();
    }
}
