/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.ui.dialog.ErrorDialog;
import org.csstudio.apputil.ui.elog.ImagePreview;
import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.logbook.LogbookFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
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
    private TabFolder image_tabfolder;

    private ILogbookFactory logbook_factory;

    private List<String> image_filenames = new ArrayList<String>();

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
            l.setText(Messages.LogEntry_ErrorNoLog + ex.getMessage());
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

        image_tabfolder = new TabFolder(parent, SWT.TOP);
        image_tabfolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        for (String image : image_filenames)
            addImage(image);

        // Add Image
        final Button add_image = new Button(parent, SWT.PUSH);
        add_image.setText(Messages.ELogEntryView_AddImage);
        add_image.setToolTipText(Messages.ELogEntryView_AddImageTT);
        add_image.setLayoutData(new GridData(SWT.LEFT, 0, true, false, layout.numColumns-1, 1));
        add_image.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addImage();
            }
        });

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

    /** Prompt for image file to add */
    protected void addImage()
    {
        final FileDialog dlg = new FileDialog(getSite().getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String [] { "*.png" }); //$NON-NLS-1$
        dlg.setFilterNames(new String [] { "PNG Image" }); //$NON-NLS-1$
        final String filename = dlg.open();
        if (filename != null)
            addImage(filename);
    }

    /** Add image preview to tab folder
     *  @param filename Image file name
     */
    private void addImage(final String filename)
    {
        // Add tab item
        final TabItem tab = new TabItem(image_tabfolder, 0);
        tab.setText(NLS.bind(Messages.LogEntry_ImageTabFmt, image_tabfolder.getItemCount()));

        final Composite box = new Composite(image_tabfolder, 0);
        box.setLayout(new GridLayout(2, false));

        // Preview
        final ImagePreview image_preview = new ImagePreview(box, null, filename);
        image_preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Delete button
        final Button delete = new Button(box, SWT.PUSH);
        delete.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        delete.setText(Messages.LogEntry_RemoveImage);
        delete.setToolTipText(Messages.LogEntry_RemoveImageTT);
        delete.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                TabItem tabs[] = image_tabfolder.getItems();
                for (int i=0; i<tabs.length; ++i)
                    if (tabs[i] == tab)
                    {
                        removeImage(i);
                        return;
                    }
            }
        });

        // File name label
        final Label label = new Label(box, 0);
        label.setText(filename);
        label.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        tab.setControl(box);

        // Select the newly added tab, i.e. the last one
        image_tabfolder.setSelection(image_tabfolder.getItemCount()-1);

        // Add file name to list
        image_filenames.add(filename);
    }

    /** Remove image from preview and list of images-to-add
     *  @param i Index of image
     */
    protected void removeImage(final int i)
    {
        // Remove tab with preview
        final TabItem tab = image_tabfolder.getItem(i);
        final Control tab_control = tab.getControl();
        tab.dispose();
        tab_control.dispose();

        // Remove from list of file names
        image_filenames.remove(i);

        // Re-number the tabs
        final TabItem tabs[] = image_tabfolder.getItems();
        for (int t=i; t<tabs.length; ++t)
            tabs[t].setText(NLS.bind(Messages.LogEntry_ImageTabFmt, t+1));
    }

    /** Create Logbook entry with current GUI values */
    protected void makeLogEntry()
    {
        final String logbook_value = logbook.getText().trim();
        final String user_name_value = user_name.getText().trim();
        final String password_value = password.getText().trim();
        final ILogbook log;
        try
        {
            log = logbook_factory.connect(logbook_value, user_name_value, password_value);
        }
        catch (Exception ex)
        {
            ErrorDialog.open(getSite().getShell(), Messages.Error,
                    NLS.bind(Messages.LogEntry_ErrorCannotConnectFMT, ex.getMessage()));
            return;
        }
        try
        {
            final String filenames[] = image_filenames.toArray(new String[image_filenames.size()]);
            log.createEntry(title.getText().trim(), text.getText().trim(), filenames);
        }
        catch (Exception ex)
        {
            ErrorDialog.open(getSite().getShell(), Messages.Error,
                    NLS.bind(Messages.LogEntry_ErrorFMT, ex.getMessage()));
            return;
        }
        password.setText(""); //$NON-NLS-1$
        text.setFocus();
    }
}
