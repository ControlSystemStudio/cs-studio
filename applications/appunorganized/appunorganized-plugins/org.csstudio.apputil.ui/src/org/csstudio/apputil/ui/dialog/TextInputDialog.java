/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.dialog;

import org.csstudio.apputil.ui.Activator;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for editing multi-line text
 *  @author Kay Kasemir
 */
public class TextInputDialog extends TitleAreaDialog
{
    final String title, message, initial_value;
    private Text text;
    private String value = ""; //$NON-NLS-1$

    /** Initialize
     *  @param shell Shell
     *  @param title Title
     *  @param message Message
     *  @param initial_value Alarm model
     */
    public TextInputDialog(final Shell shell,
            final String title, final String message,
            final String initial_value)
    {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.title = title;
        this.message = message;
        this.initial_value = initial_value;
    }

    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(title);
    }

    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Set title & image, arrange for disposal of image
        setTitle(title);
        setMessage(message);
        final Image title_image =
            Activator.getImageDescriptor("icons/text_input_image.png").createImage(); //$NON-NLS-1$
        setTitleImage(title_image);
        parent_widget.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(final DisposeEvent e)
            {
                title_image.dispose();
            }
        });

        text = new Text(parent_composite, SWT.BORDER | SWT.MULTI);
        text.setText(initial_value);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return parent_composite;
    }

    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        value = this.text.getText().trim();
        super.okPressed();
    }

    public String getValue()
    {
        return value;
    }
}
