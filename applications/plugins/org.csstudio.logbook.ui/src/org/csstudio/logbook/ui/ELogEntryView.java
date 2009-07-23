package org.csstudio.logbook.ui;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
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

    private Button add_image;

    private Label status;

    private ILogbookFactory logbook_factory;

    private ImagePreview image;

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
        final GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        // User: ____
        Label l = new Label(parent, 0);
        l.setText(Messages.LogEntry_User);
        l.setLayoutData(new GridData());
        
        user_name = new Text(parent, SWT.BORDER);
        user_name.setToolTipText(Messages.LogEntry_User_TT);
        user_name.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Password: ____
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Password);
        l.setLayoutData(new GridData());

        password = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        password.setToolTipText(Messages.LogEntry_Password_TT);
        password.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        if (logbooks.length > 0)
        {
            // Logbook: ____
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
            logbook = null;

        // Title: ____
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Title);
        l.setLayoutData(new GridData());

        title = new Text(parent, SWT.BORDER);
        title.setToolTipText(Messages.LogEntry_Title_TT);
        title.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // Text:
        // __ text __
        // __________
        l = new Label(parent, 0);
        l.setText(Messages.LogEntry_Text);
        l.setLayoutData(new GridData(SWT.BEGINNING, 0, true, false, layout.numColumns, 1));

        text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        text.setToolTipText(Messages.LogEntry_Text_TT);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        
        // Box with...
        final Composite box = new Composite(parent, 0);
        box.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
        final GridLayout box_layout = new GridLayout(2, false);
        box_layout.marginLeft = 0;
        box_layout.marginRight = 0;
        box_layout.marginBottom = 0;
        box.setLayout(box_layout);
        
        image = new ImagePreview(box);
        image.setLayoutData(new GridData());
        
        add_image = new Button(box, SWT.PUSH);
        add_image.setText("Add Image");
        add_image.setToolTipText("Select an image to add to the entry");
        add_image.setLayoutData(new GridData());
        add_image.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addImage();
            }
        });
        
        // __status__ Submit
        status = new Label(box, 0);
        status.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        final Button submit = new Button(box, SWT.PUSH);
        submit.setText(Messages.LogEntry_Submit);
        submit.setToolTipText(Messages.LogEntry_Submit_TT);
        submit.setLayoutData(new GridData(0, 0, false, false));
        submit.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                makeLogEntry();
            }
        });

        updateStatus(Messages.LogEntry_InitialMessage, false);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        if (text != null)
            text.setFocus();
    }
    
    private void updateStatus(final String text, final boolean error)
    {
        status.setText(text);
        if (error)
            status.setForeground(status.getDisplay().getSystemColor(SWT.COLOR_RED));
        else
            status.setForeground(null);
    }

    /** Prompt for image file to add */
    protected void addImage()
    {
        final FileDialog dlg = new FileDialog(add_image.getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String [] { "*.png" });
        dlg.setFilterNames(new String [] { "PNG Image" });
        final String filename = dlg.open();
        image.setImage(filename);
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
            updateStatus(NLS.bind(Messages.LogEntry_ErrorCannotConnectFMT, ex.getMessage()), true);
            return;
        }
        try
        {
            log.createEntry(title.getText().trim(), text.getText().trim(), image.getImage());
        }
        catch (Exception ex)
        {
            updateStatus(NLS.bind(Messages.LogEntry_ErrorFMT, ex.getMessage()), true);
            return;
        }
        password.setText(""); //$NON-NLS-1$
        text.setFocus();
        updateStatus(Messages.LogEntry_OKMessage, false);
    }
}
