package org.csstudio.email.ui;

import org.csstudio.email.EMailSender;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for entering EMail and sending it.
 *  @author Kay Kasemir
 */
public class EMailDialog extends TitleAreaDialog
{
    final private String host, from, to, subject, body;
    final private String image_filename;
    
    private Text txt_from, txt_to, txt_subject, txt_body;

    /** Initialize for plain-text entry
     *  @param shell
     *  @param host
     *  @param from
     *  @param to
     *  @param subject
     *  @param body
     */
    public EMailDialog(final Shell shell, final String host, final String from,
            final String to, final String subject, final String body)
    {
        this(shell, host, from, to, subject, body, null);
    }

    /** Initialize for entry with image
     *  @param shell
     *  @param host
     *  @param from
     *  @param to
     *  @param subject
     *  @param body
     *  @param image_filename
     */
    public EMailDialog(final Shell shell, final String host, final String from,
            final String to, final String subject, final String body,
            final String image_filename)
    {
        super(shell);
        this.host = host;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.image_filename = image_filename;

        // Try to allow resize, because the 'text' section could
        // use more or less space depending on use.
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /** Set the dialog title. */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText("Send E-Mail");
    }
    
    /** Create the GUI. */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        // Title, title image, handle image disposal
        final Image title_image =
            Activator.getImageDescriptor("icons/email_image.png").createImage(); //$NON-NLS-1$
        setTitle("Send E-Mail");
        setMessage("Enter destination, edit subject and details...");
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
        
        // From:     ____from_______
        // To:       ___to____
        // Subject:  ___subject____
        // _____________text _______
        Label l = new Label(box, 0);
        l.setText("From:");
        l.setLayoutData(new GridData());
        txt_from = new Text(box, SWT.BORDER);
        txt_from.setToolTipText("Email address from which this is sent");
        txt_from.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_from.setText(from);

        l = new Label(box, 0);
        l.setText("To:");
        l.setLayoutData(new GridData());
        txt_to = new Text(box, SWT.BORDER);
        txt_to.setToolTipText("Destination Email address");
        txt_to.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_to.setText(to);

        l = new Label(box, 0);
        l.setText("Subject:");
        l.setLayoutData(new GridData());
        txt_subject = new Text(box, SWT.BORDER);
        txt_subject.setToolTipText("Email subject");
        txt_subject.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_subject.setText(subject);

        txt_body = new Text(box, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        txt_body.setToolTipText("Email subject");
        GridData gd = new GridData();
        gd.heightHint = 400; // Size guess. Hope that 'RESIZE' works as well.
        gd.widthHint = 400;
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        txt_body.setLayoutData(gd);
        txt_body.setText(body);
        
        // Maybe add image
        if (image_filename != null)
        {
            new ImagePreview(sash, "Attached Image", image_filename);
            sash.setWeights(new int[] { 80, 20 });
        }
    
        txt_to.setFocus();
        
        return area;
    }
    
    

    /** Send email, display errors. */
    @Override
    protected void okPressed()
    {
        try
        {
            final EMailSender mailer = new EMailSender(host,
                    txt_from.getText().trim(),
                    txt_to.getText().trim(),
                    txt_subject.getText().trim());
            mailer.addText(txt_body.getText().trim());
            if (image_filename != null)
                mailer.attachImage(image_filename);
            mailer.close();
        }
        catch (Exception ex)
        {
            setErrorMessage(ex.getMessage());
            return;
        }
        super.okPressed();
    }
}
