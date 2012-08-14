/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.ui;

import org.csstudio.apputil.ui.swt.ImageTabFolder;
import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.logbook.LogbookFactory;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/** View for creating logbook entry
 *  @author Kay Kasemir
 */
public class ELogEntryView extends ViewPart
{
    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.logbook.ui.ELogEntryView"; //$NON-NLS-1$

    // GUI Elements
    private Text user_name;
    private Text password;
    private Combo logbook;
    private Text title;
    private Text text;
    private ImageTabFolder image_tabfolder;

    private ILogbookFactory logbook_factory;

    /** Create elog entry form */
    @Override
    public void createPartControl(final Composite parent)
    {
        final String[] logbooks;
        try
        {
            logbook_factory = LogbookFactory.getInstance();
            logbooks = logbook_factory.getLogbooks();
        }
        catch (Throwable ex)
        {
            // Error message, quit
            final Label l = new Label(parent, 0);
            l.setText(NLS.bind(Messages.LogEntry_ErrorNoLogFMT, ex.getMessage()));
            return;
        }

        // Create GUI elements
        final GridLayout layout = new GridLayout(6, false);
        parent.setLayout(layout);

        // User: ____
        Label l = new Label(parent, 0);
        l.setText(Messages.LogEntry_User);
        l.setLayoutData(new GridData());

        user_name = new Text(parent, SWT.BORDER);
        user_name.setToolTipText(Messages.LogEntry_User_TT);
        user_name.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // ...    Password: ____
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Password);
        l.setLayoutData(new GridData());

        password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        password.setToolTipText(Messages.LogEntry_Password_TT);
        password.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        if (logbooks.length > 0)
        {
            // .... ....   Logbook: ____
            l = new Label(parent, 0);
            l.setText(Messages.LogEntry_Logbook);
            l.setLayoutData(new GridData());

            logbook = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            logbook.setToolTipText(Messages.LogEntry_Logbook_TT);
            logbook.setItems(logbooks);
            logbook.setText(logbook_factory.getDefaultLogbook());
            logbook.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        }
        else
        {
            logbook = null;
            // Dummy label
            l = new Label(parent, 0);
            l.setLayoutData(new GridData(0, 0, false, false, 2, 1));
        }

        // Title: ____
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Title);
        l.setLayoutData(new GridData());

        title = new Text(parent, SWT.BORDER);
        title.setToolTipText(Messages.LogEntry_Title_TT);
        title.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-1, 1));

        // Text:
        // __ text __
        // __________
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Text);
        l.setLayoutData(new GridData(SWT.BEGINNING, 0, true, false, layout.numColumns, 1));

        text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        text.setToolTipText(Messages.LogEntry_Text_TT);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1);
        gd.minimumHeight = 50;
        text.setLayoutData(gd);

        // Images
        image_tabfolder = new ImageTabFolder(parent, SWT.TOP);
        image_tabfolder.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        // Add Image
        final Button add_image = image_tabfolder.createAddButton(parent);
        add_image.setLayoutData(new GridData(SWT.LEFT, 0, true, false, layout.numColumns-1, 1));

        //  Submit
        final Button submit = new Button(parent, SWT.PUSH);
        submit.setText(Messages.LogEntry_Submit);
        submit.setToolTipText(Messages.LogEntry_Submit_TT);
        submit.setLayoutData(new GridData(SWT.RIGHT, 0, true, false));
        submit.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                makeLogEntry();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        if (text != null)
            text.setFocus();
    }

    /** Create Logbook entry with current GUI values */
    protected void makeLogEntry()
    {
        final Shell shell = logbook.getShell();
        final String logbook_value = logbook.getText().trim();
        final String user_name_value = user_name.getText().trim();
        final String password_value = password.getText().trim();
        final String title_value = title.getText().trim();
        final String text_value = text.getText().trim();
        final String[] images = image_tabfolder.getFilenames();

		final Job create = new Job("Create log entry") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
				try
				{
				    final ILogbook logbook = logbook_factory.connect(logbook_value, user_name_value, password_value);
					logbook.createEntry(title_value, text_value, images);
					logbook.close();
				}
                catch (final Exception ex)
                {
                    shell.getDisplay().asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
                        }
                    });
                }
				return Status.OK_STATUS;
			}
		};
		create.setUser(true);
		create.schedule();
        password.setText(""); //$NON-NLS-1$
        text.setFocus();
    }
}
