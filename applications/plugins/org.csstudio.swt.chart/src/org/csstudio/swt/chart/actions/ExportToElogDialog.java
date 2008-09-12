package org.csstudio.swt.chart.actions;

import org.eclipse.jface.dialogs.TitleAreaDialog;
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
public class ExportToElogDialog extends TitleAreaDialog
{
    private Text user, password, title, body;
    private ExportToElogInfo info;
    private Combo logbook;
    private String[] logbooks;

    /** Construct a dialog
     *  @param shell The parent shell
     *  @param logbooks List of available logbooks or <code>null</code>
     *  @param Info entered so far
     */
    public ExportToElogDialog(final Shell shell, final String[] logbooks,
            final ExportToElogInfo info)
    {
        super(shell);
        this.logbooks = logbooks;
        this.info  = info;
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
        if (logbooks != null)
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
        user.setText(info.getUser());
        password.setText(info.getPassword());
        if (logbook != null)
        {
            logbook.setItems(logbooks);
            logbook.setText(info.getLogbook());
        }
        title.setText(info.getTitle());
        body.setText(info.getBody());
        
        return area;
    }
    
    /** Update the formula item. */
    @Override
    protected void okPressed()
    {
        final String log_name = logbook != null ? logbook.getText() : null;
        info = new ExportToElogInfo(user.getText(),
                password.getText(), log_name, title.getText(),
                body.getText());
        super.okPressed();
    }

    /** @return Export info (only valid after OK was pressed) */
    public ExportToElogInfo getInfo()
    {
        return info;
    }
}
