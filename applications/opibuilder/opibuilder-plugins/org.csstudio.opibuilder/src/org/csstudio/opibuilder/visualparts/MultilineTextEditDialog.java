/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**The dialog for editing multiline text.
 * @author Xihui Chen
 *
 */
public class MultilineTextEditDialog extends Dialog {

    private String title;
    private String contents;
    private Text text;

    protected MultilineTextEditDialog(Shell parentShell, String stringValue, String dialogTitle) {
        super(parentShell);
        this.title = dialogTitle;
        this.contents = stringValue;
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);
        // Single Text area within container.
        // Resize doesn't fully work, at least on OS X:
        // Making the Dialog bigger is fine, vertical scrollbars also work.
        // But when making the Dialog smaller, no horiz. scrollbars appear.
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        //gridData.widthHint = 300;
        gridData.heightHint = 150;
        text = new Text(container, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        text.setText(contents);
        text.setSelection(0, contents.length());
        text.setLayoutData(gridData);
        text.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == '\r') { // Return key
                    if ((e.stateMask & SWT.CTRL) != 0) {
                        okPressed();
                    }
                }
                return;
            }
        });
        return container;
    }

    @Override
    protected void okPressed() {
        contents = text.getText();
        super.okPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    public String getResult() {

        return contents;
    }
}
