/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.ui;

import org.csstudio.apputil.ui.swt.ImageTabFolder;
import org.csstudio.email.EMailSender;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/** Dialog for entering EMail and sending it.
 *  @author Kay Kasemir, Boris Versic
 */
public class EMailSenderDialog extends TitleAreaDialog
{
    final private String host, from, to, subject, body;
    final private String image_filename;

    private Text txt_from, txt_to, txt_subject, txt_body;
    private ImageTabFolder image_tabfolder;
    
    // AbstractSendEMailAction creates a new Shell as the parent for this dialog, because otherwise
    // the dialog is opened in the background when running a fullscreen OPI.
    // AbstractSendEMailAction stores this shell for EMailSenderDialog to dispose it on close.
    private Shell cleanupShell;

    /** Initialize for plain-text entry
     *  @param shell
     *  @param host
     *  @param from
     *  @param to
     *  @param subject
     *  @param body
     */
    public EMailSenderDialog(final Shell shell, final String host, final String from,
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
    public EMailSenderDialog(final Shell shell, final String host, final String from,
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
        this.cleanupShell = null;
    }

    /** Initialize size and position of dialog parent shell, and make EMail dialog dispose of it in onClose().
     *  Call only once, before calling open().
     * 
     * @param shell
     */
    public void initializeOwnParentShell(final Shell shell) {
    	this.cleanupShell = shell;

    	shell.setSize(20, 20);
		Rectangle windowBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
		// center horizontally, but place it near the top of the window
		Point location = new Point(windowBounds.x + windowBounds.width / 2 - shell.getBounds().x / 2,
                                   windowBounds.y + 100 + shell.getBounds().y + shell.getBounds().height);
		shell.setLocation(location);
    }
    
    @Override
	public boolean close() {
    	if (this.cleanupShell != null) this.cleanupShell.dispose();
		return super.close();
	}

	/** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** Make EMail dialog non-modal to allow user access to the "rest"
     *  of the application.
     *  This was requested by operators who tend to edit the entry
     *  for a while but still need for example operator displays
     *  to remain responsive.
     */
    @Override
    protected void setShellStyle(final int style)
    {
        super.setShellStyle(style & ~SWT.APPLICATION_MODAL);
    }

    /** Set the dialog title. */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.SendEmail);
    }

    /** Create the GUI. */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        // Title, title image, handle image disposal
        final Image title_image =
            Activator.getImageDescriptor("icons/mail-edit-48.png").createImage(); //$NON-NLS-1$
        setTitle(Messages.SendEmail);
        setMessage(Messages.EmailDialogMessage);
        setTitleImage(title_image);
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
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
        final Composite box = new Composite(sash, SWT.BORDER);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        box.setLayout(layout);

        // From:     ____from_______
        // To:       ___to____
        // Subject:  ___subject____
        // _____________text _______
        Label l = new Label(box, 0);
        l.setText(Messages.From);
        l.setLayoutData(new GridData());
        txt_from = new Text(box, SWT.BORDER);
        txt_from.setToolTipText(Messages.FromTT);
        txt_from.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_from.setText(from);

        l = new Label(box, 0);
        l.setText(Messages.To);
        l.setLayoutData(new GridData());
        txt_to = new Text(box, SWT.BORDER);
        txt_to.setToolTipText(Messages.ToTT);
        txt_to.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_to.setText(to);

        l = new Label(box, 0);
        l.setText(Messages.Subject);
        l.setLayoutData(new GridData());
        txt_subject = new Text(box, SWT.BORDER);
        txt_subject.setToolTipText(Messages.SubjectTT);
        txt_subject.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        txt_subject.setText(subject);

        txt_body = new Text(box, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        txt_body.setToolTipText(Messages.MessageBodyTT);
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

        image_tabfolder = new ImageTabFolder(sash, SWT.BORDER);
        // Maybe add image
        if (image_filename != null)
            image_tabfolder.addImage(image_filename);
        sash.setWeights(new int[] { 80, 20 });

        // User needs to enter at least a 'to', replacing
        // the default "<enter email here>".
        // User is also likely to update the complete default
        // subject and 'from', so select all on focus:
        final FocusListener select_all = new FocusAdapter()
        {
            @Override
            public void focusGained(final FocusEvent e)
            {
                final Text text = (Text) e.widget;
                text.selectAll();
            }
        };
        txt_from.addFocusListener(select_all);
        txt_to.addFocusListener(select_all );
        txt_subject.addFocusListener(select_all);

        txt_to.setFocus();

        return area;
    }

    /** Add an "Add Image" button to the dialog's button bar */
    @Override
    protected Control createButtonBar(final Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(4, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        composite.setFont(parent.getFont());

        Button button = image_tabfolder.createAddButton(composite);
        button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        button = image_tabfolder.createScreenshotButton(composite, true);
        button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        button = image_tabfolder.createScreenshotButton(composite, false);
        button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        super.createButtonBar(composite);
        return composite;
    }

    /** Send email, display errors. */
    @Override
    protected void okPressed()
    {
        try
        {
            final String from = txt_from.getText().trim();
            final String to = txt_to.getText().trim();

            // Basic verification
            if (from.isEmpty())
            {
                setErrorMessage(Messages.FromErrorMsg);
                txt_from.setFocus();
                return;
            }
            if (to.isEmpty())
            {
                setErrorMessage(Messages.ToErrorMsg);
                txt_to.setFocus();
                return;
            }
            final EMailSender mailer = new EMailSender(host,
                    from,
                    to,
                    txt_subject.getText().trim());
            mailer.addText(txt_body.getText().trim());
            for (String image : image_tabfolder.getFilenames())
                mailer.attachImage(image);
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
