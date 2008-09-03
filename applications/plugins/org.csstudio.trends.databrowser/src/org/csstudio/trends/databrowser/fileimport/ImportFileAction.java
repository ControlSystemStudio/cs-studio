package org.csstudio.trends.databrowser.fileimport;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.trends.databrowser.configview.ConfigView;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Import samples from an external file.
 * 
 * @author jhatje
 * 
 */
public class ImportFileAction extends Action {

    /**
     * The Parent. Contain the Databrowser-Model.
     */
    private ConfigView _config;
    
    /**
     * The start path for the import File.
     */
    private static String _startPath = Messages.ImportFileAction_StartPath;

    /**
     * 
     * @param config The parent Config View, need it for the Databrowser Model.
     */
    public ImportFileAction(final ConfigView config) {
        CentralLogger.getInstance().debug(this,
                Messages.ImportFileAction_DebugConstructorImportFileAction);
        _config = config;
        setText(Messages.ImportFile);
        setToolTipText(Messages.ImportFile_TT);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_COPY_DISABLED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        CentralLogger.getInstance().debug(this, Messages.ImportFileAction_DebugRunImportFileAction);

        //
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog fd = new FileDialog(shell, SWT.OPEN);
        fd.setText(Messages.ImportFileAction_FileDialogOpen);
        fd.setFilterPath(_startPath);
        String[] filterExt = { Messages.ImportFileAction_FileExtension1,
                Messages.ImportFileAction_FileExtension2, Messages.ImportFileAction_FileExtension3,
                Messages.ImportFileAction_FileExtension4, Messages.ImportFileAction_FileExtension5,
                Messages.ImportFileAction_FileExtension6 };
        fd.setFilterExtensions(filterExt);
        String fileName = fd.open();
        if (fileName == null) {
            return;
        }
        File dataFile = new File(fileName);
        if (dataFile.exists()) {
            _startPath = dataFile.getAbsolutePath();
            if (dataFile.isFile() && dataFile.canRead()) {

                String[] tmp = dataFile.getName().split("\\."); //$NON-NLS-1$
                String extendsion = tmp[tmp.length - 1];
                if (extendsion.compareToIgnoreCase("csv") == 0) { //$NON-NLS-1$
                    SampleFileImportSettings result = new SampleFileImportSettings();
                    SampleFileReader reader = new SampleFileReader(
                            Messages.ImportFileAction_SampleFileReader, dataFile,
                            _config.getModel(), result);
                    result = (SampleFileImportSettings) reader.getSettings();
                    SampleFileDialog dialog = new SampleFileDialog(shell, _config.getModel(), result);
                    dialog.open();
                    if (result.getSelectedSize() != 0) {
                    	reader.schedule();
                    }else{
                        reader=null;
                    }

                } else if (extendsion.compareToIgnoreCase("scope") == 0) { //$NON-NLS-1$
                    SampleFileImportSettings result = new SampleFileImportSettings();
                    ScopeFileReader reader = new ScopeFileReader(
                            Messages.ImportFileAction_ScopeFileReader, dataFile, _config.getModel(),
                            result);
                    if (reader.hasTime()) {
                        reader.parseHeader();
                    } else {
                        final Calendar timeCalendar = GregorianCalendar.getInstance();
                        final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
                        dialog.setLayout(new GridLayout(2, false));
                        dialog.setText(Messages.ImportFileAction_TrigerTimeDialogHeadline);

                        final DateTime calendar = new DateTime(dialog, SWT.CALENDAR | SWT.BORDER);
                        calendar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2,
                                1));
                        new Label(dialog, SWT.NONE).setText(Messages.ImportFileAction_TimeLabel);
                        new Label(dialog, SWT.NONE)
                                .setText(Messages.ImportFileAction_MillisecLabel);
                        final DateTime time = new DateTime(dialog, SWT.TIME | SWT.LONG);
                        time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
                        final Spinner milli = new Spinner(dialog, SWT.WRAP | SWT.BORDER);
                        milli.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
                        milli.setMaximum(999);
                        new Label(dialog, SWT.NONE);

                        Button ok = new Button(dialog, SWT.PUSH);
                        ok.setText(Messages.ImportFileAction_OkButton);
                        ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
                        ok.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(final SelectionEvent e) {
                                timeCalendar.setTimeInMillis(new GregorianCalendar(calendar.getYear(), calendar
                                        .getMonth(), calendar.getDay(), time.getHours(), time
                                        .getMinutes(), time.getSeconds()).getTimeInMillis());
                                timeCalendar.add(Calendar.MILLISECOND, milli.getDigits());
                                dialog.close();
                            }
                        });
                        dialog.setDefaultButton(ok);
                        dialog.pack();
                        dialog.open();
                        Display display = dialog.getDisplay();
                        while (!dialog.isDisposed()) {
                            if (!display.readAndDispatch()){
                                display.sleep();
                            }
                        }
                        reader.parseHeader(timeCalendar);
                    }
                    result = (SampleFileImportSettings) reader.getSettings();
                    SampleFileDialog dialog = new SampleFileDialog(shell, _config.getModel(), result);
                    dialog.open();
                    if (result.getSelectedSize() != 0) {
                        reader.schedule();
                    }else{
                        reader=null;
                    }
                }

            }
        }

    }

}
