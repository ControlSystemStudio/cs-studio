/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.ui;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/** Pseudo {@link FieldEditor} that displays a text
 *  <p>
 *  While the {@link FieldEditorPreferencePage} is convenient for
 *  handling the preference access, it does not offer many
 *  layout options.
 *  <p>
 *  This 'FieldEditor' can be added to simply display a text
 *  and an optional separator. It is no true {@link FieldEditor}, it is
 *  not connected to any preference.
 *
 *  @author Kay Kasemir
 */
public class LabelFieldEditor extends FieldEditor
{
    final private boolean with_separator;

    /** Initialize
     *  @param label_text Text to display
     *  @param with_separator Include separator before the label?
     *  @param parent Parent widget
     */
    public LabelFieldEditor(final String label_text, final boolean with_separator,
            final Composite parent)
    {
        init("", label_text); //$NON-NLS-1$
        this.with_separator = with_separator;
        createControl(parent);
    }

    /** {@inheritDoc} */
    @Override
    public int getNumberOfControls()
    {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    protected void doFillIntoGrid(final Composite parent, final int numColumns)
    {
        if (with_separator)
        {
            final Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
            sep.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
        }
        final Label label = getLabelControl(parent);
        label.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
    }

    /** {@inheritDoc} */
    @Override
    protected void adjustForNumColumns(final int numColumns)
    {
        // NOP
    }

    /** Do not perform any preference access */
    @Override
    protected void doLoad()
    {
        // NOP
    }

    /** Do not perform any preference access */
    @Override
    protected void doLoadDefault()
    {
        // NOP
    }

    /** Do not perform any preference access */
    @Override
    protected void doStore()
    {
        // NOP
    }
}
