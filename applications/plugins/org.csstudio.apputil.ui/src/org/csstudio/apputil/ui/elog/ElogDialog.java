package org.csstudio.apputil.ui.elog;

import org.csstudio.apputil.ui.Activator;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.logbook.LogbookFactory;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for creating elog entry.
 *  <p>
 *  Derived class must implement <code>makeElogEntry</code>.
 *  @author Kay Kasemir
 */
abstract public class ElogDialog extends TitleAreaDialog
{
    final private ILogbookFactory logbook_factory;
    final private String message, initial_title, initial_body, image_filename;
    final private String[] logbooks;

    private Text user, password, title, body;
    private Combo logbook;

    /** Construct a dialog
     *  @param shell The parent shell
     *  @param message Message, explanation of entry
     *  @param initial_title Initial title for new entry
     *  @param initial_body Initial body text for new entry
     *  @param image_filename Name of image file or <code>null</code> 
     *  @throws Exception on error
     */
    public ElogDialog(final Shell shell,
            final String message,
            final String initial_title,
            final String initial_body,
            final String image_filename) throws Exception
    {
        super(shell);
        this.logbook_factory = LogbookFactory.getInstance();
        this.message = message;
        this.initial_title = initial_title;
        this.initial_body = initial_body;
        this.logbooks = logbook_factory.getLoogbooks();
        this.image_filename = image_filename;

        // Try to allow resize, because the 'text' section could
        // use more or less space depending on use.
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    /** @return Logbook Factory */
    protected ILogbookFactory getLogbook_factory()
    {
        return logbook_factory;
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
        // Title, title image, handle image disposal
        final Image title_image =
            Activator.getImageDescriptor("icons/elog_image.png").createImage(); //$NON-NLS-1$
        setTitle(Messages.ELog_Dialog_DialogTitle);
        setMessage(message);
        setTitleImage(title_image);
        parent.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                title_image.dispose();
            }
        });

        // From peeking at super.createDialogArea we happen to expect a Compos.
        final Composite area = (Composite) super.createDialogArea(parent);
        
        final SashForm sash = new SashForm(area, SWT.VERTICAL);
        sash.setLayout(new FillLayout());
        sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        // Put our widgets in another box to have own layout in there 
        final Composite box = new Composite(sash, 0);
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
        GridData gd = new GridData();
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
            
            logbook.setItems(logbooks);
            logbook.setText(logbook_factory.getDefaultLogbook());
        }
        
        // New Row
        l = new Label(box, 0);
        l.setText(Messages.ELog_Dialog_Title);
        l.setLayoutData(new GridData());

        title = new Text(box, SWT.BORDER);
        title.setText(initial_title);
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
        body.setText(initial_body);
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
        
        // Maybe add image
        if (image_filename != null)
        {
            new ImagePreview(sash, Messages.Elog_Dialog_ImageComment, image_filename);
            sash.setWeights(new int[] { 80, 20 });
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
     *  Has to make the actual elog entry, or throw exception in case of
     *  errors, which will then be displayed by the dialog.
     *  @param logbook_name Name of logbook or <code>null</code> if there is
     *                      no support for multiple logbooks
     *  @param user User name
     *  @param password Password
     *  @param title Title of entry
     *  @param body Body text of entry
     */
    abstract public void makeElogEntry(String logbook_name, String user, String password, String title, String body) throws Exception;
}
