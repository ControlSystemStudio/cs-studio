package org.csstudio.swt.chart.actions;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.csstudio.swt.chart.Messages;

/** Dialog for creating elog entry.
 *  @author Kay Kasemir
 */
abstract public class ExportToElogDialog extends TitleAreaDialog
{
    final private String application;
    final private String[] logbooks;
    final private String default_logbook;

    private Text user, password, title, body;
    private Combo logbook;

    /** Construct a dialog
     *  @param shell The parent shell
     *  @param application Application name
     *  @param logbooks List of available logbooks or <code>null</code>
     *  @param default logbook or <code>null</code>
     */
    public ExportToElogDialog(final Shell shell,
            final String application,
            final String[] logbooks,
            final String default_logbook)
    {
        super(shell);
        this.application = application;
        this.logbooks = logbooks;
        this.default_logbook = default_logbook;
        // Try to allow resize, because the 'text' section could
        // use more or less space depending on use.
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    /** Set the dialog title. */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.ELog_Dialog_WindowTitle);
    }
    
    /** Create the GUI. */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        setTitle(Messages.ELog_Dialog_DialogTitle);
        setMessage(Messages.ELog_Dialog_Message);

        // From peeking at super.createDialogArea we happen to expect a Compos.
        final Composite area = (Composite) super.createDialogArea(parent);
        // Put our widgets in another box to have own layout in there 
        final Composite box = new Composite(area, 0);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        box.setLayoutData(gd);
      
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        box.setLayout(layout);
        
        // User:     ____user_______
        // Password: ___password____
        // Logbook:  ___logbook____
        // Title:    ___password____
        // Text:
        // _____________text _______
        Label l = new Label(box, 0);
        l.setText(Messages.ELog_Dialog_User);
        l.setLayoutData(new GridData());

        user = new Text(box, SWT.BORDER);
        user.setToolTipText(Messages.ELog_Dialog_User_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        user.setLayoutData(gd);

        // New Row
        l = new Label(box, 0);
        l.setText(Messages.ELog_Dialog_Password);
        l.setLayoutData(new GridData());

        password = new Text(box, SWT.BORDER | SWT.PASSWORD);
        password.setToolTipText(Messages.ELog_Dialog_Password_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        password.setLayoutData(gd);
        
        // New Row
        if (logbooks.length > 0)
        {
            l = new Label(box, 0);
            l.setText(Messages.ELog_Dialog_Logbook);
            l.setLayoutData(new GridData());
    
            logbook = new Combo(box, SWT.READ_ONLY | SWT.DROP_DOWN);
            logbook.setToolTipText(Messages.ELog_Dialog_Logbook_TT);
            gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
            logbook.setLayoutData(gd);
        }
        
        // New Row
        l = new Label(box, 0);
        l.setText(Messages.ELog_Dialog_Title);
        l.setLayoutData(new GridData());

        title = new Text(box, SWT.BORDER);
        title.setToolTipText(Messages.ELog_Dialog_Title_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        title.setLayoutData(gd);

        // New Row
        l = new Label(box, 0);
        l.setText(Messages.ELog_Dialog_Body);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        l.setLayoutData(gd);

        // New Row
        body = new Text(box, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        body.setToolTipText(Messages.ELog_Dialog_Body_TT);
        gd = new GridData();
        gd.heightHint = 550; // Size guess. Hope that 'RESIZE' works as well.
        gd.widthHint = 400;
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        body.setLayoutData(gd);
        
        // Initialize GUI with data from info
        title.setText(NLS.bind(Messages.ELog_TitleFormat, application));
        body.setText(NLS.bind(Messages.ELog_BodyFormat, application));
        if (logbook != null)
        {
            logbook.setItems(logbooks);
            logbook.setText(default_logbook);
        }
        
        return area;
    }
    
    /** Make the elog entry, display errors. */
    @Override
    protected void okPressed()
    {
        final String log_name = logbook != null ? logbook.getText() : ""; //$NON-NLS-1$
        try
        {
            makeElogEntry(log_name, user.getText().trim(),
                    password.getText().trim(), title.getText().trim(),
                    body.getText().trim());
        }
        catch (Exception ex)
        {
            setErrorMessage(ex.getMessage());
            return;
        }
        super.okPressed();
    }

    /** To be implemented by derived class.
     *  Has to make the actual elog entry.
     *  @param logbook_name 
     *  @param user 
     *  @param password 
     *  @param title 
     *  @param body 
     */
    abstract void makeElogEntry(String logbook_name, String user, String password, String title, String body) throws Exception;
}
