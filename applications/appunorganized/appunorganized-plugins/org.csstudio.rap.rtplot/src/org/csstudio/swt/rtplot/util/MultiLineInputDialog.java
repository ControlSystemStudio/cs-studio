/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.util;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/** InputDialog that allows entering multi-lined text.
 *  @author Kay Kasemir based on http://bingjava.appspot.com/model?id=752002
 */
public class MultiLineInputDialog extends InputDialog
{
    public MultiLineInputDialog(final Shell parentShell, final String dialogTitle,
            final String dialogMessage, final String initialValue, final IInputValidator validator)
    {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
    }

    /** Make text field multi-lined and add scroll bar */
    @Override
    protected int getInputTextStyle()
    {
        return SWT.MULTI | SWT.BORDER | SWT.V_SCROLL;
    }

    /** Increase height */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Control res = super.createDialogArea(parent);
        ((GridData) this.getText().getLayoutData()).heightHint = 100;
        return res;
    }
}
